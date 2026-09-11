package fi.vm.sade.eperusteet.ylops.test.docker;

import fi.vm.sade.eperusteet.ylops.service.mocks.TestPermissionEvaluator;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.PermissionEvaluator;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@ImportResource({"classpath:it-test-context.xml", "classpath:it-docker-test-context.xml"})
public class TestConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean(name = "julkaisuTaskExecutor")
    public Executor julkaisuTaskExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean("testPermissionEvaluator")
    public PermissionEvaluator testPermissionEvaluator() {
        return new TestPermissionEvaluator();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .outOfOrder(true)
                .table("schema_version")
                .load();
    }
}
