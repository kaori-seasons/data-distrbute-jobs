package com.sucheon.jobs.functions;

import com.alibaba.fastjson.JSON;
import com.sucheon.jobs.event.PointData;
import com.sucheon.jobs.event.PointTree;
import org.apache.flink.api.common.functions.MapFunction;

public class Json2EventBeanMapFunction implements MapFunction<String, PointData> {


    @Override
    public PointData map(String value) throws Exception {

        PointData pointTree = null;
        try {
            pointTree = JSON.parseObject(value, PointData.class);
        } catch (Exception ex) {

        }
        return pointTree;
    }
}
