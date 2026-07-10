# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


# Runtime Stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar game-service.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "game-service.jar"]