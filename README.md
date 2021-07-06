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

    