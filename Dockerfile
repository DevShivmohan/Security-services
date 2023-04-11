FROM ubuntu:latest
FROM openjdk:17
CMD apt-get install net-tools
ADD /security-server/target/tx-rx-server.jar tx-rx-server.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","tx-rx-server.jar"]
