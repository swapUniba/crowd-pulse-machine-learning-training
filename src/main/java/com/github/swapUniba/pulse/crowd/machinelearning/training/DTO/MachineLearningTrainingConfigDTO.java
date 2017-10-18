package com.github.swapUniba.pulse.crowd.machinelearning.training.DTO;

public class MachineLearningTrainingConfigDTO {

    private String algorithm = "";
    private String algorithmParams = "";
    private String modelName = "";
    private String feature = "";
    private String constraints = "";

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
        this.algorithmParams = algorithmParams;
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
