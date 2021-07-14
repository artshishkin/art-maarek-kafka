package net.shyshkin.study.kafka.twitter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class TwitterProducerApplication {

    public static void main(String[] args) {

        TwitterProducer twitterProducer = new TwitterProducer();
        twitterProducer.run();
    }

}
