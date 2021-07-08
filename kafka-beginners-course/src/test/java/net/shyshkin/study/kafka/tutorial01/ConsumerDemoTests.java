package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
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
    @DisplayName("3 consumers in a group subscribes topic with 3 partitions -> eachwill pollChanging Group ID will lead to that Consumer will read all the `earliest` messages from one partition then from another and so on")
    void multipleConsumersInGroup() throws InterruptedException {

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
        consumers.forEach(cons -> cons.subscribe(topics));

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

    private void logRecord(ConsumerRecord<String, String> record) {
        log.info("Key: {}, Value: {}", record.key(), record.value());
        log.info("Headers: {}, Partition: {}, Offset: {}", record.headers(), record.partition(), record.offset());
        System.out.println("------------------");
    }

}