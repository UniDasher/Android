package com.uni.unidasher.data.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class Constants {

    public static final String KEY_ASSESSMENT_ID = "assessment_id";
    public static final String KEY_CHALLENGE_ID = "challenge_id";
    public static final String KEY_SEQUENCE_ID = "sequence_id";
    public static final String KEY_QUESTION_ID = "question_id";
    public static final String NOT_SET = "not set";

    public static final Gson GSON_RECEIVED;
    public static final Gson GSON_SENT;
    static {
        ExclusionStrategy strategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getAnnotation(SerializedName.class) == null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
        GSON_RECEIVED = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(strategy)
                .create();
        GSON_SENT = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(strategy)
                .create();
    }

}
