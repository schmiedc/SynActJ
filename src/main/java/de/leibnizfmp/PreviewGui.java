package de.leibnizfmp;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.plugin.Concatenator;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class PreviewGui {

    String[] thresholdString = { "Default", "Huang", "IJ_IsoData", "Intermodes",
            "IsoData ", "Li", "MaxEntropy", "Mean", "MinError", "Minimum",
            "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag",
            "Triangle","Yen",
    };

    // creates the panel that contains the buttons boxlayout vertical aligned
    Box buttonBox = new Box(BoxLayout.Y_AXIS);

    // tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();

    // settings for spot segmentation
    JList list;
    SpinnerModel doubleSpinnerLoGSpot;
    SpinnerModel doubleSpinnerProminenceSpot;
    SpinnerModel doubleSpinnerGaussSpot;
    SpinnerModel intSpinnerRollingBallSpot;
    JComboBox thresholdListSpot;
    SpinnerModel intSpinnerGradient;
    JTextField textSizeFilterSpot;
    JTextField textCircFilterSpot;

    // settings for background segmentation
    SpinnerModel intSpinBack1;
    SpinnerModel intSpinBack2;
    JComboBox thresholdListBack;
    JTextField textBack1;
    JTextField textBack2;


    public void setUpSpotTab() {

        // Setup Interactions for Segment Boutons
        Box boxSpotSeg = new Box(BoxLayout.Y_AXIS);

        // Spinner for some number input
        doubleSpinnerLoGSpot = new SpinnerNumberModel(0.5, 0.0,5.0, 0.1);
        String spinLabelSpot1 = "LoG sigma: ";
        String spinUnitSpot1 = "µm";
        Box spinSpot1 = (Box) addLabeledSpinnerUnit(spinLabelSpot1, doubleSpinnerLoGSpot, spinUnitSpot1);
        boxSpotSeg.add(spinSpot1);


        doubleSpinnerProminenceSpot = new SpinnerNumberModel(0.005, 0.0,1.000, 0.001);
        String spinLabelSpot2 = "Prominence - grayValue: ";
        String spinUnitSpot2 = "";
        Box spinSpot2 = (Box) addLabeledSpinnerUnit(spinLabelSpot2, doubleSpinnerProminenceSpot, spinUnitSpot2);
        boxSpotSeg.add(spinSpot2);

        doubleSpinnerGaussSpot = new SpinnerNumberModel(1.0, 0.0,10.0, 0.1);
        String spinLabelSpot3 = "Gauss sigma (px): ";
        String spinUnitSpot3 = "px";
        Box spinSpot3 = (Box) addLabeledSpinnerUnit(spinLabelSpot3, doubleSpinnerGaussSpot, spinUnitSpot3);
        boxSpotSeg.add(spinSpot3);

        intSpinnerRollingBallSpot = new SpinnerNumberModel(30, 0,100, 1);
        String spinLabelSpot4 = "RollingBall Radius: ";
        String spinUnitSpot4 = "px";
        Box spinSpot4 = (Box) addLabeledSpinnerUnit(spinLabelSpot4, intSpinnerRollingBallSpot, spinUnitSpot4);
        boxSpotSeg.add(spinSpot4);

        thresholdListSpot = new JComboBox(thresholdString);
        JLabel thresholdListSpotLabel  = new JLabel("Select threshold: ");
        Box thresholdListSpotBox= new Box(BoxLayout.X_AXIS);
        thresholdListSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListSpot.getMinimumSize().height));
        thresholdListSpot.setSelectedIndex(15);
        thresholdListSpotBox.add(thresholdListSpotLabel);
        thresholdListSpotBox.add(thresholdListSpot);
        boxSpotSeg.add(thresholdListSpotBox);

        intSpinnerGradient = new SpinnerNumberModel(3,0,10,1);
        String spinLabelSpot5 = "Gradient radius: ";
        String spinUnitSpot5 = "px";
        Box spinSpot5 = (Box) addLabeledSpinnerUnit(spinLabelSpot5, intSpinnerGradient, spinUnitSpot5);
        boxSpotSeg.add(spinSpot5);

        textSizeFilterSpot = new JTextField("0-Infinity");
        String labelSpot1 = "Set size filter: ";
        String unitSpot1 = "µm²";
        Box textSpotBox1 = (Box) addLabeledTextField(labelSpot1,textSizeFilterSpot ,unitSpot1);
        boxSpotSeg.add(textSpotBox1);

        textCircFilterSpot = new JTextField("0-1.00");
        String labelSpot2 = "Set circ. filter: ";
        String unitSpot2 = "";
        Box textSpotBox2 = (Box) addLabeledTextField(labelSpot2, textCircFilterSpot,unitSpot2);
        boxSpotSeg.add(textSpotBox2);

        // Preview Button for Spot segmentation
        JButton previewSpot = new JButton("Preview");
        previewSpot.addActionListener(new MyPreviewSpotListener());
        boxSpotSeg.add(previewSpot);

        // create tabbed panes
        tabbedPane.addTab("Boutons", boxSpotSeg);
    }

    public void setUpBackTab() {

        // Setup Interactions for Segment Background
        Box boxBackground = new Box(BoxLayout.Y_AXIS);

        intSpinBack1 = new SpinnerNumberModel(4.0, 0.0,20.0, 1.0);
        String spinBackLabel1 = "Gauss sigma: ";
        String spinBackUnit1 = "px";
        Box spinnerBack1 = (Box) addLabeledSpinnerUnit(spinBackLabel1,intSpinBack1, spinBackUnit1);
        boxBackground.add(spinnerBack1);

        intSpinBack2 = new SpinnerNumberModel(30.0, 0.0,100.0, 1.0);
        String spinBackLabel2 = "RollingBall radius: ";
        String spinBackUnit2 = "px";
        Box spinnerBack2 = (Box) addLabeledSpinnerUnit(spinBackLabel2,intSpinBack2, spinBackUnit2);
        boxBackground.add(spinnerBack2);

        thresholdListBack = new JComboBox(thresholdString);
        JLabel thresholdListBackLabel  = new JLabel("Select threshold: ");
        Box thresholdListBackBox= new Box(BoxLayout.X_AXIS);
        thresholdListBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListBack.getMinimumSize().height));
        thresholdListBack.setSelectedIndex(8);
        thresholdListBackBox.add(thresholdListBackLabel);
        thresholdListBackBox.add(thresholdListBack);
        boxBackground.add(thresholdListBackBox);

        textBack1 = new JTextField("0-Infinity");
        String labelBack1 = "Set size filter: ";
        String unitBack1 = "µm²";
        Box textBack1Box = (Box) addLabeledTextField(labelBack1,textBack1,unitBack1);
        boxBackground.add(textBack1Box);

        textBack2 = new JTextField("0-1.00");
        String labelBack2 = "Set circ. filter: ";
        String unitBack2  = "";
        Box textBack2Box = (Box) addLabeledTextField(labelBack2, textBack2, unitBack2 );
        boxBackground.add(textBack2Box);

        // setup Buttons
        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(new MyPreviewBackListener());
        boxBackground.add(previewButton);

        tabbedPane.addTab("Background", boxBackground);

    }

    public void setUpSettingsTab(){

        // Setup Interactions for experimental settings
        Box boxSettings = new Box(BoxLayout.Y_AXIS);

        SpinnerModel spinSettings1 = new SpinnerNumberModel(0.1620, 0.0000,1.0000, 0.0001);
        String spinSettingsLabel1 = "Pixel size: ";
        String spinSettingsunit1 = "µm";
        Box spinSettingsBox1 = (Box) addLabeledSpinnerUnit(spinSettingsLabel1,spinSettings1, spinSettingsunit1);
        boxSettings.add(spinSettingsBox1);

        tabbedPane.addTab("Settings", boxSettings);

    }

    public JScrollPane setUpFileList(ArrayList<String> aListOfFiles) {

        // setup List
        list = new JList(aListOfFiles.toArray());

        // create a new scroll pane
        JScrollPane scroller = new JScrollPane(list);

        // set scroller to use only vertical scrollbar
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // only 4 items visible
        list.setVisibleRowCount(10);
        // only 1 selection possible
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.addListSelectionListener(this);

        return scroller;
    }

    public void setUpButtons() {

        JButton batchButton = new JButton("Batch Process");
        batchButton.addActionListener(new MyBatchListener());
        buttonBox.add(batchButton);
    }

    public void setUpGui(ArrayList<String> aListOfFiles) {

        JFrame theFrame;

        // sets up the frame
        theFrame = new JFrame("pHluorin Processing");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);

        // creates margin between edges of the panel and where the components
        // are placed
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setUpSpotTab();
        setUpBackTab();
        setUpSettingsTab();
        JScrollPane scroller = setUpFileList(aListOfFiles);
        setUpButtons();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setPreferredSize(new Dimension(300, 80));

        // setup Buttons
        JButton  saveButton = new JButton("Save settings");
        saveButton.addActionListener(new MySaveListener());

        // add boxes to panel and frame
        background.add(BorderLayout.WEST, tabbedPane);
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.CENTER, scroller);
        background.add(BorderLayout.SOUTH, saveButton);
        theFrame.getContentPane().add(background);

        theFrame.setSize(800,600);
        theFrame.setVisible(true);

    }

    static protected Box addLabeledTextField(String label, JTextField field, String unit) {

        Box textBox = new Box(BoxLayout.X_AXIS);

        JLabel labelLabel  = new JLabel(label);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getMinimumSize().height));
        JLabel unitLabel  = new JLabel(unit);
        unitLabel.setPreferredSize(new Dimension(30, unitLabel.getMinimumSize().height));

        textBox.add(labelLabel);
        textBox.add(field);
        textBox.add(unitLabel);

        return textBox;
    }

    static protected Box addLabeledSpinnerUnit(String label,
                                           SpinnerModel model,
                                           String unit) {

        Box spinnerLabelBox = new Box(BoxLayout.X_AXIS);
        JLabel l1 = new JLabel(label);
        spinnerLabelBox.add(l1);

        JSpinner spinner = new JSpinner(model);
        l1.setLabelFor(spinner);
        spinnerLabelBox.add(spinner);
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinner.getMinimumSize().height));

        JLabel l2 = new JLabel(unit);
        l2.setPreferredSize(new Dimension(30, l2.getMinimumSize().height));
        spinnerLabelBox.add(l2);

        return spinnerLabelBox;
    }

    // Upon pressing the start button call buildTrackAndStart() method
    public class MyPreviewSpotListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

            System.out.println("Starting preview for spot segmentation");

            // test settings
            String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";
            String projMethod = "median";
            int startBefore = 1;
            int endBefore = 4;
            int startAfter = 5;
            int endAfter = 10;

            // get values in gui fields

            int selectionChecker = list.getSelectedIndex();
            if (selectionChecker != -1){

                String selectedFile = (String) list.getSelectedValue();
                System.out.println("Selected File: " + selectedFile);

                Double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
                System.out.println("LoG sigma: " + Double.toString(sigmaLoG));

                Double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
                System.out.println("Prominence: " + Double.toString(prominence));

                Double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
                System.out.println("Gauss sigma: " + Double.toString(sigmaSpots));

                Integer rollingSpots = (Integer) intSpinnerRollingBallSpot.getValue();
                System.out.println("Rolling Ball radius: " + Integer.toString(rollingSpots));

                String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
                System.out.println("Threshold: " + thresholdSpots);

                Integer radiusGradient = (Integer) intSpinnerGradient.getValue();
                System.out.println("Gradient Radius: " + Integer.toString(radiusGradient));

                // start preview
                Image previewImage = new Image(testDir);
                ImagePlus originalImage = previewImage.openImage(selectedFile);

                DifferenceImage processImage = new DifferenceImage(projMethod);
                ImagePlus diffImage = processImage.createDiffImage(originalImage, startBefore, endBefore, startAfter, endAfter);

                SpotSegmenter spot = new SpotSegmenter();
                ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
                ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots);

                ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

                ImagePlus projectedImage = previewImage.projectImage(originalImage, "max");

                ImageProcessor projectedImage8Bit = projectedImage.getProcessor().convertToByteProcessor();
                ImagePlus projectedImage8Bit2 = new ImagePlus("projected", projectedImage8Bit);

                Concatenator add = new Concatenator();
                ImagePlus result = add.concatenate(watershed, projectedImage8Bit2,false);
                result.setDimensions(2,1,1);
                CompositeImage composite = new CompositeImage(result, CompositeImage.COMPOSITE);
                composite.setDisplayRange(0,100);
                composite.show();

            } else {
                System.out.println("Please choose a file in the file list!");
            }

        }

    } // close inner class

    // Upon pressing the start button call buildTrackAndStart() method
    public class MyPreviewBackListener implements ActionListener {

        public void actionPerformed(ActionEvent a) {

            // test settings
            String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";

            System.out.println("Starting preview for background segmentation");

            int selectionChecker = list.getSelectedIndex();

            if (selectionChecker != -1){

                // get values from fields
                String selectedFile = (String) list.getSelectedValue();
                System.out.println("Selected File: " + selectedFile);

                Double sigmaBackground = (Double) intSpinBack1.getValue();

                Double rollingBackground = (Double) intSpinBack2.getValue();

                String thresholdBackground = (String) thresholdListBack.getSelectedItem();

                String sizeBack1 = (String) textBack1.getText();
                String circBack2 = (String) textBack2.getText();

                // segment background and show for validation
                // start preview
                Image previewImage = new Image(testDir);
                ImagePlus originalImage = previewImage.openImage(selectedFile);

                ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "max");

                BackgroundSegmenter back = new BackgroundSegmenter();
                ByteProcessor background = back.segmentBackground(forBackSegmentation, sigmaBackground, rollingBackground, thresholdBackground);

                ImagePlus testBack = new ImagePlus("test", background);
                testBack.show();

                ImagePlus projectedImage = previewImage.projectImage(originalImage, "max");
                ImageProcessor projectedImage8Bit = projectedImage.getProcessor().convertToByteProcessor();
                ImagePlus projectedImage8Bit2 = new ImagePlus("projected", projectedImage8Bit);

                Concatenator add = new Concatenator();
                ImagePlus result = add.concatenate(testBack, projectedImage8Bit2,false);
                result.setDimensions(2,1,1);
                CompositeImage composite = new CompositeImage(result, CompositeImage.COMPOSITE);
                composite.setDisplayRange(0,100);
                composite.show();

            } else {

            System.out.println("Please choose a file in the file list!");

            }

        }

    } // close inner class

    public class MySaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a) { System.out.println("Saving settings");}
    }

    // upon pressing the stop button call sequencer.stop() method
    public class MyBatchListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            System.out.println("Starting batch");
        }
    } // close inner class

}
