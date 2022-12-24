FROM ubuntu:latest
FROM openjdk:17
ADD /target/tx-rx-server.jar tx-rx-server
EXPOSE 8090
ENTRYPOINT ["java","-jar","tx-rx-server.jar"]