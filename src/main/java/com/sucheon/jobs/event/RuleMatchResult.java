package com.sucheon.jobs.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * 命中对应算法规则之后的配置结果
 */
@Getter
@Setter
public class RuleMatchResult implements Serializable {

    /**
     * 分发的是哪种类型的数据(算法端和边缘端)
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String origin;

    /**
     * 结果命中之后传输到哪个topic
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnore
    private String topic;

    /**
     * 该算法模版的code编码列表
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String codeList;

    /**
     * 该算法模版归属的算法组
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String algGroup;

    /**
     * 测点还是算法实例类型
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String eventBean;

    /**
     * 测点id (上游必传字段)
     */
    @JsonProperty(value = "point_id")
    private String pointId;

    /**
     * 设备上送的通道 (上游必传字段)
     */
    @JsonProperty(value = "device_channel")
    private String deviceChannel;

    /**
     * 设备已经进入kafka的处理时间 (上游必传字段)
     */
    @JsonProperty(value = "device_timestamp")
    private Long deviceTimestamp;


    /**
     * 上送每一笔数据的批次号 (上游必传字段)
     */
    @JsonProperty(value = "batch_id")
    private String batchId;

    /**
     * iot字段
     */
    @JsonProperty(value = "iot_json")
    private String iotJson;

    /**
     * 算法字段
     */
    @JsonProperty(value = "alg_json")
    private String algJson;

}
