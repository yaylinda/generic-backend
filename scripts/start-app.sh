#!/bin/sh
export PATH=/home/ec2-user/apache-maven-3.6.0/bin:$PATH
export PATH=/home/ec2-user/node-v11.9.0-linux-x64/bin:$PATH
echo $PATH
sudo service mongod start
cd /home/ec2-user/generic-backend
git pull
mvn spring-boot:run &
cd /home/ec2-user/generic-frontend
git pull
ng serve --host 0.0.0.0 -c dev
