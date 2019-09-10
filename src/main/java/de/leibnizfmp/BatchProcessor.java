package de.leibnizfmp;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.measure.Calibration;
import ij.plugin.Commands;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

import java.io.File;
import java.util.ArrayList;

public class BatchProcessor {

    String inputDir;
    String outputDir;
    ArrayList<String> fileList;
    Boolean calibrationSetting;
    Double pxSizeMicron;
    Double frameRate;

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

    private Calibration calibration;

    public void loopOverImages() {

        int minSizePxSpot;
        int maxSizePxSpot;

        int minSizePxBack;
        int maxSizePxBack;

        for (String image : fileList) {

            System.out.println("Processing file: " + image);

            Image batchImage = new Image( inputDir, pxSizeMicron, frameRate );
            ImagePlus imageToProcess = batchImage.openImage(image);

            if (calibrationSetting) {

                calibration = batchImage.calibrate();
                minSizePxSpot = batchImage.calculateMinSizePx(pxSizeMicron, minSizeSpot);
                maxSizePxSpot = batchImage.calculateMaxSizePx(pxSizeMicron, maxSizeSpot);

                minSizePxBack = batchImage.calculateMinSizePx(pxSizeMicron, minSizeBack);
                maxSizePxBack = batchImage.calculateMaxSizePx(pxSizeMicron, maxSizeBack);

                System.out.println("Metadata will be overwritten.");
                System.out.println("Pixel size set to: " + pxSizeMicron);
                System.out.println("Frame rate set to: " + frameRate);

            } else {

                calibration = imageToProcess.getCalibration();
                Double pxSizeFromImage = calibration.pixelWidth;
                minSizePxSpot = batchImage.calculateMinSizePx(pxSizeFromImage, minSizeSpot);
                maxSizePxSpot = batchImage.calculateMaxSizePx(pxSizeFromImage, maxSizeSpot);

                minSizePxBack = batchImage.calculateMinSizePx(pxSizeFromImage, minSizeBack);
                maxSizePxBack = batchImage.calculateMaxSizePx(pxSizeFromImage, maxSizeBack);

                System.out.println("Metadata will no be overwritten");

            }

            spotAnalysis(imageToProcess, minSizePxSpot, maxSizePxSpot);

            //backgroundAnalysis(imageToProcess, minSizePxBack, maxSizePxBack);

        }

    }

    public void spotAnalysis(ImagePlus inputImage, int minSizePxSpot, int maxSizePxSpot) {

        DifferenceImage processImage = new DifferenceImage(projMethod);
        ImagePlus diffImage = processImage.createDiffImage(inputImage, stimFrame);

        SpotSegmenter spot = new SpotSegmenter();
        ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots);

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null, minSizePxSpot, maxSizePxSpot, lowCirc, highCirc );
        //analyzer.analyze(watershed);

    }

    public void backgroundAnalysis(ImagePlus inputImage, int minSizePxBack, int maxSizePxBack) {

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(inputImage, sigmaBackground, thresholdBackground);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePxBack, maxSizePxBack);

        ImagePlus testBack = new ImagePlus("test", background);
        //backAnalyzer.analyze(testBack);

    }

    public BatchProcessor(String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess,
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
