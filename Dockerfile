FROM amazoncorretto:21 AS build
WORKDIR /workspace

RUN yum install -y findutils && yum clean all

COPY . .
RUN ./gradlew clean bootJar --no-daemon

FROM amazoncorretto:21 AS runtime
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]
