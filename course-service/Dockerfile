FROM openjdk:17-slim
WORKDIR /app
COPY target/course-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 1235
ENTRYPOINT ["java", "-jar", "app.jar"]