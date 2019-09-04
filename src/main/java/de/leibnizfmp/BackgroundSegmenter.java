package de.leibnizfmp;

import ij.ImagePlus;
import ij.plugin.ZProjector;
import ij.plugin.filter.BackgroundSubtracter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class BackgroundSegmenter {

    public ByteProcessor segmentBackground(ImagePlus image, double gauss, double rolling, String threshold){

        ImageProcessor processImage = image.getProcessor();
        processImage.blurGaussian(gauss);

        BackgroundSubtracter backSubtract = new BackgroundSubtracter();
        backSubtract.rollingBallBackground(processImage, rolling, false, false, true, false, false);

        processImage.setAutoThreshold(threshold, true, 1);
        ByteProcessor result = processImage.createMask();

        result.erode();
        result.erode();
        result.invert();

        return result;


    }
}
