package com.sucheon.jobs.functions;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sucheon.jobs.constant.CommonConstant;
import com.sucheon.jobs.event.*;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import static com.sucheon.jobs.utils.InternalTypeUtils.iaAllFieldsNull;

/**
 * 规则匹配是否复合算法组的点位树json配置
 */
public class RuleMatchKeyedProcessFunction extends KeyedBroadcastProcessFunction<String, DynamicKeyedBean, PointTree, RuleMatchResult> {



    @Override
    public void processElement(DynamicKeyedBean dynamicKeyedBean, KeyedBroadcastProcessFunction<String, DynamicKeyedBean, PointTree, RuleMatchResult>.ReadOnlyContext readOnlyContext, Collector<RuleMatchResult> collector) throws Exception {

        if (dynamicKeyedBean == null) {
            return;
        }

        PointData pointData = dynamicKeyedBean.getPointData();
        if (pointData == null) {
            return;
        }

        // 清空来源和节点编号
        pointData.setOrigin(null);
        pointData.setCode(null);
        // 边缘端从默认配置取数据， 算法侧从广播变量取数据
        if (dynamicKeyedBean.getOrigin().equals(CommonConstant.iotLabel)) {

            //分发到clickhouse
            DistrbutePointData iotTopicDistrbuteData = buildRuleMatchResult(pointData, CommonConstant.iotSinkTopic);
            RuleMatchResult ckRuleMatchResult = buildRuleMatchResult(iotTopicDistrbuteData, pointData);
            if (sendIfMessageNotNull(ckRuleMatchResult)) {
                collector.collect(ckRuleMatchResult);
            }

            //分发到告警
            DistrbutePointData alarmTopicData = buildRuleMatchResult(pointData, CommonConstant.alarmTopic);
            RuleMatchResult alarmRuleMatchResult = buildRuleMatchResult(alarmTopicData, pointData);
            if (sendIfMessageNotNull(alarmRuleMatchResult)) {
                collector.collect(alarmRuleMatchResult);
            }
        }


    }

    @Override
    public void processBroadcastElement(PointTree pointTree, KeyedBroadcastProcessFunction<String, DynamicKeyedBean, PointTree, RuleMatchResult>.Context context, Collector<RuleMatchResult> collector) throws Exception {
        //todo 算法侧处理广播数据
    }


    /**
     * 构建分发规则命中结果集(暂时支持边缘端)
     * @param iotTopicDistrbuteData
     * @param pointData
     * @return
     * @throws JsonProcessingException
     */
    public RuleMatchResult buildRuleMatchResult(DistrbutePointData iotTopicDistrbuteData, PointData pointData) throws JsonProcessingException {
        RuleMatchResult ruleMatchResult = new RuleMatchResult();
        ruleMatchResult.setTopic(iotTopicDistrbuteData.getTopic());
        ruleMatchResult.setDeviceTimestamp(pointData.getDeviceTimeStamp());
        ruleMatchResult.setPointId(pointData.getPointId());
        ruleMatchResult.setBatchId(pointData.getBatchId());
        ruleMatchResult.setDeviceChannel(pointData.getDeviceChannel());
        ruleMatchResult.setIotJson(CommonConstant.objectMapper.writeValueAsString(iotTopicDistrbuteData));
        return ruleMatchResult;
    }

    /**
     * 构建边缘端待分发数据
     * @param pointData
     * @param topic
     * @return
     * @throws JsonProcessingException
     */
    public DistrbutePointData buildRuleMatchResult(PointData pointData, String topic) throws JsonProcessingException {
        RuleMatchResult iotTopicDistrbuteData = new RuleMatchResult();

        String iotJson = CommonConstant.objectMapper.writeValueAsString(pointData);
        iotTopicDistrbuteData.setEventBean(iotJson);
        iotTopicDistrbuteData.setBatchId(pointData.getBatchId());
        iotTopicDistrbuteData.setPointId(pointData.getPointId());
        iotTopicDistrbuteData.setDeviceChannel(pointData.getDeviceChannel());
        iotTopicDistrbuteData.setDeviceTimestamp(pointData.getDeviceTimeStamp());
        DistrbutePointData distrbutePointData = new DistrbutePointData();
        BeanUtil.copyProperties(pointData, distrbutePointData);
        distrbutePointData.setTopic(topic);
        return distrbutePointData;
    }

    /**
     * 判断当前除了topic之外的实体属性是不是null值
     * @param data
     * @return
     */
    public boolean sendIfMessageNotNull(RuleMatchResult data){
        String topic = data.getTopic();
        data.setTopic(null);

        if (iaAllFieldsNull(data)) {
            data.setTopic(topic);
            return false;
        }else {
            data.setTopic(topic);
            return true;
        }
    }

}
