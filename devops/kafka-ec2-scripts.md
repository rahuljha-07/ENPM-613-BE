### update-system.sh
```bash
echo "----- Updating and installing necessary packages"
yum update -y
yum install -y git java-21-amazon-corretto-devel maven
```

### install-kafka.sh
```bash

#!/bin/bash
echo "----- Installing Kafka"
wget https://archive.apache.org/dist/kafka/2.8.0/kafka_2.13-2.8.0.tgz
tar -xzf kafka_2.13-2.8.0.tgz
mv kafka_2.13-2.8.0 kafka
cd kafka

echo "----- Configuring Kafka Retention Policies"
cat >> config/server.properties <<EOL
log.retention.hours=1
log.retention.bytes=104857600
log.cleanup.policy=delete
EOL

echo "----- Starting Zookeeper and Kafka (in the background)"
export KAFKA_HEAP_OPTS="-Xms256M -Xmx512M"
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > /home/ec2-user/zookeeper.log 2>&1 &
sleep 5
nohup bin/kafka-server-start.sh config/server.properties > /home/ec2-user/kafka.log 2>&1 &
```