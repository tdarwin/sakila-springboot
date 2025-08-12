package tdarwin.sakila.metrics;

/**
 * JMX MBean interface for movie search metrics.
 * Provides monotonic count of number of movies returned from searches.
 */
public interface MovieSearchMetricsMBean {
    
    /**
     * Get the total number of movies returned from all searches since application start.
     * This is a monotonic counter that only increases.
     * 
     * @return total count of movies returned from searches
     */
    long getTotalMoviesReturned();
    
    /**
     * Get the total number of search operations performed since application start.
     * This is a monotonic counter that only increases.
     * 
     * @return total count of search operations
     */
    long getTotalSearchOperations();
    
    /**
     * Get the average number of movies returned per search.
     * 
     * @return average movies per search, or 0 if no searches performed
     */
    double getAverageMoviesPerSearch();
    
    /**
     * Reset all counters to zero (for testing/administrative purposes).
     */
    void resetCounters();
}
