package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Tag;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.swapUniba.pulse.crowd.machinelearning.training.DTO.MachineLearningTrainingConfigDTO;
import com.github.swapUniba.pulse.crowd.machinelearning.training.modelTraining.TrainModel;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        boolean testClassification = true;

        if (testClassification) {
            TestMessageClassification();
        }
        else {
            //TestRegression();
        }
    }


    private static void TestMessageClassification() {

        List<Message> msgs = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            Message msg = new Message();
            Random rndm = new Random();
            int nTokens = rndm.nextInt(3) + 1;
            int nTags = rndm.nextInt(3) + 1;

            List<Token> tokens = new ArrayList<>();
            Set<Tag> tags = new HashSet<>();

            for (int ii = 0; ii < nTokens;ii++) {
                tokens.add(new Token(getRandomString()));
            }

            for (int ii = 0; ii < nTags;ii++) {
                Tag tag = new Tag();
                tag.setText("#" + getRandomString());
                tags.add(tag);
            }

            int rn = rndm.nextInt(2);
            String pol = "";
            if (rn > 0) {
                pol = "m5s";
            }
            else {
                pol = "pd";
            }

            msg.setLatitude(rndm.nextDouble());
            msg.setLongitude(rndm.nextDouble());
            msg.setLanguage("en");
            msg.setSentiment(rndm.nextDouble());
            //msg.setParent(pol);
            Tag tag = new Tag();
            tag.setText("training_modello_class_" + pol);
            tags.add(tag);
            msg.setTokens(tokens);
            msg.setTags(tags);
            msgs.add(msg);
        }

        MachineLearningTrainingConfigDTO mlcfg = new MachineLearningTrainingConfigDTO();
        mlcfg.setAlgorithm("J48");
        mlcfg.setFeatures(new String[]{"tokens","tags","sentiment","language","latitude","longitude"});
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
