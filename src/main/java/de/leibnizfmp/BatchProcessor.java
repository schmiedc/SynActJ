package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import trainableSegmentation.ImageScience;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class applies the image analysis operations over all loaded images
 *
 * @author christopher schmied
 * @version 1.0.0
 */
class BatchProcessor {

    private String inputDir;
    private String outputDir;
    private ArrayList<String> fileList;
    private Boolean calibrationSetting;
    private Double pxSizeMicron;
    private Double frameRate;

    private String projMethod;
    private double sigmaLoG;
    private double prominence;
    private double sigmaSpots;
    private double rollingSpots;
    private String thresholdSpots;
    private boolean spotErosion;
    private int radiusGradient;
    private double minSizeSpot;
    private double maxSizeSpot;
    private double lowCirc;
    private double highCirc;

    private double sigmaBackground;
    private String thresholdBackground;
    private double minSizeBack;
    private double maxSizeBack;
    private int stimFrame;

    /**
     * loops the following operations over the images in the fileList
     * creation and opening of image
     * adjustment of calibration settings
     * spotAnalysis
     * backgroundAnalysis
     */
    void loopOverImages() {

        int minSizePxSpot;
        int maxSizePxSpot;

        int minSizePxBack;
        int maxSizePxBack;

        Calibration calibration;

        // first get original setting
        boolean backgroundSetting = Prefs.blackBackground;
        IJ.log("Black background set to: " + backgroundSetting);

        // set background color to black
        Prefs.blackBackground = true;
        IJ.log("Black background set to: " + Prefs.blackBackground);

        IJ.showStatus("Running SynActJ Batch processing...");

        for (String image : fileList) {


            IJ.log("Processing file: " + image);

            Image batchImage = new Image( inputDir, pxSizeMicron, frameRate );
            ImagePlus imageToProcess = batchImage.openImage(image);

            // if true adjusts image calibration using input px size settings
            if (calibrationSetting) {

                calibration = batchImage.calibrate();

                imageToProcess.setCalibration(calibration);
                minSizePxSpot = Image.calculateSizePx(pxSizeMicron, minSizeSpot);
                maxSizePxSpot = Image.calculateSizePx(pxSizeMicron, maxSizeSpot);

                minSizePxBack = Image.calculateSizePx(pxSizeMicron, minSizeBack);
                maxSizePxBack = Image.calculateSizePx(pxSizeMicron, maxSizeBack);

                IJ.log("Metadata will be overwritten.");
                IJ.log("Pixel size set to: " + pxSizeMicron);
                IJ.log("Frame rate set to: " + frameRate);

            } else {

                calibration = imageToProcess.getCalibration();
                Double pxSizeFromImage = calibration.pixelWidth;
                minSizePxSpot = Image.calculateSizePx(pxSizeFromImage, minSizeSpot);
                maxSizePxSpot = Image.calculateSizePx(pxSizeFromImage, maxSizeSpot);

                minSizePxBack = Image.calculateSizePx(pxSizeFromImage, minSizeBack);
                maxSizePxBack = Image.calculateSizePx(pxSizeFromImage, maxSizeBack);

                IJ.log("Metadata will no be overwritten");

            }

            spotAnalysis(imageToProcess, minSizePxSpot, maxSizePxSpot);

            backgroundAnalysis(imageToProcess, minSizePxBack, maxSizePxBack);
            IJ.log("Measurement in image " + image + " finished");

        }

        // restore original setting
        Prefs.blackBackground = backgroundSetting;
        IJ.log("Original blackBackground setting restored");

        IJ.showStatus("SynActJ Batch processing finished!");
        IJ.log("Finished batch Processing");

        IJ.selectWindow("Log");

        try  {

            String logName = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss'-settings.xml'").format(new Date());
            IJ.saveAs("Text", outputDir + File.separator + logName + "_Log.txt");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("WARNING: saving of log file failed");

        }

    }

    /**
     * performs measurements in ROIs in every frame of the movie
     *
     * @param imageMeasure image for measurement
     * @param binaryImage for creating ROIs
     * @param saveDir directory for saving results and ROIs
     * @param particleAnalyzer particle analyzer object that creates ROIs from binary image
     * @param measureName results name
     */
    private void measureROI(ImagePlus imageMeasure, ImagePlus binaryImage, String saveDir,
                            ParticleAnalyzer particleAnalyzer, String measureName) {

        // setup ROI manager
        RoiManager manager = new RoiManager(false);
        ParticleAnalyzer.setRoiManager(manager);

        // setup measurements
        IJ.run("Set Measurements...", "area mean standard modal min integrated median" +
                " redirect=None decimal=3");

        // get ROIs
        particleAnalyzer.analyze(binaryImage);

        // get count of ROIs
        int roiCount = manager.getCount();
        IJ.log("There are " + roiCount + " ROI(s) for " + measureName);

        // if no ROIs abort measurement else proceed
        if (roiCount == 0) {

            IJ.log("WARNING: Skipping Measurements for " + measureName);

        } else {

            // save ROIs
            try {

                manager.runCommand("Save", saveDir + File.separator +
                        imageMeasure.getShortTitle().replace(File.separator, "_") + "_" +
                        measureName + ".zip");

            } catch (Exception ex) {

                ex.printStackTrace();
                IJ.log("Unable to save ROI(s)");

            }

            // loops over available ROIs
            // performs measurements and saved Results
            for (int roi = 0; roi <= roiCount; roi++) {

                manager.select(roi);
                ResultsTable results = manager.multiMeasure(imageMeasure);

                try {

                    results.saveAs(saveDir +
                            imageMeasure.getShortTitle().replace(File.separator, "_") +
                            "_Roi-" + String.format("%03d", roi) +
                            "_" + measureName + ".csv");

                } catch (Exception ex) {

                    ex.printStackTrace();
                    IJ.log("Could not save spot measurement results: " +
                            imageMeasure.getShortTitle().replace(File.separator, "_"));

                }

            }

            IJ.log("Measuring intensities for " + measureName + " finished.");

        }
    }

