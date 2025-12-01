package fi.vm.sade.eperusteet.ylops.service.util.impl;

import fi.vm.sade.eperusteet.ylops.service.util.MemoryStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
public class MemoryStatisticsServiceImpl implements MemoryStatisticsService {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Map<String, Map<String, Object>> getCacheStatistics() {
        Map<String, Map<String, Object>> statistics = new LinkedHashMap<>();
        
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            long totalMemoryBytes = 0;
            long totalEntries = 0;
            
            for (String cacheName : cacheManager.getCacheNames()) {
                try {
                    Map<String, Object> cacheStats = new LinkedHashMap<>();
                    
                    // Get the native cache to count entries
                    org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);
                    if (springCache != null) {
                        Object nativeCache = springCache.getNativeCache();
                        
                        // Count entries
                        long cacheSize = 0;
                        if (nativeCache instanceof Cache) {
                            @SuppressWarnings({"unchecked", "resource"})
                            Cache<Object, Object> jcache = (Cache<Object, Object>) nativeCache;
                            cacheSize = StreamSupport.stream(jcache.spliterator(), false).count();
                            cacheStats.put("entries", cacheSize);
                            totalEntries += cacheSize;
                        }
                        
                        // Try to get memory statistics from Ehcache management MBean
                        try {
                            ObjectName poolObjectName = new ObjectName(
                                "org.ehcache:type=CacheManager,name=*,Cache=" + cacheName + ",tier=*"
                            );
                            Set<ObjectName> poolMbeans = mBeanServer.queryNames(poolObjectName, null);
                            
                            long cacheMemory = 0;
                            for (ObjectName poolMbean : poolMbeans) {
                                try {
                                    // Try to get occupiedByteSize or allocatedByteSize
                                    Long occupiedSize = null;
                                    try {
                                        occupiedSize = (Long) mBeanServer.getAttribute(poolMbean, "OccupiedByteSize");
                                    } catch (Exception ignored) {
                                        // Attribute might not exist
                                    }
                                    
                                    if (occupiedSize != null && occupiedSize > 0) {
                                        cacheMemory += occupiedSize;
                                        String tier = poolMbean.getKeyProperty("tier");
                                        cacheStats.put("memory_" + tier + "_bytes", occupiedSize);
                                        cacheStats.put("memory_" + tier + "_MB", String.format("%.2f MB", occupiedSize / (1024.0 * 1024.0)));
                                    }
                                } catch (Exception e) {
                                    // Continue to next tier
                                }
                            }
                            
                            if (cacheMemory > 0) {
                                cacheStats.put("totalMemoryBytes", cacheMemory);
                                cacheStats.put("totalMemoryMB", String.format("%.2f MB", cacheMemory / (1024.0 * 1024.0)));
                                totalMemoryBytes += cacheMemory;
                            }
                        } catch (Exception e) {
                            // Memory statistics might not be available
                            cacheStats.put("memoryNote", "Exact memory usage not available via MBean");
                        }
                    }
                    
                    // Get JMX cache statistics
                    try {
                        ObjectName objectName = new ObjectName(
                            "javax.cache:type=CacheStatistics,CacheManager=*,Cache=" + cacheName
                        );
                        
                        Set<ObjectName> mbeans = mBeanServer.queryNames(objectName, null);
                        
                        if (!mbeans.isEmpty()) {
                            ObjectName mbean = mbeans.iterator().next();
                            
                            // Basic statistics
                            Long cacheHits = (Long) mBeanServer.getAttribute(mbean, "CacheHits");
                            Long cacheMisses = (Long) mBeanServer.getAttribute(mbean, "CacheMisses");
                            Long cachePuts = (Long) mBeanServer.getAttribute(mbean, "CachePuts");
                            Long cacheRemovals = (Long) mBeanServer.getAttribute(mbean, "CacheRemovals");
                            Long cacheEvictions = (Long) mBeanServer.getAttribute(mbean, "CacheEvictions");
                            
                            Float hitPercentage = (Float) mBeanServer.getAttribute(mbean, "CacheHitPercentage");
                            Float missPercentage = (Float) mBeanServer.getAttribute(mbean, "CacheMissPercentage");
                            
                            Float averageGetTime = (Float) mBeanServer.getAttribute(mbean, "AverageGetTime");
                            Float averagePutTime = (Float) mBeanServer.getAttribute(mbean, "AveragePutTime");
                            Float averageRemoveTime = (Float) mBeanServer.getAttribute(mbean, "AverageRemoveTime");
                            
                            cacheStats.put("hits", cacheHits);
                            cacheStats.put("misses", cacheMisses);
                            cacheStats.put("puts", cachePuts);
                            cacheStats.put("removals", cacheRemovals);
                            cacheStats.put("evictions", cacheEvictions);
                            cacheStats.put("hitPercentage", String.format("%.2f%%", hitPercentage));
                            cacheStats.put("missPercentage", String.format("%.2f%%", missPercentage));
                            cacheStats.put("averageGetTime", String.format("%.4f ms", averageGetTime));
                            cacheStats.put("averagePutTime", String.format("%.4f ms", averagePutTime));
                            cacheStats.put("averageRemoveTime", String.format("%.4f ms", averageRemoveTime));
                            
                            // Calculate total requests
                            long totalRequests = cacheHits + cacheMisses;
                            cacheStats.put("totalRequests", totalRequests);
                        }
                    } catch (Exception e) {
                        cacheStats.put("jmxStatsError", e.getMessage());
                    }
                    
                    statistics.put(cacheName, cacheStats);
                } catch (Exception e) {
                    Map<String, Object> cacheStats = new LinkedHashMap<>();
                    cacheStats.put("error", "Failed to retrieve statistics: " + e.getMessage());
                    statistics.put(cacheName, cacheStats);
                }
            }
            
