package com.practice.easyexam.app.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class QuestionResponseDeserializer implements JsonDeserializer<QuestionResponse> {
    @Override
    public QuestionResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        QuestionResponse response = new QuestionResponse();
        response.setMessage(jsonObject.get("message").getAsString());

        // Parse the test data
        Map<String, Question> testData = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            if (!"message".equals(key)) {
                Question test = context.deserialize(entry.getValue(), Question.class);
                testData.put(key, test);
            }
        }
//        response.setQuestions(testData);

        return response;
    }
}
