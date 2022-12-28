package ru.cft.shift.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String getJson(T message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T tryParse(String messageJson, Class<T> classTo) {
        try {
            return objectMapper.readValue(messageJson, classTo);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
