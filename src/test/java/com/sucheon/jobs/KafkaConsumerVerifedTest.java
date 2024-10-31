package com.sucheon.jobs;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Properties;

public class KafkaConsumerVerifedTest {

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","192.168.3.48:30092");
        properties.setProperty("auto.offset.reset", "latest");
        properties.setProperty("fetch.min.bytes", "2000000");
        properties.setProperty("fetch.max.bytes", "15000000");
        properties.setProperty("max.partition.fetch.bytes", "5000000");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(1);
        KafkaSourceBuilder kafkaSourceBuilder = new KafkaSourceBuilder();

        DataStream<String> algResult = env.fromSource(kafkaSourceBuilder.newBuild("iot_kv_main", "test3", properties), WatermarkStrategy.noWatermarks(), "iot-data");

        algResult.print();
        env.execute();

    }
}
