package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

import java.util.ArrayList;

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

        for (String image : fileList) {

            System.out.println("Processing file: " + image);

            Image batchImage = new Image( inputDir, pxSizeMicron, frameRate );
            ImagePlus imageToProcess = batchImage.openImage(image);

            if (calibrationSetting) {

                batchImage.calibrate();
                minSizePxSpot = Image.calculateMinSizePx(pxSizeMicron, minSizeSpot);
                maxSizePxSpot = batchImage.calculateMaxSizePx(pxSizeMicron, maxSizeSpot);

                minSizePxBack = Image.calculateMinSizePx(pxSizeMicron, minSizeBack);
                maxSizePxBack = batchImage.calculateMaxSizePx(pxSizeMicron, maxSizeBack);

                System.out.println("Metadata will be overwritten.");
                System.out.println("Pixel size set to: " + pxSizeMicron);
                System.out.println("Frame rate set to: " + frameRate);

            } else {

                Calibration calibration = imageToProcess.getCalibration();
                Double pxSizeFromImage = calibration.pixelWidth;
                minSizePxSpot = Image.calculateMinSizePx(pxSizeFromImage, minSizeSpot);
                maxSizePxSpot = batchImage.calculateMaxSizePx(pxSizeFromImage, maxSizeSpot);

                minSizePxBack = Image.calculateMinSizePx(pxSizeFromImage, minSizeBack);
                maxSizePxBack = batchImage.calculateMaxSizePx(pxSizeFromImage, maxSizeBack);

                System.out.println("Metadata will no be overwritten");

            }

            spotAnalysis(imageToProcess, minSizePxSpot, maxSizePxSpot);

            backgroundAnalysis(imageToProcess, minSizePxBack, maxSizePxBack);

            System.out.println("Measurement in image " + image + " finished");

        }

        System.out.println("Finished batch Processing");

    }

    private void spotAnalysis(ImagePlus inputImage, int minSizePxSpot, int maxSizePxSpot) {

        DifferenceImage processImage = new DifferenceImage(projMethod);
        ImagePlus diffImage = processImage.createDiffImage(inputImage, stimFrame);

        SpotSegmenter spot = new SpotSegmenter();
        ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots);

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null, minSizePxSpot, maxSizePxSpot, lowCirc, highCirc );
        analyzer.analyze(watershed);

        // setup measurements
        ResultsTable table;
        IJ.run("Set Measurements...", "area mean standard modal min integrated median redirect=None decimal=3");

        // loop over original image
        int frameNumber = inputImage.getNFrames();
        System.out.println("There are " + frameNumber + " frames");

        for (int frame = 1; frame <= frameNumber; frame++ ) {


            inputImage.setT(frame);
            System.out.println("Measuring frame " + frame);
            manager.runCommand(inputImage, "Select All");
            manager.runCommand(inputImage, "Measure");
            table = ResultsTable.getResultsTable();
            table.save(outputDir + inputImage.getShortTitle() + "_" + String.format("%03d", frame) + "_signal.csv");

            if ( IJ.isResultsWindow() ){
                IJ.selectWindow("Results");
                IJ.run("Close");
            }

        }

        if ( IJ.isResultsWindow() ){
            IJ.selectWindow("Results");
            IJ.run("Close");
        }

        manager.close();
        System.out.println("Measuring intensities in spots finished.");
    }

    private void backgroundAnalysis(ImagePlus inputImage, int minSizePxBack, int maxSizePxBack) {

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(inputImage, sigmaBackground, thresholdBackground);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePxBack, maxSizePxBack);

        ImagePlus testBack = new ImagePlus("test", background);
        backAnalyzer.analyze(testBack);

        // setup measurements
        ResultsTable table;
        IJ.run("Set Measurements...", "area mean standard modal min integrated median redirect=None decimal=3");

        // loop over original image
        int frameNumber = inputImage.getNFrames();
        System.out.println("There are " + frameNumber + " frames");

        for (int frame = 1; frame <= frameNumber; frame++ ) {

            inputImage.setT(frame);
            System.out.println("Measuring frame " + frame);
            manager.runCommand(inputImage, "Select All");
            manager.runCommand(inputImage, "Measure");
            table = ResultsTable.getResultsTable();
            table.save(outputDir + inputImage.getShortTitle() + "_" + String.format("%03d", frame) + "_background.csv");

            if ( IJ.isResultsWindow() ){
                IJ.selectWindow("Results");
                IJ.run("Close");
            }

        }

        if ( IJ.isResultsWindow() ){
            IJ.selectWindow("Results");
            IJ.run("Close");
        }

        manager.close();
        System.out.println("Measuring intensities in background finished.");
    }

    BatchProcessor(String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess,
                          String projectionMethod, double setSigmaLoG, double setProminence,
                          double setSigmaSpots, double setRollingSpots, String setThresholdSpots, int setRadiusGradient,
                          double setMinSizePxSpot, double setMaxSizePxSpot, double setLowCirc, double setHighCirc,
                          double setSigmaBackground, String setThresholdBackground,
                          double setMinSizePxBack, double setMaxSizePxBack,
                          int setStimFrame, Boolean setCalibrationSetting, Double setSizeMicron, Double setFrameRate ) {

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList = filesToProcess;
        projMethod = projectionMethod;
        sigmaLoG = setSigmaLoG;
        prominence = setProminence;
        sigmaSpots = setSigmaSpots;
        rollingSpots = setRollingSpots;
        thresholdSpots = setThresholdSpots;
        radiusGradient = setRadiusGradient;
        minSizeSpot = setMinSizePxSpot;
        maxSizeSpot = setMaxSizePxSpot;
        lowCirc = setLowCirc;
        highCirc = setHighCirc;
        sigmaBackground = setSigmaBackground;
        thresholdBackground = setThresholdBackground;
        minSizeBack = setMinSizePxBack;
        maxSizeBack =  setMaxSizePxBack;
        stimFrame = setStimFrame;
        calibrationSetting = setCalibrationSetting;
        pxSizeMicron = setSizeMicron;
        frameRate = setFrameRate ;

    }


}
