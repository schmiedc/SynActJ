package de.leibnizfmp;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.measure.Calibration;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

class SegmentationVisualization {

    void spotVisualization(ImagePlus originalImage, String projMethod, int stimFrame, double sigmaLoG,
                        double prominence, double sigmaSpots, double rollingSpots, String thresholdSpots, boolean spotErosion,
                        int radiusGradient, int minSizePx, int maxSizePx, double lowCirc, double highCirc,
                        Calibration calibration, boolean setDisplayRange) {

        // set the specified calibration
        originalImage.setCalibration(calibration);

        DifferenceImage processImage = new DifferenceImage(projMethod);
        ImagePlus diffImage = processImage.createDiffImage(originalImage, stimFrame);

        SpotSegmenter spot = new SpotSegmenter();
        ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots, spotErosion);

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null, minSizePx, maxSizePx, lowCirc, highCirc );
        analyzer.analyze(watershed);

        manager.moveRoisToOverlay(watershed);
        Overlay overlay = watershed.getOverlay();
        overlay.drawLabels(false);

        originalImage.setOverlay(overlay);

        if (setDisplayRange) {

            double rangeMin = originalImage.getDisplayRangeMin();
            double newLower = rangeMin * 1.75;
            double rangeMax = originalImage.getDisplayRangeMax();
            double newUpper = (rangeMax / 2 );

            originalImage.setDisplayRange(newLower,newUpper);

        }

        originalImage.show();

        manager.reset();
        manager.close();

    }

    void backgroundVisualization(ImagePlus forBackSegmentation, double sigmaBackground, String thresholdBackground,
                        int minSizePx, int maxSizePx, ImagePlus originalImage, String titleOriginal,
                        Calibration calibration, boolean setDisplayRange) {

        // set the specified calibration
        originalImage.setCalibration(calibration);

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(forBackSegmentation, sigmaBackground, thresholdBackground);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePx, maxSizePx);

        ImagePlus testBack = new ImagePlus("test", background);
        backAnalyzer.analyze(testBack);

        manager.moveRoisToOverlay(testBack);
        Overlay overlay = testBack.getOverlay();
        overlay.drawLabels(false);

        originalImage.setOverlay(overlay);
        originalImage.setTitle(titleOriginal);

        if (setDisplayRange) {

            double rangeMin = originalImage.getDisplayRangeMin();
            double newLower = rangeMin * 1.75;
            double rangeMax = originalImage.getDisplayRangeMax();
            double newUpper = (rangeMax / 3);
            originalImage.setDisplayRange(newLower, newUpper);

        }

        originalImage.show();

        manager.reset();
        manager.close();

    }
}
