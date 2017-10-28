package com.github.swapUniba.pulse.crowd.machinelearning.training.modelTraining;

import com.github.frapontillo.pulse.crowd.data.entity.Entity;
import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.swapUniba.pulse.crowd.machinelearning.training.DTO.MachineLearningTrainingConfigDTO;
import com.github.swapUniba.pulse.crowd.machinelearning.training.MachineLearningTrainingConfig;
import com.github.swapUniba.pulse.crowd.machinelearning.training.MachineLearningTrainingPlugin;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.MessageToWeka;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.WekaModelHandler;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.enums.MLAlgorithmEnum;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

// http://weka.sourceforge.net/doc.dev/weka/classifiers/evaluation/Evaluation.html
// opzioni per l'evaluation

public class TrainModel {

    private MachineLearningTrainingConfig config;
    private List<Entity> entities;

    public TrainModel(MachineLearningTrainingConfig cfg, List<Entity> entities) {
        this.config = cfg;
        this.entities = entities;
    }


    public boolean RunTraining() {

        boolean classifierBuilt = false;

        try {

            AbstractClassifier algorithm = null;
            Instances instances = MessageToWeka.getInstancesFromEntities(entities, config.getFeatures(),config.getModelName());

            if (MLAlgorithmEnum.valueOf(config.getAlgorithm()) == MLAlgorithmEnum.J48) {
                algorithm = new J48();
            }

            if (MLAlgorithmEnum.valueOf(config.getAlgorithm()) == MLAlgorithmEnum.NaiveBayes) {
                algorithm = new NaiveBayes();
            }

            if (MLAlgorithmEnum.valueOf(config.getAlgorithm()) == MLAlgorithmEnum.LinearRegression) {
                algorithm = new LinearRegression();
            }

            instances.setClassIndex(instances.numAttributes() - 1);

            // +++++ EVALUATION +++++
            try {
                String[] evalOptions = weka.core.Utils.splitOptions(config.getEvaluation());
                List<String> evalOpt = new ArrayList<>();
                String[] algorithmOptions = weka.core.Utils.splitOptions(config.getAlgorithmParams());

                for (String s : algorithmOptions) { //aggiungo le opzioni dell'algoritmo di class.
                    evalOpt.add(s);
                }
                for (String s : evalOptions) { // aggiungo le opzioni per la valutazione (-x 10 per il 10FCV, etc)
                    evalOpt.add(s);
                }
                evalOpt.add("-t"); // gli dico dove prendere il file del training set
                evalOpt.add(WekaModelHandler.getModelPath(config.getModelName()));
                String[] evalNewOpt = evalOpt.stream().toArray(String[]::new);

                String evaluationOutput = Evaluation.evaluateModel(algorithm, evalNewOpt);

                System.out.println(evaluationOutput);
                classifierBuilt = true;
                System.out.println("");
            }
            catch (Exception ex) {
                MachineLearningTrainingPlugin.logger.error("Errore nella valutazione..." + ex.toString());
            }
            // +++++ END EVALUATION +++++
            if (classifierBuilt) {
                MachineLearningTrainingPlugin.logger.info("Model has been built!");
                WekaModelHandler.SaveModel(config.getModelName(), algorithm); //salvare il modello con il suo nome
            }

        }
        catch (Exception ex) {
            MachineLearningTrainingPlugin.logger.error(ex.toString());
            ex.printStackTrace();
        }

        return classifierBuilt;

    }


}