    /**
     * performs spot segmentation using a marker controlled watershed
     * calls the spot detection, spot segmentation and watershed
     *
     * @param inputImage takes the movie
     * @param minSizePxSpot minimum spot size considered for segmentation
     * @param maxSizePxSpot maximum spot size considered for segmentation
     */
    private void spotAnalysis(ImagePlus inputImage, int minSizePxSpot, int maxSizePxSpot) {

        DifferenceImage processImage = new DifferenceImage(projMethod);
        ImagePlus diffImage = processImage.createDiffImage(inputImage, stimFrame);

        SpotSegmenter spot = new SpotSegmenter();
        ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots,
                                                        rollingSpots, thresholdSpots, spotErosion);

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null, minSizePxSpot,
                maxSizePxSpot, lowCirc, highCirc );

        measureROI(inputImage, watershed, outputDir, analyzer, "Spot");

        FileSaver saveDiffImage = new FileSaver(diffImage);

        try {

            saveDiffImage.saveAsTiff( outputDir + File.separator +
                    inputImage.getShortTitle().replace(File.separator, "_") + "_spot.tif");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("Unable to save image");

        }

        ImagePlus logImage = ImageScience.computeLaplacianImage(sigmaLoG, diffImage);
        FileSaver saveLogImage = new FileSaver(logImage);

        try {

            saveLogImage.saveAsTiff( outputDir + File.separator +
                    inputImage.getShortTitle().replace(File.separator, "_") + "_LoG.tif");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("Unable to save image");

        }

    }

    /**
     * performs the background analysis by calling the backgroundSegmenter
     *
     * @param inputImage movie
     * @param minSizePxBack minimum background region size
     * @param maxSizePxBack maximum background region size
     */
    private void backgroundAnalysis(ImagePlus inputImage, int minSizePxBack, int maxSizePxBack) {

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(inputImage, sigmaBackground, thresholdBackground);
        ImagePlus backgroundImage = new ImagePlus("background", background);

        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null,
                minSizePxBack, maxSizePxBack);

        measureROI(inputImage, backgroundImage, outputDir, backAnalyzer, "background");

        FileSaver saveBackImage = new FileSaver(inputImage);

        try {

            saveBackImage.saveAsTiff(outputDir + File.separator +
                    inputImage.getShortTitle().replace(File.separator, "_") + "_background.tif");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("Unable to save image");

        }


    }

    /**
     * Batch processor constructor
     *
     * @param inputDirectory directory for input images
     * @param outputDirectory directory for saving results
     * @param filesToProcess list the file names for batch
     * @param setProjectionMethod projection method
     * @param setSigmaLoG sigma for LoG
     * @param setProminence prominence for spot detection
     * @param setSigmaSpots sigma for spot segmentation
     * @param setRollingSpots rolling ball background radius for spot segmentation
     * @param setThresholdSpots global intensity based threshold for spots
     * @param setSpotErosion binary mask erosion for spots
     * @param setRadiusGradient radius for creating gradient image (watershed)
     * @param setMinSizePxSpot minimum spot size in px
     * @param setMaxSizePxSpot maximum spot size in px
     * @param setLowCirc minimum circularity of spots
     * @param setHighCirc maximum circularity of spots
     * @param setSigmaBackground sigma gaussian blur for background segmentation
     * @param setThresholdBackground global intensity threshold for background segmentation
     * @param setMinSizePxBack minimum background region size
     * @param setMaxSizePxBack maximum background region size
     * @param setStimFrame frame when stimulation happens
     * @param setCalibrationSetting image calibration setting
     * @param setSizeMicron pixel size in micron
     * @param setFrameRate frame rate in seconds
     */
    BatchProcessor(String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess,
                          String setProjectionMethod, double setSigmaLoG, double setProminence,
                          double setSigmaSpots, double setRollingSpots, String setThresholdSpots,
                          boolean setSpotErosion, int setRadiusGradient,
                          double setMinSizePxSpot, double setMaxSizePxSpot, double setLowCirc, double setHighCirc,
                          double setSigmaBackground, String setThresholdBackground,
                          double setMinSizePxBack, double setMaxSizePxBack,
                          int setStimFrame, Boolean setCalibrationSetting, Double setSizeMicron, Double setFrameRate ) {

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList = filesToProcess;

        projMethod = setProjectionMethod;

        sigmaLoG = setSigmaLoG;
        prominence = setProminence;
        sigmaSpots = setSigmaSpots;
        rollingSpots = setRollingSpots;
        thresholdSpots = setThresholdSpots;
        spotErosion = setSpotErosion;
        radiusGradient = setRadiusGradient;
        minSizeSpot = setMinSizePxSpot;
        maxSizeSpot = setMaxSizePxSpot;
        lowCirc = setLowCirc;
        highCirc = setHighCirc;

        sigmaBackground = setSigmaBackground;
        thresholdBackground = setThresholdBackground;
        minSizeBack = setMinSizePxBack;
        maxSizeBack =  setMaxSizePxBack;

        calibrationSetting = setCalibrationSetting;
        stimFrame = setStimFrame;
        pxSizeMicron = setSizeMicron;
        frameRate = setFrameRate;

    }

}
