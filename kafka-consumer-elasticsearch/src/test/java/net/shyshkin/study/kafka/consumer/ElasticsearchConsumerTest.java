package net.shyshkin.study.kafka.consumer;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class ElasticsearchConsumerTest {

    @Test
    void putJson() throws IOException, InterruptedException {
        //given
        String jsonString = "{\"foo\":\"bar\"}";
        ElasticsearchConsumer elasticsearchConsumer = new ElasticsearchConsumer();

        //when
        elasticsearchConsumer.putJson(jsonString);

        //then
        Thread.sleep(2000);
        elasticsearchConsumer.stop();
    }
}