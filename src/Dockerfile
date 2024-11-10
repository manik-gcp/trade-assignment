# Dockerfile
FROM openjdk:11
WORKDIR /app
COPY target/trade-store.jar trade-store.jar
ENTRYPOINT ["java", "-jar", "trade-store.jar"]
