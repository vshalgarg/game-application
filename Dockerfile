FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar tic-tac-toe-game-engine.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","tic-tac-toe-game-engine.jar"]