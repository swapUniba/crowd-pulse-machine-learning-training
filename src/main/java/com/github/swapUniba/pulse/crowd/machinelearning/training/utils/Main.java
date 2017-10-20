package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.swapUniba.pulse.crowd.machinelearning.training.DTO.MachineLearningTrainingConfigDTO;
import com.github.swapUniba.pulse.crowd.machinelearning.training.modelTraining.TrainModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        List<Message> msgs = new ArrayList<>();


        for (int i = 0; i < 100; i++) {

            Message msg = new Message();
            Random rndm = new Random();
            int nTokens = rndm.nextInt(3) + 1;

            List<Token> tokens = new ArrayList<>();

            for (int ii = 0; ii < nTokens;ii++) {
                tokens.add(new Token(getRandomString()));
            }

            int rn = rndm.nextInt(2);
            String pol = "";
            if (rn > 0) {
                pol = "m5s";
            }
            else {
                pol = "pd";
            }

            msg.setParent(pol);
            msg.setTokens(tokens);
            msgs.add(msg);
        }

        MachineLearningTrainingConfigDTO mlcfg = new MachineLearningTrainingConfigDTO();
        mlcfg.setAlgorithm("J48");
        mlcfg.setFeature("token");
        mlcfg.setModelName("modello");
        mlcfg.setAlgorithmParams("-R");
        TrainModel trainer = new TrainModel(mlcfg,msgs);
        trainer.RunTraining();
    }

    private static String getRandomString() {
        //char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] chars = "abc".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(2)+1; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

}
