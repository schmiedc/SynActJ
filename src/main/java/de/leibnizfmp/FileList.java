package de.leibnizfmp;

import ij.IJ;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileList {

    // getFileList based on Files.walk
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
