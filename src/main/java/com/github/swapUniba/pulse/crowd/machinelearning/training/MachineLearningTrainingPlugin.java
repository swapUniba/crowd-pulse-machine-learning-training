package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Entity;
import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.util.PulseLogger;
import com.github.swapUniba.pulse.crowd.machinelearning.training.DTO.MachineLearningTrainingConfigDTO;
import com.github.swapUniba.pulse.crowd.machinelearning.training.modelTraining.TrainModel;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

import java.util.ArrayList;
import java.util.List;

public class MachineLearningTrainingPlugin extends IPlugin<Entity,Entity,MachineLearningTrainingConfig> {

    private static final String PLUGIN_NAME = "machine-learning-training";
    public static final Logger logger = PulseLogger.getLogger(MachineLearningTrainingPlugin.class);

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public MachineLearningTrainingConfig getNewParameter() {
        return new MachineLearningTrainingConfig();
    }

    @Override
    protected Observable.Operator<Entity, Entity> getOperator(MachineLearningTrainingConfig machineLearningTrainingConfig) {

        List<Entity> entities = new ArrayList<>();
        MachineLearningTrainingConfigDTO dto = new MachineLearningTrainingConfigDTO();
        dto.setAlgorithm(machineLearningTrainingConfig.getAlgorithm());
        dto.setAlgorithmParams(machineLearningTrainingConfig.getAlgorithmParams());
        dto.setConstraints(machineLearningTrainingConfig.getConstraints());
        dto.setFeatures(machineLearningTrainingConfig.getFeatures());
        dto.setModelName(machineLearningTrainingConfig.getModelName());

        return subscriber -> new SafeSubscriber<>(new Subscriber<Entity>() {

            @Override
            public void onCompleted() {
                try {

                    logger.info(dto.getAlgorithm() + dto.getModelName());
                    logger.info("N° messaggi in cache:" + entities.size());
                    logger.info("COSTRUZIONE CLASSIFICATORE IN CORSO...");

                    TrainModel trainer = new TrainModel(dto,entities);
                    boolean classifierBuilt = trainer.RunTraining();
                    if (!classifierBuilt) {
                        logger.error("ERRORE: classificatore non costruito!");
                    }
                }
                catch(Exception ex) {
                    System.out.println("ERRORE: " + ex.toString());
                    logger.error("ERRORE: " + ex.toString());
                }

                subscriber.onCompleted();

            }

            @Override
            public void onError(Throwable e) {
                logger.error("ERRORE:" + e.toString());
                subscriber.onError(e);
            }

            //memorizza tutti i messaggi in memoria o su file
            @Override
            public void onNext(Entity entity) {
                logger.info("Entity: " + entity.toString());
                entities.add(entity);
                subscriber.onNext(entity);
            }
        });
    }
}
