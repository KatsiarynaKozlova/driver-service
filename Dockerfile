FROM maven:3.8.8-eclipse-temurin-17  AS build-pass-serv
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build-pass-serv /app/target/driver-service-0.0.1-SNAPSHOT.jar /app/driver-service-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/driver-service-0.0.1-SNAPSHOT.jar"]
