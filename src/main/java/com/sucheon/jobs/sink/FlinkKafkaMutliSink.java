package com.sucheon.jobs.sink;

import com.sucheon.jobs.event.RuleMatchResult;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducerBase;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import java.util.Properties;

public class FlinkKafkaMutliSink extends FlinkKafkaProducer<RuleMatchResult> {


    public FlinkKafkaMutliSink(String defaultTopicId, KeyedSerializationSchema<RuleMatchResult> serializationSchema, Properties producerConfig, FlinkKafkaPartitioner<RuleMatchResult> customPartitioner) {
        super(defaultTopicId, serializationSchema, producerConfig, java.util.Optional.ofNullable(customPartitioner));
    }
}