            // Add summary with memory information
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("totalCaches", cacheManager.getCacheNames().size());
            summary.put("totalEntries", totalEntries);
            
            if (totalMemoryBytes > 0) {
                summary.put("totalCacheMemoryBytes", totalMemoryBytes);
                summary.put("totalCacheMemoryMB", String.format("%.2f MB", totalMemoryBytes / (1024.0 * 1024.0)));
            } else {
                summary.put("note", "Exact memory measurements require accessing Ehcache MBeans - check individual cache entries counts");
            }
            
            // Add JVM memory information for context
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            Map<String, Object> jvmMemory = new LinkedHashMap<>();
            jvmMemory.put("maxHeapBytes", maxMemory);
            jvmMemory.put("maxHeapMB", String.format("%.2f MB", maxMemory / (1024.0 * 1024.0)));
            jvmMemory.put("totalHeapBytes", totalMemory);
            jvmMemory.put("totalHeapMB", String.format("%.2f MB", totalMemory / (1024.0 * 1024.0)));
            jvmMemory.put("usedHeapBytes", usedMemory);
            jvmMemory.put("usedHeapMB", String.format("%.2f MB", usedMemory / (1024.0 * 1024.0)));
            jvmMemory.put("freeHeapBytes", freeMemory);
            jvmMemory.put("freeHeapMB", String.format("%.2f MB", freeMemory / (1024.0 * 1024.0)));
            jvmMemory.put("heapUsagePercentage", String.format("%.2f%%", (usedMemory * 100.0) / maxMemory));
            
            if (totalMemoryBytes > 0 && usedMemory > 0) {
                jvmMemory.put("cachePercentageOfUsedHeap", 
                    String.format("%.2f%%", (totalMemoryBytes * 100.0) / usedMemory));
                jvmMemory.put("cachePercentageOfMaxHeap", 
                    String.format("%.2f%%", (totalMemoryBytes * 100.0) / maxMemory));
            }
            
            summary.put("jvmMemory", jvmMemory);
            summary.put("timestamp", System.currentTimeMillis());
            statistics.put("_summary", summary);
            
            return statistics;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Failed to retrieve cache statistics: " + e.getMessage());
            error.put("stackTrace", e.toString());
            statistics.put("_error", error);
            return statistics;
        }
    }

    @Override
    public Map<String, Object> getCacheMemorySummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        
        try {
            // JVM memory information
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            summary.put("maxHeapMB", String.format("%.2f", maxMemory / (1024.0 * 1024.0)));
            summary.put("usedHeapMB", String.format("%.2f", usedMemory / (1024.0 * 1024.0)));
            summary.put("freeHeapMB", String.format("%.2f", freeMemory / (1024.0 * 1024.0)));
            summary.put("heapUsagePercent", String.format("%.2f%%", (usedMemory * 100.0) / maxMemory));
            
            // Cache entry counts
            Map<String, Long> cacheEntries = new LinkedHashMap<>();
            long totalEntries = 0;
            
            for (String cacheName : cacheManager.getCacheNames()) {
                org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);
                if (springCache != null) {
                    Object nativeCache = springCache.getNativeCache();
                    if (nativeCache instanceof Cache) {
                        @SuppressWarnings({"unchecked", "resource"})
                        Cache<Object, Object> jcache = (Cache<Object, Object>) nativeCache;
                        long count = StreamSupport.stream(jcache.spliterator(), false).count();
                        cacheEntries.put(cacheName, count);
                        totalEntries += count;
                    }
                }
            }
            
            summary.put("totalCacheEntries", totalEntries);
            summary.put("cacheEntriesByName", cacheEntries);
            summary.put("timestamp", System.currentTimeMillis());
            
            // Warn if heap usage is high
            double heapUsagePercent = (usedMemory * 100.0) / maxMemory;
            if (heapUsagePercent > 80) {
                summary.put("warning", "Heap usage is above 80% - consider increasing heap size or reducing cache sizes");
            }
            
            return summary;
        } catch (Exception e) {
            summary.put("error", e.getMessage());
            return summary;
        }
    }
}

