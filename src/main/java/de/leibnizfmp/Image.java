package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.ZProjector;

import java.io.File;

public class Image {

    String directory;
    String sizeUnit;
    String timeUnit;
    double pxSizeCalib;
    double frameRateCalib;

    public ImagePlus openImage(String inputFile) {

        // open a example pHlorin image
        IJ.log("Opening file: " + inputFile);
        ImagePlus image = IJ.openImage(directory + inputFile);

        return image;
    }

    public Calibration calibrate(){

        Calibration imageScale = new Calibration();
        imageScale.setTimeUnit(timeUnit);
        imageScale.setXUnit(sizeUnit);
        imageScale.setYUnit(sizeUnit);

        imageScale.pixelHeight = pxSizeCalib;
        imageScale.pixelWidth = pxSizeCalib;
        imageScale.frameInterval = frameRateCalib;

        return imageScale;

    }

    public ImagePlus projectImage(ImagePlus image, String method) {

        ImagePlus maxProjectImage = ZProjector.run(image, method);

        return maxProjectImage;

    }

    public static int calculateMinSizePx(Double pxSize, Double minSize) {

        Double pxArea = pxSize * pxSize;
        Integer minSizePx = (int)Math.round(minSize / pxArea);

        return minSizePx;

    }

    public int calculateMaxSizePx(Double pxSize, Double maxSize) {

        Double pxArea = pxSize * pxSize;
        Integer maxSizePx = (int)Math.round(maxSize  / pxArea);

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
