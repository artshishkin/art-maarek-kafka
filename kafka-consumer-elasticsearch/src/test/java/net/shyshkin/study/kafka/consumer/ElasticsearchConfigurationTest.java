package net.shyshkin.study.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CredentialsProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ElasticsearchConfigurationTest {

    @Test
    void getCredentialsProvider() {
        //given
        ElasticsearchConfiguration elasticsearchConfiguration = ElasticsearchConfiguration.getInstance();

        //when
        CredentialsProvider credentialsProvider = elasticsearchConfiguration.getCredentialsProvider();

        //then
        assertThat(credentialsProvider).isNotNull();
    }

    @Test
    void getHostname() {

        //given
        ElasticsearchConfiguration elasticsearchConfiguration = ElasticsearchConfiguration.getInstance();

        //when
        String hostname = elasticsearchConfiguration.getHostname();

        //then
        assertThat(hostname)
                .isNotBlank()
                .contains("http", "://")
                .satisfies(host -> log.info("Hostname: {}", host));
    }
}