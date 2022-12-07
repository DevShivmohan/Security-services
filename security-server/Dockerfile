FROM ubuntu:latest
FROM openjdk:17
VOLUME /tmp
EXPOSE 8090
ADD target/tx-rx-server.jar tx-rx-server.jar
ENTRYPOINT ["java","-jar","/tx-rx-server.jar"]