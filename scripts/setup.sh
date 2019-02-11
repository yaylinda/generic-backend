sudo su

sudo yum update -y

sudo yum install -y git

git clone https://github.com/yaylinda/generic-backend.git
git clone https://github.com/yaylinda/generic-frontend.git

sudo sh -c "echo '[mongodb-org-4.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/amazon/2013.03/mongodb-org/4.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-4.0.asc' > /etc/yum.repos.d/mongodb-org-4.0.repo"

sudo yum install -y mongodb-org

sudo yum install -y java-1.8.0-openjdk-devel

sudo yum install -y wget

sudo wget http://mirrors.gigenet.com/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz

sudo tar -xvf apache-maven-3.6.0-bin.tar.gz

export PATH=/home/ec2-user/apache-maven-3.6.0/bin:$PATH

rm -rf apache-maven-3.6.0-bin.tar.gz

sudo wget http://nodejs.org/dist/latest/node-v11.9.0-linux-x64.tar.xz

export PATH=/home/ec2-user/node-v11.9.0-linux-x64/bin:$PATH

rm -rf node-v11.9.0-linux-x64.tar.gz

npm install -g npm

cd /home/ec2-user/generic-backend

mvn clean install

cd /home/ec2-user/generic-frontend

npm install

npm install -g @angular/cli

npm i --save-exact --save terser@3.16.1

sudo chmod u+x /home/ec2-user/generic-backend/scripts/start-app-dev.service

sudo chmod u+x /home/ec2-user/generic-backend/scripts/start-app-dev.sh

sudo cp /home/ec2-user/generic-backend/scripts/start-app-dev.service /etc/systemd/system/start-app-dev.service

sudo chmod u+x /home/ec2-user/generic-backend/scripts/start-app-master.service

sudo chmod u+x /home/ec2-user/generic-backend/scripts/start-app-master.sh

sudo cp /home/ec2-user/generic-backend/scripts/start-app-master.service /etc/systemd/system/start-app-master.service