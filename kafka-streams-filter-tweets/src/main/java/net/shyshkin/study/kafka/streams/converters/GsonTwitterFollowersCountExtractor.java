package net.shyshkin.study.kafka.streams.converters;

import com.google.gson.JsonParser;

import java.util.Optional;

public class GsonTwitterFollowersCountExtractor implements TwitterFollowersCountExtractor {

    @Override
    public Optional<Integer> extractFollowersCount(String tweetJson) {
        try {
            int followersCount = JsonParser.parseString(tweetJson)
                    .getAsJsonObject()
                    .get("user")
                    .getAsJsonObject()
                    .get("followers_count")
                    .getAsInt();
            return Optional.of(followersCount);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
