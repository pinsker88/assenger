##
## Multistage Dockerfile for the Spring Boot signaling server
##
## The first stage builds the Java application using Maven inside an
## official Maven image. The second stage uses a minimal JRE image to
## run the compiled JAR. Separating build and runtime stages keeps the
## final image lean and free of build‑time dependencies. You can
## customise the Maven version or the JRE version as needed.

# -------- build stage --------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven build descriptor and download dependencies in a separate
# step to leverage Docker layer caching. This speeds up iterative builds
# because dependencies are only downloaded again if pom.xml changes.
COPY . .

# Package the application. Skip tests to minimise build time. Adjust
# flags if you need test execution in your CI.
RUN mvn clean package

# -------- runtime stage --------
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Copy the packaged JAR from the build stage into this runtime image.
COPY --from=build /app/target/signal-server-0.0.1.jar app.jar

# Expose the default port that the Spring Boot application listens on.
EXPOSE 8080

# Run the application using the JVM. The entrypoint uses "exec" form
# which ensures the process receives UNIX signals correctly (e.g. SIGTERM
# for graceful shutdown). If you wish to add JVM options such as heap
# sizing or GC tuning, append them here.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]