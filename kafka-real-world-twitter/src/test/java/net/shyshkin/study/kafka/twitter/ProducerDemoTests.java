package net.shyshkin.study.kafka.twitter;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
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
        Properties properties = KafkaConfiguration.getProducerProperties();

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
        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConfiguration.TOPIC, messageToSend);
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
        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConfiguration.TOPIC, messageToSend);
        Future<RecordMetadata> send = producer.send(record);

        //then
        RecordMetadata recordMetadata = send.get(3, TimeUnit.SECONDS);
        logMetadata(recordMetadata);

        assertThat(recordMetadata.topic()).isEqualTo(KafkaConfiguration.TOPIC);
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

            ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConfiguration.TOPIC, messageToSend);
            producer.send(record,
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Exception happened", exception);
                        } else {
                            logMetadata(metadata);
                            assertThat(metadata.topic()).isEqualTo(KafkaConfiguration.TOPIC);
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

    @Test
    void allMessagesWithTheSameKeyShouldBeInTheSamePartition() {

        //given
        Map<Integer, Integer> partitionCount = new HashMap<>();
        int messageCount = 100;
        String key = "id_" + UUID.randomUUID();

        //when
        for (int i = 0; i < messageCount; i++) {

            String messageToSend = FAKER.educator().university();
            log.info("Sending message: {}", messageToSend);

            ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConfiguration.TOPIC, key, messageToSend);
            producer.send(record,
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Exception happened", exception);
                        } else {
                            assertThat(metadata.topic()).isEqualTo(KafkaConfiguration.TOPIC);
                            assertThat(metadata.serializedKeySize()).isGreaterThan(1);
                            int partition = metadata.partition();
                            partitionCount.merge(partition, 1, Integer::sum);
                        }
                    }
            );
        }

        producer.flush(); //block execution to make it synchronous - don't do this in production

        //then
        assertThat(partitionCount.keySet())
                .hasSize(1)
                .containsAnyOf(0, 1, 2);

        assertThat(partitionCount.values())
                .hasSize(1)
                .contains(messageCount);
    }

    @RepeatedTest(10)
//    @Test
    void allMessagesWithTheSameKeyAndConstantBrokerCount_ShouldBeInTheSamePartition_ALL_OVER_THE_WORLD() {

        //given
        int messageCount = 10;
        String key = "id_constant";
        int expectedPartition = 2;

        //when
        for (int i = 0; i < messageCount; i++) {

            String messageToSend = FAKER.educator().university();
            log.info("Sending message: {}", messageToSend);

            ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConfiguration.TOPIC, key, messageToSend);
            producer.send(record,
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Exception happened", exception);
                        } else {
                            int partition = metadata.partition();

                            //then
                            assertThat(partition).isEqualTo(expectedPartition);
                        }
                    }
            );
        }
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