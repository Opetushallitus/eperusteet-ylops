package fi.vm.sade.eperusteet.ylops.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
@ImportResource({"classpath*:spring/application-context.xml"})
public class DefaultConfigs {
}
