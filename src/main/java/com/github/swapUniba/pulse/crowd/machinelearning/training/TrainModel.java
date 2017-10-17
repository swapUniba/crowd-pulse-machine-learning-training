package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.MessageToWeka;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.WekaModelHandler;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.enums.Feature;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.enums.MLAlgorithmEnum;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class TrainModel {

    private MachineLearningTrainingConfig config;
    private List<Message> messages;

    public TrainModel(MachineLearningTrainingConfig cfg, List<Message> msgs) {
        this.config = cfg;
        this.messages = msgs;
    }


    public boolean RunTraining() {

        boolean classifierBuilt = false;

        try {

            AbstractClassifier algorithm = null;
            Instances instances = MessageToWeka.getInstancesFromMessages(messages, Feature.valueOf(config.getFeature()),config.getModelName());


            if (MLAlgorithmEnum.valueOf(config.getAlgorithm()) == MLAlgorithmEnum.J48) {
                algorithm = new J48();
            }

            if (MLAlgorithmEnum.valueOf(config.getAlgorithm()) == MLAlgorithmEnum.NaiveBayes) {
                algorithm = new NaiveBayes();
            }

            if (MLAlgorithmEnum.valueOf(config.getAlgorithm()) == MLAlgorithmEnum.LinearRegression) {
                algorithm = new LinearRegression();
            }

            String[] options = weka.core.Utils.splitOptions(config.getAlgorithmParams());
            algorithm.setOptions(options);
            algorithm.buildClassifier(instances);

            MachineLearningTrainingPlugin.logger.info("Model has been built!");
            WekaModelHandler.SaveModel(config.getModelName(), algorithm); //salvare il modello con il suo nome
            classifierBuilt = true;

        }
        catch (Exception ex) {
            MachineLearningTrainingPlugin.logger.error(ex.toString());
        }

        return classifierBuilt;

    }


}
