package com.sucheon.jobs.partitioner;

import com.sucheon.jobs.event.RuleMatchResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;

import java.io.Serializable;
import java.util.Map;

public class CustomPartitioner extends FlinkKafkaPartitioner<RuleMatchResult>
        implements Serializable {

    private final Map<String, Integer> expectedTopicsToNumPartitions;

    public CustomPartitioner(Map<String, Integer> expectedTopicsToNumPartitions) {
        this.expectedTopicsToNumPartitions = expectedTopicsToNumPartitions;
    }

    @Override
    public int partition(
            RuleMatchResult next,
            byte[] serializedKey,
            byte[] serializedValue,
            String topic,
            int[] partitions) {

        //todo 根据投递的事件做对应的分发策略
        return (int) (next.hashCode() % partitions.length);
    }
}
