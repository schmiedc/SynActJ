package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.ZProjector;

class DifferenceImage {

    private String projMethod;
    private int frameUp;
    private int frameDown;


    ImagePlus createDiffImage(ImagePlus image, int stimulationFrame) {

        int startBefore = stimulationFrame - frameDown;
        int endBefore = stimulationFrame - 1;
        int startAfter = stimulationFrame;
        int endAfter = stimulationFrame + frameUp;

        // perform z projections before and after and create difference image
        IJ.log("Creating difference image...");
        ImagePlus impBefore = ZProjector.run(image,projMethod,startBefore,endBefore);
        ImagePlus impAfter = ZProjector.run(image,projMethod,startAfter,endAfter);
        ImageCalculator ic = new ImageCalculator();
        ImagePlus impDiff = ic.run("Divide create 32-bit", impAfter, impBefore );
        IJ.log("Difference image created.");

        return impDiff;

    }

    DifferenceImage(){

        projMethod = "median";
        frameUp = 4;
        frameDown = 4;
        IJ.log("Projection Method set to: " + projMethod);


    }

    DifferenceImage(String method){

        projMethod = method;
        frameUp = 4;
        frameDown = 4;
        IJ.log("Projection Method set to: " + projMethod);

    }

    DifferenceImage (String method, int downProj, int upProj) {

        projMethod = method;
        frameDown = downProj;
        frameUp = upProj;
        IJ.log("Projection Method set to: " + projMethod);

    }

}
