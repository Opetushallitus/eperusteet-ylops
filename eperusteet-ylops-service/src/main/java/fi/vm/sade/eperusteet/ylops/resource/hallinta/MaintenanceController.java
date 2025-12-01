package fi.vm.sade.eperusteet.ylops.resource.hallinta;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.service.util.MaintenanceService;
import fi.vm.sade.eperusteet.ylops.service.util.MemoryStatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/api/maintenance")
@Profile("!test")
@Tag(name = "Maintenance")
public class MaintenanceController {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private MemoryStatisticsService memoryStatisticsService;

    @RequestMapping(value = "/cacheclear/{cache}", method = GET)
    public ResponseEntity<Void> clearCache(@PathVariable final String cache) {
        maintenanceService.clearCache(cache);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @RequestMapping(value = "/julkaisut", method = GET)
    public void teeJulkaisut(
            @RequestParam(value = "julkaisekaikki", defaultValue = "false") boolean julkaiseKaikki,
            @RequestParam(value = "koulutustyypit", required = false) final Set<String> koulutustyypit
    ) {
        maintenanceService.teeJulkaisut(julkaiseKaikki,
                koulutustyypit != null ? koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()) : null);
    }


    @RequestMapping(value = "/cache/julkaisut", method = GET)
    public void cacheOpetussuunnitelmaJulkaisut() {
        maintenanceService.cacheOpetussuunnitelmaNavigaatiot();
    }

    @RequestMapping(value = "/cache/memory", method = GET)
    public ResponseEntity<Map<String, Object>> getCacheMemorySummary() {
        Map<String, Object> summary = memoryStatisticsService.getCacheMemorySummary();
        
        if (summary.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(summary);
        }
        
        return ResponseEntity.ok(summary);
    }

    @RequestMapping(value = "/cache/statistics", method = GET)
    public ResponseEntity<Map<String, Map<String, Object>>> getCacheStatistics() {
        Map<String, Map<String, Object>> statistics = memoryStatisticsService.getCacheStatistics();
        
        if (statistics.containsKey("_error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(statistics);
        }
        
        return ResponseEntity.ok(statistics);
    }
}
