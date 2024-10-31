package com.sucheon.jobs.functions;

import com.sucheon.jobs.cache.PointDataCacheService;
import com.sucheon.jobs.cache.PointTreeCacheService;
import com.sucheon.jobs.constant.CommonConstant;
import com.sucheon.jobs.event.*;
import com.sucheon.jobs.exception.DistrbuteException;
import com.sucheon.jobs.state.StateDescContainer;
import com.sucheon.jobs.utils.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 兼容联动点位（code1,code2,code3...）的多个keyby分发
 */
public class DynamicKeyByByReplicationFunction extends BroadcastProcessFunction<PointData, PointTree, DynamicKeyedBean> {


    /**
     * 通过点位树的配置得知需要分发到哪些topic当中
     */
    ReadOnlyBroadcastState<String, List<PointTree>> broadcastState;


    /**
     * 如果是第一次推送到算法服务的数据，此时用户可能还没有建场景，需要进行缓存存在redis当中
     */
    @Autowired
    private PointDataCacheService pointDataCacheService;

    /**
     * 如果点位树这个时候用户配置了节点配置，需要建立缓存
     */
    @Autowired
    private PointTreeCacheService pointTreeCacheService;

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
        if (Objects.isNull(pointData)) {
            return;
        }

        if (StringUtils.isBlank(pointData.getPointId())){
            throw new DistrbuteException("当前测点数据为空, 请排查!");
        }

        if (StringUtils.isNotBlank(pointData.getOrigin()) && pointData.getOrigin().equals(CommonConstant.algLabel)) {

            List<PointTree> pointTreeList = new ArrayList<>();

            if (StringUtils.isNotBlank(pointData.getCode())) {
                pointTreeList = broadcastState.get(pointData.getCode());
            }


            //todo 如果是算法服务回传数据失败 通过二级缓存来加载
//            if (pointTreeList == null || pointTreeList.size() == 0) {
//                return;
//            }
//
//            pointTreeCacheService.buildCacheKey(pointData.getCode());
//            pointTreeCacheService.put(pointData.getCode(), pointTreeList);

            //保存节点点位Id映射到测点id到对应特征值二级映射关系
            HashMap<String, Map<String, String>> codePointAndAlgFieldMap = new HashMap<>();


            //todo 匹配当前点位数据和点位树配置实体一样
            PointTree pointTree = new PointTree();
//            if (!pointTree.getCode().equals(pointData.getCode())) {
//                continue;
//            }

            //匹配需要将哪些code下的字段收拢，分发到哪个算法组
            List<AlgFieldList> algResultList =  pointTree.getFields();

            Map<String, String> pointAndAlgFieldMap = new HashMap<>();

            //收集算法组和字段列表 根据告警组将字段分组
            Map<String, StringBuilder> algGroupMap = new HashMap<>();
            for (int j=0;j<algResultList.size();j++) {
                AlgFieldList algFieldList = algResultList.get(j);

                pointAndAlgFieldMap.put(algFieldList.getPointId(), algFieldList.getKey());

                if (StringUtils.isBlank(algGroupMap.get(algFieldList.getGroup()))){
                    algGroupMap.put(algFieldList.getGroup(),new StringBuilder().append(algFieldList.getKey()));
                } else {
                    StringBuilder fieldList = algGroupMap.get(algFieldList.getGroup());
                    fieldList.append(algFieldList.getKey());
                    algGroupMap.put(algFieldList.getGroup(), fieldList);
                }

            }
            codePointAndAlgFieldMap.put(pointData.getCode(), pointAndAlgFieldMap);


            AlgResult algResult = new AlgResult();

            List<InstanceWork> instanceWorkList = new ArrayList<>();
            //将算法控制台广播的规则和数据主流相关联 方便下游按照code进行分流
            for (Map.Entry<String, Map<String,String>> algItem: codePointAndAlgFieldMap.entrySet() ) {

                //将本次算法结果绑定点位
                algResult.setCode(algItem.getKey());
                //点位和算法字段之间的映射关系
                Map<String, String> algFieldList = algItem.getValue();
                List<String> currentPointList = new ArrayList<>();
                List<String> algFieldNamesList = new ArrayList<>();
                for (Map.Entry<String, String> item: algFieldList.entrySet()) {
                    currentPointList.add(item.getKey());
                    algFieldNamesList.add(item.getValue());
                }

                //比较当前点位数据存在的特征值，和点位树配置的特征值相匹配
                Map<String, String> result = ReflectUtils.pointCompare(algFieldNamesList, pointData);

                algResult.setNodeIdList(new ArrayList<>(currentPointList));
                InstanceWork instanceWork = new InstanceWork();
                instanceWork.setFieldMap(result);
                //todo 等待后端接入 <算法实例,算法组>的映射配置
//                    instanceWork.setAlgInstanceList();
                instanceWorkList.add(instanceWork);



            }
            //设置当前节点编号code发往下游的算法实例列表
            algResult.setInstanceWorkList(instanceWorkList);
            algResult.setAlgFieldGroupMapping(algGroupMap);

            //发送给算法下游的算法实例格式
            DynamicKeyedBean dynamicKeyedBean = new DynamicKeyedBean();
            dynamicKeyedBean.setAlgResult(algResult);
            dynamicKeyedBean.setAlgGroup(pointTree.getAlgGroup());
            dynamicKeyedBean.setCodeList(algResult.getCode());
            dynamicKeyedBean.setOrigin(pointData.getOrigin());
            collector.collect(dynamicKeyedBean);


       }else if (StringUtils.isNotBlank(pointData.getOrigin()) && pointData.getOrigin().equals(CommonConstant.iotLabel)){
            DynamicKeyedBean dynamicKeyedBean = new DynamicKeyedBean();
            dynamicKeyedBean.setPointData(pointData);
            dynamicKeyedBean.setOrigin(pointData.getOrigin());
            collector.collect(dynamicKeyedBean);
        }

    }

    //todo 如果配置有发生更新 需要在此处进行匹配
    @Override
    public void processBroadcastElement(PointTree pointTree, BroadcastProcessFunction<PointData, PointTree, DynamicKeyedBean>.Context context, Collector<DynamicKeyedBean> collector) throws Exception {




    }
}
