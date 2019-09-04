package de.leibnizfmp;

import ij.CompositeImage;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.Concatenator;
import ij.plugin.RGBStackMerge;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class PreviewGui {

    String[] thresholdString = { "Default", "Huang", "Huang2", "Intermodes",
            "Li", "MaxEntropy", "Mean", "MinError(I)",
            "Moments", "Otsu", "Percentile", "RenyiEntropy",
            "Triangle","Yen",
    };

    JList list;
    SpinnerModel doubleSpinnerLoGSpot;
    SpinnerModel doubleSpinnerProminenceSpot;
    SpinnerModel doubleSpinnerGaussSpot;
    SpinnerModel intSpinnerRollingBallSpot;
    JComboBox thresholdListSpot;
    SpinnerModel intSpinnerGradient;
    JTextField textSizeFilterSpot;
    JTextField textCircFilterSpot;

    // creates the panel that contains the buttons boxlayout vertical aligned
    Box buttonBox = new Box(BoxLayout.Y_AXIS);

    // tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();

    public void setUpSpotTab() {

        // Setup Interactions for Segment Boutons
        Box boxSpotSeg = new Box(BoxLayout.Y_AXIS);

        // Spinner for some number input
        doubleSpinnerLoGSpot = new SpinnerNumberModel(0.5, 0.0,5.0, 0.1);
        String spinLabelSpot1 = "LoG sigma: ";
        Box spinSpot1 = (Box) addLabeledSpinner(spinLabelSpot1, doubleSpinnerLoGSpot);
        boxSpotSeg.add(spinSpot1);


        doubleSpinnerProminenceSpot = new SpinnerNumberModel(0.005, 0.0,1.000, 0.001);
        String spinLabelSpot2 = "Prominence: ";
        Box spinSpot2 = (Box) addLabeledSpinner(spinLabelSpot2, doubleSpinnerProminenceSpot);
        boxSpotSeg.add(spinSpot2);

        doubleSpinnerGaussSpot = new SpinnerNumberModel(1.0, 0.0,10.0, 0.1);
        String spinLabelSpot3 = "Gaussian blur sigma: ";
        Box spinSpot3 = (Box) addLabeledSpinner(spinLabelSpot3, doubleSpinnerGaussSpot);
        boxSpotSeg.add(spinSpot3);

        intSpinnerRollingBallSpot = new SpinnerNumberModel(30, 0,100, 1);
        String spinLabelSpot4 = "Rolling Ball Radius: ";
        Box spinSpot4 = (Box) addLabeledSpinner(spinLabelSpot4, intSpinnerRollingBallSpot);
        boxSpotSeg.add(spinSpot4);

        thresholdListSpot = new JComboBox(thresholdString);
        JLabel thresholdListSpotLabel  = new JLabel("Select threshold: ");
        Box thresholdListSpotBox= new Box(BoxLayout.X_AXIS);
        thresholdListSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListSpot.getMinimumSize().height));
        thresholdListSpot.setSelectedIndex(12);
        thresholdListSpotBox.add(thresholdListSpotLabel);
        thresholdListSpotBox.add(thresholdListSpot);
        boxSpotSeg.add(thresholdListSpotBox);

        intSpinnerGradient = new SpinnerNumberModel(3,0,10,1);
        String spinLabelSpot5 = "Gradient radius: ";
        Box spinSpot5 = (Box) addLabeledSpinner(spinLabelSpot5, intSpinnerGradient);
        boxSpotSeg.add(spinSpot5);

        textSizeFilterSpot = new JTextField("0-Infinity");
        JLabel textSpotLabel1  = new JLabel("Set size filter: ");
        Box textSpotBox1 = new Box(BoxLayout.X_AXIS);
        textSizeFilterSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, textSizeFilterSpot.getMinimumSize().height));
        textSpotBox1.add(textSpotLabel1);
        textSpotBox1.add(textSizeFilterSpot);
        boxSpotSeg.add(textSpotBox1);

        textCircFilterSpot = new JTextField("0-1.00");
        JLabel textLabelSpot2  = new JLabel("Set circ. filter: ");
        Box textBoxSpot2 = new Box(BoxLayout.X_AXIS);
        textCircFilterSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, textCircFilterSpot.getMinimumSize().height));
        textBoxSpot2.add(textLabelSpot2);
        textBoxSpot2.add(textCircFilterSpot);
        boxSpotSeg.add(textBoxSpot2);

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

        SpinnerModel intSpinBack1 = new SpinnerNumberModel(4, 0,20, 1);
        String spinBackLabel1 = "Gaussian blur sigma: ";
        Box spinnerBack1 = (Box) addLabeledSpinner(spinBackLabel1,intSpinBack1);
        boxBackground.add(spinnerBack1);

        SpinnerModel intSpinBack2 = new SpinnerNumberModel(20, 0,100, 1);
        String spinBackLabel2 = "Rolling Ball radius: ";
        Box spinnerBack2 = (Box) addLabeledSpinner(spinBackLabel2,intSpinBack2);
        boxBackground.add(spinnerBack2);

        JComboBox thresholdListBack = new JComboBox(thresholdString);
        JLabel thresholdListBackLabel  = new JLabel("Select threshold: ");
        Box thresholdListBackBox= new Box(BoxLayout.X_AXIS);
        thresholdListBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListBack.getMinimumSize().height));
        thresholdListBack.setSelectedIndex(7);
        thresholdListBackBox.add(thresholdListBackLabel);
        thresholdListBackBox.add(thresholdListBack);
        boxBackground.add(thresholdListBackBox);

        JTextField textBack1 = new JTextField("0-Infinity");
        JLabel textBack1Label  = new JLabel("Set size filter: ");
        Box textBack1Box = new Box(BoxLayout.X_AXIS);
        textBack1.setMaximumSize(new Dimension(Integer.MAX_VALUE, textBack1.getMinimumSize().height));
        textBack1Box.add(textBack1Label);
        textBack1Box.add(textBack1);
        boxBackground.add(textBack1Box);

        JTextField textBack2 = new JTextField("0-1.00");
        JLabel textBack2Label  = new JLabel("Set circ. filter: ");
        Box textBack2Box = new Box(BoxLayout.X_AXIS);
        textBack2.setMaximumSize(new Dimension(Integer.MAX_VALUE, textBack2.getMinimumSize().height));
        textBack2Box.add(textBack2Label);
        textBack2Box.add(textBack2 );
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
        Box spinSettingsBox1 = (Box) addLabeledSpinner(spinSettingsLabel1,spinSettings1);
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

    static protected Box addLabeledSpinner(String label,
                                           SpinnerModel model) {

        Box spinnerLabelBox = new Box(BoxLayout.X_AXIS);
        JLabel l = new JLabel(label);
        spinnerLabelBox.add(l);

        JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        spinnerLabelBox.add(spinner);
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinner.getMinimumSize().height));

        return spinnerLabelBox;
    }

    // Upon pressing the start button call buildTrackAndStart() method
    public class MyPreviewSpotListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

            System.out.println("Starting preview");

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

                ImagePlus projectedImage = previewImage.projectImage(originalImage);

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
            System.out.println("Starting preview");
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
