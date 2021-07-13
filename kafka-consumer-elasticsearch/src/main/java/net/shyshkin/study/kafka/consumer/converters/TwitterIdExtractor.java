package net.shyshkin.study.kafka.consumer.converters;

@FunctionalInterface
public interface TwitterIdExtractor {

    String extract(String tweetJson);

}
