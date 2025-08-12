package tdarwin.sakila.metrics;

/**
 * JMX MBean interface for database connection metrics.
 * Provides current number of database connections.
 */
public interface DatabaseConnectionMetricsMBean {
    
    /**
     * Get the current number of active database connections.
     * 
     * @return current active database connections
     */
    int getCurrentActiveConnections();
    
    /**
     * Get the maximum number of database connections allowed in the pool.
     * 
     * @return maximum connections in pool
     */
    int getMaxConnections();
    
    /**
     * Get the minimum number of database connections maintained in the pool.
     * 
     * @return minimum connections in pool
     */
    int getMinConnections();
    
    /**
     * Get the number of connections currently idle in the pool.
     * 
     * @return idle connections count
     */
    int getIdleConnections();
    
    /**
     * Get connection pool usage as a percentage.
     * 
     * @return usage percentage (0-100)
     */
    double getConnectionPoolUsagePercent();
}
