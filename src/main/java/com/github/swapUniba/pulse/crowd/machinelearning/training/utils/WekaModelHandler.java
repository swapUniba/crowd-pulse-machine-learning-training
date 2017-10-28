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

    private static final String curPath = System.getProperty("user.dir") + "//models//";

/*    public static String getModelsPath() throws IOException {
         File directory = new File(".");
         String curPath2 = System.getProperty(directory.getCanonicalPath() + File.separator + "models" + File.separator);
         return curPath2;
    }*/

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
        System.out.println("PERCORSO MODELLI SALVATI: " + curPath);
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

    public static Instances LoadTrainingSet(String modelName) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(curPath +modelName+ "_training.arff"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArffLoader.ArffReader arff = null;
        try {
            arff = new ArffLoader.ArffReader(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Instances data = arff.getData();
        data.setClassIndex(-1);

        return data;
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

    public static void saveFeatures(String[] features, String modelName) {

        try(  PrintWriter out = new PrintWriter( curPath + modelName + ".features" )  ){
            for (String ft : features) {
                out.println( ft );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String[] loadFeatures(String modelName) {

        List<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(curPath + modelName + ".features"))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] features = new String[result.size()];
        features = result.toArray(features);

        return features;

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
