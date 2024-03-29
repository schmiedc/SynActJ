package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.plugin.Commands;
import org.scijava.util.ArrayUtils;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * class implements the previewGUI that allows the adjust the segmentation for the workflow
 *
 * @author christopher schmied
 * @version 1.0.0
 */
public class PreviewGui extends JPanel{

    private final String[] thresholdString = { "Default", "Huang", "IJ_IsoData", "Intermodes",
            "IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum",
            "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag",
            "Triangle","Yen",
    };

    private String inputDir;
    private String outputDir;
    private ArrayList<String> fileList;

    private String projMethod;

    private double sigmaLoG;
    private double prominence;
    private double sigmaSpots;
    private double rollingSpots;
    private String thresholdSpots;
    private boolean spotErosionSetting;
    private int radiusGradient;
    private double minSizeSpot;
    private double maxSizeSpot;
    private double lowCirc;
    private double highCirc;

    private double sigmaBackground;
    private String thresholdBackground;
    private double minSizeBack;
    private double maxSizeBack;

    private boolean calibrationSetting;
    private int stimFrame;
    private double pxSizeMicron;
    private double frameRate;

    private static File settingsFile = null;

    private boolean setDisplayRange = false;

    // tabbed pane
    private JTabbedPane tabbedPane = new JTabbedPane();

    // settings for spot segmentation
    Box boxSpotSeg = new Box(BoxLayout.Y_AXIS);
    private JList list;
    private SpinnerModel doubleSpinnerLoGSpot;
    private SpinnerModel doubleSpinnerProminenceSpot;
    private SpinnerModel doubleSpinnerGaussSpot;
    private SpinnerModel doubleSpinnerRollingBallSpot;
    private JComboBox<String> thresholdListSpot;
    private JCheckBox erosionCheckBox;
    private SpinnerModel intSpinnerGradient;
    private SpinnerModel doubleSpinnerMinSize;
    private SpinnerModel doubleSpinnerMaxSize;
    private SpinnerModel doubleSpinnerLowCirc;
    private SpinnerModel doubleSpinnerHighCirc;

    // settings for background segmentation
    Box boxBackground = new Box(BoxLayout.Y_AXIS);
    private SpinnerModel doubleSpinBack1;
    private JComboBox<String> thresholdListBack;
    private SpinnerModel doubleSpinBack2;
    private SpinnerModel doubleSpinBack3;

    // experimental settings
    Box batchBox = new Box(BoxLayout.Y_AXIS);
    Box boxSettings = new Box(BoxLayout.Y_AXIS);
    private SpinnerModel doubleSpinnerPixelSize;
    private JCheckBox checkCalibration;
    private SpinnerModel doubleSpinnerFrameRate;
    private SpinnerModel integerSpinnerStimulationFrame;

    private JLabel actionLabel;

    private Border blackline;

    JFrame theFrame;

    private void saveSettings(String name) {
        IJ.log("Saving settings");

        // get current settings for spot segmentation
        // get all the values from the GUI
        double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
        IJ.log("Spot LoG sigma: " + sigmaLoG);

        double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
        IJ.log("Spot prominence: " + prominence);

        double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
        IJ.log("Spot gauss sigma: " + sigmaSpots);

        double rollingSpots = (Double) doubleSpinnerRollingBallSpot.getValue();
        IJ.log("Spot rolling ball radius: " + rollingSpots);

        String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
        IJ.log("Spot threshold: " + thresholdSpots);

        // check if erosion is applied
        boolean spotErosion;
        if (erosionCheckBox.isSelected()) spotErosion = true;
        else spotErosion = false;
        IJ.log("Spot erosion: " + spotErosion);

        int radiusGradient = (Integer) intSpinnerGradient.getValue();
        IJ.log("Spot gradient Radius: " + radiusGradient);

        double minSizeSpot = (Double) doubleSpinnerMinSize.getValue();
        double maxSizeSpot = (Double) doubleSpinnerMaxSize.getValue();
        IJ.log("Spots size from: " + minSizeSpot + " to " + maxSizeSpot + " µm²");

        double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
        double highCirc = (Double) doubleSpinnerHighCirc.getValue();
        IJ.log("Spots circ. from: " + lowCirc + " to " + highCirc);

        // get current settings for background segmentation
        double sigmaBackground = (Double) doubleSpinBack1.getValue();
        IJ.log("Background sigma: " + sigmaBackground);

        String thresholdBackground = (String) thresholdListBack.getSelectedItem();
        IJ.log("Background threshold: " + thresholdBackground);

        double minSizeBack = (Double) doubleSpinBack2.getValue();
        double maxSizeBack = (Double) doubleSpinBack3.getValue();
        IJ.log("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²");

        // check if calibration needs to be overwritten
        boolean calibrationSetting;

        if (checkCalibration.isSelected()) calibrationSetting = true;
        else calibrationSetting = false;
        IJ.log("Calibration override: " + calibrationSetting);

        double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
        IJ.log("Calibration: " + pxSizeMicron + " µm");

        double frameRate = (Double) doubleSpinnerFrameRate.getValue();
        IJ.log("Calibration: " + frameRate + " sec");

        int stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
        IJ.log("Stimulation frame: " + stimFrame);

        XmlHandler writeToXml = new XmlHandler();

        writeToXml.xmlWriter(outputDir, name, projMethod,
                sigmaLoG, prominence,
                sigmaSpots, rollingSpots, thresholdSpots, spotErosion,
                radiusGradient,
                minSizeSpot, maxSizeSpot, lowCirc, highCirc,
                sigmaBackground, thresholdBackground,
                minSizeBack, maxSizeBack,
                stimFrame, calibrationSetting, pxSizeMicron, frameRate);
    }

