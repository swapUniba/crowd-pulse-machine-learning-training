package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.WekaModelHandler;
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

/*            List<MongoWekaDTO> collectionList = new ArrayList<>();

            for (String coll : _collectionNames){
                MongoWekaDTO mongoWekaDTO = CollectionConverter.getDatasetFromMongo(_dbname, coll,_collectionNames);
                 collectionList.add(mongoWekaDTO);
            }

            Instances trainingSet = CollectionConverter.mergeInstances(collectionList, config.getModelName());
            trainingSet.setClassIndex(trainingSet.numAttributes() - 1); //l'attributo di classe è sempre l'ultimo che aggiungo
            AbstractClassifier algorithm = null;

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
            algorithm.buildClassifier(trainingSet);
            System.out.println("Model has been built!");
            WekaModelHandler.SaveModel(config.getModelName(), algorithm); //salvare il modello con il suo nome
            classifierBuilt = true;*/

        }
        catch (Exception ex) {
            MachineLearningTrainingPlugin.logger.error(ex.toString());
        }

        return classifierBuilt;

    }


}
