package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ProducerDemoTests {

    public static final Faker FAKER = Faker.instance();
    private KafkaProducer<String, String> producer;

    @BeforeEach
    void setUp() {
        // create producer properties
        Properties properties = ProducerConfiguration.getProperties();

        //create producer
        producer = new KafkaProducer<>(properties);
    }

    @AfterEach
    void tearDown() {
        producer.flush();
        producer.close();
    }

    @Test
    void producerDemo() {

        //given
        String messageToSend = FAKER.educator().university();
        log.info("Sending message: {}", messageToSend);

        //when
        ProducerRecord<String, String> record = new ProducerRecord<>(ProducerConfiguration.TOPIC, messageToSend);
        producer.send(record);

        //then
        log.info("View logs and kafka-console-consumer to ensure that data was sent");
    }

    @Test
    void producerDemo_withFutureGet() throws InterruptedException, ExecutionException, TimeoutException {

        //given
        String messageToSend = FAKER.educator().university();
        log.info("Sending message: {}", messageToSend);

        //when
        ProducerRecord<String, String> record = new ProducerRecord<>(ProducerConfiguration.TOPIC, messageToSend);
        Future<RecordMetadata> send = producer.send(record);

        //then
        RecordMetadata recordMetadata = send.get(3, TimeUnit.SECONDS);
        logMetadata(recordMetadata);

        assertThat(recordMetadata.topic()).isEqualTo(ProducerConfiguration.TOPIC);
    }

    @Test
    void producerDemo_withCallback() throws InterruptedException {

        //given
        Set<Integer> partitions = new HashSet<>();

        //when
        for (int i = 0; i < 100; i++) {

            Thread.sleep(10);

            String messageToSend = FAKER.educator().university();
            log.info("Sending message: {}", messageToSend);

            ProducerRecord<String, String> record = new ProducerRecord<>(ProducerConfiguration.TOPIC, messageToSend);
            producer.send(record,
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Exception happened", exception);
                        } else {
                            logMetadata(metadata);
                            assertThat(metadata.topic()).isEqualTo(ProducerConfiguration.TOPIC);
                            int partition = metadata.partition();
                            partitions.add(partition);
                        }
                    }
            );
        }
        producer.flush();
        assertThat(partitions)
                .hasSize(3)
                .contains(0, 1, 2);
    }

    void logMetadata(RecordMetadata metadata) {
        log.info("recordMetadata: {}", metadata.toString());
        log.info("partition: {}", metadata.partition());
        log.info("offset: {}", metadata.offset());
        log.info("serializedKeySize: {}", metadata.serializedKeySize());
        log.info("serializedValueSize: {}", metadata.serializedValueSize());
        log.info("topic: {}", metadata.topic());
        log.info("timestamp: {}", metadata.timestamp());
    }
}