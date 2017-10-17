package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.spi.IPlugin;
import rx.Observable;

public class MachineLearningTrainingPlugin extends IPlugin<Message,Message,MachineLearningTrainingConfig> {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public MachineLearningTrainingConfig getNewParameter() {
        return null;
    }

    @Override
    protected Observable.Operator<Message, Message> getOperator(MachineLearningTrainingConfig machineLearningTrainingConfig) {
        return null;
    }
}
