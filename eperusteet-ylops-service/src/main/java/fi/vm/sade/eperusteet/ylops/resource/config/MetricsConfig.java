package fi.vm.sade.eperusteet.ylops.resource.config;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMetrics
public class MetricsConfig extends MetricsConfigurerAdapter {

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        registerReporter(JmxReporter
                .forRegistry(metricRegistry)
                .build()).start();

        registerReporter(ConsoleReporter
                .forRegistry(metricRegistry)
                .build()).start(1, TimeUnit.DAYS);
    }

}
