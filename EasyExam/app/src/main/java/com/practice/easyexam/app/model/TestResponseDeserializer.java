package com.practice.easyexam.app.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TestResponseDeserializer implements JsonDeserializer<TestResponse> {
    @Override
    public TestResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        TestResponse response = new TestResponse();
        response.setMessage(jsonObject.get("message").getAsString());

        // Parse the test data
        Map<String, Test> testData = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            if (!"message".equals(key)) {
                Test test = context.deserialize(entry.getValue(), Test.class);
                testData.put(key, test);
            }
        }
        response.setTestData(testData);

        return response;
    }
}

