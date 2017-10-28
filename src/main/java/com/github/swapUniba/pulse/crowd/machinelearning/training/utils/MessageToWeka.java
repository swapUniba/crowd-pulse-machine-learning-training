package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import com.github.frapontillo.pulse.crowd.data.entity.*;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.enums.MessageFeatures;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import java.util.*;

public class MessageToWeka {

    private static String classAttributeName = "predictedClass";

    public static Instances getInstancesFromEntities(List<Entity> entities, String[] features, String modelName) {

        Instances result = null;

        if (entities.size() > 0) {

            Entity entity = entities.get(0);
            Class entityCls = entity.getClass();
            if (entityCls == Message.class) {
                List<? super Message> ent = entities;
                List<Message> messages = (List<Message>)ent;
                result = getInstancesFromMessages(messages,features,modelName);
            }
            else if (entityCls == Profile.class) {
                // creare metodi di elaborazione per i profili
            }

        }

        return result;
    }

    private static Instances getInstancesFromMessages(List<Message> msgs, String[] features, String modelName) {

        Instances result = null;
        List<String> words;
        ArrayList<Attribute> attributes = new ArrayList<>();

        List<Message> messages = filterMessages(msgs,modelName); //elimina i messaggi che non hanno la classe corretta

        List<Attribute> numericAttributes = getNumericAttributes(features);
        List<Attribute> stringAttributes = getStringAttributes(messages,features);
        List<Attribute> dateAttributes = getDateAttributes(features);

        attributes.addAll(numericAttributes);
        attributes.addAll(stringAttributes);
        attributes.addAll(dateAttributes);

        List<String> classValues = getClassValues(messages,modelName);
        Attribute classAttr = new Attribute(classAttributeName,classValues);
        attributes.add(classAttr);

        result = new Instances(modelName,attributes,10);
        result.setClassIndex(result.numAttributes() - 1);

        //SALVATAGGIO DELLA STRUTTURA!
        WekaModelHandler.SaveInstanceStructure(result,modelName);

        for (Message m : messages) {

            Instance inst = new DenseInstance(attributes.size()); //nAttributes deve essere già scremato dagli id
            inst.setDataset(result);

            for (String feature : features) {

                for(Attribute attr : attributes) { //dove non c'è l'occorrenza devo mettere 0

                    if(!attr.name().toLowerCase().startsWith(classAttributeName.toLowerCase())) {

                        MessageFeatures msgFeature = MessageFeatures.valueOf(feature.toLowerCase());

                        if (msgFeature == MessageFeatures.cluster_kmeans && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getClusterKmeans());
                        }
                        else if (msgFeature == MessageFeatures.sentiment && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getSentiment());
                        }
                        else if (msgFeature == MessageFeatures.number_cluster && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getCluster());
                        }
                        else if (msgFeature == MessageFeatures.language && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getLanguage());
                        }
                        else if (msgFeature == MessageFeatures.shares && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getShares());
                        }
                        else if (msgFeature == MessageFeatures.favs && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getFavs());
                        }
                        else if (msgFeature == MessageFeatures.latitude && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getLatitude());
                        }
                        else if (msgFeature == MessageFeatures.longitude && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getLongitude());
                        }
                        else if (msgFeature == MessageFeatures.text && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getText());
                        }
                        else if (msgFeature == MessageFeatures.source && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getSource());
                        }
                        else if (msgFeature == MessageFeatures.fromUser && attr.name().equalsIgnoreCase(msgFeature.toString())) {
                            inst.setValue(attr, m.getFromUser());
                        }
                        else {
                            if ((msgFeature == MessageFeatures.tags || msgFeature == MessageFeatures.tokens || msgFeature == MessageFeatures.toUsers
                                    || msgFeature == MessageFeatures.refUsers) && !Arrays.asList(features).contains(attr.name())) {

                                List<String> wordsInMessage = getWordsFromMessage(m, MessageFeatures.valueOf(feature.toLowerCase()));
                                if (wordsInMessage.indexOf(attr.name()) == -1) {
                                    if (inst.value(attr) != 1) {
                                        inst.setValue(attr, 0);
                                    }
                                } else {
                                    inst.setValue(attr, 1);
                                }
                            }
                        }
                    }
                }
            }
            String classValue = getMessageClassLabel(m,modelName);
            if (!classValue.equalsIgnoreCase("")) {
                inst.setValue(classAttr,classValue);
                result.add(inst);
            }

        }

        WekaModelHandler.saveFeatures(features,modelName);
        WekaModelHandler.SaveTrainingSet(result,modelName);

        return result;
    }

    private static List<Attribute> getNumericAttributes(String[] features) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            Attribute attr = null;
            MessageFeatures curFeature = MessageFeatures.valueOf(feature.toLowerCase());
            boolean consider = false;

            if (curFeature == MessageFeatures.cluster_kmeans) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.number_cluster) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.number_cluster) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.sentiment) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.shares) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.favs) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.latitude) {
                consider = true;
            }
            else if (curFeature == MessageFeatures.longitude) {
                consider = true;
            }

            if (consider) {
                attr = new Attribute(curFeature.toString().toLowerCase());
            }

            if (attr != null) {
                result.add(attr);
            }

        }

        return result;
    }

    private static List<Attribute> getStringAttributes(List<Message> messages, String[] features) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            Attribute attr = null;
            int considerFeature = 0;
            MessageFeatures curFeature = MessageFeatures.valueOf(feature.toLowerCase());

            if (curFeature == MessageFeatures.text) {
                considerFeature = 1;
            }
            else if (curFeature == MessageFeatures.source) {
                considerFeature = 1;
            }
            else if (curFeature == MessageFeatures.tokens) {
                considerFeature = 2;
            }
            else if (curFeature == MessageFeatures.tags) {
                considerFeature = 2;
            }
            else if (curFeature == MessageFeatures.fromUser) {
                considerFeature = 1;
            }
            else if (curFeature == MessageFeatures.parent) {
                considerFeature = 1;
            }
            else if (curFeature == MessageFeatures.language) {
                considerFeature = 1;
            }
            else if (curFeature == MessageFeatures.customTags) {
                considerFeature = 2;
            }
            else if (curFeature == MessageFeatures.toUsers) {
                considerFeature = 2;
            }
            else if (curFeature == MessageFeatures.refUsers) {
                considerFeature = 2;
            }

            if (considerFeature == 1) { //Stringa semplice
                List<String> attrValues = getWords(messages,curFeature);
                attr = new Attribute(curFeature.toString(),attrValues);
                result.add(attr);
            }
            else if (considerFeature == 2) { // Lista di stringhe
                List<String> attrValues = getWords(messages,curFeature);
                for (String attrVal : attrValues) {
                    attr = new Attribute(attrVal);
                    result.add(attr);
                }
            }

        }

        return result;
    }

    private static List<Attribute> getDateAttributes(String[] features) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            Attribute attr = null;
            boolean considerFeature = false;
            MessageFeatures curFeature = MessageFeatures.valueOf(feature.toLowerCase());

            if (curFeature == MessageFeatures.date) {
                considerFeature = true;
            }

            if (considerFeature) {
                attr = new Attribute("dateTime","yyyy-MM-dd HH:mm");
            }

            if (attr != null) {
                result.add(attr);
            }

        }

        return result;
    }

    private static List<String> getWords(List<Message> messages, MessageFeatures feature) {

        List<String> result = new ArrayList<>();

        for (Message m : messages) {
            Set<String> words = new HashSet(getWordsFromMessage(m,feature));
            result.addAll(words);
        }

        Set<String> words = new HashSet<>(result);
        List<String> output = new ArrayList<>();
        output.addAll(words);
        return output;

    }

    private static List<String> getWordsFromMessage(Message message, MessageFeatures feature) {

        List<String> result = new ArrayList<>();

        if (feature == MessageFeatures.tokens) {
            List<Token> tokens = message.getTokens();
            for (Token tk : tokens) {
                result.add("tk_" + tk.getText());
            }
        }
        if (feature == MessageFeatures.tags) {
            Set<Tag> tags = message.getTags();
            for (Tag tg : tags) { //ESCLUDE I TAG DI TRAINING E DI TESTING
                if (!tg.getText().toLowerCase().startsWith("training_") && !tg.getText().toLowerCase().startsWith("testing_")) {
                    result.add("tg_" + tg.getText());
                }
            }
        }
        if (feature == MessageFeatures.toUsers) {
            List<String> users = message.getToUsers();
            for (String usr : users) {
                users.add("tu_" + usr);
            }
        }
        if (feature == MessageFeatures.refUsers) {
            List<String> users = message.getRefUsers();
            for (String usr : users) {
                users.add("ru_" + usr);
            }
        }
        if (feature == MessageFeatures.text) {
            result.add(message.getText());
        }
        if (feature == MessageFeatures.source) {
            result.add(message.getSource());
        }
        if (feature == MessageFeatures.fromUser) {
            result.add(message.getFromUser());
        }
        if (feature == MessageFeatures.parent) {
            result.add(message.getParent());
        }
        if (feature == MessageFeatures.language) {
            result.add(message.getLanguage());
        }

        return result;

    }

    public static Instance getSingleInstanceFromMessage(Message message, MessageFeatures feature) {

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

    private static List<String> getClassValues(List<Message> messages, String modelName) {

        List<String> result = new ArrayList<>();
        List<String> classValues = new ArrayList<>();

        for (Message m : messages) {

            for (Tag tag : m.getTags()) {

                if (tag.getText().toLowerCase().startsWith("training_" + modelName + "_class_")) {
                    classValues.add(tag.getText());
                }
            }
        }

        Set<String> distClass = new HashSet<>(classValues);
        result.addAll(distClass);

        return result;

    }

    private static List<Message> filterMessages(List<Message> messages, String modelName) {

        List<Message> result = new ArrayList<>();
        String classPattern = "training_" + modelName + "_class_";

        for (Message msg : messages) {

            Set<Tag> msgTags = msg.getTags();

            if (msgTags != null) {

                for (Tag tag : msgTags) {

                    if (tag.getText().toLowerCase().startsWith(classPattern.toLowerCase())) {
                        result.add(msg);
                    }

                }

            }

        }

        return result;

    }

    private static String getMessageClassLabel(Message message, String modelName) {

        String result = "";
        String classPattern = "training_"+modelName+"_class_";
        for (Tag tag : message.getTags()) {
            if (tag.getText().toLowerCase().startsWith(classPattern.toLowerCase())) {
                result = tag.getText().toLowerCase();
                break;
            }
        }

        return result;
    }
}
