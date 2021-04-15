package fi.vm.sade.eperusteet.ylops.service.config;

import java.util.concurrent.Executor;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@EnableAsync
@EnableScheduling
public class SpringConfiguration {

    @Bean(name = "defaultTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();

        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

}
