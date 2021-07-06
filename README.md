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

