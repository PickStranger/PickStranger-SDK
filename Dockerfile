FROM amazoncorretto:21-alpine3.24-jdk AS build
WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

RUN cp "$(ls build/libs/*.jar | grep -v plain)" app.jar

FROM amazoncorretto:21-alpine3.24-jdk AS runtime
WORKDIR /app

COPY --from=build /workspace/app.jar app.jar

EXPOSE 9091

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
