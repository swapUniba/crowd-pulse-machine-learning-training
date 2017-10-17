package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import weka.classifiers.Classifier;


public class WekaModelHandler {

    public static void main(String[] args) throws Exception{

        SaveModel("cazzarola","Strunz");

        }

    public static void SaveModel(String filename, Object predictiveModel) throws Exception {
        String curPath = System.getProperty("user.dir") + "//models//";
        File f = new File(curPath);
        if (!f.exists() && !f.isDirectory()) {
            Files.createDirectory(Paths.get(curPath));
        }
        weka.core.SerializationHelper.write(curPath  + filename+".model", predictiveModel);
    }

    public static Classifier LoadModel(String filename) throws Exception {
        Classifier cls = (Classifier) weka.core.SerializationHelper.read("C://models//"+filename+".model");
        return cls;
    }

}
