package com.sucheon.jobs.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Configuration
@Data
public class UserProperties implements Serializable {

    @Value("${ddps.kafka.bootstrap-servers}")
    private String bootStrapServers;

    @Value("${ddps.kafka.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${ddps.kafka.fetch-max-bytes}")
    private String fetchMaxBytes;

    @Value("${ddps.kafka.auto-create-topics}")
    private String autoCreateTopics;

    @Value("${ddps.kafka.iot-source-topic}")
    private String iotSourceTopic;

    @Value("${ddps.kafka.iot-group-id}")
    private String iotSourceGroup;

    @Value("${ddps.kafka.alg-source-topic}")
    private String algSourceTopic;

    @Value("${ddps.kafka.alg-group-id}")
    private String algSourceGroup;



}
