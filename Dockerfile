FROM eclipse-temurin:26-jdk-alpine AS builder
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN java -Djarmode=tools \
    -jar app.jar \
    extract \
    --layers \
    --launcher \
    --destination extracted

FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring /app/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/extracted/application/ ./

USER spring:spring
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
