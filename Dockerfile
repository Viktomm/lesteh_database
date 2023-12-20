FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=target/*.jar
ENV MONGODB_URI=mongodb://mongo:27017/dbrobo
COPY ${JAR_FILE} dbrobo-service.jar
ENTRYPOINT ["java","-jar","-Dspring.data.mongodb.uri=${MONGODB_URI}","/dbrobo-service.jar"]
