package com.sucheon.jobs.functions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sucheon.jobs.constant.CommonConstant;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Map;

import static com.sucheon.jobs.constant.CommonConstant.iotLabel;

public class LabelIotResultMapFunction implements MapFunction<String, String> {




    @Override
    public String map(String algResult) throws Exception {

        Map<String, Object> map = CommonConstant.objectMapper.readValue(algResult, new TypeReference<Map<String, Object>>(){});
        map.put("origin", iotLabel);

        algResult = CommonConstant.objectMapper.writeValueAsString(map);
        return algResult;
    }
}
