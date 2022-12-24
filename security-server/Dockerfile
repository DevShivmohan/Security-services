FROM ubuntu:latest
FROM openjdk:17
WORKDIR /home/send
ADD /security-server/target/tx-rx-server.jar /home/send/tx-rx-server.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","tx-rx-server.jar"]