package com.sucheon.jobs.event;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.functions.KeySelector;

import java.util.Optional;

/**
 * 对于算法数据和iot数据进行区分
 * 如果合流之后的数据没有设置告警组， 则意味着是从测点上报的数据
 * key取0 ，代表不分区
 */
public class DistrubuteKeyFunction implements KeySelector<DynamicKeyedBean , Object> {


    @Override
    public Object getKey(DynamicKeyedBean dynamicKeyedBean) throws Exception {

        if (dynamicKeyedBean == null || StringUtils.isBlank(dynamicKeyedBean.getAlgGroup())){
            return 0;
        } else {
            return dynamicKeyedBean.getAlgGroup();
        }
    }
}
