package net.shyshkin.study.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.kafka.consumer.converters.GsonTwitterIdExtractor;
import net.shyshkin.study.kafka.consumer.converters.TwitterIdExtractor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ElasticsearchConsumer {

    private final RestHighLevelClient client;
    private boolean stopPolling = false;
    private static final TwitterIdExtractor idExtractor = new GsonTwitterIdExtractor();

    public ElasticsearchConsumer() {
        client = ElasticsearchConfiguration.getInstance().getClient();
    }

    public void putJson(String jsonString, String id) throws IOException {

        IndexRequest indexRequest = new IndexRequest("twitter")
                .id(id)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        String savedId = indexResponse.getId();
        log.info("Document id: {}", savedId);
        log.info("Index Response: {}", indexResponse);
    }

    public void stop() {
        stopPolling = true;
    }

    public KafkaConsumer<String, String> createConsumer(Collection<String> topics) {
        // create consumer properties
        Properties properties = KafkaConfiguration.getConsumerProperties();

        //create consumer
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(topics);
        return kafkaConsumer;
    }

    public void run() {
        KafkaConsumer<String, String> consumer = createConsumer(List.of(KafkaConfiguration.TOPIC));

        while (!stopPolling) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                String message = record.value();
                try {
//                    String id = record.topic() + "_" + record.partition() + "_" + record.offset();
                    String id = idExtractor.extract(record.value());
                    putJson(message, id);
                } catch (IOException exception) {
                    log.error("Exception while putting JSON into elasticsearch", exception);
                }
            }
        }
        try {
            client.close();
        } catch (IOException exception) {
            log.error("Exception while closing elasticsearch client", exception);
        }
        consumer.close();
    }
}
