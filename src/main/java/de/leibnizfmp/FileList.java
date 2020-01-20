package de.leibnizfmp;

import ij.IJ;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * class creates the file list from input directory for batch processing
 *
 * @author christopher schmied
 * @version 1.0.0
 */
class FileList {

    /**
     * walks through input directory
     * filters file for file suffix and writes string into an array
     *
     * @param inputDir input directory
     * @return list containing file names as string for processing
     */

    ArrayList<String> getFileList(String inputDir) {

        String suffix = "tif";
        List<String> results = null;
        Path inputPath = Paths.get(inputDir);

        // opens a stream and walks through file tree of given path
        try(Stream<Path> walk = Files.walk(inputPath)) {

            // gets the filenames converts them to a string
           results = walk.map(x -> inputPath.relativize(x).toString())
                   // filters them for the suffix
                   .filter(f -> f.endsWith(suffix))
                   // puts results to a List
                   .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: files not found");
            IJ.error("Error: no valid files found!");
        }

        // casts list to arrayList
        ArrayList<String> filesToProcess = new ArrayList<>(results);

        return filesToProcess;

    }

    /**
     * loops through list1 and compares if items are in list2
     *
     * @param list1 first file list
     * @param list2 second file list
     * @return intersection
     */
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
