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

  


 