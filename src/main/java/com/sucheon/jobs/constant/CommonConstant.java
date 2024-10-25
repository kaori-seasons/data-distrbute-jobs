package com.sucheon.jobs.constant;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class CommonConstant {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static final List<String> ckTopicList = new ArrayList<String>() { {add("ck_iot_main");} {add("ck_alg_main");} };

    public static final String algLabel = "alg-data";

    public static final String iotLabel = "iot-data";
}
