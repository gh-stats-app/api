FROM eclipse-temurin:25-jdk-alpine
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Xmx220M", "-jar", "/app.jar"]
