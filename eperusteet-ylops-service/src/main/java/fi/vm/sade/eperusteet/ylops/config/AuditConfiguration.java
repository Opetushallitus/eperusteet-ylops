package fi.vm.sade.eperusteet.ylops.config;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.eperusteet.ylops.service.audit.LoggerForAudit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfiguration {

    @Bean
    public Audit audit() {
        return new Audit(new LoggerForAudit(), "eperusteet-ylops-service", ApplicationType.VIRKAILIJA);
    }

}
