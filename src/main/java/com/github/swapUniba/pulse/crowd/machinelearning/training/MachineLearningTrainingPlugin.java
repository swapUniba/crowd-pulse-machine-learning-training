package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.util.PulseLogger;
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
        return subscriber -> new SafeSubscriber<>(new Subscriber<Message>() {

            List<Message> messages = new ArrayList<>();

            //quando il flusso dei messaggi è finito costruisci il modello
            @Override
            public void onCompleted() {

                TrainModel trainer = new TrainModel(machineLearningTrainingConfig,messages);
                boolean classifierBuilt = trainer.RunTraining();
                if (!classifierBuilt) {
                    logger.error("ERRORE: classificatore non costruito!");
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
                messages.add(message);
                subscriber.onNext(message);
            }
        });
    }
}
