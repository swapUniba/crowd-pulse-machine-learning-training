package com.github.swapUniba.pulse.crowd.machinelearning.training;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        List<Message> msgs = new ArrayList<>();

        for (int i = 0; i < 300; i++) {

            Message msg = new Message();
            Random rndm = new Random();
            int nTokens = rndm.nextInt(5) + 2;

            List<Token> tokens = new ArrayList<>();

            for (int ii = 0; ii < nTokens;ii++) {
                tokens.add(new Token(getRandomString()));
            }

            msg.setTokens(tokens);
            msgs.add(msg);
        }

        MachineLearningTrainingConfig mlcfg = new MachineLearningTrainingConfig();
        mlcfg.setAlgorithm("J48");
        mlcfg.setFeature("token");
        mlcfg.setModelName("modello");
        TrainModel trainer = new TrainModel(mlcfg,msgs);
        trainer.RunTraining();

    }

    private static String getRandomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(10)+3; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

}
