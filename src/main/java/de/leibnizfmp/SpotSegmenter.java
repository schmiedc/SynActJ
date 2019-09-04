package de.leibnizfmp;

import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.morphology.Strel;
import inra.ijpb.watershed.Watershed;
import trainableSegmentation.ImageScience;

import static inra.ijpb.morphology.Morphology.externalGradient;

public class SpotSegmenter {

    public ByteProcessor detectSpots(ImagePlus diffImage, double simgaLoG, double prominence) {

        System.out.println("Applying a LoG filter with sigma: " + Double.toString(simgaLoG));
        ImagePlus logImage = ImageScience.computeLaplacianImage(simgaLoG, diffImage);

        System.out.println("Detecting Minima with prominence: " + Double.toString(prominence));
        ImageProcessor getMaxima = logImage.getProcessor().convertToFloatProcessor();

        getMaxima.invert();
        MaximumFinder maxima = new MaximumFinder();
        ByteProcessor selection = maxima.findMaxima(getMaxima, prominence, 0, false);
        System.out.println("Spot detection finished");

        return selection;

    }

    public ByteProcessor segmentSpots(ImagePlus image, double gauss, double rolling, String threshold){

        ImageProcessor processImage = image.getProcessor().convertToShortProcessor();

        System.out.println("Applying Guassian blur with sigma: " + Double.toString(gauss));
        processImage.blurGaussian(gauss);

        System.out.println("Background subtraction with radius: " + Double.toString(rolling));
        BackgroundSubtracter backSubtract = new BackgroundSubtracter();
        backSubtract.rollingBallBackground(processImage, rolling, false, false, false, false, false);

        System.out.println("Autothreshold with method: " + threshold);
        processImage.setAutoThreshold(threshold, true, 1);
        ByteProcessor result = processImage.createMask();

        result.dilate();

        System.out.println("Spot segmentation finished.");
        // needs size and circ filter
        return result;

    }

    public ImagePlus watershed(ImagePlus inputImage, ByteProcessor marker, ByteProcessor mask, int radius){

        System.out.println("Performing watershed object separation...");
        Strel strel = Strel.Shape.DISK.fromRadius( radius );
        ImageProcessor extGradient = externalGradient(inputImage.getProcessor(), strel);
        ImagePlus extImage = new ImagePlus("input", extGradient);
        ImagePlus markerImage = new ImagePlus("marker", marker);
        ImagePlus maskImage = new ImagePlus("mask", mask);

        ImagePlus marker1 = BinaryImages.componentsLabeling(markerImage, 8, 32);
        ImagePlus resultImage = Watershed.computeWatershed(extImage, marker1, maskImage, 8, true );

        BinaryImages.removeLargestRegion(resultImage);
        System.out.println("Watershed finished.");
        return resultImage;
    }
}
