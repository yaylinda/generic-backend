sudo su

sudo yum update -y

sudo yum install -y lsof

sudo yum install -y git

git clone https://github.com/yaylinda/generic-backend.git

sudo sh -c "echo '[mongodb-org-4.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/amazon/2013.03/mongodb-org/4.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-4.0.asc' > /etc/yum.repos.d/mongodb-org-4.0.repo"

sudo yum install -y mongodb-org

sudo yum install -y java-1.8.0-openjdk-devel

sudo yum install -y wget

sudo wget http://mirrors.ibiblio.org/apache/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.tar.gz

sudo tar -xvf apache-maven-3.6.2-bin.tar.gz

export PATH=/home/ec2-user/apache-maven-3.6.2/bin:$PATH

rm -rf apache-maven-3.6.2-bin.tar.gz

cd /home/ec2-user/generic-backend

mvn clean install

sudo chmod u+x /home/ec2-user/generic-backend/scripts/start-app-dev.service

sudo chmod u+x /home/ec2-user/generic-backend/scripts/start-app-dev.sh

sudo cp /home/ec2-user/generic-backend/scripts/start-app-dev.service /etc/systemd/system/start-app-dev.service

sudo systemctl enable start-app-dev

sudo systemctl start start-app-dev
