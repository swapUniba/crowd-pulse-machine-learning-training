package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/***
 * Salva e carica i modelli dei classificatori di Weka
 */
public class WekaModelHandler {

    public static final String curPath = System.getProperty("user.dir") + "//models//";


    public static void SaveModel(String filename, Object predictiveModel) throws Exception {

        File f = new File(curPath);
        if (!f.exists() && !f.isDirectory()) {
            Files.createDirectory(Paths.get(curPath));
        }

        weka.core.SerializationHelper.write(curPath  + filename+".model", predictiveModel);
    }

    public static Classifier LoadModel(String filename) throws Exception {
        Classifier cls = (Classifier) weka.core.SerializationHelper.read(curPath + filename + ".model");
        return cls;
    }


    public static void SaveInstanceStructure(Instances insts, String filename) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(insts);
        try {
            saver.setFile(new File(curPath +filename+ "_structure.arff"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Instances LoadInstanceStructure(String modelName) {

        BufferedReader reader =
                null;
        try {
            reader = new BufferedReader(new FileReader(curPath +modelName+ "_structure.arff"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArffLoader.ArffReader arff = null;
        try {
            arff = new ArffLoader.ArffReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Instances data = arff.getData();
        data.setClassIndex(data.numAttributes() - 1);

        return data;
    }

}
