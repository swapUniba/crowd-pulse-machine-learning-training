package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import weka.classifiers.Classifier;

public class WekaModelHandler {


    public static void SaveModel(String filename, Object predictiveModel) throws Exception {
        weka.core.SerializationHelper.write("C://models//"+filename+".model", predictiveModel);
    }

    public static Classifier LoadModel(String filename) throws Exception {
        Classifier cls = (Classifier) weka.core.SerializationHelper.read("C://models//"+filename+".model");
        return cls;
    }

}
