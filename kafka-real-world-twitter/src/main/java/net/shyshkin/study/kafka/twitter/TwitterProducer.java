package net.shyshkin.study.kafka.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor
public class TwitterProducer {

    private Client hosebirdClient;
    private final List<String> terms = Lists.newArrayList("bitcoin");

    public void run() {

        log.info("Setup");

        /* Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

        // create twitter client
        hosebirdClient = createTwitterClient(msgQueue);

        hosebirdClient.connect();

        // create twitter producer
        KafkaProducer<String, String> producer = createKafkaProducer();

        //add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping application...");
            log.info("shutting down client from twitter...");
            hosebirdClient.stop();
            log.info("closing kafka producer...");
            producer.close();
            log.info("done!");

        }));

        // loops to send tweets to kafka
        // on a different thread, or multiple different threads....
        while (!hosebirdClient.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Exception in polling Twitter API", e);
                hosebirdClient.stop();
            }
            if (msg != null) {
                log.info("Message: {}", msg);
                producer.send(new ProducerRecord<>(KafkaConfiguration.TOPIC, msg),
                        (metadata, exception) -> {
                            if (exception != null) {
                                log.error("Exception in producer", exception);
                            }
                        });
            }
        }
        log.info("End of application");
    }

    public void stop() {
        if (hosebirdClient != null)
            hosebirdClient.stop();
    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) {

        /* Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = getAuthentication();

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }

    protected Authentication getAuthentication() {

        Authentication hosebirdAuth = null;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("secrets.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String consumerKey = properties.getProperty("consumerKey");
            String consumerSecret = properties.getProperty("consumerSecret");
            String token = properties.getProperty("token");
            String secret = properties.getProperty("secret");

//            log.info("consumerKey = {},consumerSecret = {},token = {},secret = {}", consumerKey, consumerSecret, token, secret);

            hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return hosebirdAuth;
    }

    private KafkaProducer<String, String> createKafkaProducer() {

        // create producer properties
        Properties properties = KafkaConfiguration.getProducerProperties();

        //create producer
        return new KafkaProducer<>(properties);
    }

}
