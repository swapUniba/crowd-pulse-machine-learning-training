package com.github.swapUniba.pulse.crowd.machinelearning.training.utils;

import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Method getGettersMethods(Class aClass, String featureName){

        Method[] methods = aClass.getMethods();

        Method result = null;
        for(Method method : methods){
            if(isGetter(method) && method.getName().contains(featureName)) result = method;
        }

        return result;

    }

    public static Method getSettersMethods(Class aClass, String featureName){

        Method[] methods = aClass.getMethods();
        Method result = null;

        for(Method method : methods){
            if(isSetter(method) && method.getName().contains(featureName)) result = method;
        }

        return result;

    }

    public static boolean isGetter(Method method){
        if(!method.getName().startsWith("get"))      return false;
        if(method.getParameterTypes().length != 0)   return false;
        if(void.class.equals(method.getReturnType())) return false;
        return true;
    }

    public static boolean isSetter(Method method){
        if(!method.getName().startsWith("set")) return false;
        if(method.getParameterTypes().length != 1) return false;
        return true;
    }

}
