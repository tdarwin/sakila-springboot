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
EXPOSE 1099

# Set the command to run the application with OpenTelemetry and JMX
ENTRYPOINT ["java", \
  "-javaagent:/app/opentelemetry-javaagent.jar", \
  "-Dotel.service.name=sakila-demo", \
  "-Dotel.traces.exporter=otlp", \
  "-Dotel.metrics.exporter=otlp", \
  "-Dotel.exporter.otlp.endpoint=http://otel-collector:4317", \
  "-Dotel.exporter.otlp.protocol=grpc", \
  "-Dotel.metric.export.interval=10000", \
  "-Dotel.jmx.config=/jmx-rules.yaml", \
  "-Dcom.sun.management.jmxremote", \
  "-Dcom.sun.management.jmxremote.port=1099", \
  "-Dcom.sun.management.jmxremote.rmi.port=1099", \
  "-Dcom.sun.management.jmxremote.local.only=false", \
  "-Dcom.sun.management.jmxremote.authenticate=false", \
  "-Dcom.sun.management.jmxremote.ssl=false", \
  "-Djava.rmi.server.hostname=0.0.0.0", \
  "-jar", "/app/app.jar"]