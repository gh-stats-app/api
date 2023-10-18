FROM eclipse-temurin:21-jdk-alpine
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Xmx180M", "-jar", "/app.jar"]
