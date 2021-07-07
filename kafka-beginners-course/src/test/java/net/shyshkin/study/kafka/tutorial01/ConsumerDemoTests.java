package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Slf4j
class ConsumerDemoTests {

    public static final Faker FAKER = Faker.instance();
    private KafkaConsumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        // create consumer properties
        Properties properties = KafkaConfiguration.getConsumerProperties();

        //create consumer
        consumer = new KafkaConsumer<>(properties);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void consumerDemo() {

        //given
        Collection<String> topics = List.of(KafkaConfiguration.TOPIC);

        //when
        consumer.subscribe(topics);

        //then
        boolean stopPolling = false;
        while (!stopPolling) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {

                logRecord(record);

                if ("exit".equals(record.value())) {
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