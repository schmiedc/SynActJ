package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    void loopOverImages() {

        int minSizePxSpot;
        int maxSizePxSpot;

        int minSizePxBack;
        int maxSizePxBack;

        Calibration calibration;

        IJ.showStatus("Running pHlourin Batch processing...");

        for (String image : fileList) {


            IJ.log("Processing file: " + image);

            Image batchImage = new Image( inputDir, pxSizeMicron, frameRate );
            ImagePlus imageToProcess = batchImage.openImage(image);

            if (calibrationSetting) {

                calibration = batchImage.calibrate();

                imageToProcess.setCalibration(calibration);
                minSizePxSpot = Image.calculateMinSizePx(pxSizeMicron, minSizeSpot);
                maxSizePxSpot = Image.calculateMaxSizePx(pxSizeMicron, maxSizeSpot);

                minSizePxBack = Image.calculateMinSizePx(pxSizeMicron, minSizeBack);
                maxSizePxBack = Image.calculateMaxSizePx(pxSizeMicron, maxSizeBack);

                IJ.log("Metadata will be overwritten.");
                IJ.log("Pixel size set to: " + pxSizeMicron);
                IJ.log("Frame rate set to: " + frameRate);

            } else {

                calibration = imageToProcess.getCalibration();
                Double pxSizeFromImage = calibration.pixelWidth;
                minSizePxSpot = Image.calculateMinSizePx(pxSizeFromImage, minSizeSpot);
                maxSizePxSpot = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeSpot);

                minSizePxBack = Image.calculateMinSizePx(pxSizeFromImage, minSizeBack);
                maxSizePxBack = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeBack);

                IJ.log("Metadata will no be overwritten");

            }

            spotAnalysis(imageToProcess, minSizePxSpot, maxSizePxSpot);

            backgroundAnalysis(imageToProcess, minSizePxBack, maxSizePxBack);
            IJ.log("Measurement in image " + image + " finished");

        }

        IJ.showStatus("pHlourin Batch processing finished!");
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

    private void measureROI(ImagePlus imageMeasure, ImagePlus binaryImage, String saveDir, ParticleAnalyzer particleAnalyzer, String measureName) {

        // setup ROI manager
        RoiManager manager = new RoiManager(false);
        ParticleAnalyzer.setRoiManager(manager);

        // setup measurements
        IJ.run("Set Measurements...", "area mean standard modal min integrated median redirect=None decimal=3");

        // get ROIs
        particleAnalyzer.analyze(binaryImage);

        // save ROIs
        try {

            manager.runCommand("Save", saveDir + File.separator + imageMeasure.getShortTitle().replace(File.separator, "_") + "_" + measureName + ".zip");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("Unable to save ROI(s)");

         }

        // get count of ROIs
        int roiCount = manager.getCount();
        IJ.log("There are " + roiCount + " ROI(s) for " + measureName);

        for (int roi = 0; roi <= roiCount; roi++ ) {

            manager.select(roi);
            ResultsTable results = manager.multiMeasure(imageMeasure);

            try  {

                results.saveAs(saveDir + imageMeasure.getShortTitle().replace(File.separator, "_") + "_Roi-" + String.format("%03d", roi) + "_" + measureName + ".csv");

            } catch (Exception ex) {

                ex.printStackTrace();
                IJ.log("Could not save spot measurement results: " + imageMeasure.getShortTitle().replace(File.separator, "_"));

            }

        }

        IJ.log("Measuring intensities for " + measureName + " finished.");

    }

    private void spotAnalysis(ImagePlus inputImage, int minSizePxSpot, int maxSizePxSpot) {

        DifferenceImage processImage = new DifferenceImage(projMethod);
        ImagePlus diffImage = processImage.createDiffImage(inputImage, stimFrame);

        SpotSegmenter spot = new SpotSegmenter();
        ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots, spotErosion);

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null, minSizePxSpot, maxSizePxSpot, lowCirc, highCirc );
        measureROI(inputImage, watershed, outputDir, analyzer, "Spot");

        FileSaver saveDiffImage = new FileSaver(diffImage);

        try {

            saveDiffImage.saveAsTiff( outputDir + File.separator + inputImage.getShortTitle().replace(File.separator, "_") + "_spot.tif");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("Unable to save image");

        }

    }

    private void backgroundAnalysis(ImagePlus inputImage, int minSizePxBack, int maxSizePxBack) {

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(inputImage, sigmaBackground, thresholdBackground);
        ImagePlus backgroundImage = new ImagePlus("background", background);

        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePxBack, maxSizePxBack);

        measureROI(inputImage, backgroundImage, outputDir, backAnalyzer, "background");

        FileSaver saveBackImage = new FileSaver(inputImage);

        try {

            saveBackImage.saveAsTiff(outputDir + File.separator + inputImage.getShortTitle().replace(File.separator, "_") + "_background.tif");

        } catch (Exception ex) {

            ex.printStackTrace();
            IJ.log("Unable to save image");

        }


    }

    BatchProcessor (String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess) {

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList = filesToProcess;

        // Projection Method
        projMethod = "median";

        sigmaLoG = 0.5;
        prominence = 0.005;
        sigmaSpots = 1.0;
        rollingSpots = 30.0;
        thresholdSpots = "Triangle";
        spotErosion = false;
        radiusGradient = 3;
        minSizeSpot = 0.0;
        maxSizeSpot = 1000.0;
        lowCirc = 0.0;
        highCirc = 1.0;

        sigmaBackground = 4.0;
        thresholdBackground = "MinError";
        minSizeBack = 0.0;
        maxSizeBack = 10000.0;

        calibrationSetting = false;
        stimFrame = 5;
        pxSizeMicron = 0.162;
        frameRate = 2.0;
    }

    BatchProcessor(String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess,
                          String setProjectionMethod, double setSigmaLoG, double setProminence,
                          double setSigmaSpots, double setRollingSpots, String setThresholdSpots, boolean setSpotErosion,
                          int setRadiusGradient,
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
