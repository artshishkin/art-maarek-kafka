package net.shyshkin.study.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

@Slf4j
public class ElasticsearchConsumer {

    private final RestHighLevelClient client;

    public ElasticsearchConsumer() {
        client = ElasticsearchConfiguration.getInstance().getClient();
    }

    public void putJson(String jsonString) throws IOException {

        IndexRequest indexRequest = new IndexRequest("twitter")
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        log.info("Document id: {}", id);
        log.info("Index Response: {}", indexResponse);
    }

    public void stop() throws IOException {
        client.close();
    }
}
