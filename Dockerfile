# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# Runtime Stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar game-service.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "game-service.jar"]