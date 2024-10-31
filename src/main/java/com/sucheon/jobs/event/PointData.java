package com.sucheon.jobs.event;

import com.sucheon.jobs.typeinfo.PointDataTypeInfoFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.sql.Timestamp;
import java.time.LocalDate;


/**
 * 测点数据
 */
@TypeInfo(PointDataTypeInfoFactory.class)
@Getter
@Setter
public class PointData extends EventBean{

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

}
