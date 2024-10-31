package com.sucheon.jobs.functions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sucheon.jobs.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sucheon.jobs.constant.CommonConstant.iotLabel;

@Slf4j
public class LabelIotResultMapFunction implements MapFunction<String, String> {




    @Override
    public String map(String algResult) throws Exception {

        Map<String, Object> map = new HashMap<>();
        try {
            map = CommonConstant.objectMapper.readValue(algResult, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e){
            log.error(String.format("当前边缘端数据解析错误,源数据为: %s", algResult));
        }
        map.put("origin", iotLabel);

        algResult = CommonConstant.objectMapper.writeValueAsString(map);
        return algResult;
    }
}
