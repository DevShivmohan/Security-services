FROM ubuntu:latest
FROM openjdk:17
ADD /security-server/target/tx-rx-server.jar tx-rx-server.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","tx-rx-server.jar"]
