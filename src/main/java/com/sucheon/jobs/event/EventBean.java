package com.sucheon.jobs.event;

import com.alibaba.fastjson.annotation.JSONField;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * 需要用JsonfieldConverter转换下划线
 */
public class EventBean {


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
    private String deviceTimestamp;


    /**
     * 上送每一笔数据的批次号 (上游必传字段)
     */
    @JsonProperty(value = "batch_id")
    private String batchId;

    /**
     * 区分上送数据的来源 (在上送数据的时候不需要序列化)
     */
    @JsonIgnore
    private String origin;

    /**
     * 分发的点位编码 在算法的配置字典中维护 (在上送数据的时候不需要序列化)
     */
    @JsonIgnore
    private String code;


    /**
     * 当前点位树配置分发到哪几个kafka当中去(边缘端上送数据使用)
     */
    private List<String> topicList;


    public List<String> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<String> topicList) {
        this.topicList = topicList;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getDeviceChannel() {
        return deviceChannel;
    }

    public void setDeviceChannel(String deviceChannel) {
        this.deviceChannel = deviceChannel;
    }

    public String getDeviceTimestamp() {
        return deviceTimestamp;
    }

    public void setDeviceTimestamp(String deviceTimestamp) {
        this.deviceTimestamp = deviceTimestamp;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
