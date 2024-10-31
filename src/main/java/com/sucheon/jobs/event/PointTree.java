package com.sucheon.jobs.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.sucheon.jobs.typeinfo.PointTreeTypeInfoFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.util.List;
import java.util.Map;

/**
 * 点位树(来自用户配置的规则)
 */
@TypeInfo(PointTreeTypeInfoFactory.class)
@Getter
@Setter
public class PointTree {

    @JSONField(ordinal = 1)
    private String code;

    /**
     * "fields":[			//字段
     * {
     * "key":"feature1",	//字段key
     * "point_id":1,		//测点id 可选（算法结果没有）
     * "instance_id":1,		//实例id 可选（测点数据没有）
     * "group":"1"			//分组 可选（测点数据没有）
     * }
     * ]
     */
    private List<AlgFieldList> fields;


    /**
     * 这里得到的应该是补的数据推进水位线之后每个code对应的最大水位线
     */
    private Long timestamp;


    /**
     * 当前点位树配置分发到哪几个kafka当中去
     */
    private List<String> topicList;


    /**
     * 用户所配置的算法组
     */
    private String algGroup;

    /**
     * 需要从后端的广播流订阅<算法实例，算法组>的映射关系
     */


}
