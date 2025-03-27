FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Download the OpenTelemetry Java agent
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar -o opentelemetry-javaagent.jar

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy OpenTelemetry Java agent
COPY --from=build /app/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy the built JAR file
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Set the command to run the application with OpenTelemetry
ENTRYPOINT ["java", \
  "-javaagent:/app/opentelemetry-javaagent.jar", \
  "-Dotel.service.name=sakila-demo", \
  "-Dotel.traces.exporter=otlp", \
  "-Dotel.metrics.exporter=otlp", \
  "-Dotel.exporter.otlp.endpoint=http://otel-collector:4317", \
  "-Dotel.exporter.otlp.protocol=grpc", \
  "-Dotel.metric.export.interval=15000", \
  "-jar", "/app/app.jar"]