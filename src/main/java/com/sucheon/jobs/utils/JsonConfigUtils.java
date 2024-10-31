package com.sucheon.jobs.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonConfigUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static ObjectMapper getObjectMapper(){
        //默认转驼峰
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        return objectMapper;
    }
}
