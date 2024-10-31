package com.sucheon.jobs.event;

import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分发侧根据控制台的数据组织完成之后，当前算法实例所包含的信息
 */
@Data
public class InstanceWork {

    /**
     * 当前算法实例所包含的特征字段
     */
    private Map<String, String> fieldMap;

    /**
     * 输出到下游算法实例的集合
     */
    private List<String> algInstanceList;

    /**
     * 当前算法实例的id
     */
    private String instanceId;

}
