### update-system.sh
```bash
echo "----- Updating and installing necessary packages"
yum update -y

echo "----- Install Node, npm, nginx git
yum install -y git nginx
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
dnf install nodejs -y

echo "----- Configure GIT credentials"
ssh-keyscan github.com >> /home/ec2-user/.ssh/known_hosts
chown -R ec2-user:ec2-user /home/ec2-user/.ssh
aws ssm get-parameter --name "GitHubSSHKey" --with-decryption --region us-east-1 --output text --query Parameter.Value > /home/ec2-user/.ssh/id_rsa
chmod 600 /home/ec2-user/.ssh/id_rsa

echo "----- Cloning ilim-frontend"
git clone git@github.com:rahuljha-07/ENPM-613-FE.git

echo "----- Install ilim-frontend deps"
cd ENPM-613-FE
npm install --legacy-peer-deps

#echo "----- Build ilim-frontend"
#npm run build

echo "----- Run ilim-frontend"
#npm start
npm run dev

echo "----- Configure nginx"
sudo mkdir /etc/nginx/sites-enabled/

sudo cat > /etc/nginx/sites-available/ilim-frontend <<EOL
server {
    listen 80;
    server_name ilim.online www.ilim.online;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
EOL

# Disable the default Nginx configuration
sudo rm /etc/nginx/sites-enabled/default

# Enable your configuration
sudo ln -s /etc/nginx/sites-available/ilim-frontend /etc/nginx/sites-enabled/

# Test Nginx configuration
sudo nginx -t

echo "----- Start nginx"
# Restart Nginx
sudo systemctl restart nginx


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