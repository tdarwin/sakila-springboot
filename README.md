# Sakila Demo App

A simple spring app that connects to a postgresql database populated with the [Sakila Database](https://github.com/jOOQ/sakila).

The app provides a REST API that can be used to search for movies by title and is instrumented with OpenTelemetry for distributed tracing.

This can be built into a jar with Maven and used however you like.

## The rest of the repository

This repo also has some docker compose components to allow you to easily test the app and generate observability data with opentelemetry, sending it through the opentelemetry collector

## Setup

1. Add your Honeycomb API Key to the environment variable in the docker-compose.yaml file.  OR reconfigure the collector to send to the observability platform of your choice

2. Start the application with Docker Compose:

   ```shell
   docker-compose up -d
   ```

3. Access the API at http://localhost:8080/api/films/search?title=ACADEMY

   ```shell
   curl http://localhost:8080/api/films/search?title=ACADEMY
   ```

4. If you're looking to generate a continuous stream of telemetry, you can run the search.sh script which will slowly iterate through a list of search words forward, and then backwards. This will generate lots of telemetry

## Observability

The application is instrumented with OpenTelemetry and sends telemetry data to the configured collector. You can view traces in Honeycomb or any other compatible observability platform.
