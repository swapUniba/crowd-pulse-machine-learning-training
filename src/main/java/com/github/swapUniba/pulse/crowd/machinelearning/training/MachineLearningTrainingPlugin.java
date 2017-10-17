package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.util.PulseLogger;
import org.apache.logging.log4j.Logger;
import rx.Observable;

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
        return null;
    }
}
