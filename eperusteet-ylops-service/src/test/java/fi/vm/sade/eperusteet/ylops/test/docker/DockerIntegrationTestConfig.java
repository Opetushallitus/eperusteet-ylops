package fi.vm.sade.eperusteet.ylops.test.docker;

import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepositoryFactoryBean;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiAsyncConfig;
import fi.vm.sade.eperusteet.ylops.service.mocks.TestPermissionEvaluator;
import jakarta.persistence.EntityManager;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.id.enhanced.SingleNamingStrategy;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Profile("docker")
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableCaching
@EnableTransactionManagement
@EnableMethodSecurity(securedEnabled = true)
@PropertySource("classpath:eperusteet-ylops-service.properties")
@EntityScan(basePackages = "fi.vm.sade.eperusteet.ylops.domain")
@EnableJpaRepositories(
        basePackages = "fi.vm.sade.eperusteet.ylops.repository",
        repositoryFactoryBeanClass = JpaWithVersioningRepositoryFactoryBean.class)
@Import({ValidointiServiceDockerMock.class, ExternalPdfServiceDockerMock.class, DokumenttiServiceDockerMock.class})
@ComponentScan(
        basePackages = {
                "fi.vm.sade.eperusteet.ylops.service",
                "fi.vm.sade.eperusteet.ylops.repository.impl",
                "fi.vm.sade.eperusteet.utils"
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "fi\\.vm\\.sade\\.eperusteet\\.ylops\\.service\\.external\\..*"),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = DokumenttiAsyncConfig.class)
        })
public class DockerIntegrationTestConfig {

    @Bean
    @DependsOn("flywayInitializer")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setPersistenceUnitName("eperusteet-ylops-pu");
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("fi.vm.sade.eperusteet.ylops.domain");
        entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactory.setEntityManagerInterface(EntityManager.class);
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.show_sql", false);
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("jakarta.persistence.sharedCache.mode", "ENABLE_SELECTIVE");
        props.put("org.hibernate.envers.audit_strategy", "org.hibernate.envers.strategy.internal.DefaultAuditStrategy");
        props.put("org.hibernate.envers.revision_listener", "fi.vm.sade.eperusteet.ylops.service.internal.AuditRevisionListener");
        props.put("hibernate.jdbc.batch_size", 20);
        props.put("hibernate.jdbc.fetch_size", 20);
        props.put(AvailableSettings.ID_DB_STRUCTURE_NAMING_STRATEGY, SingleNamingStrategy.STRATEGY_NAME);
        props.put("hibernate.javax.cache.missing_cache_strategy", "create");
        entityManagerFactory.setJpaPropertyMap(props);
        return entityManagerFactory;
    }

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean(name = "julkaisuTaskExecutor")
    public Executor julkaisuTaskExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean
    public static CustomScopeConfigurer dockerRequestScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("request", new SimpleThreadScope());
        return configurer;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        return new TestPermissionEvaluator();
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler expressionHandler(PermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
