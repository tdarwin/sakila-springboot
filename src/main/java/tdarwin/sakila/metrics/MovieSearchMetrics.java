package tdarwin.sakila.metrics;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JMX MBean implementation for movie search metrics.
 * Thread-safe implementation using atomic counters.
 */
@Component
public class MovieSearchMetrics implements MovieSearchMetricsMBean {
    
    private final AtomicLong totalMoviesReturned = new AtomicLong(0);
    private final AtomicLong totalSearchOperations = new AtomicLong(0);
    
    @Override
    public long getTotalMoviesReturned() {
        return totalMoviesReturned.get();
    }
    
    @Override
    public long getTotalSearchOperations() {
        return totalSearchOperations.get();
    }
    
    @Override
    public double getAverageMoviesPerSearch() {
        long operations = totalSearchOperations.get();
        if (operations == 0) {
            return 0.0;
        }
        return (double) totalMoviesReturned.get() / operations;
    }
    
    @Override
    public void resetCounters() {
        totalMoviesReturned.set(0);
        totalSearchOperations.set(0);
    }
    
    /**
     * Record a search operation and the number of movies returned.
     * This method is called by the service layer to update metrics.
     * 
     * @param moviesCount the number of movies returned from the search
     */
    public void recordSearch(int moviesCount) {
        totalMoviesReturned.addAndGet(moviesCount);
        totalSearchOperations.incrementAndGet();
    }
}
