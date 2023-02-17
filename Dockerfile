FROM eclipse-temurin:19-jdk-alpine
COPY ./build/libs/*.jar app.jar
ENTRYPOINT [
    "java",
    "-Xmx200M",
    "-jar",
    "/app.jar"
]
