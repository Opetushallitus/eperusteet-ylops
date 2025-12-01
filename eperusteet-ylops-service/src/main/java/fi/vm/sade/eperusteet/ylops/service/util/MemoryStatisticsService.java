package fi.vm.sade.eperusteet.ylops.service.util;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

public interface MemoryStatisticsService {
    
    /**
     * Get comprehensive cache statistics including memory usage, hit rates, and evictions.
     * 
     * @return Map of cache names to their statistics
     */
    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    Map<String, Map<String, Object>> getCacheStatistics();
    
    /**
     * Get a quick summary of cache memory usage and JVM heap status.
     * 
     * @return Map containing memory summary information
     */
    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    Map<String, Object> getCacheMemorySummary();
}

