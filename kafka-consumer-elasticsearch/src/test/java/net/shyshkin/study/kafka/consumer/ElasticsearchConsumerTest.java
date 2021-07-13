package net.shyshkin.study.kafka.consumer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

class ElasticsearchConsumerTest {

    private static ElasticsearchConsumer elasticsearchConsumer;

    @Test
    void putJson() throws IOException, InterruptedException {
        //given
        String jsonString = "{\"foo\":\"bar\"}";
        elasticsearchConsumer = new ElasticsearchConsumer();

        //when
        elasticsearchConsumer.putJson(jsonString, UUID.randomUUID().toString());

        //then

    }

    @Test
    void run() throws InterruptedException, IOException {
        //given
        elasticsearchConsumer = new ElasticsearchConsumer();

        //when
        CompletableFuture.runAsync(() -> elasticsearchConsumer.run());

        //then
        Thread.sleep(15000);
        elasticsearchConsumer.stop();
        Thread.sleep(2000);
    }

    @AfterAll
    static void afterAll() throws IOException, InterruptedException {
        Thread.sleep(2000);
        elasticsearchConsumer.stop();
    }
}