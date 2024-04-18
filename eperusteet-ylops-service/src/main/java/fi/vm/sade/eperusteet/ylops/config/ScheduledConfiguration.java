package fi.vm.sade.eperusteet.ylops.config;

import fi.vm.sade.eperusteet.ylops.repository.OphSessionMappingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Profile("!test & !docker")
public class ScheduledConfiguration {

    @Autowired
    private OphSessionMappingStorage ophSessionMappingStorage;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanOphSession() {
        ophSessionMappingStorage.clean();
    }

}
