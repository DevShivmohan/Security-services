FROM ubuntu:latest
FROM openjdk:17
WORKDIR /usr/src/tx-rx-server
COPY /target/tx-rx-server.jar /usr/src/tx-rx-server/
EXPOSE 8090
ENTRYPOINT ["java","-jar","tx-rx-server.jar"]