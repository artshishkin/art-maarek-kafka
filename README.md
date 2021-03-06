# art-maarek-kafka
Learn Apache Kafka for Beginners - Tutorial from Stephane Maarek (Udemy)

####  Section 5: Starting Kafka

#####  28. Windows - Start Zookeeper & Kafka

1.  Create additional folders
    -  `<kafka-root>/data/kafka`
    -  `<kafka-root>/data/zookeeper`
2.  Configure zookeeper
    -  edit `config/zookeeper.properties`
    -  `dataDir=c:/Users/Admin/Downloads/kafka/data/zookeeper`    
3.  Start zookeeper
    -  `zookeeper-server-start.bat ./config/zookeeper.properties` from kafka folder
4.  Configure kafka
    -  edit `server.properties`
    -  `log.dirs=c:/Users/Admin/Downloads/kafka/data/kafka/kafka-logs`
5.  Start kafka
    -  `kafka-server-start.bat ./config/server.properties`
    -  view logs
        -  `[2021-07-06 16:07:17,268] INFO [KafkaServer id=0] started (kafka.server.KafkaServer)`           

#####  Start Zookeeper & Kafka (confluentinc from Baeldung)

1.  Use [tutorial from Baeldung](https://www.baeldung.com/ops/kafka-docker-setup)
2.  Start [confluentinc-baeldung/docker-compose.yml](docker-compose/confluentinc-baeldung/docker-compose.yml)
3.  Testing Kafka with [Kafka Tool GUI](https://kafkatool.com/download.html)
    -  add cluster
        -  Cluster name: `confluentinc-baeldung`
        -  Kafka Cluster Version: 2.7
        -  Zookeeper Port: 22181
    -  Advanced
        -  Bootstrap servers: `localhost:29092`
    -  Test -> OK -> Add
    
#####  Start Zookeeper & Kafka (wurstmeister from habr)

1.  Use [tutorial from habr](https://habr.com/ru/post/505720/)

####  Section 6: CLI (Command Line Interface) 101

#####  32. Kafka Topics CLI

-  create topic
    -  `kafka-topics.bat --create --topic first-topic --zookeeper localhost:2181 --replication-factor 1 --partitions 3`
-  list topics
    -  `kafka-topics.bat --list --zookeeper localhost:2181`    
    -  **OR**
    -  `kafka-topics.bat --list --bootstrap-server localhost:9092`
-  describe topic
    -  `kafka-topics.bat --bootstrap-server localhost:9092 --topic first-topic --describe`    

#####  33. Kafka Console Producer CLI

-  Without Key
    -  `kafka-console-producer.bat --broker-list localhost:9092 --topic first-topic`
-  Producer properties (example)
    -  `kafka-console-producer.bat --broker-list localhost:9092 --topic first-topic --producer-property acks=all`    
-  Producing to non-existing topic
    -  `kafka-console-producer.bat --broker-list localhost:9092 --topic some-new-topic`
    -  send message
    -  will receive
        -  `[2021-07-06 22:02:34,238] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {some-new-topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)`
    -  but topic will be created
    -  with 1 partition and 1 replication factor
-  Changing default partition count
    -  server.properties
    -  num.partitions=3        

#####  34. Kafka Console Consumer CLI

-  consuming just after launching
    -  `kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic third-new-topic`
-  consuming from beginning
    -  `kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic third-new-topic --from-beginning`    

#####  35. Kafka Consumers in Group

-  start 2 consumers in group
    -  `kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic third-new-topic --group my-first-application`
    -  populate messages to different consumers
-  stop consumers
-  start 1 consumer in group to read from beginning
    -  `kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic third-new-topic --group my-second-application --from-beginning`
    -  read all the messages    
    -  restart consumer
    -  no new messages
    -  stop consumer
    -  produce new messages
    -  restart consumer
    -  only new messages arrived    

#####  36. Kafka Consumer Groups CLI

-  list consumer groups
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list`
        -  `my-first-application`
        -  `my-second-application`
-  describe consumer group
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092 --describe --group my-first-application`        
```
Consumer group 'my-first-application' has no active members.

GROUP                TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
my-first-application third-new-topic 2          7               7               0               -               -               -
my-first-application third-new-topic 1          12              12              0               -               -               -
my-first-application third-new-topic 0          12              12              0               -               -               -
```   
-  produce new messages (for example 3)
-  describe once again
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092 --describe --group my-first-application`
```
Consumer group 'my-first-application' has no active members.

GROUP                TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
my-first-application third-new-topic 2          7               8               1               -               -               -
my-first-application third-new-topic 1          12              13              1               -               -               -
my-first-application third-new-topic 0          12              13              1               -               -               -
```
-  we have LAGs of 3 totally
-  consume again -> LAGs will disappear
    -  `kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic third-new-topic --group my-first-application`
-  start consumer
-  describe
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092 --describe --group my-first-application`
```
GROUP                TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                          HOST            CLIENT-ID
my-first-application third-new-topic 0          13              13              0               consumer-my-first-application-1-afbb9df5-61e6-467f-92c2-97e50d72af12 /127.0.0.1      consumer-my-first-application-1
my-first-application third-new-topic 1          13              13              0               consumer-my-first-application-1-afbb9df5-61e6-467f-92c2-97e50d72af12 /127.0.0.1      consumer-my-first-application-1
my-first-application third-new-topic 2          8               8               0               consumer-my-first-application-1-afbb9df5-61e6-467f-92c2-97e50d72af12 /127.0.0.1      consumer-my-first-application-1
```


#####  37. Resetting Offsets

-  start consumers
-  try to reset offsets
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092  --group my-first-application --reset-offsets --to-earliest --execute --topic third-new-topic`
```
Error: Assignments can only be reset if the group 'my-first-application' is inactive, but the current state is Stable.

GROUP                          TOPIC                          PARTITION  NEW-OFFSET
```
-  stop consumers
-  rerun
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092  --group my-first-application --reset-offsets --to-earliest --execute --topic third-new-topic`
```
GROUP                          TOPIC                          PARTITION  NEW-OFFSET
my-first-application           third-new-topic                0          0
my-first-application           third-new-topic                1          0
my-first-application           third-new-topic                2          0
```
-  restart consumer -> will see all the data
-  **shift by**
    -  `kafka-consumer-groups.bat --bootstrap-server localhost:9092  --group my-first-application --reset-offsets --shift-by -2 --execute --topic third-new-topic`
```
GROUP                          TOPIC                          PARTITION  NEW-OFFSET
my-first-application           third-new-topic                0          11
my-first-application           third-new-topic                1          11
my-first-application           third-new-topic                2          6
```
-  start consumer -> will see 6 messages (shift 2 by 3 partitions)

#####  38. CLI Options that are good to know

-  Producer with keys
```shell script
kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
> key,value
> another key,another value  
```
-  Consumer with keys
```shell script
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,
```

####  Section 9: Kafka Twitter Producer &  Advanced Configurations
 
#####  56. Twitter Setup

1.  Take the developer account
    -  [https://developer.twitter.com/](https://developer.twitter.com/)
    -  took for 2 days to be approved (and some conversation with support team)
2.  Create Project
    -  Name: `Kafka for Beginners Course Project`
    -  Describe you: `Student`
    -  Describe your new Project:      
`
I intend to use Twitter APIs to get real time data streams  into an application that will put data into Kafka. This data will end up in ElasticSearch at the end and this is just for proof-of-concept purposes. No commercial application will result out of this and I won't have any users besides, just myself. Twitter data will not be displayed, and I will only extract tweets on low volume terms.
I do not intend to analyze Tweets, Twitter users, or their content.
I won't display Twitter content off of Twitter.
`
    -  App name: `Kafka for Beginners Art App`
    -  Create
3.  Take keys and tokens    
    - API Key: `3bG...0seR`
    - API Secret Key: `4DcUf...LGEa` 
    - Bearer token: `AAAAAA...DPzrjKRQD` 
    - Access Token: `893...T8ZUmF`
    - Access Token Secret: `oTxnO...haPgc`
 
####  Section 10: Kafka ElasticSearch Consumer & Advanced Configurations

#####  71. Setting up ElasticSearch in the Cloud

-  Use [https://bonsai.io](https://bonsai.io) 
-  Cluster Name: `TWITTER_TWEETS`
-  Copy Credentials and URL
    -  URL: `https://a9..7b:5r..sj@twitter-tweets-6...42.eu-central-1.bonsaisearch.net:443`
    -  ACCESS KEY: `a9..7b`
    -  ACCESS SECRET: `5r..sj`

#####  72. ElasticSearch 101

-  [Bansai Console](https://app.bonsai.io/clusters/twitter-tweets-6787269342/console)
-  [Elastic docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster.html)
-  Health status: `/_cluster/health`
-  Wait for health status: `/_cluster/health?wait_for_status=yellow&timeout=50s`
-  Nodes: `/_nodes`
-  Indices: `_cat/indices`
-  Create index: `twitter`
    -  `/PUT /twitter`
    -  `/GET /_cat/indices?v`
-  Insert data into index:
    - `/PUT /twitter/tweets/1`
    -  Body: `{"course":"Kafka for Beginners","instructor":"Stephane Maarek","module":"Elasticsearch"}`     
    -  Response:
```json
{
  "_index": "twitter",
  "_type": "tweets",
  "_id": "1",
  "_version": 1,
  "result": "created",
  "_shards": {
    "total": 2,
    "successful": 2,
    "failed": 0
  },
  "_seq_no": 0,
  "_primary_term": 1
}
```  
-  Update document:
    -  `/PUT /twitter/tweets/1` and new body  
-  Get document:
    -  `/GET /twitter/tweets/1` 
-  Delete document:       
    -  `/DELETE /twitter/tweets/1` 
-  Get again and 404
```json
{
  "_index": "twitter",
  "_type": "tweets",
  "_id": "1",
  "found": false
}
```
-  Delete the index
    -  `/DELETE /twitter` -> 200
```json
{
  "acknowledged": true
}
```            

#####  73. Consumer Part 1 - Setup Project

-  after running test we get document id: `cJw7nHoBWCBIynXURU6W`
-  `/GET /twitter/tweets/cJw7nHoBWCBIynXURU6W` - deprecated
-  **or**
-  `/GET /twitter/_doc/cJw7nHoBWCBIynXURU6W`
 
#####  76. Consumer Part 3 - Idempotence

-  2 strategies to create Elasticsearch ID
    -  kafka generic ID: `id = record.topic() + record.partition() + record.offset()`
    -  twitter feed specific ID: `id = extractIdFromTweet(record.value())`

#####  78. Consumer Offset Commit Strategies

-  2 strategies:
    -  (easy) `enable.auto.commit=true` & synchronous processing of batches
        -  offsets will be commited at a regular interval (`auto.commit.interval.ms=5000` by default) every time you call `.poll()`
        -  is quite risky because if you don't use synchronous processing you will be in `at-most-once` behaviour because offsets will be committed before data is processed    
    -  (medium) `enable.auto.commit=false` & manual commit of offsets
    
#####  Fixing bug when sending to Elasticsearch

-  increasing Total Fields limit in Local Elasticsearch

```shell script
curl --location --request PUT 'http://localhost:9200/twitter/_settings' \
--header 'Content-Type: application/json' \
--data-raw '{
  "index.mapping.total_fields.limit": 2000
}'
```

-  increasing Total Fields limit in Bonsai Elasticsearch

```
/PUT /twitter/_settings {"index.mapping.total_fields.limit": 2000}
```

#####  82. Consumer Part 6 - Replaying Data

-  `kafka-consumer-groups.bat --bootstrap-server localhost:29092 --group kafka-demo-elasticsearch --describe`
    -  LAG = 0 (we took all the messages and committed them)
    -  when consumers are running we get
```
GROUP                    TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                              HOST            CLIENT-ID
kafka-demo-elasticsearch twitter-tweets  0          148             148             0               consumer-kafka-demo-elasticsearch-1-49f3a15c-1e78-4699-81a8-abf52181fecc /172.26.0.1     consumer-kafka-demo-elasticsearch-1
kafka-demo-elasticsearch twitter-tweets  1          187             187             0               consumer-kafka-demo-elasticsearch-1-49f3a15c-1e78-4699-81a8-abf52181fecc /172.26.0.1     consumer-kafka-demo-elasticsearch-1
kafka-demo-elasticsearch twitter-tweets  2          181             181             0               consumer-kafka-demo-elasticsearch-1-49f3a15c-1e78-4699-81a8-abf52181fecc /172.26.0.1     consumer-kafka-demo-elasticsearch-1
```    
-  `kafka-consumer-groups.bat --bootstrap-server localhost:29092 --group kafka-demo-elasticsearch --reset-offsets --execute --to-earliest --topic twitter-tweets`

####  Section 12: Kafka Extended APIs for Developers

#####  86.1 Kafka Connect Twitter Hands-On - Locally

-  go to hithub and download [release](https://github.com/jcustenborder/kafka-connect-twitter/releases/tag/0.2.26)
-  connect-standalone.bat
```shell script
   USAGE: "c:\Users\Admin\Downloads\kafka\bin\windows\connect-standalone.bat" connect-standalone.properties
```
-  copy file `\kafka\config\connect-standalone.properties` into the project
    -  add `plugin.path=connectors`
-  create `twitter.properties` (also `twitter-secret.properties`)
-  create topics in kafka
    -  `kafka-topics.bat --create --topic twitter_status_connect --bootstrap-server localhost:29092 --replication-factor 1 --partitions 3`    
    -  `kafka-topics.bat --create --topic twitter_delete_connect --bootstrap-server localhost:29092 --replication-factor 1 --partitions 3`    
-  create `run.bat`
-  got something weird with a lot of warnings and not correct data in kafka consumer
```
C:\Users\Admin\IdeaProjects\Study\StephaneMaarek\Kafka\LearnApacheKafkaForBeginners_v2\art-maarek-kafka\kafka-connect>connect-standalone.bat connect-standalone.properties twitter-secret.properties 
[2021-07-13 15:43:50,246] WARN could not get type for name org.jdom.output.XMLOutputter from any class loader (org.reflections.Reflections)
org.reflections.ReflectionsException: could not get type for name org.jdom.output.XMLOutputter
	at org.reflections.ReflectionUtils.forName(ReflectionUtils.java:312)
	at org.reflections.Reflections.expandSuperTypes(Reflections.java:382)
	at org.reflections.Reflections.<init>(Reflections.java:140)
	at org.apache.kafka.connect.runtime.isolation.DelegatingClassLoader$InternalReflections.<init>(DelegatingClassLoader.java:444)
```
   
#####  86.2 Kafka Connect Twitter Hands-On - in Docker

-  start `docker-compose-kafka-elastic.yml`
-  go inside the container of kafka_2:
    -  `docker container exec -it confluentinc-baeldung_kafka-2_1 sh`
    -  `cd /usr/share/kafka-connect/`
-  start kafka connect    
    -  `connect-standalone connect-standalone.properties twitter-secret.properties`
-  view consumer of `twitter_status_connect`
-  stop it - `Ctrl+C`
-  **OR**
-  run command directly
    -  `docker container exec -it -w /usr/share/kafka-connect confluentinc-baeldung_kafka-2_1 sh`
    -  `./run.sh`
-  **OR**
-  `docker container exec -it -w /usr/share/kafka-connect confluentinc-baeldung_kafka-2_1 sh "./run.sh"`    



              