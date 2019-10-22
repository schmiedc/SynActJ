package de.leibnizfmp;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.plugin.ZProjector;

class BackgroundSegmenter {

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
