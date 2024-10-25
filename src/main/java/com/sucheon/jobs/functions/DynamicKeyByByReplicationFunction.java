package com.sucheon.jobs.functions;

import com.sucheon.jobs.event.*;
import com.sucheon.jobs.state.StateDescContainer;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 兼容联动点位（code1,code2,code3...）的多个keyby分发
 */
public class DynamicKeyByByReplicationFunction extends BroadcastProcessFunction<PointData, PointTree, DynamicKeyedBean> {


    /**
     * 通过点位树的配置得知需要分发到哪些topic当中
     */
    ReadOnlyBroadcastState<String, List<PointTree>> broadcastState;

    /**
     * 算法控制台 存在算法配置，数据流存在点位配置
     * 输出到kafka的需要以算法组为准
     * @param pointData
     * @param ctx
     * @param collector
     * @throws Exception
     */

    @Override
    public void processElement(PointData pointData, BroadcastProcessFunction<PointData, PointTree, DynamicKeyedBean>.ReadOnlyContext ctx, Collector<DynamicKeyedBean> collector) throws Exception {
        broadcastState = ctx.getBroadcastState(StateDescContainer.ruleStateDesc);

        if (broadcastState == null) {
            return;
        }

        //将当前点位编码code从上下文中取出
        if (Objects.isNull(pointData) || StringUtils.isBlank(pointData.getCode())) {
            return;
        }
        List<PointTree> pointTreeList =  broadcastState.get(pointData.getCode());

        if (pointTreeList == null || pointTreeList.size() == 0) {
            return;
        }

        HashMap<String, Map<String, String>> codePointAndAlgFieldMap = new HashMap<>();
       for (int i=0; i<pointTreeList.size();i++){
           PointTree pointTree = pointTreeList.get(i);
           if (!pointTree.getCode().equals(pointData.getCode())) {
               continue;
           }

           //匹配需要将哪些code下的字段收拢，分发到哪个算法组
           List<AlgFieldList> algResultList =  pointTree.getFieldList();

           Map<String, String> pointAndAlgFieldMap = new HashMap<>();
           for (int j=0;j<algResultList.size();j++) {
               AlgFieldList algFieldList = algResultList.get(i);

               pointAndAlgFieldMap.put(algFieldList.getPointId(), algFieldList.getKey());
           }

            codePointAndAlgFieldMap.put(pointData.getCode(), pointAndAlgFieldMap);

           //todo 将结果转换为dynamicBean
       }







    }

    @Override
    public void processBroadcastElement(PointTree pointTree, BroadcastProcessFunction<PointData, PointTree, DynamicKeyedBean>.Context context, Collector<DynamicKeyedBean> collector) throws Exception {

    }
}
