package fi.vm.sade.eperusteet.ylops.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration
public class AccessLogConfiguration {

    @Bean
    @ConditionalOnProperty(name = "logback.access")
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return container -> container.addContextCustomizers(context -> {
            LogbackValve logbackValve = new LogbackValve();
            logbackValve.setFilename("logback-access.xml");
            context.getPipeline().addValve(logbackValve);
        });
    }
}
