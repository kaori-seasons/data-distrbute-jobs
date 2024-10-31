package com.sucheon.jobs;

import com.alibaba.fastjson.JSON;
import com.sucheon.jobs.config.UserProperties;
import com.sucheon.jobs.event.*;
import com.sucheon.jobs.functions.*;
import com.sucheon.jobs.partitioner.CustomPartitioner;
import com.sucheon.jobs.sink.FlinkKafkaMutliSink;
import com.sucheon.jobs.state.StateDescContainer;
import com.sucheon.jobs.utils.InternalTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.sucheon.jobs.utils.InternalTypeUtils.iaAllFieldsNull;

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
@Slf4j
@SpringBootApplication(exclude = {GsonAutoConfiguration.class, JacksonAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class})
public class DistrbuteJobMain {


    public static void main(String[] args) throws Exception {

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
        System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DistrbuteJobMain.class, args);

        //get properties pojo
        UserProperties ddpsKafkaProperties = applicationContext.getBean(UserProperties.class);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", ddpsKafkaProperties.getBootStrapServers());
        properties.setProperty("auto.offset.reset", ddpsKafkaProperties.getAutoOffsetReset());
        properties.setProperty("fetch.max.bytes", ddpsKafkaProperties.getFetchMaxBytes());
        properties.setProperty("auto.create.topics", ddpsKafkaProperties.getAutoCreateTopics());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(1);

        KafkaSourceBuilder kafkaSourceBuilder = new KafkaSourceBuilder();
        DataStream<String> algResult = env.fromSource(kafkaSourceBuilder.newBuild(ddpsKafkaProperties.getAlgSourceTopic(), ddpsKafkaProperties.getAlgSourceGroup(), properties), WatermarkStrategy.noWatermarks(), "iot-data");
        algResult =  algResult.map(new LabelAlgResultMapFunction());
        DataStream<String> iotResult = env.fromSource(kafkaSourceBuilder.newBuild(ddpsKafkaProperties.getIotSourceTopic(), ddpsKafkaProperties.getIotSourceGroup(), properties), WatermarkStrategy.noWatermarks(), "alg-data");
        iotResult = iotResult.map(new LabelIotResultMapFunction());
        algResult = algResult.union(iotResult);
        //json解析
        DataStream<PointData> pointTreeDataStream = algResult.map(new Json2EventBeanMapFunction()).filter(e -> e!=null);


        //添加事件事件分配 去掉补的数据
        WatermarkStrategy<PointData> watermarkStrategy = WatermarkStrategy.<PointData>forBoundedOutOfOrderness(Duration.ofMillis(0))
                .withTimestampAssigner(new SerializableTimestampAssigner<PointData>() {
                    @Override
                    public long extractTimestamp(PointData pointTree, long l) {
                        Long deviceTimestamp = pointTree.getDeviceTimeStamp();
                        long currentWatermark = 0L;
                        if (deviceTimestamp != null) {
                            currentWatermark = deviceTimestamp;
                        }else {
                            currentWatermark = LocalDateTime.now(ZoneOffset.of("+8")).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        }

                        return currentWatermark;
                    }
                });

        SingleOutputStreamOperator<PointData> pointTreeWithWatermark = pointTreeDataStream.assignTimestampsAndWatermarks(watermarkStrategy);



        //FIXME 读取kafka中规则操作数据流canal binlog 仅针对算法侧使用的配置流
        // DataStream<String> ruleBinlogs = env.addSource(kafkaSourceBuilder.build("topic-xxx"));

        //仅边缘端数据测试临时使用
        DataStream<String> ruleBinlogs = env.fromCollection(Arrays.asList("{\n" +
                "  \"code\": 111,\n" +
                "  \"fields\":[\t\t\t\n" +
                "     {\n" +
                "        \"key\":\"feature1\",\t\n" +
                "        \"point_id\":1,\t\t\n" +
                "        \"instance_id\":1,\t\t\n" +
                "        \"group\":\"1\"\n" +
                "     }\n" +
                "    ],\n" +
                "    \"timestamp\": 1730096789,\n" +
                "    \"topicList\": [\"iot_kv_main\"],\n" +
                "    \"algGroup\": \"\"\n" +
                "}","{\n" +
                "    \"code\": 111,\n" +
                "    \"fields\":[\t\t\t\n" +
                "       {\n" +
                "          \"key\":\"feature2\",\t\n" +
                "          \"point_id\":2,\t\t\n" +
                "          \"instance_id\":1,\t\t\n" +
                "          \"group\":\"1\"\n" +
                "       }\n" +
                "      ],\n" +
                "      \"timestamp\": 1730096789,\n" +
                "      \"topicList\": [\"iot_kv_main\"],\n" +
                "      \"algGroup\": \"\"\n" +
                "  }","{\n" +
                "    \"code\": 112,\n" +
                "    \"fields\":[\t\t\t\n" +
                "       {\n" +
                "          \"key\":\"feature1\",\t\n" +
                "          \"point_id\":1,\t\t\n" +
                "          \"instance_id\":1,\t\t\n" +
                "          \"group\":\"1\"\n" +
                "       }\n" +
                "      ],\n" +
                "      \"timestamp\": 1730096790,\n" +
                "      \"topicList\": [\"iot_kv_main\"],\n" +
                "      \"algGroup\": \"\"\n" +
                "  }"));

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
            public byte[] serializeKey(RuleMatchResult data) {
               return InternalTypeUtils.transferData(data);
            }

            @Override
            public byte[] serializeValue(RuleMatchResult data) {
                return InternalTypeUtils.transferData(data);
            }

            @Override
            public String getTargetTopic(RuleMatchResult algResult) {
                if (algResult == null) {
                    return "default-topic";
                }

               return algResult.getTopic();
            }
        };

        FlinkKafkaMutliSink distrbuteSink = new FlinkKafkaMutliSink("default-topic", routeDistrute, properties, new CustomPartitioner());

        //匹配的算法结果输出到kafka
        ruleMatchResultDataStream.addSink(distrbuteSink);

        env.execute();

    }
}
