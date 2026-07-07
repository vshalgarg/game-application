#Stage 1: Build stage to make jar

FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

#first copy pom.xml to download dependency for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

#now copy source code and make jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage (to RUN App)
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy jar from builder stage which is stage-1
COPY --from=builder /build/target/*.jar ludo-engine.jar

#this open the port within container
EXPOSE 808X
ENTRYPOINT ["java", "-jar", "ludo-engine.jar"]