    /**
     * creates the tab for adjusting the spot segmentation settings
     */
    private void setUpSpotTab() {

        //box with titled borders
        Box detectionBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleDetection;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleDetection = BorderFactory.createTitledBorder(blackline, "Detect: number & position of spots");
        detectionBox.setBorder(titleDetection);

        // Spinner for some number input
        doubleSpinnerLoGSpot = new SpinnerNumberModel(sigmaLoG, 0.0,20.0, 0.1);
        String spinLabelSpot1 = "LoG sigma: ";
        String spinUnitSpot1 = "µm";
        Box spinSpot1 = addLabeledSpinnerUnit(spinLabelSpot1, doubleSpinnerLoGSpot, spinUnitSpot1);
        detectionBox.add(spinSpot1);

        doubleSpinnerProminenceSpot = new SpinnerNumberModel(prominence, 0.0,1000.0, 0.0001);
        String spinLabelSpot2 = "Prominence: ";
        String spinUnitSpot2 = "";
        Box spinSpot2 = addLabeledSpinner5Digit(spinLabelSpot2, doubleSpinnerProminenceSpot, spinUnitSpot2);
        detectionBox.add(spinSpot2);

        boxSpotSeg.add(detectionBox);

        // box with titled borders
        Box segmentationBox = new Box(BoxLayout.Y_AXIS);
        segmentationBox.setPreferredSize(new Dimension(350, 100));
        TitledBorder titleSegmentation;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleSegmentation = BorderFactory.createTitledBorder(blackline, "Segment: size & number of spots");
        segmentationBox.setBorder(titleSegmentation);

        doubleSpinnerGaussSpot = new SpinnerNumberModel(sigmaSpots, 0.0,50.0, 0.1);
        String spinLabelSpot3 = "Gauss sigma: ";
        String spinUnitSpot3 = "px";
        Box spinSpot3 = addLabeledSpinnerUnit(spinLabelSpot3, doubleSpinnerGaussSpot, spinUnitSpot3);
        segmentationBox.add(spinSpot3);

        doubleSpinnerRollingBallSpot = new SpinnerNumberModel(rollingSpots, 0,100, 1);
        String spinLabelSpot4 = "RollingBall Radius: ";
        String spinUnitSpot4 = "px";
        Box spinSpot4 = addLabeledSpinnerUnit(spinLabelSpot4, doubleSpinnerRollingBallSpot, spinUnitSpot4);
        segmentationBox.add(spinSpot4);

        thresholdListSpot = new JComboBox<>(thresholdString);
        JLabel thresholdListSpotLabel  = new JLabel("Select threshold: ");
        Box thresholdListSpotBox= new Box(BoxLayout.X_AXIS);
        thresholdListSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListSpot.getMinimumSize().height));

        // get index of selected threshold using arrayUtils
        int indexOfThreshold = ArrayUtils.indexOf(thresholdString, thresholdSpots);
        // set this index as selected in the threshold list
        thresholdListSpot.setSelectedIndex(indexOfThreshold);

        thresholdListSpotBox.add(thresholdListSpotLabel);
        thresholdListSpotBox.add(thresholdListSpot);
        segmentationBox.add(thresholdListSpotBox);

        erosionCheckBox = new JCheckBox("Erode Mask");
        erosionCheckBox.setSelected(spotErosionSetting);
        segmentationBox.add(erosionCheckBox);

        boxSpotSeg.add(segmentationBox);

        // box with titled borders
        Box splittingBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleSplitting;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleSplitting = BorderFactory.createTitledBorder(blackline, "Split spots: separation of spots");
        splittingBox.setBorder(titleSplitting);

        intSpinnerGradient = new SpinnerNumberModel(radiusGradient,0,50,1);
        String spinLabelSpot5 = "Gradient radius: ";
        String spinUnitSpot5 = "px";
        Box spinSpot5 = addLabeledSpinnerUnit(spinLabelSpot5, intSpinnerGradient, spinUnitSpot5);
        splittingBox.add(spinSpot5);

        boxSpotSeg.add(splittingBox);

        // box with titled borders
        Box filterBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleFilter;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleFilter = BorderFactory.createTitledBorder(blackline, "Filter spots: size and circ.");
        filterBox.setBorder(titleFilter);

        doubleSpinnerMinSize = new SpinnerNumberModel(minSizeSpot, 0.0,1000000, 0.1);
        String labelMinSize = "Min. spot size: ";
        String unitMinSize = "µm²";
        Box minSizeBox = addLabeledSpinnerUnit(labelMinSize , doubleSpinnerMinSize, unitMinSize);
        filterBox.add(minSizeBox);

        doubleSpinnerMaxSize = new SpinnerNumberModel(maxSizeSpot, 0.0,1000000, 0.1);
        String labelMaxSize = "Max. spot size: ";
        String unitMaxSize = "µm²";
        Box maxSizeBox = addLabeledSpinnerUnit(labelMaxSize , doubleSpinnerMaxSize, unitMaxSize);
        filterBox.add(maxSizeBox);

        doubleSpinnerLowCirc = new SpinnerNumberModel(lowCirc, 0.0,1.0, 0.01);
        String labelLowCirc = "Min. spot circ.: ";
        String unitLowCirc = "";
        Box lowCirc = addLabeledSpinnerUnit(labelLowCirc , doubleSpinnerLowCirc, unitLowCirc);
        filterBox.add(lowCirc);

        doubleSpinnerHighCirc = new SpinnerNumberModel(highCirc, 0.0,1.0, 0.01);
        String labelHighCirc = "Maximum spot circ.: ";
        String unitHighCirc = "";
        Box highCirc = addLabeledSpinnerUnit(labelHighCirc , doubleSpinnerHighCirc , unitHighCirc );
        filterBox.add(highCirc);

        boxSpotSeg.add(filterBox);

        // Preview Button for Spot segmentation
        Box previewBox = new Box(BoxLayout.X_AXIS);
        JButton previewSpot = new JButton("Preview");
        previewSpot.addActionListener(new MyPreviewSpotListener());
        previewBox.add(previewSpot);
        boxSpotSeg.add(previewBox);

        // add a information for the detected count(s)
        Box messageBox = new Box(BoxLayout.X_AXIS);
        actionLabel = new JLabel("Press preview for counts");
        messageBox.add(actionLabel);
        boxSpotSeg.add(messageBox);
    }

    /**
     * creates the tab for adjusting the background segmentation settings
     */
    private void setUpBackTab() {

        // box with titled borders
        Box segmentationBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleSegmentation;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleSegmentation = BorderFactory.createTitledBorder(blackline, "Segmentation: size of background");
        segmentationBox.setBorder(titleSegmentation);

        doubleSpinBack1 = new SpinnerNumberModel(sigmaBackground, 0.0,50.0, 0.1);
        String spinBackLabel1 = "Gauss sigma: ";
        String spinBackUnit1 = "px";
        Box spinnerBack1 = addLabeledSpinnerUnit(spinBackLabel1, doubleSpinBack1, spinBackUnit1);
        segmentationBox.add(spinnerBack1);

        thresholdListBack = new JComboBox<>(thresholdString);
        JLabel thresholdListBackLabel  = new JLabel("Select threshold: ");
        Box thresholdListBackBox= new Box(BoxLayout.X_AXIS);
        thresholdListBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListBack.getMinimumSize().height));

        // get index of selected threshold using arrayUtils
        int indexOfThreshold = ArrayUtils.indexOf(thresholdString, thresholdBackground);
        // set this index as selected in the threshold list
        thresholdListBack.setSelectedIndex(indexOfThreshold);

        thresholdListBackBox.add(thresholdListBackLabel);
        thresholdListBackBox.add(thresholdListBack);
        segmentationBox.add(thresholdListBackBox);

        boxBackground.add(segmentationBox);

        // box with titled borders
        Box filterBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleFilter;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleFilter = BorderFactory.createTitledBorder(blackline, "Filter: size");
        filterBox .setBorder(titleFilter);

        doubleSpinBack2 = new SpinnerNumberModel(minSizeBack,0.0,1000000,10.0);
        String minSizeLabel = "Select min. size: ";
        String minUnitLabel = "µm²";
        Box spinnerBack2 = addLabeledSpinnerUnit(minSizeLabel, doubleSpinBack2, minUnitLabel );
        filterBox.add(spinnerBack2);

        doubleSpinBack3 = new SpinnerNumberModel(maxSizeBack,0.0,1000000,10.0);
        String maxSizeLabel = "Select max. size: ";
        String maxUnitLabel = "µm²";
        Box spinnerBack3 = addLabeledSpinnerUnit(maxSizeLabel, doubleSpinBack3, maxUnitLabel);
        filterBox.add(spinnerBack3);

        boxBackground.add(filterBox);

        // setup Buttons
        Box previewBox = new Box(BoxLayout.X_AXIS);
        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(new MyPreviewBackListener());
        previewBox.add(previewButton);
        boxBackground.add(previewBox);

    }

    /**
     * creates the tab that allows to change the image settings
     */
    private void setUpSettingsTab(){

        JLabel settingsLabel = new JLabel("Specify experimental Settings: ");
        boxSettings.add(settingsLabel);

        doubleSpinnerPixelSize = new SpinnerNumberModel(pxSizeMicron, 0.000,10.000, 0.001);
        String pixelSizeLabel = "Pixel size: ";
        String pixelSizeUnit = "µm";
        Box boxPixelSize = addLabeledSpinnerUnit(pixelSizeLabel,doubleSpinnerPixelSize, pixelSizeUnit);
        boxSettings.add(boxPixelSize);

        doubleSpinnerFrameRate = new SpinnerNumberModel(frameRate, 0.0,100.0, 1.0);
        String frameRateLabel = "Frame rate: ";
        String frameRateUnit = "s";
        Box boxFrameRate = addLabeledSpinnerUnit(frameRateLabel, doubleSpinnerFrameRate, frameRateUnit);
        boxSettings.add(boxFrameRate);

        checkCalibration = new JCheckBox("Override metadata?");
        checkCalibration.setSelected(calibrationSetting);
        boxSettings.add(checkCalibration);

        integerSpinnerStimulationFrame = new SpinnerNumberModel(stimFrame, 0,10000, 1);
        String stimulationFrameLabel = "Stimulation Frame: ";
        String stimulationFrameUnit = "";
        Box boxStimulationFrame = addLabeledSpinnerUnit(stimulationFrameLabel, integerSpinnerStimulationFrame, stimulationFrameUnit);
        boxSettings.add(boxStimulationFrame);

        JButton batchButton = new JButton("Batch Process");
        batchButton.addActionListener(new MyBatchListener());

        boxSettings.add(batchButton);

        boxSettings.add(Box.createRigidArea(new Dimension(0, 60)));

        URL url = getClass().getResource("/LogoSynActJ1.png");

        if (url == null)
            System.out.println( "Could not find image!" );
        else {
            System.out.println( "Could find image!" );

            try {

                final BufferedImage myLogo = ImageIO.read(url);
                JLabel logoLabel = new JLabel(new ImageIcon(myLogo));
                boxSettings.add(logoLabel);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * sets up the center JList for showing the file names
     *
     * @param aListOfFiles list of file name strings
     * @return a scroller that contains a file list
     */
    private JScrollPane setUpFileList(ArrayList<String> aListOfFiles) {

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

    /**
     * creates the PreviewGui
     */
    void setUpGui() {

        // sets up the frame
        theFrame = new JFrame("SynActJ Processing");
        // needs to set to dispose otherwise it also closes Fiji
        theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);

        // creates margin between edges of the panel and where the components
        // are placed
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setUpSpotTab();
        // create tabbed panes
        tabbedPane.addTab("Boutons", boxSpotSeg);

        setUpBackTab();
        tabbedPane.addTab("Background", boxBackground);

        setUpSettingsTab();
        batchBox.add(boxSettings);

        JScrollPane scroller = setUpFileList(fileList);


        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setPreferredSize(new Dimension(290, 100));

        // Setup Interactions for Segment Boutons
        Box saveLoadBox = new Box(BoxLayout.X_AXIS);

        // setup Buttons
        JButton  saveButton = new JButton("Save settings");
        saveButton.addActionListener(new MySaveListener());
        saveLoadBox.add(saveButton);

        JButton loadButton = new JButton("Load settings");
        loadButton.addActionListener(new MyLoadListener());
        saveLoadBox.add(loadButton);

        JButton resetButton = new JButton("Reset Processing Settings");
        resetButton.addActionListener(new MyResetListener());
        saveLoadBox.add(resetButton);

        JButton resetDirButton = new JButton("Reset Directories");
        resetDirButton.addActionListener(new MyResetDirectoryListener());
        saveLoadBox.add(resetDirButton);

        // add boxes to panel and frame
        background.add(BorderLayout.WEST, tabbedPane);
        background.add(BorderLayout.EAST, batchBox);
        background.add(BorderLayout.CENTER, scroller);
        background.add(BorderLayout.SOUTH, saveLoadBox);
        theFrame.getContentPane().add(background);

        theFrame.setSize(1000,500);
        theFrame.setVisible(true);

    }

    /**
     * creates a labeled text field
     * @param label name
     * @param field field for inputs
     * @param unit label
     * @return a box with the labeled textField
     */
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

    /**
     * creates a labeled spinner
     * @param label name
     * @param model for which spinner
     * @param unit label after spinner
     * @return box with labeled spinner
     */
    private static Box addLabeledSpinnerUnit(String label,
                                             SpinnerModel model,
                                             String unit) {

        Box spinnerLabelBox = new Box(BoxLayout.X_AXIS);
        JLabel l1 = new JLabel(label);
        l1.setPreferredSize(new Dimension(150, l1.getMinimumSize().height));
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

    /**
     * creates a 5 digit spinner
     * @param label name
     * @param model for spinner
     * @param unit label after the spinner box
     * @return box with labeled spinner with 5 digits
     */
    private static Box addLabeledSpinner5Digit(String label,
                                             SpinnerModel model,
                                             String unit) {

        Box spinnerLabelBox = new Box(BoxLayout.X_AXIS);
        JLabel l1 = new JLabel(label);
        l1.setPreferredSize(new Dimension(150, l1.getMinimumSize().height));
        spinnerLabelBox.add(l1);

        JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(4);
        l1.setLabelFor(spinner);
        spinnerLabelBox.add(spinner);
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinner.getMinimumSize().height));

        JLabel l2 = new JLabel(unit);
        l2.setPreferredSize(new Dimension(30, l2.getMinimumSize().height));
        spinnerLabelBox.add(l2);

        return spinnerLabelBox;
    }

    /**
     * starts spot segmentation visualization
     */
    public class MyPreviewSpotListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

            ImagePlus originalImage;
            Calibration calibration;

            int countBouton;

            IJ.log("Starting preview for spot segmentation");

            // get all the values from the GUI
            double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
            IJ.log("LoG sigma: " + sigmaLoG);

            double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
            IJ.log("Prominence: " + prominence);

            double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
            IJ.log("Gauss sigma: " + sigmaSpots);

            double rollingSpots = (Double) doubleSpinnerRollingBallSpot.getValue();
            IJ.log("Rolling Ball radius: " + rollingSpots);

            String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
            IJ.log("Threshold: " + thresholdSpots);

            // check if erosion is applied
            boolean spotErosion;
            if (erosionCheckBox.isSelected()) spotErosion = true;
            else spotErosion = false;
            IJ.log("Spot erosion: " + spotErosion);

            int radiusGradient = (Integer) intSpinnerGradient.getValue();
            IJ.log("Gradient Radius: " + radiusGradient);

            // calculate size in pixel
            double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            boolean calibrationSetting = checkCalibration.isSelected();

            double minSizeMicron = (Double) doubleSpinnerMinSize.getValue();
            double maxSizeMicron = (Double) doubleSpinnerMaxSize.getValue();
            IJ.log("Spots size from: " + minSizeMicron + " to " + maxSizeMicron + " µm²" );

            double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
            double highCirc = (Double) doubleSpinnerHighCirc.getValue();
            IJ.log("Spots circ. from: " + lowCirc + " to " + highCirc);

            double frameRate = (Double) doubleSpinnerFrameRate.getValue();

            int stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
            IJ.log("Stimulation frame: " + stimFrame);

            Image previewImage = new Image(inputDir, pxSizeMicron, frameRate);

            // checks if there is a file selected
            int selectionChecker = list.getSelectedIndex();

            // if a file for preview is selected then proceed with preview
            if (selectionChecker != -1){

                String selectedFile = (String) list.getSelectedValue();
                IJ.log("Selected File: " + selectedFile);

                // check if there are windows open already
                int openImages = WindowManager.getImageCount();

                // if there are image windows open check if they are of the list and of the selected image
                if  ( openImages != 0 ) {

                    IJ.log("There are images open!");

                    String[] openImage = WindowManager.getImageTitles();
                    ArrayList<String> openImageList = new ArrayList<>(Arrays.asList(openImage));

                    FileList fileUtility = new FileList();
                    ArrayList<String> openInputImages = fileUtility.intersection(openImageList, fileList);

                    boolean selectedFileChecker = false;

                    for (String image : openInputImages) {

                        if (image.equals(selectedFile)) {

                            IJ.log(selectedFile + " is already open");
                            selectedFileChecker = true;

                        } else {

                            IJ.selectWindow(image);
                            IJ.run("Close");

                        }

                    }

                    if (selectedFileChecker) {

                        IJ.log("The selected image is already open");

                        IJ.selectWindow(selectedFile);
                        setDisplayRange = false;
                        ImagePlus selectedImage = WindowManager.getCurrentWindow().getImagePlus();

                        int minSizePx;
                        int maxSizePx;

                        if (calibrationSetting) {

                            calibration = previewImage.calibrate();
                            minSizePx = Image.calculateSizePx(pxSizeMicron, minSizeMicron);
                            maxSizePx = Image.calculateSizePx(pxSizeMicron, maxSizeMicron);
                            IJ.log("Metadata will be overwritten.");
                            IJ.log("Pixel size set to: " + pxSizeMicron);
                            IJ.log("Frame rate set to: " + frameRate);
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);


                        } else {

                            originalImage = previewImage.openImage(selectedFile);
                            calibration = originalImage.getCalibration();
                            Double pxSizeFromImage = calibration.pixelWidth;
                            minSizePx = Image.calculateSizePx(pxSizeFromImage, minSizeMicron);
                            maxSizePx = Image.calculateSizePx(pxSizeFromImage, maxSizeMicron);

                            IJ.log("Metadata will no be overwritten");
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                        }

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        countBouton = visualizer.spotVisualization(selectedImage, projMethod, stimFrame, sigmaLoG, prominence,
                                sigmaSpots, rollingSpots, thresholdSpots, spotErosion,
                                radiusGradient, minSizePx, maxSizePx, lowCirc, highCirc,
                                calibration, setDisplayRange);

                        actionLabel.setText("Found " + countBouton + " Bouton(s)");

                    } else {

                        IJ.log("The selected image is not open");
                        IJ.log("Opening new image");

                        setDisplayRange = true;
                        // start preview for spot segmentation
                        originalImage = previewImage.openImage(selectedFile);

                        int minSizePx;
                        int maxSizePx;

                        // check if calibration is overwritten or used from original image
                        if (calibrationSetting) {

                            calibration = previewImage.calibrate();
                            minSizePx = Image.calculateSizePx(pxSizeMicron, minSizeMicron);
                            maxSizePx = Image.calculateSizePx(pxSizeMicron, maxSizeMicron);

                            IJ.log("Metadata will be overwritten.");
                            IJ.log("Pixel size set to: " + pxSizeMicron);
                            IJ.log("Frame rate set to: " + frameRate);
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);


                        } else {

                            calibration = originalImage.getCalibration();
                            Double pxSizeFromImage = calibration.pixelWidth;
                            minSizePx = Image.calculateSizePx(pxSizeFromImage, minSizeMicron);
                            maxSizePx = Image.calculateSizePx(pxSizeFromImage, maxSizeMicron);

                            IJ.log("Metadata will no be overwritten");
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                        }

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        countBouton = visualizer.spotVisualization(originalImage, projMethod, stimFrame, sigmaLoG, prominence,
                                sigmaSpots, rollingSpots, thresholdSpots, spotErosion,
                                radiusGradient, minSizePx, maxSizePx, lowCirc, highCirc,
                                calibration, setDisplayRange);

                        actionLabel.setText("Found " + countBouton + " Bouton(s)");

                    }

                } else {

                    IJ.log("There are no images open!");

                    // start preview for spot segmentation
                    setDisplayRange = true;
                    previewImage = new Image(inputDir, pxSizeMicron, frameRate );
                    originalImage = previewImage.openImage(selectedFile);

                    int minSizePx;
                    int maxSizePx;

                    if (calibrationSetting) {

                        calibration = previewImage.calibrate();
                        minSizePx = Image.calculateSizePx(pxSizeMicron, minSizeMicron);
                        maxSizePx = Image.calculateSizePx(pxSizeMicron, maxSizeMicron);
                        IJ.log("Metadata will be overwritten.");
                        IJ.log("Pixel size set to: " + pxSizeMicron);
                        IJ.log("Frame rate set to: " + frameRate);
                        IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);


                    } else {

                        calibration = originalImage.getCalibration();
                        Double pxSizeFromImage = calibration.pixelWidth;
                        minSizePx = Image.calculateSizePx(pxSizeFromImage, minSizeMicron);
                        maxSizePx = Image.calculateSizePx(pxSizeFromImage, maxSizeMicron);

                        IJ.log("Metadata will no be overwritten");
                        IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                    }

                    SegmentationVisualization visualizer = new SegmentationVisualization();

                    countBouton = visualizer.spotVisualization(originalImage, projMethod, stimFrame, sigmaLoG, prominence,
                            sigmaSpots, rollingSpots, thresholdSpots, spotErosion,
                            radiusGradient, minSizePx, maxSizePx, lowCirc, highCirc,
                            calibration, setDisplayRange);

                    IJ.log("Found " + countBouton + " Bouton(s)");

                    actionLabel.setText("Found " + countBouton + " Bouton(s)");

                }

            } else {

                IJ.error("Please choose a file in the file list!");

            }

        }

    } // close inner class

    /**
     * starts background segmentation visualization
     */
    public class MyPreviewBackListener implements ActionListener {

        public void actionPerformed(ActionEvent a) {

            Calibration calibration;

            IJ.log("Starting preview for background segmentation");

            Double sigmaBackground = (Double) doubleSpinBack1.getValue();
            IJ.log("Sigma background set to: " + sigmaBackground);

            String thresholdBackground = (String) thresholdListBack.getSelectedItem();
            IJ.log("Threshold for background set to: " + thresholdBackground);

            Double minSizeBack = (Double) doubleSpinBack2.getValue();
            Double maxSizeBack  = (Double) doubleSpinBack3.getValue();
            IJ.log("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²" );

            // calculate size in pixel
            boolean calibrationSetting = checkCalibration.isSelected();

            Double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            Double frameRate = (Double) doubleSpinnerFrameRate.getValue();

            Image previewImage = new Image(inputDir, pxSizeMicron, frameRate);

            int selectionChecker = list.getSelectedIndex();

            if (selectionChecker != -1){

                String selectedFile = (String) list.getSelectedValue();
                IJ.log("Selected File: " + selectedFile);

                // check if there are windows open already
                int openImages = WindowManager.getImageCount();

                // if there are image windows open check if they are of the list and of the selected image
                if  ( openImages != 0 ) {

                    IJ.log("There are images open!");

                    String[] openImage = WindowManager.getImageTitles();
                    ArrayList<String> openImageList = new ArrayList<>(Arrays.asList(openImage));

                    FileList fileUtility = new FileList();
                    ArrayList<String> openInputImages = fileUtility.intersection(openImageList, fileList);

                    boolean selectedFileChecker = false;

                    for (String image : openInputImages) {

                        if (image.equals(selectedFile)) {

                            IJ.log(selectedFile + " is already open");
                            selectedFileChecker = true;

                        } else {

                            IJ.selectWindow(image);
                            IJ.run("Close");

                        }

                    }

                    if (selectedFileChecker) {

                        IJ.log("Selected file is already open");


                        IJ.selectWindow(selectedFile);
                        ImagePlus selectedImage = WindowManager.getCurrentWindow().getImagePlus();

                        setDisplayRange = false;

                        String titleOriginal = selectedImage.getTitle();

                        int minSizePx;
                        int maxSizePx;

                        if (calibrationSetting) {

                            calibration = previewImage.calibrate();
                            minSizePx = Image.calculateSizePx(pxSizeMicron, minSizeBack);
                            maxSizePx = Image.calculateSizePx(pxSizeMicron, maxSizeBack);

                            IJ.log("Metadata will be overwritten.");
                            IJ.log("Pixel size set to: " + pxSizeMicron);
                            IJ.log("Frame rate set to: " + frameRate);
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);


                        } else {

                            ImagePlus originalImage = previewImage.openImage(selectedFile);
                            calibration = originalImage.getCalibration();
                            Double pxSizeFromImage = calibration.pixelWidth;
                            minSizePx = Image.calculateSizePx(pxSizeFromImage, minSizeBack);
                            maxSizePx = Image.calculateSizePx(pxSizeFromImage, maxSizeBack);

                            IJ.log("Metadata will no be overwritten");
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                        }

                        ImagePlus forBackSegmentation = previewImage.projectImage(selectedImage, "max");

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        visualizer.backgroundVisualization(forBackSegmentation, sigmaBackground, thresholdBackground,
                                        minSizePx, maxSizePx, selectedImage, titleOriginal,
                                        calibration, setDisplayRange);

                    } else {

                        IJ.log("The selected image is not open");

                        // segment background and show for validation
                        setDisplayRange = true;
                        ImagePlus originalImage = previewImage.openImage(selectedFile);

                        String titleOriginal = originalImage.getTitle();

                        ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "max");

                        int minSizePx;
                        int maxSizePx;

                        if (calibrationSetting) {

                            calibration = previewImage.calibrate();
                            minSizePx = Image.calculateSizePx(pxSizeMicron, minSizeBack);
                            maxSizePx = Image.calculateSizePx(pxSizeMicron, maxSizeBack);

                            IJ.log("Metadata will be overwritten.");
                            IJ.log("Pixel size set to: " + pxSizeMicron);
                            IJ.log("Frame rate set to: " + frameRate);
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);


                        } else {

                            calibration = originalImage.getCalibration();
                            Double pxSizeFromImage = calibration.pixelWidth;
                            minSizePx = Image.calculateSizePx(pxSizeFromImage, minSizeBack);
                            maxSizePx = Image.calculateSizePx(pxSizeFromImage, maxSizeBack);

                            IJ.log("Metadata will no be overwritten");
                            IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                        }

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        visualizer.backgroundVisualization(forBackSegmentation, sigmaBackground, thresholdBackground,
                                minSizePx, maxSizePx, originalImage, titleOriginal,
                                calibration, setDisplayRange);

                    }

                } else {

                    IJ.log("There are no images open!");

                    // segment background and show for validation
                    ImagePlus originalImage = previewImage.openImage(selectedFile);

                    setDisplayRange = true;
                    String titleOriginal = originalImage.getTitle();

                    ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "max");

                    int minSizePx;
                    int maxSizePx;

                    if (calibrationSetting) {

                        calibration = previewImage.calibrate();
                        minSizePx = Image.calculateSizePx(pxSizeMicron, minSizeBack);
                        maxSizePx = Image.calculateSizePx(pxSizeMicron, maxSizeBack);

                        IJ.log("Metadata will be overwritten.");
                        IJ.log("Pixel size set to: " + pxSizeMicron);
                        IJ.log("Frame rate set to: " + frameRate);
                        IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                    } else {

                        calibration = originalImage.getCalibration();
                        Double pxSizeFromImage = calibration.pixelWidth;
                        minSizePx = Image.calculateSizePx(pxSizeFromImage, minSizeBack);
                        maxSizePx = Image.calculateSizePx(pxSizeFromImage, maxSizeBack);

                        IJ.log("Metadata will no be overwritten");
                        IJ.log("MinSizePx " + minSizePx + " MaxSizePx " + maxSizePx);

                    }


                    SegmentationVisualization visualizer = new SegmentationVisualization();

                    visualizer.backgroundVisualization(forBackSegmentation, sigmaBackground, thresholdBackground,
                            minSizePx, maxSizePx, originalImage, titleOriginal,
                            calibration, setDisplayRange);
                }

            } else {

                IJ.error("Please choose a file in the file list!");

            }

        }

    } // close inner class

    /**
     * listener that executes the saving of the settings in the PreviewGUI to an xml file
     */
    public class MySaveListener implements ActionListener {

        public void actionPerformed(ActionEvent a) {

            String fileName = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss'-settings.xml'").format(new Date());

            saveSettings(fileName);

        }
    }

    /**
     * loads settings from the specified xml file
     */
    public class MyLoadListener extends Component implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
                    "xml files (*.xml)", "xml");

            JFileChooser settingsFileChooser = new JFileChooser();
            settingsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            settingsFileChooser.setFileFilter(xmlfilter);

            int option = settingsFileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {

                settingsFile = settingsFileChooser.getSelectedFile();
                String settingsFileString = settingsFile.toString();
                IJ.log("Loading xml: " + settingsFileString);

                try {

                    XmlHandler readMyXml = new XmlHandler();
                    readMyXml.xmlReader(settingsFileString);

                    projMethod = readMyXml.readProjMethod;

                    sigmaLoG = readMyXml.readSigmaLoG;
                    prominence = readMyXml.readProminence;
                    sigmaSpots = readMyXml.readSigmaSpots;
                    rollingSpots = readMyXml.readRollingSpots;
                    thresholdSpots = readMyXml.readThresholdSpots;
                    spotErosionSetting = readMyXml.readSpotErosion;
                    radiusGradient = readMyXml.readRadiusGradient;
                    minSizeSpot = readMyXml.readMinSizeSpot;
                    maxSizeSpot = readMyXml.readMaxSizeSpot;
                    lowCirc = readMyXml.readLowCirc;
                    highCirc = readMyXml.readHighCirc;

                    sigmaBackground = readMyXml.readSigmaBackground;
                    thresholdBackground = readMyXml.readThresholdBackground;
                    minSizeBack = readMyXml.readMinSizeBack;
                    maxSizeBack =  readMyXml.readMaxSizeBack;

                    calibrationSetting = readMyXml.readCalibrationSetting;
                    stimFrame = readMyXml.readStimFrame;
                    pxSizeMicron = readMyXml.readPxSizeMicron;
                    frameRate = readMyXml.readFrameRate;


                } catch (ParserConfigurationException ex) {

                    ex.printStackTrace();
                    IJ.log("ERROR: XML reader, Parser Configuration exception");
                    IJ.error("Please select a valid .xml or leave empty");
                    settingsFile = null;

                } catch (IOException ex) {

                    ex.printStackTrace();
                    IJ.log("ERROR: XML reader, IOException");
                    IJ.error("Please select a valid .xml or leave empty");
                    settingsFile = null;

                } catch (SAXException ex) {

                    ex.printStackTrace();
                    IJ.log("ERROR: XML reader, SAXException");
                    IJ.error("Please select a valid .xml or leave empty");
                    settingsFile = null;

                }

                boxSpotSeg.removeAll();
                setUpSpotTab();
                tabbedPane.addTab("Boutons", boxSpotSeg);

                boxBackground.removeAll();
                setUpBackTab();
                tabbedPane.addTab("Background", boxBackground);

                boxSettings.removeAll();
                setUpSettingsTab();
                batchBox.add(boxSettings);

            } else {

                settingsFile = null;
                IJ.error("Invalid settings file");

            }
        }
    }

    public class MyResetDirectoryListener extends Component implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            boolean checkDir = IJ.showMessageWithCancel("Warning!", "Do you want to reset Directories? \n \n " +
                    "Settings will remain the same!");

            if ( checkDir ){

                //theFrame.set

                String fileName = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss'-settings.xml'").format(new Date());
                saveSettings(fileName);

                String settingFilePath = outputDir + File.separator + fileName;

                theFrame.dispose();
                InputGuiFiji start = new InputGuiFiji( settingFilePath, false);

                start.createWindow();

                IJ.log("Resetting directories...");

            } else {

                IJ.log("Directory reset canceled");

            }

        }

    }

    /**
     * resets the settings to default values
     */
    public class MyResetListener extends Component implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            boolean checkResetSettings = IJ.showMessageWithCancel("Warning!", "Reset Segmentation Settings?");

            if ( checkResetSettings ) {

                IJ.log("Resetting settings to default parameters");

                // Projection Method
                projMethod = "max";

                sigmaLoG = 0.5;
                prominence = 0.005;
                sigmaSpots = 1.0;
                rollingSpots = 30.0;
                thresholdSpots = "Triangle";
                spotErosionSetting = false;
                radiusGradient = 3;
                minSizeSpot = 0.0;
                maxSizeSpot = 1000.0;
                lowCirc = 0.0;
                highCirc = 1.0;

                sigmaBackground = 4.0;
                thresholdBackground = "MinError";
                minSizeBack = 0.0;
                maxSizeBack = 10000.0;

                boxSpotSeg.removeAll();
                setUpSpotTab();
                // create tabbed panes
                tabbedPane.addTab("Boutons", boxSpotSeg);

                boxBackground.removeAll();
                setUpBackTab();
                tabbedPane.addTab("Background", boxBackground);

            } else {

                IJ.log("Canceled resetting of processing settings!");

            }
        }
    }

    /**
     * Starts the batch processing of all the files in the FileList
     */
    public class MyBatchListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

            IJ.log("Starting batch processing");
            Commands.closeAll();

            // get current settings for spot segmentation
            // get all the values from the GUI
            double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
            IJ.log("LoG sigma: " + sigmaLoG);

            double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
            IJ.log("Prominence: " + prominence);

            double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
            IJ.log("Gauss sigma: " + sigmaSpots);

            double rollingSpots = (Double) doubleSpinnerRollingBallSpot.getValue();
            IJ.log("Rolling Ball radius: " + rollingSpots);

            String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
            IJ.log("Threshold: " + thresholdSpots);

            // check if erosion is applied
            boolean spotErosion;
            if (erosionCheckBox.isSelected()) spotErosion = true;
            else spotErosion = false;
            IJ.log("Spot erosion: " + spotErosion);

            int radiusGradient = (Integer) intSpinnerGradient.getValue();
            IJ.log("Gradient Radius: " + radiusGradient);

            double minSizeSpot = (Double) doubleSpinnerMinSize.getValue();
            double maxSizeSpot = (Double) doubleSpinnerMaxSize.getValue();
            IJ.log("Spots size from: " + minSizeSpot + " to " + maxSizeSpot + " µm²" );

            double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
            double highCirc = (Double) doubleSpinnerHighCirc.getValue();
            IJ.log("Spots circ. from: " + lowCirc + " to " + highCirc);

            // get current settings for background segmentation
            double sigmaBackground = (Double) doubleSpinBack1.getValue();
            IJ.log("Sigma background set to: " + sigmaBackground);

            String thresholdBackground = (String) thresholdListBack.getSelectedItem();
            IJ.log("Threshold for background set to: " + thresholdBackground);

            double minSizeBack = (Double) doubleSpinBack2.getValue();
            double maxSizeBack  = (Double) doubleSpinBack3.getValue();
            IJ.log("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²" );

            // check if calibration needs to be overwritten
            boolean calibrationSetting;

            if (checkCalibration.isSelected()) calibrationSetting = true;
            else calibrationSetting = false;

            double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            double frameRate = (Double) doubleSpinnerFrameRate.getValue();

            int stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
            IJ.log("Stimulation frame: " + stimFrame);

            String fileName = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss'-settings.xml'").format(new Date());

            saveSettings(fileName);

            BatchProcessor batch = new BatchProcessor(inputDir, outputDir, fileList, projMethod,
                    sigmaLoG, prominence, sigmaSpots, rollingSpots, thresholdSpots, spotErosion,
                    radiusGradient,
                    minSizeSpot, maxSizeSpot, lowCirc, highCirc,
                    sigmaBackground, thresholdBackground,
                    minSizeBack, maxSizeBack,
                    stimFrame, calibrationSetting, pxSizeMicron, frameRate
                    );

            batch.loopOverImages();

        }

    } // close inner class

    /**
     * Constructor for PreviewGui using default settings
     * @param inputDirectory from InputGui
     * @param outputDirectory from InputGui
     * @param filesToProcess from InputGui
     */
    PreviewGui (String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess){

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList= filesToProcess;

        // Projection Method
        projMethod = "max";

        sigmaLoG = 0.5;
        prominence = 0.005;
        sigmaSpots = 1.0;
        rollingSpots = 30.0;
        thresholdSpots = "Triangle";
        spotErosionSetting = false;
        radiusGradient = 3;
        minSizeSpot = 0.0;
        maxSizeSpot = 1000.0;
        lowCirc = 0.0;
        highCirc = 1.0;

        sigmaBackground = 4.0;
        thresholdBackground = "MinError";
        minSizeBack = 0.0;
        maxSizeBack =  10000.0;

        calibrationSetting = false;
        stimFrame = 5;
        pxSizeMicron = 0.162;
        frameRate = 2.0;

    }

    /**
     * Constructor for PreviewGui with settings from the select settings file
     *
     * @param inputDirectory directory for input images
     * @param outputDirectory directory for saving results
     * @param filesToProcess list the file names for batch
     * @param projectionMethod projection method
     * @param getSigmaLoG sigma for LoG
     * @param getProminence prominence for spot detection
     * @param getSigmaSpots sigma for spot segmentation
     * @param getRollingSpots rolling ball background radius for spot segmentation
     * @param getThresholdSpots global intensity based threshold for spots
     * @param getSpotErosion binary mask erosion for spots
     * @param getRadiusGradient radius for creating gradient image (watershed)
     * @param getMinSizePxSpot minimum spot size in px
     * @param getMaxSizePxSpot maximum spot size in px
     * @param getLowCirc minimum circularity of spots
     * @param getHighCirc maximum circularity of spots
     * @param getSigmaBackground sigma gaussian blur for background segmentation
     * @param getThresholdBackground global intensity threshold for background segmentation
     * @param getMinSizePxBack minimum background region size
     * @param getMaxSizePxBack maximum background region size
     * @param getStimFrame frame when stimulation happens
     * @param getCalibrationSetting image calibration setting
     * @param getSizeMicron pixel size in micron
     * @param getFrameRate frame rate in seconds
     */
    PreviewGui (String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess,
                       String projectionMethod, double getSigmaLoG, double getProminence,
                       double getSigmaSpots, double getRollingSpots, String getThresholdSpots,
                      boolean getSpotErosion, int getRadiusGradient,
                       double getMinSizePxSpot, double getMaxSizePxSpot, double getLowCirc, double getHighCirc,
                       double getSigmaBackground, String getThresholdBackground,
                       double getMinSizePxBack, double getMaxSizePxBack,
                       int getStimFrame, boolean getCalibrationSetting, double getSizeMicron, double getFrameRate ){

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList = filesToProcess;

        projMethod = projectionMethod;

        sigmaLoG = getSigmaLoG;
        prominence = getProminence;
        sigmaSpots = getSigmaSpots;
        rollingSpots = getRollingSpots;
        thresholdSpots = getThresholdSpots;
        spotErosionSetting = getSpotErosion;
        radiusGradient = getRadiusGradient;
        minSizeSpot = getMinSizePxSpot;
        maxSizeSpot = getMaxSizePxSpot;
        lowCirc = getLowCirc;
        highCirc = getHighCirc;

        sigmaBackground = getSigmaBackground;
        thresholdBackground = getThresholdBackground;
        minSizeBack = getMinSizePxBack;
        maxSizeBack =  getMaxSizePxBack;

        calibrationSetting = getCalibrationSetting;
        stimFrame = getStimFrame;
        pxSizeMicron = getSizeMicron;
        frameRate = getFrameRate;

    }

}