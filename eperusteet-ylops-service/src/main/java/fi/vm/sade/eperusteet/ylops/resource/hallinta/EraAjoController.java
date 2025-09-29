package fi.vm.sade.eperusteet.ylops.resource.hallinta;

import fi.vm.sade.eperusteet.ylops.service.scheduled.task.ScheduledTask;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/eraajo")
@Hidden
public class EraAjoController {

    @Autowired
    private Map<String, ScheduledTask> tasks;

    @GetMapping("/{nimi}/execute")
    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    public void executeTask(@PathVariable String nimi) {
        tasks.get(nimi).executeAsync();
    }

}