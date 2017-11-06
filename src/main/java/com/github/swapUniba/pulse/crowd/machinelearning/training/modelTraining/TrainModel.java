package com.github.swapUniba.pulse.crowd.machinelearning.training.modelTraining;

import com.github.frapontillo.pulse.crowd.data.entity.Entity;
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
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.instance.SparseToNonSparse;

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


            if (MLAlgorithmEnum.J48.name().toLowerCase().startsWith(config.getAlgorithm().toLowerCase())) {
                algorithm = new J48();
            }

            if (MLAlgorithmEnum.NaiveBayes.name().toLowerCase().startsWith(config.getAlgorithm().toLowerCase())) {
                algorithm = new NaiveBayes();
            }

            if (MLAlgorithmEnum.RandomForest.name().toLowerCase().startsWith(config.getAlgorithm().toLowerCase())) {
                algorithm = new RandomForest();
            }

            if (MLAlgorithmEnum.LinearRegression.name().toLowerCase().startsWith(config.getAlgorithm().toLowerCase())) {
                algorithm = new LinearRegression();
                if (config.getRegressionAttribute() == null || config.getRegressionAttribute() == "") {
                    throw new Exception("Indicare l'attributo da usare come variabile dipendente nella regressione!");
                }
                Attribute regrAttribute = instances.attribute(config.getRegressionAttribute());
                instances = reorderAttributes(instances,regrAttribute.index());
                //instances.setClassIndex(regrAttribute.index());
            }

            if (instances.classIndex() == -1) {
                instances.setClassIndex(instances.numAttributes() - 1);
            }

            WekaModelHandler.saveFeatures(config.getFeatures(),config.getModelName());
            WekaModelHandler.SaveTrainingSet(instances,config.getModelName());
            //WekaModelHandler.SaveInstanceStructure(instances,config.getModelName());

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
                evalOpt.add("-c"); // imposto l'attributo di classe
                evalOpt.add(Integer.toString(instances.classIndex() + 1)); // in questo caso l'indice è 1-based!
                String[] evalNewOpt = evalOpt.stream().toArray(String[]::new);

                String evaluationOutput = Evaluation.evaluateModel(algorithm, evalNewOpt);

                System.out.println(evaluationOutput);

                if (config.isPrintFile()) {
                    WekaModelHandler.writeOutputFile(evaluationOutput,config.getModelName());
                }

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

    /**
     * Riordina gli attributi mettendo per ultimo quello usato come variabile dipendente, per
     * facilitare poi la fase di testing
     * @param instances
     * @param attrIndex
     * @return
     */
    private static Instances reorderAttributes(Instances instances, int attrIndex) {

        Reorder reorderFilter = new Reorder();

        try {
            reorderFilter.setAttributeIndices(Integer.toString(attrIndex+2) + "-last," + Integer.toString(attrIndex+1));
            reorderFilter.setInputFormat(instances);
            instances = Filter.useFilter(instances, reorderFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instances;

    }

}
