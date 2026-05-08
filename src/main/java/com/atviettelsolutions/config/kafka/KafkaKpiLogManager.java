package com.atviettelsolutions.config.kafka;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Slf4j
public class KafkaKpiLogManager {
    private Producer<String, String> kafkaProducer;
    private final String topic;
    private final Environment environment;
    private final Gson gson;

    KafkaKpiLogManager(Environment environment, Gson gson) {
        this.gson = gson;
        this.environment = environment;
        this.topic = environment.getProperty("kpi.datasource.topic");
    }

    @PostConstruct
    public void init() {
        String url = environment.getProperty("kpi.datasource.url");
        Properties props = new Properties();
        props.put("bootstrap.servers", url);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("acks", environment.getProperty("kpi.kafka.acks", "all"));
        props.put("retries", Integer.parseInt(environment.getProperty("kpi.kafka.retries", "2147483647")));
        props.put("linger.ms", environment.getProperty("kpi.kafka.linger-ms", "5"));  // Small batch delay for throughput
        props.put("batch.size", environment.getProperty("kpi.kafka.batch-size", "65536"));  // 64KB batches
        props.put("compression.type", environment.getProperty("kpi.kafka.compression-type", "lz4"));  // Compress for KPI logs
        kafkaProducer = new KafkaProducer<>(props);
    }

    public <T> void send(String key, T value) {
        String jsonValue = gson.toJson(value);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, jsonValue);

        log.info("sending message to topic {}: ({}, {})", topic, key, value);
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send KPI log to topic {}: {}", topic, exception.getMessage(), exception);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        if (kafkaProducer != null) {
            kafkaProducer.flush();
            kafkaProducer.close();
            log.info("Kafka producer closed");
        }
    }
}
