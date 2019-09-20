package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.ZProjector;

import java.io.File;

public class Image {

    private String directory;
    private String sizeUnit;
    private String timeUnit;
    private double pxSizeCalib;
    private double frameRateCalib;

    ImagePlus openImage(String inputFile) {

        // open a example pHlorin image
        IJ.log("Opening file: " + inputFile);
        ImagePlus image = IJ.openImage(directory + File.separator + inputFile);

        return image;
    }

    Calibration calibrate(){

        Calibration imageScale = new Calibration();
        imageScale.setTimeUnit(timeUnit);
        imageScale.setXUnit(sizeUnit);
        imageScale.setYUnit(sizeUnit);

        imageScale.pixelHeight = pxSizeCalib;
        imageScale.pixelWidth = pxSizeCalib;
        imageScale.frameInterval = frameRateCalib;

        return imageScale;

    }

    ImagePlus projectImage(ImagePlus image, String method) {

        ImagePlus maxProjectImage = ZProjector.run(image, method);

        return maxProjectImage;

    }

    static int calculateMinSizePx(Double pxSize, Double minSize) {

        double pxArea = pxSize * pxSize;
        int minSizePx = (int)Math.round(minSize / pxArea);

        return minSizePx;

    }

    static int calculateMaxSizePx(Double pxSize, Double maxSize) {

        double pxArea = pxSize * pxSize;
        int maxSizePx = (int)Math.round(maxSize  / pxArea);

        return maxSizePx;

    }

    public Image(String inputDir, double pxSize, double frameRate){

        directory = inputDir;
        sizeUnit = "micron";
        timeUnit = "sec";
        pxSizeCalib = pxSize;
        frameRateCalib = frameRate;

    }

}
