package net.shyshkin.study.kafka.twitter;

import com.twitter.hbc.httpclient.auth.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class TwitterProducerTest {

    @Test
    void getAuthenticationTest() {
        //given
        TwitterProducer twitterProducer = new TwitterProducer();

        //when
        Authentication authentication = twitterProducer.getAuthentication();

        //then
        log.info("Authentication: {}", authentication);
        assertThat(authentication).isNotNull();

    }

    @Test
    void run() throws InterruptedException {
        //given
        TwitterProducer twitterProducer = new TwitterProducer();

        //when
        CompletableFuture.runAsync(twitterProducer::run);

        //then
        Thread.sleep(10000);
        twitterProducer.stop();
        Thread.sleep(1000);
    }
}