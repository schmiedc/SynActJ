package de.leibnizfmp;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.measure.Calibration;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

/**
 * implements the visualization of the segmentation for the previewGui
 */
class SegmentationVisualization {

    /**
     * visualization of the spot segmentation
     *
     * @param originalImage selected image for vis
     * @param projMethod projection method
     * @param stimFrame frame when stimulation happens
     * @param sigmaLoG sigma for LoG
     * @param prominence prominence for spot detection
     * @param sigmaSpots sigma for spot segmentation
     * @param rollingSpots rolling ball background radius for spot segmentation
     * @param thresholdSpots global intensity based threshold for spots
     * @param spotErosion binary mask erosion for spots
     * @param radiusGradient radius for creating gradient image (watershed)
     * @param minSizePx minimum spot size in px
     * @param maxSizePx maximum spot size in px
     * @param lowCirc minimum circularity of spots
     * @param highCirc maximum circularity of spots
     * @param calibration image calibration setting
     * @param setDisplayRange sets the display range for vis
     */
    void spotVisualization(ImagePlus originalImage, String projMethod, int stimFrame, double sigmaLoG,
                        double prominence, double sigmaSpots, double rollingSpots, String thresholdSpots,
                        boolean spotErosion, int radiusGradient, int minSizePx, int maxSizePx,
                        double lowCirc, double highCirc,
                        Calibration calibration, boolean setDisplayRange) {

        // set the specified calibration
        originalImage.setCalibration(calibration);
        originalImage.setOverlay(null);

        DifferenceImage processImage = new DifferenceImage(projMethod);
        ImagePlus diffImage = processImage.createDiffImage(originalImage, stimFrame);

        SpotSegmenter spot = new SpotSegmenter();
        ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);

        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots,
                rollingSpots, thresholdSpots, spotErosion);

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        RoiManager manager = new RoiManager();

        ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null,
                minSizePx, maxSizePx, lowCirc, highCirc );

        analyzer.analyze(watershed);

        manager.moveRoisToOverlay(originalImage);
        Overlay overlay = originalImage.getOverlay();
        overlay.drawLabels(false);

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

    /**
     * visualization of the background segmentation
     * @param forBackSegmentation projected image used for the background segmentation
     * @param sigmaBackground sigma gaussian blur for background segmentation
     * @param thresholdBackground global intensity threshold for background segmentation
     * @param minSizePx minimum background region size
     * @param maxSizePx maximum background region size
     * @param originalImage the original image for vis
     * @param titleOriginal title of the original image
     * @param calibration image calibration setting
     * @param setDisplayRange sets the display range for vis
     */
    void backgroundVisualization(ImagePlus forBackSegmentation, double sigmaBackground, String thresholdBackground,
                        int minSizePx, int maxSizePx, ImagePlus originalImage, String titleOriginal,
                        Calibration calibration, boolean setDisplayRange) {

        // set the specified calibration
        originalImage.setCalibration(calibration);
        originalImage.setOverlay(null);

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(forBackSegmentation, sigmaBackground, thresholdBackground);

        RoiManager manager = new RoiManager();
        ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePx, maxSizePx);

        ImagePlus testBack = new ImagePlus("test", background);
        backAnalyzer.analyze(testBack);

        manager.moveRoisToOverlay(originalImage);
        Overlay overlay = originalImage.getOverlay();
        overlay.drawLabels(false);
        originalImage.setTitle(titleOriginal);

        if (setDisplayRange) {

            double rangeMin = originalImage.getDisplayRangeMin();
            double newLower = rangeMin * 1.75;
            double rangeMax = originalImage.getDisplayRangeMax();
            double newUpper = (rangeMax / 2);
            originalImage.setDisplayRange(newLower, newUpper);

        }

        originalImage.show();

        manager.reset();
        manager.close();

    }
}
