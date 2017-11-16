package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/***
 * Salva e carica i modelli dei classificatori di Weka
 */
public class WekaModelHandler {

    private static final String curPath = System.getProperty("user.dir") + File.separator + "models" + File.separator;


    public static void SaveModel(String filename, Object predictiveModel) throws Exception {

        File f = new File(curPath);
        if (!f.exists() && !f.isDirectory()) {
            Files.createDirectory(Paths.get(curPath));
        }

        weka.core.SerializationHelper.write(curPath  + filename+".model", predictiveModel);
    }

    public static void SaveTrainingSet(Instances insts, String modelName) {

        ArffSaver saver = new ArffSaver();
        saver.setInstances(insts);
        try {
            saver.setFile(new File(curPath + modelName + "_training.arff"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getModelPath(String modelName) {
        return curPath + modelName + "_training.arff";
    }

    public static void writeOutputFile(String content, String modelName) {
        try(  PrintWriter out = new PrintWriter( curPath + modelName + "_training_output.txt")  ){
            out.println( content );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
