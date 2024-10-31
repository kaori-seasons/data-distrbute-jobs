package com.sucheon.jobs.partitioner;

import com.sucheon.jobs.event.DistrbutePointData;
import com.sucheon.jobs.event.RuleMatchResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.util.Preconditions;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomPartitioner extends FlinkKafkaPartitioner<RuleMatchResult>
        implements Serializable {

    private final ConcurrentHashMap<String, AtomicInteger> topicCountMap = new ConcurrentHashMap<>();
    private Integer parallelInstanceId;

    public CustomPartitioner() {

    }

    @Override
    public void open(int parallelInstanceId, int parallelInstances) {

        Preconditions.checkArgument(parallelInstanceId >=0, "Id of subTask cannot be negative");
        Preconditions.checkArgument(parallelInstances > 0, "Number of subtasks must be large than 0");
        this.parallelInstanceId = parallelInstanceId;
    }

    @Override
    public int partition(
            RuleMatchResult next,
            byte[] serializedKey,
            byte[] serializedValue,
            String targetTopic,
            int[] partitions) {

        //根据不同并行度轮训发到各个分区
        return this.nextValue(targetTopic) % partitions.length;
    }


    private int nextValue(String topic){
        AtomicInteger counter = this.topicCountMap.computeIfAbsent(topic ,(k) -> { return new AtomicInteger(0);});
        return counter.getAndIncrement();
    }
}
