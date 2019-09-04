package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ZProjector;

public class Image {

    String directory;

    public ImagePlus openImage(String inputFile) {

        // open a example pHlorin image
        System.out.println("Opening file: " + inputFile);
        ImagePlus image = IJ.openImage(directory + inputFile);

        return image;
    }

    public ImagePlus projectImage(ImagePlus image, String method) {

        ImagePlus maxProjectImage = ZProjector.run(image, method);

        return maxProjectImage;

    }

    public Image(String inputDir){
        directory = inputDir;
    }

}
