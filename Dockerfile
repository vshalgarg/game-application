# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


# Runtime Stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar tic-tac-toe-game-engine.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "tic-tac-toe-game-engine.jar"]