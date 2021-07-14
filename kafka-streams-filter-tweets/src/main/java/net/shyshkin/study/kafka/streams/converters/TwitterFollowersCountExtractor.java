package net.shyshkin.study.kafka.streams.converters;

import java.util.Optional;

@FunctionalInterface
public interface TwitterFollowersCountExtractor {

    Optional<Integer> extractFollowersCount(String tweetJson);

}
