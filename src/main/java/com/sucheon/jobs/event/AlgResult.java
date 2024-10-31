package com.sucheon.jobs.event;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 算法实例
 */
@Data
public class AlgResult extends EventBean {

    /**
     * 本次算法控制台等待匹配的输入测点集合
     */
    private List<String> nodeIdList;

    /**
     * 当前算法结果包含的所有算法实例的信息
     */
    private List<InstanceWork> instanceWorkList;

    /**
     * 根据算法组来进行匹配对应的特征字段列表
     */
    private Map<String, StringBuilder> algFieldGroupMapping;


}
