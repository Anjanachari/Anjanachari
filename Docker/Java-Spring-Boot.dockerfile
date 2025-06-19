FROM eclipse-temurin:17-jdk

LABEL maintainer="you@example.com"
WORKDIR /app

COPY target/myapp.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
