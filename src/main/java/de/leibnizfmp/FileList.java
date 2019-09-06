package de.leibnizfmp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public ArrayList<String> union(ArrayList<String> list1, ArrayList<String> list2) {
        Set<String> set = new HashSet<String>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<String>(set);
    }

    public ArrayList<String> intersection(ArrayList<String> list1, ArrayList<String> list2) {
        ArrayList<String> list = new ArrayList<>();

        for (String item : list1) {

            if(list2.contains(item)) {

                list.add(item);

            }
        }

        return list;
    }

}
