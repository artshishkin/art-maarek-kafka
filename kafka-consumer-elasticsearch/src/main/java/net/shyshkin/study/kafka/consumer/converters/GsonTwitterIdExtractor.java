package net.shyshkin.study.kafka.consumer.converters;

import com.google.gson.JsonParser;

public class GsonTwitterIdExtractor implements TwitterIdExtractor {

    @Override
    public String extract(String tweetJson) {
        return JsonParser
                .parseString(tweetJson)
                .getAsJsonObject()
                .get("id_str")
                .getAsString();
    }
}
