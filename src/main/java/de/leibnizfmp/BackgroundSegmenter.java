package de.leibnizfmp;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

class BackgroundSegmenter {

    ByteProcessor segmentBackground(ImagePlus image, double gauss, String threshold){

        ImageProcessor processImage = image.getProcessor();
        processImage.blurGaussian(gauss);

        processImage.setAutoThreshold(threshold, true, 1);
        ByteProcessor result = processImage.createMask();

        //result.erode();
        result.erode();
        result.invert();

        return result;


    }
}
