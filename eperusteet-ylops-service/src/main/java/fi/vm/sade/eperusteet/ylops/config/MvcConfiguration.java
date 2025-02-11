package fi.vm.sade.eperusteet.ylops.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.vm.sade.eperusteet.ylops.resource.config.MappingModule;
import fi.vm.sade.eperusteet.ylops.resource.config.ReferenceNamingStrategy;
import fi.vm.sade.eperusteet.ylops.resource.util.CacheHeaderInterceptor;
import fi.vm.sade.eperusteet.ylops.resource.util.LoggingInterceptor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Autowired
    EntityManagerFactory emf;

    @Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(true);
        matcher.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ui/").setViewName("forward:/ui/index.html");
        registry.addRedirectViewController("/ui", "/ui/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayConverter());
        converters.add(converter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        converters.add(stringHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(new CacheHeaderInterceptor());
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setPrettyPrint(true);
        converter.getObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        converter.getObjectMapper().enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        converter.getObjectMapper().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.getObjectMapper().disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        converter.getObjectMapper().registerModule(new Jdk8Module());
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        MappingModule module = new MappingModule();
        converter.getObjectMapper().registerModule(module);
        converter.getObjectMapper().setPropertyNamingStrategy(new ReferenceNamingStrategy());
        return converter;
    }


    @Bean
    ByteArrayHttpMessageConverter byteArrayConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes(Arrays.asList("application/pdf", "image/jpeg", "image/png")));
        return converter;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadFactory(new CustomizableThreadFactory("AsyncThreadFactory-"));
        executor.afterPropertiesSet();

        configurer.setTaskExecutor(executor).setDefaultTimeout(120000);
    }
}
