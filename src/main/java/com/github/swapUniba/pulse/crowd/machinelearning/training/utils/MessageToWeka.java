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

    private static String classAttributeName = "predClass";

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

        List<String> classValues = getClassValues(messages);
        Attribute classAttr = new Attribute(classAttributeName,classValues);
        attributes.add(classAttr);

        result = new Instances(modelName,attributes,10);

        //SALVATAGGIO DELLA STRUTTURA!
        WekaModelHandler.SaveInstanceStructure(result,modelName);

        for (Message m : messages) {

            Instance inst = new DenseInstance(attributes.size()); //nAttributes deve essere già scremato dagli id
            inst.setDataset(result);
            List<String> wordsInMessage = getWordsFromMessage(m,feature);

            //dove non c'è l'occorrenza devo mettere 0
            for(Attribute attr : attributes) {

                if(!attr.name().equalsIgnoreCase(classAttributeName)) {

                    if (wordsInMessage.indexOf(attr.name()) == -1) {
                        inst.setValue(attr, 0);
                    } else {
                        inst.setValue(attr, 1);
                    }
                }
            }

            inst.setValue(attributes.size()-1,m.getParent());
            result.add(inst);

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

    public static Instance getSingleInstanceFromMessage(Message message, Feature feature) {

        List<String> words;
        ArrayList<Attribute> attributes = new ArrayList<>();

        List<Message> msgs = new ArrayList<>();
        msgs.add(message);
        words = getWords(msgs,feature);

        Set<String> uniqueWords = new HashSet(words); //effettua la distinct delle parole

        for (String word : uniqueWords) {
            Attribute a = new Attribute(word);
            attributes.add(a);
        }
        List<String> classValues = new ArrayList<>();
        classValues.add("?");
        Attribute classAttr = new Attribute("predictedClass",classValues);
        attributes.add(classAttr);

        Instance inst = new DenseInstance(attributes.size()); //nAttributes deve essere già scremato dagli id

        for (String word : words) {
            int attrIndex = attributes.indexOf(new Attribute(word));
            inst.setValue(attrIndex,1);
        }

        return inst;

    }

    private static List<String> getClassValues(List<Message> messages) {

        List<String> result = new ArrayList<>();
        List<String> classValues = new ArrayList<>();

        for (Message m : messages) {
            classValues.add(m.getParent());
        }

        Set<String> distClass = new HashSet<>(classValues);
        result.addAll(distClass);

        return result;

    }

}
