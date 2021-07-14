package net.shyshkin.study.kafka.streams;

import net.shyshkin.study.kafka.streams.converters.GsonTwitterFollowersCountExtractor;
import net.shyshkin.study.kafka.streams.converters.TwitterFollowersCountExtractor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class StreamsFilterTweets {

    private static final TwitterFollowersCountExtractor extractor = new GsonTwitterFollowersCountExtractor();

    public static void main(String[] args) {
        //create properties
        Properties properties = KafkaConfiguration.getStreamsProperties();

        //create a topology
        var streamsBuilder = new StreamsBuilder();

        //input topic
        KStream<String, String> inputTopic = streamsBuilder.stream(KafkaConfiguration.TOPIC);
        KStream<String, String> filteredTopic = inputTopic.filter((k, tweetJson) -> {
//            $.user.followers_count>10000
            return extractor.extractFollowersCount(tweetJson).orElse(-1) > 10000;
        });
        filteredTopic.to(KafkaConfiguration.FILTERED_TOPIC);

        //build the topology
        var kafkaStreams = new KafkaStreams(
                streamsBuilder.build(),
                properties
        );

        //start our streams application
        kafkaStreams.start();
    }

}
