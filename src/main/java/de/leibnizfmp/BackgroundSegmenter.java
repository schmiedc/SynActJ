package de.leibnizfmp;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.plugin.ZProjector;

/**
 * segments background regions of the image
 * background means strictly camera offset regions!
 *
 * @author christopher schmied
 * @version 1.0.0
 */
class BackgroundSegmenter {

    /**
     * Global Intensity threshold based segmentation
     *
     * @param image movie
     * @param gauss sigma for gaussian blur
     * @param threshold global intensity based threshold
     * @return returns binary mask of background regions
     */
    ByteProcessor segmentBackground(ImagePlus image, double gauss, String threshold){

        ImagePlus backProject = ZProjector.run(image,"Max");

        ImageProcessor processImage = backProject.getProcessor();
        processImage.blurGaussian(gauss);

        processImage.setAutoThreshold(threshold, true, 1);
        ByteProcessor result = processImage.createMask();

        //result.erode();
        result.erode();
        result.invert();

        return result;


    }
}
