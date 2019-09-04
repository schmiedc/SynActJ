package de.leibnizfmp;

import java.io.File;
import java.util.ArrayList;

public class FileList {

    public ArrayList<String> getFileList(String inputDir) {

        // needs to loop recursively through specified directory
        // throws error if no files are found
        String suffix = "tif";
        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> filesToProcess = new ArrayList<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() & listOfFiles[i].toString().endsWith(suffix))  {
                filesToProcess.add(listOfFiles[i].getName());

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        return filesToProcess;

    }
}
