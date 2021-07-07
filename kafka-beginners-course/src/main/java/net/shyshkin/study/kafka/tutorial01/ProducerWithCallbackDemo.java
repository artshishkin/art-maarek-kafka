package net.shyshkin.study.kafka.tutorial01;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

@Slf4j
public class ProducerWithCallbackDemo {

    public static final Faker FAKER = Faker.instance();

    public static void main(String[] args) {

        // create producer properties
        Properties properties = ProducerConfiguration.getProperties();

        //create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //send data
        for (int i = 0; i < 100; i++) {

            ProducerRecord<String, String> record = new ProducerRecord<>(ProducerConfiguration.TOPIC, FAKER.educator().university());
            producer.send(record,
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Exception happened", exception);
                        } else {
                            ProducerConfiguration.logMetadata(metadata);
                        }
                    }
            );
        }

        producer.flush();
        producer.close();

    }

}
