package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Slf4j
public class ProducerDemo {

    public static final String TOPIC = "java-test-topic";
    public static final Faker FAKER = Faker.instance();

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println("Hello world");
        log.info("Demo is starting...");

        // create producer properties
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //send data
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, FAKER.educator().university());
        Future<RecordMetadata> send = producer.send(record);
        RecordMetadata recordMetadata = send.get(3, TimeUnit.SECONDS);
        log.info("recordMetadata: {}", recordMetadata.toString());
        log.info("partition: {}", recordMetadata.partition());
        log.info("offset: {}", recordMetadata.offset());
        log.info("serializedKeySize: {}", recordMetadata.serializedKeySize());
        log.info("serializedValueSize: {}", recordMetadata.serializedValueSize());
        log.info("topic: {}", recordMetadata.topic());
        log.info("timestamp: {}", recordMetadata.timestamp());

        producer.flush();
        producer.close();

    }

}
