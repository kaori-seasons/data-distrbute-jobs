package com.sucheon.jobs.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicKeyedBean {

    /**
     * 多个code的点位树编码 code1, code2, code3....
     */
    private String keyValue;

    /**
     * 点位数据
     */
    private PointData pointData;

    /**
     * 推送到哪个终端
     */
    private String topic;

    /**
     * 输出的算法归属于哪个算法组
     */
    private String algGroup;
}
