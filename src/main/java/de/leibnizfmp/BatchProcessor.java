package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.io.LogStream;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

import java.io.File;
import java.io.IOException;
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

        IJ.showStatus("Running pHlourin Batch processing...");

        for (String image : fileList) {


            IJ.log("Processing file: " + image);

            Image batchImage = new Image( inputDir, pxSizeMicron, frameRate );
            ImagePlus imageToProcess = batchImage.openImage(image);

            if (calibrationSetting) {

                batchImage.calibrate();
                minSizePxSpot = Image.calculateMinSizePx(pxSizeMicron, minSizeSpot);
                maxSizePxSpot = Image.calculateMaxSizePx(pxSizeMicron, maxSizeSpot);

                minSizePxBack = Image.calculateMinSizePx(pxSizeMicron, minSizeBack);
                maxSizePxBack = Image.calculateMaxSizePx(pxSizeMicron, maxSizeBack);

                IJ.log("Metadata will be overwritten.");
                IJ.log("Pixel size set to: " + pxSizeMicron);
                IJ.log("Frame rate set to: " + frameRate);

            } else {

                Calibration calibration = imageToProcess.getCalibration();
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
        IJ.log("There are " + frameNumber + " frames");

        for (int frame = 1; frame <= frameNumber; frame++ ) {

            inputImage.setT(frame);
            manager.runCommand(inputImage, "Select All");
            manager.runCommand(inputImage, "Measure");
            table = ResultsTable.getResultsTable();

            try {

                table.save(outputDir + inputImage.getShortTitle().replace(File.separator, "_") + "_" + String.format("%03d", frame) + "_signal.csv");

            } catch (IOException ex) {

                ex.printStackTrace();
                IJ.log("Could not save background results: " + inputImage.getShortTitle().replace(File.separator, "_"));

            }

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
        IJ.log("Measuring intensities in spots finished.");
    }

    private void backgroundAnalysis(ImagePlus inputImage, int minSizePxBack, int maxSizePxBack) {

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(inputImage, sigmaBackground, thresholdBackground);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePxBack, maxSizePxBack);

        ImagePlus testBack = new ImagePlus("test", background);
        backAnalyzer.analyze(testBack);

        // setup measurements
        ResultsTable table = new ResultsTable();
        //TextWindow tableWindow = table.getResultsWindow();
        //tableWindow.setSize(1,1);

        IJ.run("Set Measurements...", "area mean standard modal min integrated median redirect=None decimal=3");

        // loop over original image
        int frameNumber = inputImage.getNFrames();
        IJ.log("There are " + frameNumber + " frames");

        for (int frame = 1; frame <= frameNumber; frame++ ) {

            inputImage.setT(frame);
            manager.runCommand(inputImage, "Select All");
            manager.runCommand(inputImage, "Measure");
            table = ResultsTable.getResultsTable();

            try  {

                table.saveAs(outputDir + inputImage.getShortTitle().replace(File.separator, "_") + "_" + String.format("%03d", frame) + "_background.csv");

            } catch (IOException ex) {

                ex.printStackTrace();
                IJ.log("Could not save background results: " + inputImage.getShortTitle().replace(File.separator, "_"));

            }


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
        IJ.log("Measuring intensities in background finished.");
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
                          double setSigmaSpots, double setRollingSpots, String setThresholdSpots, int setRadiusGradient,
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
