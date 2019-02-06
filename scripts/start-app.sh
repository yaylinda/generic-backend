sudo service mongod start
cd /home/ec2-user/generic-backend
mvn spring-boot:run &
cd /home/ec2-user/generic-frontend
ng serve --host 0.0.0.0 -c dev