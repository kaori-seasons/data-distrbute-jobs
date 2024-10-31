package com.sucheon.jobs;

import com.alibaba.fastjson.JSON;
import com.sucheon.jobs.event.PointTree;

public class PointTreeSerialbleTest {

    public static void main(String[] args) {
        String s = "{\n" +
                "    \"code\": 112,\n" +
                "    \"fields\":[\t\t\t\n" +
                "       {\n" +
                "          \"key\":\"feature1\",\t\n" +
                "          \"point_id\":1,\t\t\n" +
                "          \"instance_id\":1,\t\t\n" +
                "          \"group\":\"1\"\n" +
                "       }\n" +
                "      ],\n" +
                "      \"timestamp\": 1730096790,\n" +
                "      \"topicList\": [\"iot_kv_main\"],\n" +
                "      \"algGroup\": \"\"\n" +
                "  }";
        PointTree pointTree = JSON.parseObject(s, PointTree.class);
        System.out.println(pointTree);
    }
}
