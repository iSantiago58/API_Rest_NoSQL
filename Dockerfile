FROM openjdk:11
COPY src/main/resources/obligatorio-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
