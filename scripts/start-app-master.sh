#!/bin/sh
export PATH=/home/ec2-user/apache-maven-3.6.2/bin:$PATH
echo $PATH
sudo service mongod start
cd /home/ec2-user/generic-backend
git checkout master
git pull
mvn spring-boot:run &