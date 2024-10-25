package com.sucheon.jobs.functions;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sucheon.jobs.constant.CommonConstant;
import com.sucheon.jobs.event.PointData;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Map;

import static com.sucheon.jobs.constant.CommonConstant.algLabel;

/**
 * 算法侧打标
 */
public class LabelAlgResultMapFunction implements MapFunction<String, String> {



    @Override
    public String map(String algResult) throws Exception {

        Map<String, Object> map = CommonConstant.objectMapper.readValue(algResult, new TypeReference<Map<String, Object>>(){});
        map.put("origin", algLabel);

        algResult = CommonConstant.objectMapper.writeValueAsString(map);
        return algResult;
    }
}
