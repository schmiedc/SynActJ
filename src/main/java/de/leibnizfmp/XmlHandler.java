package de.leibnizfmp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlHandler {

    void xmlReader(String filePath) throws ParserConfigurationException, IOException, SAXException {

        File xmlFile = new File(filePath);

        // build a document object
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        // get nodes of tag name
        String getProjMethod = doc.getElementsByTagName("projMethod").item(0).getTextContent();

        double getSigmaLoG = Double.parseDouble(doc.getElementsByTagName("sigmaLoG").item(0).getTextContent());

        double getProminence= Double.parseDouble(doc.getElementsByTagName("prominence").item(0).getTextContent());

        double sigmaSpots = Double.parseDouble(doc.getElementsByTagName("sigmaSpots").item(0).getTextContent());

        double  rollingSpots = Double.parseDouble(doc.getElementsByTagName("rollingSpots").item(0).getTextContent());

        String thresholdSpots = doc.getElementsByTagName("thresholdSpots").item(0).getTextContent();

        int radiusGradient = Integer.parseInt(doc.getElementsByTagName("radiusGradient").item(0).getTextContent());

        double minSizeSpot = Double.parseDouble(doc.getElementsByTagName("minSizeSpot").item(0).getTextContent());

        double maxSizeSpot= Double.parseDouble(doc.getElementsByTagName("maxSizeSpot").item(0).getTextContent());

        double lowCirc = Double.parseDouble(doc.getElementsByTagName("lowCirc").item(0).getTextContent());

        double highCirc = Double.parseDouble(doc.getElementsByTagName("highCirc").item(0).getTextContent());

        double sigmaBackground = Double.parseDouble(doc.getElementsByTagName("sigmaBackground").item(0).getTextContent());

        String thresholdBackground = doc.getElementsByTagName("thresholdBackground").item(0).getTextContent();

        double minSizeBack = Double.parseDouble(doc.getElementsByTagName("minSizeBack").item(0).getTextContent());

        double maxSizeBack = Double.parseDouble(doc.getElementsByTagName("maxSizeBack").item(0).getTextContent());

        boolean calibrationSetting = Boolean.parseBoolean(doc.getElementsByTagName("calibrationSetting").item(0).getTextContent());

        double pxSizeMicron = Double.parseDouble(doc.getElementsByTagName("pxSizeMicron").item(0).getTextContent());

        double frameRate= Double.parseDouble(doc.getElementsByTagName("frameRate").item(0).getTextContent());

        int stimFrame = Integer.parseInt(doc.getElementsByTagName("stimFrame").item(0).getTextContent());

    }

    void xmlWriter(String outputPath, String getProjectionMethod,
                   double getSigmaLoG, double getProminence,
                   double getSigmaSpots, double getRollingSpots, String getThresholdSpots, int getRadiusGradient,
                   double getMinSizePxSpot, double getMaxSizePxSpot, double getLowCirc, double getHighCirc,
                   double getSigmaBackground, String getThresholdBackground,
                   double getMinSizePxBack, double getMaxSizePxBack,
                   int getStimFrame, boolean getCalibrationSetting, double getSizeMicron, double getFrameRate ){

        try
        {

            String sigmaLoG = Double.toString(getSigmaLoG);
            String prominence = Double.toString(getProminence);
            String sigmaSpots = Double.toString(getSigmaSpots);
            String rollingSpots = Double.toString(getRollingSpots);
            String radiusGradient = Integer.toString(getRadiusGradient);
            String minSizePxSpot = Double.toString(getMinSizePxSpot);
            String maxSizePxSpot = Double.toString(getMaxSizePxSpot);
            String lowCirc = Double.toString(getLowCirc);
            String highCirc = Double.toString(getHighCirc);

            String simgaBackground = Double.toString(getSigmaBackground);
            String minSizePxBack = Double.toString(getMinSizePxBack);
            String maxSizePxBack = Double.toString(getMaxSizePxBack);

            String stimFrame = Integer.toString(getStimFrame);
            String calibrationSetting = Boolean.toString(getCalibrationSetting);
            String sizeMicron = Double.toString(getSizeMicron);
            String frameRate = Double.toString(getFrameRate);

            // create document builder
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // pHluorinSettings as root element
            Element rootElement = doc.createElement("pHluorinSettings");
            doc.appendChild(rootElement);

            // add the different settings as child of root
            Element settingsProjMethod = doc.createElement("projMethod");
            settingsProjMethod.setTextContent(getProjectionMethod);
            rootElement.appendChild(settingsProjMethod);

            Element settingsSigmaLoG = doc.createElement("sigmaLoG");
            settingsSigmaLoG.setTextContent(sigmaLoG);
            rootElement.appendChild(settingsSigmaLoG);

            Element settingsProminence = doc.createElement("prominence");
            settingsProminence.setTextContent(prominence);
            rootElement.appendChild(settingsProminence);

            Element settingsSigmaSpots = doc.createElement("sigmaSpots");
            settingsSigmaSpots.setTextContent(sigmaSpots);
            rootElement.appendChild(settingsSigmaSpots);

            Element settingsRollingSpots = doc.createElement("rollingSpots");
            settingsRollingSpots.setTextContent(rollingSpots);
            rootElement.appendChild(settingsRollingSpots);

            Element settingsThresholdSpots = doc.createElement("thresholdSpots");
            settingsThresholdSpots.setTextContent(getThresholdSpots);
            rootElement.appendChild(settingsThresholdSpots);

            Element settingsRadiusGradient = doc.createElement("radiusGradient");
            settingsRadiusGradient.setTextContent(radiusGradient);
            rootElement.appendChild(settingsRadiusGradient);
            
            Element settingsMinSizePxSpot = doc.createElement("minSizePxSpot");
            settingsMinSizePxSpot.setTextContent(minSizePxSpot);
            rootElement.appendChild(settingsMinSizePxSpot);

            Element settingsMaxSizePxSpot = doc.createElement("maxSizePxSpot");
            settingsMaxSizePxSpot.setTextContent(maxSizePxSpot);
            rootElement.appendChild(settingsMaxSizePxSpot);

            Element settingsLowCirc = doc.createElement("lowCirc");
            settingsLowCirc.setTextContent(lowCirc);
            rootElement.appendChild(settingsLowCirc);

            Element settingsHighCirc = doc.createElement("highCirc");
            settingsHighCirc.setTextContent(highCirc);
            rootElement.appendChild(settingsHighCirc);

            Element settingsSimgaBackground = doc.createElement("simgaBackground");
            settingsSimgaBackground.setTextContent(simgaBackground);
            rootElement.appendChild(settingsSimgaBackground);

            Element settingsThresholdBackground = doc.createElement("thresholdBackground");
            settingsThresholdBackground.setTextContent(getThresholdBackground);
            rootElement.appendChild(settingsThresholdBackground);

            Element settingsMinSizePxBack = doc.createElement("minSizePxBack");
            settingsMinSizePxBack.setTextContent(minSizePxBack);
            rootElement.appendChild(settingsMinSizePxBack);

            Element settingsMaxSizePxBack = doc.createElement("maxSizePxBack");
            settingsMaxSizePxBack.setTextContent(maxSizePxBack);
            rootElement.appendChild(settingsMaxSizePxBack);

            Element settingsStimFrame = doc.createElement("stimFrame");
            settingsStimFrame.setTextContent(stimFrame);
            rootElement.appendChild(settingsStimFrame);

            Element settingsCalibrationSetting = doc.createElement("calibrationSetting");
            settingsCalibrationSetting.setTextContent(calibrationSetting);
            rootElement.appendChild(settingsCalibrationSetting);

            Element settingsSizeMicron = doc.createElement("sizeMicron");
            settingsSizeMicron.setTextContent(sizeMicron);
            rootElement.appendChild(settingsSizeMicron);

            Element settingsFrameRate = doc.createElement("frameRate");
            settingsFrameRate.setTextContent(frameRate);
            rootElement.appendChild(settingsFrameRate);

            // Write the content into XML file
            String fileName = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-settings.xml'").format(new Date());

            String filePath = outputPath + fileName;
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            // create transformer factory
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Beautify the format of the resulted XML using an ident with 4 spaces
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source, result);

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }

}
