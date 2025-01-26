FROM maven:3.6.3-jdk-11 AS package
ENV DB_HOST database
RUN mvn clean package
FROM openjdk:21
ENV db_password db_password
ARG JAR_FILE=./target/yacontest-helper*.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
