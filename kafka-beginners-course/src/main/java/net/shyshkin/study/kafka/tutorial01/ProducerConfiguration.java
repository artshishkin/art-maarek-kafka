package net.shyshkin.study.kafka.tutorial01;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Slf4j
public class ProducerConfiguration {

    public static final String TOPIC = "java-test-topic";

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    static void logMetadata(RecordMetadata metadata) {
        log.info("recordMetadata: {}", metadata.toString());
        log.info("partition: {}", metadata.partition());
        log.info("offset: {}", metadata.offset());
        log.info("serializedKeySize: {}", metadata.serializedKeySize());
        log.info("serializedValueSize: {}", metadata.serializedValueSize());
        log.info("topic: {}", metadata.topic());
        log.info("timestamp: {}", metadata.timestamp());
    }
}
