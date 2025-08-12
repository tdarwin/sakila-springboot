# Sakila JMX Metrics

This application now exposes JMX metrics in a custom domain called "sakila" that track:

1. **Movie Search Metrics** - Monotonic count of movies returned from searches
2. **Database Connection Metrics** - Current number of database connections

## JMX MBeans

### 1. Movie Search Metrics

- **Object Name**: `sakila:type=MovieSearchMetrics,name=SearchCounter`
- **Attributes**:
  - `TotalMoviesReturned` - Total number of movies returned from all searches (monotonic counter)
  - `TotalSearchOperations` - Total number of search operations performed (monotonic counter)
  - `AverageMoviesPerSearch` - Average number of movies returned per search
- **Operations**:
  - `resetCounters()` - Reset all counters to zero (for testing/administrative purposes)

### 2. Database Connection Metrics

- **Object Name**: `sakila:type=DatabaseConnectionMetrics,name=ConnectionPool`
- **Attributes**:
  - `CurrentActiveConnections` - Current number of active database connections
  - `MaxConnections` - Maximum number of connections allowed in the pool
  - `MinConnections` - Minimum number of connections maintained in the pool
  - `IdleConnections` - Number of connections currently idle in the pool
  - `ConnectionPoolUsagePercent` - Connection pool usage as a percentage (0-100)

## How to Access JMX Metrics

### 1. Using JConsole (Remote Connection)

1. Start the application using Docker Compose: `docker-compose up -d`
2. Open JConsole
3. Choose "Remote Process" 
4. Enter: `localhost:1099` (or `<docker-host-ip>:1099` if running on a remote Docker host)
5. Click "Connect" (no authentication required)
6. Navigate to MBeans tab
7. Look for the "sakila" domain
8. Expand the metrics to view attributes and operations

**Note**: JMX is configured without authentication and SSL for development purposes. In production, you should enable proper security.

### 2. Using JMX HTTP Endpoint (via Spring Actuator)

With Spring Actuator enabled, you can access JMX data via HTTP:

```bash
# View all JMX domains
curl http://localhost:8080/actuator/jmx

# View specific MBean
curl http://localhost:8080/actuator/jmx/sakila:type=MovieSearchMetrics,name=SearchCounter

curl http://localhost:8080/actuator/jmx/sakila:type=DatabaseConnectionMetrics,name=ConnectionPool
```

### 3. Programmatic Access

The metrics are automatically updated when:

- Movie searches are performed via the `/api/films/search?title=<title>` endpoint
- Database connections are created/destroyed by the HikariCP connection pool

## Testing the Metrics

1. Start the application
2. Make some search requests:

   ```bash
   curl "http://localhost:8080/api/films/search?title=action"
   curl "http://localhost:8080/api/films/search?title=comedy"
   ```
3. Check the JMX metrics to see the counters increment

## Configuration

### JMX Remote Access Configuration

**In `Dockerfile`** - JVM arguments for remote JMX access:
```bash
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=1099
-Dcom.sun.management.jmxremote.rmi.port=1099
-Dcom.sun.management.jmxremote.local.only=false
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Djava.rmi.server.hostname=0.0.0.0
```

**In `application.properties`**:
```properties
spring.jmx.enabled=true
spring.jmx.default-domain=sakila
management.endpoints.web.exposure.include=health,info,metrics,jmx
management.endpoints.jmx.exposure.include=*
```

**In `docker-compose.yaml`** - Port mapping:
```yaml
ports:
  - "1099:1099"  # JMX port
```

### Security Note
This configuration disables JMX authentication and SSL for development ease. For production:
1. Enable authentication: `-Dcom.sun.management.jmxremote.authenticate=true`
2. Enable SSL: `-Dcom.sun.management.jmxremote.ssl=true`
3. Configure password and access files
4. Use proper certificates
