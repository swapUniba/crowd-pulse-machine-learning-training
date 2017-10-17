package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.google.gson.JsonElement;


public class MachineLearningTrainingConfig implements IPluginConfig<MachineLearningTrainingConfig>{


    @Override
    public MachineLearningTrainingConfig buildFromJsonElement(JsonElement jsonElement) {
        return PluginConfigHelper.buildFromJson(jsonElement, MachineLearningTrainingConfig.class);
    }
}
