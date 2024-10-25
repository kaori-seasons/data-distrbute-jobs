package com.sucheon.jobs;

import com.alibaba.fastjson.JSON;
import com.sucheon.jobs.event.*;
import com.sucheon.jobs.functions.*;
import com.sucheon.jobs.partitioner.CustomPartitioner;
import com.sucheon.jobs.serializtion.JsonSerializationSchema;
import com.sucheon.jobs.sink.FlinkKafkaMutliSink;
import com.sucheon.jobs.state.StateDescContainer;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducerBase;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * 对两种数据进行处理 1.算法组回传的数据 2.边缘端上送的数据
 * 两种数据格式并不一样
 * 通用数据格式
 * {
 * "point_id":"",//测点id
 * "device_channel":"",//uuid
 * "device_timestamp"：122345667,//时间戳 5.x数据字段需变动
 * "batch_id":"",//批次号
 * //其他字段 扁平
 * }
 */
public class DistrbuteJobMain {


    public static void main(String[] args) {

        /**
         *
         * 数据格式: algorithm_id, result_data, devices列表中存在哪几个点位
         * 多个点位需要做合并算法结果的操作
         * 1464 -> elec_noise -> state_alarlm
         * 1767 -> elec_nois -> state_alarm
         *  instance_id 1
         *
         *  1767 -> elec_nois -stats_alarlm
         *  1686 -> elec_nosi -> stats_alarlm
         *
         * 目前单个点位输出，包含单个算法实例的结果，以及对应多点位的输出
         * 目前一个作业里跑多个点位的情况包括 同种工作流的点位放在一个作业执行
         * 一个算法实例包括多个点位, 如果多个点位并行在跑
         *
         * 1.点位和点位联动
         * 如果是点位和点位之间联动，可以用regex订阅多个kafka的topic, 根据不同的算法实例跑多个点位
         *
         *
         * 2.点位和算法实例联动
         *
         *
         * 3.算法实例和算法实例联动
         *
         */

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(1);

        KafkaSourceBuilder kafkaSourceBuilder = new KafkaSourceBuilder();
        DataStream<String> algResult = env.fromSource(kafkaSourceBuilder.newBuild("iot_kv_main", "test1"), WatermarkStrategy.noWatermarks(), "iot-data");
        algResult =  algResult.map(new LabelAlgResultMapFunction());
        DataStream<String> iotResult = env.fromSource(kafkaSourceBuilder.newBuild("alg_kv_main", "test1"), WatermarkStrategy.noWatermarks(), "alg-data");
        iotResult = iotResult.map(new LabelIotResultMapFunction());
        algResult = algResult.union(iotResult);
        //json解析
        DataStream<PointData> pointTreeDataStream = algResult.map(new Json2EventBeanMapFunction()).filter(e -> e!=null);


        //添加事件事件分配 去掉补的数据
        WatermarkStrategy<PointData> watermarkStrategy = WatermarkStrategy.<PointData>forBoundedOutOfOrderness(Duration.ofMillis(0))
                .withTimestampAssigner(new SerializableTimestampAssigner<PointData>() {
                    @Override
                    public long extractTimestamp(PointData pointTree, long l) {
                        return pointTree.getDeviceTimeStamp().getTime();
                    }
                });

        SingleOutputStreamOperator<PointData> pointTreeWithWatermark = pointTreeDataStream.assignTimestampsAndWatermarks(watermarkStrategy);


        //读取kafka中规则操作数据流canal binlog 仅针对算法侧使用的配置流
        DataStream<String> ruleBinlogs = env.addSource(kafkaSourceBuilder.build("topic-xxx"));
        //解析用户下发的规则配置
        DataStream<PointTree> canalLogBeands =  ruleBinlogs.map(s -> JSON.parseObject(s, PointTree.class)).returns(PointTree.class);
        BroadcastStream<PointTree> ruleBroadcast = canalLogBeands.broadcast(StateDescContainer.ruleStateDesc);


        //将数据流connect规则广播流
        BroadcastConnectedStream<PointData, PointTree> connect1 = pointTreeWithWatermark.connect(ruleBroadcast);


        //根据不同的code1,code2,code3....进行动态keyby的分发
        SingleOutputStreamOperator<DynamicKeyedBean> withDynamicKey = connect1.process(new DynamicKeyByByReplicationFunction());

        //按照算法组进行分发
        KeyedStream<DynamicKeyedBean, Object> keyedStream = withDynamicKey.keyBy(new DistrubuteKeyFunction());

        //规则计算
        BroadcastConnectedStream<DynamicKeyedBean, PointTree> connect2 = keyedStream.connect(ruleBroadcast);

        //根据不同的算法组做相关的分发
        DataStream<RuleMatchResult> ruleMatchResultDataStream =  connect2.process(new RuleMatchKeyedProcessFunction());

        KeyedSerializationSchema<RuleMatchResult> routeDistrute = new KeyedSerializationSchema<RuleMatchResult>() {
            @Override
            public byte[] serializeKey(RuleMatchResult algResult) {
                return algResult.toByteArray();
            }

            @Override
            public byte[] serializeValue(RuleMatchResult algResult) {
                return algResult.toByteArray();
            }

            @Override
            public String getTargetTopic(RuleMatchResult algResult) {
                if (Objects.isNull(algResult)) {
                    return "default-topic";
                }
                return algResult.getTopic();
            }
        };

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","localhost:9092");
        properties.setProperty("auto.offset.reset", "true");

        //todo需要维护每个topic分发到每个分区的逻辑嘛 通过hash打散还是重平衡
        FlinkKafkaMutliSink distrbuteSink = new FlinkKafkaMutliSink("default-topic", routeDistrute, properties, new CustomPartitioner(new HashMap<String, Integer>()));

        //匹配的算法结果输出到kafka
        ruleMatchResultDataStream.addSink(distrbuteSink);



    }
}
