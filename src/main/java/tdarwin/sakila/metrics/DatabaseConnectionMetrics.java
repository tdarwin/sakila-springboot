package tdarwin.sakila.metrics;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * JMX MBean implementation for database connection metrics.
 * Uses HikariCP connection pool metrics.
 */
@Component
public class DatabaseConnectionMetrics implements DatabaseConnectionMetricsMBean {
    
    private final DataSource dataSource;
    
    public DatabaseConnectionMetrics(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private HikariPoolMXBean getHikariPoolMXBean() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            return hikariDataSource.getHikariPoolMXBean();
        }
        return null;
    }
    
    @Override
    public int getCurrentActiveConnections() {
        HikariPoolMXBean poolMXBean = getHikariPoolMXBean();
        if (poolMXBean != null) {
            return poolMXBean.getActiveConnections();
        }
        return -1; // Unable to determine
    }
    
    @Override
    public int getMaxConnections() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            return hikariDataSource.getMaximumPoolSize();
        }
        return -1; // Unable to determine
    }
    
    @Override
    public int getMinConnections() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            return hikariDataSource.getMinimumIdle();
        }
        return -1; // Unable to determine
    }
    
    @Override
    public int getIdleConnections() {
        HikariPoolMXBean poolMXBean = getHikariPoolMXBean();
        if (poolMXBean != null) {
            return poolMXBean.getIdleConnections();
        }
        return -1; // Unable to determine
    }
    
    @Override
    public double getConnectionPoolUsagePercent() {
        int active = getCurrentActiveConnections();
        int max = getMaxConnections();
        
        if (active >= 0 && max > 0) {
            return (double) active / max * 100.0;
        }
        return -1.0; // Unable to determine
    }
}
