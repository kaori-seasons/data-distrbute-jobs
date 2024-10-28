package com.sucheon.jobs.event;

import com.sucheon.jobs.typeinfo.AlgFieldTypeInfoFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.common.typeinfo.TypeInfo;


/**
 * 特征字段所归属于那个输入测点和输出算法实例
 * 上层为节点编码code
 */
@TypeInfo(AlgFieldTypeInfoFactory.class)
@Getter
@Setter
public class AlgFieldList {

    /**
     * 字段key
     */
    private String key;

    /**
     * 测点ID
     */
    private String pointId;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 算法组
     */
    private String group;
}
