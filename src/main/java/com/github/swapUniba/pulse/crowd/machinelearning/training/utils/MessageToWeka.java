package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Tag;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.enums.Feature;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

public class MessageToWeka {

    public static Instances getInstancesFromMessages(List<Message> messages, Feature feature, String modelName) {

        Instances result = null;
        List<String> words;
        ArrayList<Attribute> attributes = new ArrayList<>();

        words = getWords(messages,feature);

        Set<String> uniqueWords = new HashSet(words); //effettua la distinct delle parole

        for (String word : uniqueWords) {
            Attribute a = new Attribute(word);
            attributes.add(a);
        }

        Instances instances = new Instances(modelName,attributes,10);

        for (Message m : messages) {

            Instance inst = new DenseInstance(attributes.size()); //nAttributes deve essere già scremato dagli id
            inst.setDataset(instances);
            List<String> wordsInMessage = getWordsFromMessage(m,feature);

            for (String word : wordsInMessage) {
                int attrIndex = attributes.indexOf(word);
                inst.setValue(attrIndex,1);
            }

        }

        return result;
    }


    private static List<String> getWords(List<Message> messages, Feature feature) {

        List<String> result = new ArrayList<>();

        for (Message m : messages) {
            result.addAll(getWordsFromMessage(m,feature));
        }

        return result;

    }

    private static List<String> getWordsFromMessage(Message message, Feature feature) {

        List<String> result = new ArrayList<>();

        if (feature == Feature.TOKEN) {
            List<Token> tokens = message.getTokens();
            for (Token tk : tokens) {
                result.add(tk.getText());
            }
        }
        if (feature == Feature.TAG) {
            Set<Tag> tags = message.getTags();
            for (Tag tg : tags) {
                result.add(tg.getText());
            }
        }

        return result;

    }

}
