package com.sucheon.jobs.functions;

import com.sucheon.jobs.constant.CommonConstant;
import com.sucheon.jobs.event.DynamicKeyedBean;
import com.sucheon.jobs.event.PointData;
import com.sucheon.jobs.event.PointTree;
import com.sucheon.jobs.event.RuleMatchResult;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

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

        // 边缘端从默认配置取数据， 算法侧从广播变量取数据
        if (pointData.getOrigin().equals("iot-data")) {
            pointData.setTopicList(CommonConstant.ckTopicList);
        }

    }

    @Override
    public void processBroadcastElement(PointTree pointTree, KeyedBroadcastProcessFunction<String, DynamicKeyedBean, PointTree, RuleMatchResult>.Context context, Collector<RuleMatchResult> collector) throws Exception {
        //todo 算法侧处理广播数据
    }
}
