FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY target/nevis*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]