package com.sucheon.jobs;

import com.sucheon.jobs.serializtion.JsonDeserializationSchema;
import com.sucheon.jobs.utils.ConfigNames;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaDeserializationSchemaWrapper;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.regex.Pattern;

public class KafkaSourceBuilder {


    Config config;

    public KafkaSourceBuilder(){
        config = ConfigFactory.load();
    }


    public KafkaSource<String> newBuild(String topic, String groupId, Properties properties){


        // serialization / deserialization schemas for writing and consuming the extra records
        final TypeInformation<String> resultType =
                TypeInformation.of(new TypeHint<String>() {});

        final KafkaDeserializationSchema<String> deserSchema =
                new KafkaDeserializationSchemaWrapper<>(
                        new TypeInformationSerializationSchema<>(
                                resultType, new ExecutionConfig()));
        Object value = properties.get(ConfigNames.KAFKA_BOOTSTRAP_SERVERS);

        String bootstrapServer = (String) value;

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServer)
                .setTopicPattern(Pattern.compile(topic))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class))
                .build();
        return source;
    }

    /**
     * 支持正则表达式进行多topic的匹配
     * @param topic
     * @param groupId
     * @return
     */
    public KafkaSource<Tuple2<String, String>> newBuild0(String topic, String groupId){


        // serialization / deserialization schemas for writing and consuming the extra records
        final TypeInformation<Tuple2<String, String>> resultType =
                TypeInformation.of(new TypeHint<Tuple2<String, String>>() {});

        final KafkaDeserializationSchema<Tuple2<String, String>> deserSchema =
                new KafkaDeserializationSchemaWrapper<>(
                        new TypeInformationSerializationSchema<>(
                                resultType, new ExecutionConfig()));

        KafkaSource<Tuple2<String, String>> source = KafkaSource.<Tuple2<String, String>>builder()
                .setBootstrapServers(ConfigNames.KAFKA_BOOTSTRAP_SERVERS)
                .setTopicPattern(Pattern.compile(topic))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(KafkaRecordDeserializationSchema.of(deserSchema))
                .build();
        return source;
    }


    /**
     * fixme 做成可配置化
     * @param topic
     * @return
     */
    public FlinkKafkaConsumer<String> build(String topic){

        Properties props = new Properties();
        props.setProperty("bootstrap.servers","192.168.3.48:30092");
        props.setProperty("auto.offset.reset", "earliest");

        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);

        return kafkaConsumer;

    }
}
