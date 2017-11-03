package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import com.github.frapontillo.pulse.crowd.data.entity.Entity;
import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Tag;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.swapUniba.pulse.crowd.machinelearning.training.MachineLearningTrainingConfig;
import com.github.swapUniba.pulse.crowd.machinelearning.training.modelTraining.TrainModel;
import org.joda.time.format.ISODateTimeFormat;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        boolean testClassification = true;

        if (testClassification) {
            TestMessageClassification();
        }
        else {
            TestRegression();
        }
    }


    private static void TestMessageClassification() {

        List<Entity> msgs = new ArrayList<>();

        for (int i = 0; i < 200; i++) {

            Message msg = new Message();
            Random rndm = new Random();
            int nTokens = rndm.nextInt(3) + 1;
            int nTags = rndm.nextInt(3) + 1;

            List<Token> tokens = new ArrayList<>();
            Set<Tag> tags = new HashSet<>();
            List<String> customTags = new ArrayList<>();

            for (int ii = 0; ii < nTokens;ii++) {
                tokens.add(new Token(getRandomString()));
            }

            for (int ii = 0; ii < nTags;ii++) {
                Tag tag = new Tag();
                tag.setText(getRandomString());
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

            customTags.add(getRandomString());

            msg.setoId(getRandomString());
            msg.setLatitude(rndm.nextDouble());
            msg.setLongitude(rndm.nextDouble());
            msg.setLanguage("en");
            msg.setSentiment(rndm.nextDouble());
            msg.setParent(pol);
            msg.setDate(new Date());
            msg.setFavs(rndm.nextInt(20));
            msg.setShares(rndm.nextInt(20));
            msg.setFromUser(getRandomString());
            Tag tag = new Tag();
            tag.setText("training_modello_class_" + pol);
            tags.add(tag);
            msg.setCustomTags(customTags);
            msg.setTokens(tokens);
            msg.setTags(tags);
            msgs.add(msg);
        }

        MachineLearningTrainingConfig mlcfg = new MachineLearningTrainingConfig();
        mlcfg.setPrintFile(true);
        mlcfg.setAlgorithm("J48");
        mlcfg.setFeatures(new String[]{"oId","customTags","date","favs","shares","fromuser","token","tags","sentiment","language","latitude","longitude"});
        mlcfg.setModelName("modello");
        mlcfg.setAlgorithmParams("-R");
        //mlcfg.setRegressionAttribute("favs");
        mlcfg.setEvaluation("-no-cv"); //-no-cv per usare l'intero trainingset, -x 10 per il 10FCV, -percentage-split 70, per usare il 70% come training e il 30 testing
        TrainModel trainer = new TrainModel(mlcfg,msgs);
        trainer.RunTraining();
    }

    private static void TestRegression() {

        List<Entity> msgs = new ArrayList<>();

        for (int i = 0; i < 200; i++) {

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
                tag.setText(getRandomString());
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
            msg.setParent(pol);
            msg.setFavs(rndm.nextInt(20));
            msg.setShares(rndm.nextInt(20));
            msg.setFromUser(getRandomString());
            Tag tag = new Tag();
            tag.setText("training_regressione_class_" + pol);
            tags.add(tag);
            msg.setTokens(tokens);
            msg.setTags(tags);
            msgs.add(msg);
        }

        MachineLearningTrainingConfig mlcfg = new MachineLearningTrainingConfig();
        mlcfg.setPrintFile(true);
        mlcfg.setAlgorithm("LinearRegression");
        mlcfg.setFeatures(new String[]{"favs","shares","fromuser","token","tags","sentiment","language","latitude","longitude"});
        mlcfg.setModelName("regressione");
        mlcfg.setAlgorithmParams("");
        mlcfg.setRegressionAttribute("shares");
        mlcfg.setEvaluation("-x 5"); //-no-cv per usare l'intero trainingset, -x 10 per il 10FCV, -percentage-split 70, per usare il 70% come training e il 30 testing
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
