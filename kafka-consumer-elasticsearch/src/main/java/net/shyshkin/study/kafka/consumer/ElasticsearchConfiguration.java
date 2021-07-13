package net.shyshkin.study.kafka.consumer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticsearchConfiguration {

    private static ElasticsearchConfiguration instance;
    private RestHighLevelClient client;

    public static ElasticsearchConfiguration getInstance() {
        if (instance == null)
            instance = new ElasticsearchConfiguration();
        return instance;
    }

    public RestHighLevelClient getClient() {
        if (client == null)
            client = createLocalClient();
//            client = createSecuredClient();
        return client;
    }

    private RestHighLevelClient createSecuredClient() {

        String hostname = getHostname();
        HttpHost host = HttpHost.create(hostname);
        CredentialsProvider credentialsProvider = getCredentialsProvider();

        RestClientBuilder restClientBuilder = RestClient
                .builder(host)
                .setHttpClientConfigCallback(
                        httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(restClientBuilder);
    }

    private RestHighLevelClient createLocalClient() {

        String hostname = "http://localhost:9200";
        HttpHost host = HttpHost.create(hostname);

        RestClientBuilder restClientBuilder = RestClient
                .builder(host);

        return new RestHighLevelClient(restClientBuilder);
    }

    protected CredentialsProvider getCredentialsProvider() {

        CredentialsProvider credentialsProvider = null;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("secrets.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String username = properties.getProperty("accessKey");
            String password = properties.getProperty("accessSecret");

            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        } catch (IOException exception) {
            log.error("Error while getting username and password from file", exception);
        }
        return credentialsProvider;
    }

    protected String getHostname() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("secrets.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String fullAccessUrl = properties.getProperty("url");
            if (!fullAccessUrl.contains("@")) return fullAccessUrl;

            int schemeIndex = fullAccessUrl.indexOf("://");
            String scheme = fullAccessUrl.substring(0, schemeIndex);

            int atIndex = fullAccessUrl.indexOf("@");
            String hostAndPort = fullAccessUrl.substring(atIndex + 1);
            return scheme + "://" + hostAndPort;
        } catch (IOException exception) {
            log.error("Error while getting hostname from file", exception);
        }
        return null;
    }
}
