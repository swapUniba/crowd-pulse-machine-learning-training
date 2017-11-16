package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import com.github.frapontillo.pulse.crowd.data.entity.*;
import com.github.swapUniba.pulse.crowd.machinelearning.training.MachineLearningTrainingPlugin;
import com.github.swapUniba.pulse.crowd.machinelearning.training.utils.enums.MessageFeatures;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import java.lang.reflect.Method;
import java.text.ParseException;
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

    private static Instances getInstancesFromMessages(List<Message> msgs, String[] fts, String modelName) {

        Instances result = null;
        ArrayList<Attribute> attributes = new ArrayList<>();

        List<Message> messages = filterMessages(msgs,modelName); //elimina i messaggi non etichettati per questo modello

        //bonifica i nomi delle feature prima di avviare il parsing
        List<String> featList = new ArrayList<>();
        List<String> notRecognizedFeat = new ArrayList<>();

        for (String f : fts) {
            boolean recognized = false;
            for (MessageFeatures ft : MessageFeatures.values()) { //individua la feature nell'enum
                if (ft.name().toLowerCase().startsWith(f.toLowerCase())) {
                    featList.add(ft.name());
                    recognized = true;
                    break;
                }
            }

            if (!recognized) {
                notRecognizedFeat.add(f);
            }

        }

        String[] features = new String[featList.size()];
        features = featList.toArray(features);

        List<Attribute> numericAttributes = getNumericAttributes(features);
        List<Attribute> stringAttributes = getStringAttributes(messages,features,modelName);
        List<Attribute> dateAttributes = getDateAttributes(features);
        List<Attribute> unknownAttributes = getUnknownAttributes(messages,notRecognizedFeat);//aggiunge features non codificate, trattandole come token, via reflection, dalla lista notRecognizedFeat

        attributes.addAll(numericAttributes);
        attributes.addAll(stringAttributes);
        attributes.addAll(dateAttributes);

        if (unknownAttributes != null && unknownAttributes.size() > 0) {
            attributes.addAll(unknownAttributes);
        }

        List<String> classValues = getClassValues(messages,modelName);
        Attribute classAttr = new Attribute(classAttributeName,classValues);
        attributes.add(classAttr);

        result = new Instances(modelName,attributes,10);

        for (Message m : messages) {

            Instance inst = new DenseInstance(attributes.size()); //nAttributes deve essere già scremato dagli id
            inst.setDataset(result);

            setUnknownAttrInstanceValue(notRecognizedFeat,unknownAttributes,m,inst); // imposta nell'instance i valori unknown


            for(Attribute attr : attributes) { //dove non c'è l'occorrenza devo mettere 0

                if(!attr.name().toLowerCase().startsWith(classAttributeName.toLowerCase())) {


                    if (attr.name().equalsIgnoreCase(MessageFeatures.cluster_kmeans.name())) {
                        Object val = m.getClusterKmeans();
                        if (val != null) {
                            inst.setValue(attr, m.getClusterKmeans());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.sentiment.name())) {
                        Object sentiment = m.getSentiment();
                        if (sentiment != null) {
                            inst.setValue(attr, m.getSentiment());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.number_cluster.name())) {
                        Object val = m.getCluster();
                        if (val != null) {
                            inst.setValue(attr, m.getCluster());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.language.name())) {
                        Object val = m.getLanguage();
                        if (val != null) {
                            inst.setValue(attr, m.getLanguage());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.shares.name())) {
                        Object val = m.getShares();
                        if (val != null) {
                            inst.setValue(attr, m.getShares());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.favs.name())) {
                        Object val = m.getFavs();
                        if (val != null) {
                            inst.setValue(attr, m.getFavs());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.latitude.name())) {
                        Object val = m.getLatitude();
                        if (val != null) {
                            inst.setValue(attr, m.getLatitude());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.longitude.name())) {
                        Object val = m.getLongitude();
                        if (val != null) {
                            inst.setValue(attr, m.getLongitude());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.text.name())) {
                        Object val = m.getText();
                        if (val != null) {
                            inst.setValue(attr, m.getText());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.source.name())) {
                        Object val = m.getSource();
                        if (val != null) {
                            inst.setValue(attr, m.getSource());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.fromUser.name())) {
                        Object val = m.getFromUser();
                        if (val != null) {
                            inst.setValue(attr, m.getFromUser());
                        }
                    }
                    else if (attr.name().equalsIgnoreCase(MessageFeatures.date.name())) {
                        Date date = m.getDate();
                        if (date != null) {
                            try {
                                inst.setValue(attr,attr.parseDate(attr.formatDate(date.getTime())));
                            } catch (ParseException e) {
                                MachineLearningTrainingPlugin.logger.error("ERRORE: DATA NON RICONOSCIUTA!" + e.toString());
                            }
                        }
                    }
                    else {

                        if (attr.name().startsWith("tg_")) {
                            setPresenceOfAttribute(inst, attr, MessageFeatures.tags, m, modelName);
                        }
                        else if (attr.name().startsWith("tk_")) {
                            setPresenceOfAttribute(inst, attr, MessageFeatures.tokens, m,modelName);
                        }
                        else if (attr.name().startsWith("ru_")) {
                            setPresenceOfAttribute(inst, attr, MessageFeatures.refUsers, m,modelName);
                        }
                        else if (attr.name().startsWith("tu_")) {
                            setPresenceOfAttribute(inst, attr, MessageFeatures.toUsers, m,modelName);
                        }
                        else if (attr.name().startsWith("ct_")) {
                            setPresenceOfAttribute(inst, attr, MessageFeatures.customTags, m,modelName);
                        }
                        else if (attr.name().startsWith("cg_")) {
                            setPresenceOfAttribute(inst, attr, MessageFeatures.categories, m,modelName);
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

        return result;
    }

    private static void setPresenceOfAttribute(Instance inst, Attribute attr, MessageFeatures feature, Message m, String modelName) {
        List<String> wordsInMessage = getWordsFromMessage(m, feature, modelName);
        if (wordsInMessage.indexOf(attr.name()) == -1) {
            try {
                if (inst.value(attr) != 1) {
                    inst.setValue(attr, 0);
                }
            } catch (Exception ex) {
                inst.setValue(attr, 0);
                String a = "";
            }
        } else {
            try {
                inst.setValue(attr, 1);
            } catch (Exception ex) {
                String a = "";// attributo non presente nel messaggio
            }
        }
    }

    private static List<Attribute> getNumericAttributes(String[] features) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            try {

                Attribute attr = null;
                MessageFeatures curFeature = null;
                boolean consider = false;

                for (MessageFeatures ft : MessageFeatures.values()) { //individua la feature nell'enum
                    if (ft.name().toLowerCase().startsWith(feature.toLowerCase())) {
                        curFeature = ft;
                        break;
                    }
                }

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
            catch (Exception ex) {
                MachineLearningTrainingPlugin.logger.error("FEATURE: " + feature + " non riconosciuta!");
            }

        }

        return result;
    }

    private static List<Attribute> getStringAttributes(List<Message> messages, String[] features, String modelName) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            try {

                Attribute attr = null;
                int considerFeature = 0;
                MessageFeatures curFeature = null; //MessageFeatures.valueOf(feature.toLowerCase());

                for (MessageFeatures ft : MessageFeatures.values()) { //individua la feature nell'enum
                    if (ft.name().toLowerCase().startsWith(feature.toLowerCase())) {
                        curFeature = ft;
                        break;
                    }
                }

                // 1 - stringa semplice, 2 - lista di oggetti, 3 - da identificare mediante reflection
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
                else if (curFeature == MessageFeatures.categories) {
                    considerFeature = 2;
                }
                else {
                    considerFeature = 3;

                }

                if (considerFeature == 1) { //Stringa semplice
                    List<String> attrValues = getWords(messages,curFeature,modelName);
                    attr = new Attribute(curFeature.toString(),attrValues);
                    result.add(attr);
                }
                else if (considerFeature == 2) { // Lista di stringhe
                    List<String> attrValues = getWords(messages,curFeature, modelName);
                    for (String attrVal : attrValues) {
                        attr = new Attribute(attrVal);
                        result.add(attr);
                    }
                }

            }

            catch (Exception ex) {
                MachineLearningTrainingPlugin.logger.error("FEATURE: " + feature + " non riconosciuta!");
            }

        }

        return result;
    }

    private static List<Attribute> getDateAttributes(String[] features) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            try {
                Attribute attr = null;
                boolean considerFeature = false;
                MessageFeatures curFeature = null;//MessageFeatures.valueOf(feature.toLowerCase());

                for (MessageFeatures ft : MessageFeatures.values()) { //individua la feature nell'enum
                    if (ft.name().toLowerCase().startsWith(feature.toLowerCase())) {
                        curFeature = ft;
                        break;
                    }
                }

                if (curFeature == MessageFeatures.date) {
                    considerFeature = true;
                }

                if (considerFeature) {
                    attr = new Attribute("date","yyyy-MM-dd HH:mm:ss");
                }

                if (attr != null) {
                    result.add(attr);
                }
            }
            catch (Exception ex) {
                MachineLearningTrainingPlugin.logger.error("FEATURE: " + feature + " non riconosciuta!");
            }

        }

        return result;
    }

    private static List<String> getWords(List<Message> messages, MessageFeatures feature, String modelName) {

        List<String> result = new ArrayList<>();

        for (Message m : messages) {
            Set<String> words = new HashSet(getWordsFromMessage(m,feature,modelName));
            result.addAll(words);
        }

        Set<String> words = new HashSet<>(result);
        List<String> output = new ArrayList<>();
        output.addAll(words);
        return output;

    }

    private static List<Attribute> getUnknownAttributes(List<Message> msgs, List<String> features) {

        List<Attribute> result = new ArrayList<>();

        for (String feature : features) {

            for (Message msg : msgs) {

                try {

                    //Method method;
                    //method = msg.getClass().getMethod(feature);
                    List<String> featList = null;
                    Method method = ReflectionUtils.getGettersMethods(Message.class, feature);
                    if (method != null) {
                        featList = (List<String>) method.invoke(msg, new Object[] {});
                    }

                    for (String s : featList) {
                        Attribute attr = new Attribute("ft_" + s);
                        result.add(attr);
                    }
                }
                catch (Exception ex) {
                    //MachineLearningTrainingPlugin.logger.error("Feature: " + feature + " non riconosciuta!");
                }
            }

        }

        return result;
    }

    private static List<String> getWordsFromUnknownAttribute(Message msg, String feature) {

        List<String> result = new ArrayList<>();

        try {

            List<String> featList = null;
            Method method = ReflectionUtils.getGettersMethods(Message.class, feature);
            if (method != null) {
                featList = (List<String>) method.invoke(msg, new Object[] {});
            }

            for (String s : featList) {
                result.add(s);
            }
        }
        catch (Exception ex) {
            //MachineLearningTrainingPlugin.logger.error("Feature: " + feature + " non riconosciuta!");
        }

        return result;
    }

    private static void setUnknownAttrInstanceValue(List<String> notRecognizedFeat, List<Attribute> unknownAttributes, Message m, Instance inst) {

        // tratta le feature non riconosciute come liste di stringhe
        try {
            if (notRecognizedFeat != null && notRecognizedFeat.size() > 0) {
                for (String feature : notRecognizedFeat) {
                    List<String> unknownAttributeValues = getWordsFromUnknownAttribute(m,feature);

                    Attribute unknownAttr = null;
                    Iterator<Attribute> enUnknownAttr = unknownAttributes.iterator();
                    while (enUnknownAttr.hasNext()) {
                        Attribute curAttr = enUnknownAttr.next();
                        if (curAttr.name().toLowerCase().equalsIgnoreCase("ft_" + feature.toLowerCase())) {
                            unknownAttr = curAttr;
                            break;
                        }
                    }

                    if (unknownAttributeValues.indexOf(unknownAttr.name()) == -1) {
                        if (inst.value(unknownAttr) != 1) {
                            inst.setValue(unknownAttr, 0);
                        }
                    } else {
                        inst.setValue(unknownAttr, 1);
                    }
                }
            }
        }
        catch(Exception ex) {
        }
    }

    private static List<String> getWordsFromMessage(Message message, MessageFeatures feature, String modelName) {

        List<String> result = new ArrayList<>();

        if (feature == MessageFeatures.tokens) {
            List<Token> tokens = message.getTokens();
            for (Token tk : tokens) {
                if (!tk.isStopWord()) {
                    result.add("tk_" + tk.getText());
                }
            }
        }
        else if (feature == MessageFeatures.tags) {
            Set<Tag> tags = message.getTags();
            for (Tag tg : tags) { //ESCLUDE I TAG DI TRAINING E DI TESTING
                if (!tg.isStopWord()) {
                    if (!tg.getText().toLowerCase().startsWith("training_" + modelName) && !tg.getText().toLowerCase().startsWith("testing_" + modelName)) {
                        result.add("tg_" + tg.getText());
                    }
                }
            }
        }
        else if (feature == MessageFeatures.toUsers) {
            List<String> users = message.getToUsers();
            for (String usr : users) {
                users.add("tu_" + usr);
            }
        }
        else if (feature == MessageFeatures.refUsers) {
            List<String> users = message.getRefUsers();
            for (String usr : users) {
                users.add("ru_" + usr);
            }
        }
        else if (feature == MessageFeatures.customTags) {
            List<String> customTags = message.getCustomTags();
            for (String ct : customTags) {
                result.add("ct_" + ct);
            }
        }
        else if (feature == MessageFeatures.categories) {
            try {
                Set<Tag> tags = message.getTags();
                if (tags != null) {
                    for (Tag tg : tags) {
                        if (tg.getCategories() != null) {
                            for (Category ct : tg.getCategories()) {
                                if (!ct.isStopWord()) {
                                    String[] ctgs = ct.getText().split(":");
                                    result.add("cg_" + ctgs[1]);
                                }
                            }
                        }
                    }

                }
            }
            catch (Exception ex) {
                MachineLearningTrainingPlugin.logger.error("ERRORE CATEGORIE: " + ex.toString());
            }
        }
        else if (feature == MessageFeatures.text) {
            result.add(message.getText());
        }
        else if (feature == MessageFeatures.source) {
            result.add(message.getSource());
        }
        else if (feature == MessageFeatures.fromUser) {
            result.add(message.getFromUser());
        }
        else if (feature == MessageFeatures.parent) {
            result.add(message.getParent());
        }
        else if (feature == MessageFeatures.language) {
            result.add(message.getLanguage());
        }

        return result;

    }

    public static Instance getSingleInstanceFromMessage(Message message, MessageFeatures feature, String modelName) {

        List<String> words;
        ArrayList<Attribute> attributes = new ArrayList<>();

        List<Message> msgs = new ArrayList<>();
        msgs.add(message);
        words = getWords(msgs,feature,modelName);

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
