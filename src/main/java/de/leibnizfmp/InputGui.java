package de.leibnizfmp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class InputGui {


    void createWindow() {

        JFrame frame = new JFrame("Setup dialog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createUI(frame);
        frame.setSize(560, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private void createUI(final JFrame frame) {

        JPanel panelChooser = new JPanel();
        LayoutManager layout = new FlowLayout();
        panelChooser.setLayout(layout);

        JPanel panelStarter = new JPanel();
        panelStarter.setLayout(layout);

        Box chooserBox = new Box(BoxLayout.Y_AXIS);

        JLabel inputLabel = new JLabel("Input directory: ");
        JTextField inputDir = new JTextField("Choose Directory");
        JButton inputButton = new JButton("Choose");
        inputButton.addActionListener(new InputListener());

        Box boxInput = createInputDialog(inputButton, inputLabel, inputDir);
        chooserBox.add(boxInput);

        JLabel outputLabel = new JLabel("Output directory: ");
        JTextField outputDir = new JTextField("Choose Directory");
        JButton outputButton = new JButton("Choose");
        outputButton.addActionListener(new OutputListener());

        Box boxOutput = createInputDialog(outputButton, outputLabel, outputDir);
        chooserBox.add(boxOutput );

        JLabel settingsLabel = new JLabel("Settings file: ");
        JTextField settingsDir = new JTextField("Choose File");
        JButton settingButton = new JButton("Choose");
        settingButton.addActionListener(new SettingsListener());

        Box boxSettings = createInputDialog(settingButton, settingsLabel, settingsDir);
        chooserBox.add(boxSettings);

        panelChooser.add(chooserBox);

        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(new PreviewListener());

        JButton batchButton = new JButton("Batch");
        batchButton.addActionListener(new BatchListener());

        panelStarter.add(previewButton);
        panelStarter.add(batchButton);

        frame.getContentPane().add(panelStarter, BorderLayout.SOUTH);
        frame.getContentPane().add(panelChooser, BorderLayout.CENTER);

    }

    private Box createInputDialog(JButton button,JLabel label, JTextField directory){

        Box box= new Box(BoxLayout.X_AXIS);
        box.add(label);
        box.add(directory);
        box.add(button);

        return box;

    }

    public static class InputListener extends Component implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
            } else {
                System.out.println("This is not a directory");
            }

        }
    }

    public static class OutputListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public static class SettingsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public static class PreviewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public static class BatchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }


}
