package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.google.gson.JsonElement;


public class MachineLearningTrainingConfig implements IPluginConfig<MachineLearningTrainingConfig>{

    private String algorithm = "";
    private String algorithmParams = "";
    private String modelName = "";
    private String feature = "";
    private String constraints = "";

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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }
}
