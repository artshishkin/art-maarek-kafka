package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
class ConsumerDemoTests {

    public static final Faker FAKER = Faker.instance();
    private KafkaConsumer<String, String> consumer;
    private Properties properties;

    @BeforeEach
    void setUp() {
        // create consumer properties
        properties = KafkaConfiguration.getConsumerProperties();

        //create consumer
        consumer = new KafkaConsumer<>(properties);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("Consumer should read all the `earliest` messages from one partition then from another and so on")
    void consumerDemo() {

        //given
        Collection<String> topics = List.of(KafkaConfiguration.TOPIC);

        //when
        consumer.subscribe(topics);

        //then
        boolean stopPolling = false;
        int lastPartition = -1;
        long lastOffset = -1;

        while (!stopPolling) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {

                logRecord(record);
                int partition = record.partition();
                if (lastPartition == -1) lastPartition = partition;
                assertThat(partition).isEqualTo(lastPartition);

                long offset = record.offset();
                if (lastOffset != -1)
                    assertThat(offset).isEqualTo(lastOffset + 1);
                lastOffset = offset;

                if ("exit".equals(record.value())) {
                    stopPolling = true;
                    break;
                }
            }
        }
    }

    @RepeatedTest(3)
    @DisplayName("Changing Group ID will lead to that Consumer will read all the `earliest` messages from one partition then from another and so on")
    void changeGroupId() {

        //given
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group_id_" + UUID.randomUUID());
        consumer = new KafkaConsumer<>(properties);

        Collection<String> topics = List.of(KafkaConfiguration.TOPIC);

        //when
        consumer.subscribe(topics);

        //then
        boolean stopPolling = false;
        int lastPartition = -1;
        long lastOffset = -1;

        while (!stopPolling) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {

                logRecord(record);
                int partition = record.partition();
                if (lastPartition == -1) lastPartition = partition;
                assertThat(partition).isEqualTo(lastPartition);

                long offset = record.offset();
                if (lastOffset != -1)
                    assertThat(offset).isEqualTo(lastOffset + 1);
                lastOffset = offset;

                if ("exit".equals(record.value())) {
                    stopPolling = true;
                    break;
                }
            }
        }
    }

    @Test
    @DisplayName("with ConsumerRebalanceListener - 3 consumers in a group subscribes topic with 3 partitions -> eachwill pollChanging Group ID will lead to that Consumer will read all the `earliest` messages from one partition then from another and so on")
    void multipleConsumersInGroup_withConsumerRebalanceListener() {

        //given
        String groupId = "group_id_" + UUID.randomUUID();
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        Collection<String> topics = List.of(KafkaConfiguration.TOPIC);

        Map<String, List<Integer>> consumerPartitions = new HashMap<>();

        List<KafkaConsumer<String, String>> consumers = IntStream
                .rangeClosed(1, 3)
                .mapToObj(i -> new KafkaConsumer<String, String>(properties))
                .collect(Collectors.toList());

        //when
        consumers.forEach(cons -> cons.subscribe(topics, new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                log.info("onPartitionsRevoked: {}", partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.info("onPartitionsAssigned: {}", partitions);
            }
        }));

        //then
        await()
                .timeout(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    consumers.forEach(consumer -> {

                        int lastPartition = -1;
                        long lastOffset = -1;

                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                        for (ConsumerRecord<String, String> record : records) {
                            logRecord(record);
                            int partition = record.partition();

                            consumerPartitions.merge(
                                    consumer.toString(),
                                    new ArrayList<>(List.of(partition)),
                                    (a, b) -> {
                                        a.addAll(b);
                                        return a;
                                    }
                            );

                            if (lastPartition == -1) lastPartition = partition;
                            assertThat(partition).isEqualTo(lastPartition);

                            long offset = record.offset();
                            if (lastOffset != -1)
                                assertThat(offset).isEqualTo(lastOffset + 1);
                            lastOffset = offset;
                        }
                    });
                    assertThat(consumerPartitions.keySet()).hasSize(3);
                });

        consumers.forEach(KafkaConsumer::close);

        consumerPartitions.forEach((key, partitions) -> assertThat(partitions).containsOnly(partitions.get(0)));

    }


    @Test
    @DisplayName("Assign and Seek are mostly used to replay data or fetch a specific message")
    void assignAndSeek() {

        //given
        properties.remove(ConsumerConfig.GROUP_ID_CONFIG);
        consumer = new KafkaConsumer<>(properties);
        long offsetToReadFrom = 15L;

        //when
        TopicPartition partitionToReadFrom = new TopicPartition(KafkaConfiguration.TOPIC, 0);
        consumer.assign(List.of(partitionToReadFrom));
        consumer.seek(partitionToReadFrom, offsetToReadFrom);

        //then
        boolean stopPolling = false;
        long lastOffset = offsetToReadFrom - 1;
        int messagesCount = 0;
        int maxMessagesCount = 5;

        while (!stopPolling) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {

                logRecord(record);
                int partition = record.partition();
                assertThat(partition).isEqualTo(partitionToReadFrom.partition());

                long offset = record.offset();
                assertThat(offset).isEqualTo(lastOffset + 1);
                lastOffset = offset;

                if (++messagesCount >= maxMessagesCount) {
                    stopPolling = true;
                    break;
                }
            }
        }
    }

    private void logRecord(ConsumerRecord<String, String> record) {
        log.info("Key: {}, Value: {}", record.key(), record.value());
        log.info("Headers: {}, Partition: {}, Offset: {}", record.headers(), record.partition(), record.offset());
        System.out.println("------------------");
    }

}