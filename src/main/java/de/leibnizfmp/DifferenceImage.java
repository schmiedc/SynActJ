package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.ZProjector;

public class DifferenceImage {

    String projMethod;

    public ImagePlus createDiffImage(ImagePlus image, int startBefore, int endBefore, int startAfter, int endAfter) {

        // perform z projections before and after and create difference image
        System.out.println("Creating difference image...");
        ImagePlus impBefore = ZProjector.run(image,projMethod,startBefore,endBefore);
        ImagePlus impAfter = ZProjector.run(image,projMethod,startAfter,endAfter);
        ImageCalculator ic = new ImageCalculator();
        ImagePlus impDiff = ic.run("Divide create", impAfter, impBefore );
        System.out.println("Difference image created.");

        return impDiff;

    }

    public DifferenceImage(){

        projMethod = "median";
        System.out.println("Projection Method set to: " + projMethod);

    }

    public DifferenceImage(String method){

        projMethod = method;
        System.out.println("Projection Method set to: " + projMethod);

    }
}
