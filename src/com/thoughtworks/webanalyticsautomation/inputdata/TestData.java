package com.thoughtworks.webanalyticsautomation.inputdata;

/**
 * Created by: Anand Bagmar
 * Email: anandb@thoughtworks.com, abagmar@gmail.com
 * Date: Dec 29, 2010
 * Time: 9:34:02 AM
 */

import com.thoughtworks.webanalyticsautomation.common.CONFIG;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static com.thoughtworks.webanalyticsautomation.common.FileUtils.deserializeSectionsFromFile;

public class TestData extends CONFIG implements Serializable {

    static {
        logger = Logger.getLogger(TestData.class.getName());
    }

    private static HashMap<String, ArrayList> loadedSections = new HashMap <String, ArrayList>();
    private ArrayList <Section> sectionsLoadedFromFile = new ArrayList<Section>();

    public static ArrayList<Section> sectionsFor(String absoluteFilePath, String actionName) {
        logger.info ("Loading input data file: " + absoluteFilePath);
        loadFile(absoluteFilePath);
        ArrayList<Section> subsetList = primeLoadedSections(absoluteFilePath, actionName);
        return subsetList;
    }

    private static ArrayList<Section> primeLoadedSections(String absoluteFilePath, String actionName) {
        ArrayList<Section> subsetList = new ArrayList<Section>();
        for (Object sectionObject: loadedSections.get(absoluteFilePath)) {
            Section section = (Section) sectionObject;
            if (section.hasAction(actionName)) {
                subsetList.add(section);
            }
        }
        return subsetList;
    }

    public ArrayList<Section> getSectionsLoadedFromFile() {
        return sectionsLoadedFromFile;
    }

    private static void loadFile(String absoluteFilePath) {
        if (isFileLoaded(absoluteFilePath)) {
            logger.info ("Input file is already loaded in memory");
        }
        else {
            logger.info ("Loading Input data file ...");
            loadedSections.put(absoluteFilePath, deserializeSectionsFromFile(absoluteFilePath, configureXStream()));
        }
    }

    private static boolean isFileLoaded(String absoluteFilePath) {
        return loadedSections.containsKey(absoluteFilePath);
    }

    private static XStream configureXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("Sections", TestData.class);
        xStream.omitField(TestData.class, "loadedSections");
        xStream.addImplicitCollection(TestData.class, "sectionsLoadedFromFile", "Section", Section.class);
        xStream = Section.configurePageLayoutXStream(xStream);
        return xStream;
    }
}