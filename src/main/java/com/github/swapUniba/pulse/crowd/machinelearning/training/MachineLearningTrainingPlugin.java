package com.github.swapUniba.pulse.crowd.machinelearning.training;

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

public class MachineLearningTrainingPlugin extends IPlugin<Message,Message,MachineLearningTrainingConfig> {

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
    protected Observable.Operator<Message, Message> getOperator(MachineLearningTrainingConfig machineLearningTrainingConfig) {

        List<Message> messages = new ArrayList<>();
        MachineLearningTrainingConfigDTO dto = new MachineLearningTrainingConfigDTO();
        dto.setAlgorithm(machineLearningTrainingConfig.getAlgorithm());
        dto.setAlgorithmParams(machineLearningTrainingConfig.getAlgorithmParams());
        dto.setConstraints(machineLearningTrainingConfig.getConstraints());
        dto.setFeature(machineLearningTrainingConfig.getFeature());
        dto.setModelName(machineLearningTrainingConfig.getModelName());

        return subscriber -> new SafeSubscriber<>(new Subscriber<Message>() {

            //quando il flusso dei messaggi è finito costruisci il modello
            @Override
            public void onCompleted() {
                try {

                    logger.info(dto.getAlgorithm() + dto.getModelName());
                    logger.info("N° messaggi in cache:" + messages.size());
                    logger.info("COSTRUZIONE CLASSIFICATORE IN CORSO...");
                    TrainModel trainer = new TrainModel(dto,messages);
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
            public void onNext(Message message) {
                logger.error("MESSAGGIO: " + message.toString());
                messages.add(message);
                subscriber.onNext(message);
            }
        });
    }
}
