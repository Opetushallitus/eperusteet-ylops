package fi.vm.sade.eperusteet.ylops.config;

import fi.vm.sade.eperusteet.ylops.repository.OphSessionMappingStorage;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.scheduled.task.ScheduledTask;
import fi.vm.sade.eperusteet.ylops.service.util.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@Profile("!test & !docker")
@Slf4j
public class ScheduledConfiguration {

    @Autowired
    private OphSessionMappingStorage ophSessionMappingStorage;

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private DokumenttiService dokumenttiService;

    @Autowired
    private List<ScheduledTask> tasks = new ArrayList<>();

    @Scheduled(cron = "0 0 * * * *")
    public void cleanOphSession() {
        ophSessionMappingStorage.clean();
    }

    @Scheduled(cron = "0 30 * * * *")
    public void fixStuckPrintings() {
        SecurityContextHolder.getContext().setAuthentication(useAdminAuth());
        dokumenttiService.cleanStuckPrintings();
    }

// ToBe decided: otetaanko käyttöön enää
// @Scheduled(cron = "0 * * * * *")
    public void tasks() {
        SecurityContextHolder.getContext().setAuthentication(useAdminAuth());

        for (ScheduledTask task : tasks) {
            try {
                log.debug("Starting {} job", task.getName());
                task.execute();
                log.debug("{} is done.", task.getName());
            } catch (Exception e) {
                log.debug("{} is already running.", task.getName());
            }
        }

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private Authentication useAdminAuth() {
        // Käytetään pääkäyttäjän oikeuksia.
        return new UsernamePasswordAuthenticationToken("system",
                "ROLE_ADMIN", AuthorityUtils.createAuthorityList("ROLE_ADMIN",
                "ROLE_APP_EPERUSTEET_YLOPS_CRUD_1.2.246.562.10.00000000001",
                "ROLE_APP_EPERUSTEET_YLOPS_ADMIN_1.2.246.562.10.00000000001",
                "ROLE_APP_EPERUSTEET_ADMIN_1.2.246.562.10.00000000001"));
    }
}
