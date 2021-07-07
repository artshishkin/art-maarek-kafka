package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ProducerDemo {

    public static final Faker FAKER = Faker.instance();

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println("Hello world");
        log.info("Demo is starting...");

        // create producer properties
        Properties properties = ProducerConfiguration.getProperties();

        //create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //send data
        ProducerRecord<String, String> record = new ProducerRecord<>(ProducerConfiguration.TOPIC, FAKER.educator().university());
        Future<RecordMetadata> send = producer.send(record);
        RecordMetadata recordMetadata = send.get(3, TimeUnit.SECONDS);

        ProducerConfiguration.logMetadata(recordMetadata);

        producer.flush();
        producer.close();

    }

}
