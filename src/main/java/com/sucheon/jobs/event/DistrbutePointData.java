package com.sucheon.jobs.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * 最终分发的边缘端测点数据
 */
@Getter
@Setter
public class DistrbutePointData implements Serializable {

    /**
     * 分发到哪个topic
     */
    @JsonIgnore
    private String topic;

    /**
     * 该批次总包数
     */
    
    private Integer pageNum;


    /**
     * 上送的点位数据事件时间
     */
    
    private Long time;

    /**
     * 上报的设备采样时间，抹掉毫秒
     */
    
    private LocalDate deviceTime;

    /**
     * 低分分频特征值
     */
    
    private String bandSpectrum;

    /**
     * 均值
     */
    
    private Integer mean;


    /**
     * 高频均值
     */
    
    private Long meanHf;


    /**
     * 低分振动
     */
    
    private Integer meanLf;


    /**
     * 频段区间
     */
    
    private String peakFreqs;


    /**
     * 功率区间
     */
    
    private String peakPowers;


    /**
     * 标准差
     */
    
    private Integer std;

    /**
     * 转速
     */
    
    private String speed;

    /**
     * 振动原始数据
     */
    
    private String originalVibrate;


    /**
     * 健康度检测的状态值
     * 1.停机
     * 2.运转
     * 3.过渡
     * 4.脉冲
     * 5.异常
     * 6.其他
     * 7.卸载
     * 8.待机
     * 10.温度异常
     */
    
    private Integer status;

    /**
     * 高分摩擦
     */
    
    private String feature1;

    /**
     * 高分振动
     */
    
    private String feature2;


    /**
     * 高分功率
     */
    
    private String feature3;

    /**
     * 高分质量
     */
    
    private String feature4;

    /**
     * 自定义特征值
     */
    
    private String customFeature;

    /**
     * 算法包的版本号
     */
    private Integer version;

    /**
     * 温度
     */
    private Integer temperature;




//    ###################  算法侧回传部分 #############################


    @Override
    public String toString() {
        return "{" +
                "pageNum=" + buildTypeInfo(pageNum) +
                ", time=" + buildTypeInfo(time) +
                ", deviceTime=" + buildTypeInfo(deviceTime) +
                ", bandSpectrum='" + buildTypeInfo(bandSpectrum) + '\'' +
                ", mean=" + buildTypeInfo(mean) +
                ", meanHf=" + buildTypeInfo(meanHf) +
                ", meanLf=" + buildTypeInfo(meanLf) +
                ", peakFreqs='" + buildTypeInfo(peakFreqs) + '\'' +
                ", peakPowers='" + buildTypeInfo(peakPowers) + '\'' +
                ", std=" + buildTypeInfo(std) +
                ", speed='" + buildTypeInfo(speed) + '\'' +
                ", originalVibrate='" + buildTypeInfo(originalVibrate) + '\'' +
                ", status=" + buildTypeInfo(status) +
                ", feature1='" + buildTypeInfo(feature1) + '\'' +
                ", feature2='" + buildTypeInfo(feature2) + '\'' +
                ", feature3='" + buildTypeInfo(feature3) + '\'' +
                ", feature4='" + buildTypeInfo(feature4) + '\'' +
                ", customFeature='" + buildTypeInfo(customFeature) + '\'' +
                ", version=" + buildTypeInfo(version) +
                ", temperature=" + buildTypeInfo(temperature) +
                '}';
    }

    public <T> String buildTypeInfo(T column){
        if (column == null) {
            return "";
        }else {
            return String.valueOf(column);
        }
    }

    public byte[] toByteArray() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

}
