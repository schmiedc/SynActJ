package de.leibnizfmp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class FileList {

    ArrayList<String> getFileList(String inputDir) {

        // needs to loop recursively through specified directory
        // throws error if no files are found
        String suffix = "tif";
        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> filesToProcess = new ArrayList<>();

        assert listOfFiles != null;

        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile() & listOfFile.toString().endsWith(suffix)) {
                    filesToProcess.add(listOfFile.getName());

            } else if (listOfFile.isDirectory()) {

                System.out.println("Directory " + listOfFile.getName());
            }
        }

        return filesToProcess;

    }

    ArrayList<String> union(ArrayList<String> list1, ArrayList<String> list2) {
        Set<String> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }

    ArrayList<String> intersection(ArrayList<String> list1, ArrayList<String> list2) {
        ArrayList<String> list = new ArrayList<>();

        for (String item : list1) {

            if(list2.contains(item)) {

                list.add(item);

            }
        }

        return list;
    }

}
