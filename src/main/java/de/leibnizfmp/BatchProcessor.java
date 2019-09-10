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
    ArrayList<String> fileList;
    Boolean calibrationSetting;
    Double pxSizeMicron;
    Double frameRate;

    String projMethod;
    double sigmaLoG;
    double prominence;
    double sigmaSpots;
    double rollingSpots;
    String thresholdSpots;
    int radiusGradient;
    double minSizeSpot;
    double maxSizeSpot;
    double lowCirc;
    double highCirc;

    double sigmaBackground;
    String thresholdBackground;
    double minSizeBack;
    double maxSizeBack;
    int stimFrame;

    Calibration calibration;

    public void loopOverImages(String directory, ArrayList<String> listOfFiles,
                               Boolean calibrationSetting, Double pxSize, Double frameRate,
                               Double minSizeSpot, Double maxSizeSpot, Double minSizeBack, Double maxSizeBack) {

        int minSizePxSpot;
        int maxSizePxSpot;

        int minSizePxBack;
        int maxSizePxBack;

        for (String image : listOfFiles) {

            Image batchImage = new Image( directory, pxSize, frameRate );
            ImagePlus imageToProcess = batchImage.openImage(image);

            if (calibrationSetting) {

                calibration = batchImage.calibrate();
                minSizePxSpot = batchImage.calculateMinSizePx(pxSize, minSizeSpot);
                maxSizePxSpot = batchImage.calculateMaxSizePx(pxSize, maxSizeSpot);

                minSizePxBack = batchImage.calculateMinSizePx(pxSize, minSizeBack);
                maxSizePxBack = batchImage.calculateMaxSizePx(pxSize, maxSizeBack);

                System.out.println("Metadata will be overwritten.");
                System.out.println("Pixel size set to: " + pxSize);
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
        analyzer.analyze(watershed);

    }

    public void backgroundAnalysis(ImagePlus inputImage, int minSizePxBack, int maxSizePxBack) {

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(inputImage, sigmaBackground, thresholdBackground);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePxBack, maxSizePxBack);

        ImagePlus testBack = new ImagePlus("test", background);
        backAnalyzer.analyze(testBack);

    }

    public BatchProcessor(String directory, ArrayList<String> filesToProcess,
                          String projctionMethod, double setSigmaLoG, double setProminence,
                          double setSigmaSpots, double setRollingSpots, String setThresholdSpots, int setRadiusGradient,
                          double setMinSizePxSpot, double setMaxSizePxSpot, double setLowCirc, double setHighCirc,
                          double setSigmaBackground, String setThresholdBackground,
                          double setMinSizePxBack, double setMaxSizePxBack,
                          int setStimFrame, Boolean setCalibrationSetting, Double setSizeMicron, Double setFrameRate ) {

        inputDir = directory;
        fileList = filesToProcess;
        projMethod = projctionMethod;
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
