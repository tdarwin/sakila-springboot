package tdarwin.sakila.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import tdarwin.sakila.metrics.DatabaseConnectionMetrics;
import tdarwin.sakila.metrics.MovieSearchMetrics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Configuration class for JMX MBean registration.
 * Registers all custom MBeans in the "sakila" domain.
 */
@Configuration
@DependsOn({"movieSearchMetrics", "databaseConnectionMetrics"})
public class JmxConfig {
    
    private static final String JMX_DOMAIN = "sakila";
    
    private final MBeanServer mBeanServer;
    private final MovieSearchMetrics movieSearchMetrics;
    private final DatabaseConnectionMetrics databaseConnectionMetrics;
    
    private ObjectName movieSearchObjectName;
    private ObjectName databaseConnectionObjectName;
    
    public JmxConfig(MovieSearchMetrics movieSearchMetrics, 
                     DatabaseConnectionMetrics databaseConnectionMetrics) {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        this.movieSearchMetrics = movieSearchMetrics;
        this.databaseConnectionMetrics = databaseConnectionMetrics;
    }
    
    @PostConstruct
    public void registerMBeans() {
        try {
            // Register MovieSearchMetrics MBean
            movieSearchObjectName = new ObjectName(JMX_DOMAIN + ":type=MovieSearchMetrics,name=SearchCounter");
            if (!mBeanServer.isRegistered(movieSearchObjectName)) {
                mBeanServer.registerMBean(movieSearchMetrics, movieSearchObjectName);
                System.out.println("Registered MovieSearchMetrics MBean: " + movieSearchObjectName);
            }
            
            // Register DatabaseConnectionMetrics MBean
            databaseConnectionObjectName = new ObjectName(JMX_DOMAIN + ":type=DatabaseConnectionMetrics,name=ConnectionPool");
            if (!mBeanServer.isRegistered(databaseConnectionObjectName)) {
                mBeanServer.registerMBean(databaseConnectionMetrics, databaseConnectionObjectName);
                System.out.println("Registered DatabaseConnectionMetrics MBean: " + databaseConnectionObjectName);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to register JMX MBeans: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @PreDestroy
    public void unregisterMBeans() {
        try {
            // Unregister MovieSearchMetrics MBean
            if (movieSearchObjectName != null && mBeanServer.isRegistered(movieSearchObjectName)) {
                mBeanServer.unregisterMBean(movieSearchObjectName);
                System.out.println("Unregistered MovieSearchMetrics MBean: " + movieSearchObjectName);
            }
            
            // Unregister DatabaseConnectionMetrics MBean
            if (databaseConnectionObjectName != null && mBeanServer.isRegistered(databaseConnectionObjectName)) {
                mBeanServer.unregisterMBean(databaseConnectionObjectName);
                System.out.println("Unregistered DatabaseConnectionMetrics MBean: " + databaseConnectionObjectName);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to unregister JMX MBeans: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
