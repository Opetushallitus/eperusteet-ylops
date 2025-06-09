package fi.vm.sade.eperusteet.ylops.config;

import fi.vm.sade.eperusteet.ylops.dto.util.CacheArvot;
import fi.vm.sade.eperusteet.ylops.repository.OphSessionMappingStorage;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.util.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableScheduling
@Profile("!test & !docker")
public class ScheduledConfiguration {

    @Autowired
    private OphSessionMappingStorage ophSessionMappingStorage;

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private DokumenttiService dokumenttiService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanOphSession() {
        ophSessionMappingStorage.clean();
    }

    @Scheduled(cron = "0 30 * * * *")
    public void fixStuckPrintings() {
        SecurityContextHolder.getContext().setAuthentication(useAdminAuth());
        dokumenttiService.cleanStuckPrintings();
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void cacheOpetussuunnitelmaJulkaisut() {
        SecurityContextHolder.getContext().setAuthentication(useAdminAuth());
        maintenanceService.clearCache(CacheArvot.OPETUSSUUNNITELMA_JULKAISU);
        maintenanceService.clearCache(CacheArvot.OPETUSSUUNNITELMA_NAVIGAATIO_JULKINEN);
        maintenanceService.cacheJulkaistutOpetussuunnitelmat();
        maintenanceService.cacheOpetussuunnitelmaNavigaatiot();
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
