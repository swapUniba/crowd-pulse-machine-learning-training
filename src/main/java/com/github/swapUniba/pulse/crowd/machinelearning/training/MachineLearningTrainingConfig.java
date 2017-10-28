package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.google.gson.JsonElement;


public class MachineLearningTrainingConfig implements IPluginConfig<MachineLearningTrainingConfig>{

    private String algorithm = "";
    private String algorithmParams = "";
    private String modelName = "";
    private String[] features;
    private String constraints = "";
    private String evaluation = "";
    private boolean printFile = false;

    @Override
    public MachineLearningTrainingConfig buildFromJsonElement(JsonElement jsonElement) {
        return PluginConfigHelper.buildFromJson(jsonElement, MachineLearningTrainingConfig.class);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithmParams() {
        return algorithmParams;
    }

    public void setAlgorithmParams(String algorithmParams) {
        if (algorithmParams == null) {
            this.algorithmParams = "";
        }
        else {
            this.algorithmParams = algorithmParams;
        }
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] feature) {
        this.features = feature;
    }


    public boolean isPrintFile() {
        return printFile;
    }

    public void setPrintFile(boolean printFile) {
        this.printFile = printFile;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }
}
