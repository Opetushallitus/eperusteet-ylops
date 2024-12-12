package fi.vm.sade.eperusteet.ylops.config;

import fi.vm.sade.eperusteet.ylops.service.util.DevSecurityRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import java.util.List;

@Profile({"dev"})
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfigurationDev {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/buildversion.txt").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**").permitAll()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .anyRequest().authenticated()
                .and().logout(logout -> {
                    logout.logoutUrl("/api/logout");
                    logout.logoutSuccessUrl("http://localhost:9040");
                    logout.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL)));
                    logout.invalidateHttpSession(true);
                })
                .headers().defaultsDisabled().cacheControl();
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails test = User.withDefaultPasswordEncoder()
                .username("test")
                .password("test")
                .roles("USER",
                        DevSecurityRole.amosaa().admin().oid("1.2.246.562.10.00000000001").build(),
                        DevSecurityRole.amosaa().admin().oid("1.2.246.562.10.15738250156").build(),
                        DevSecurityRole.amosaa().admin().oid("1.2.246.562.10.54645809036").build(),
                        DevSecurityRole.amosaa().admin().oid("1.2.246.562.28.37193106103").build(),
                        DevSecurityRole.amosaa().build(),
                        DevSecurityRole.eperusteet().admin().oid("1.2.246.562.10.00000000001").build(),
                        DevSecurityRole.eperusteet().admin().oid("1.2.246.562.28.39318578962").build(),
                        DevSecurityRole.eperusteet().build(),
                        DevSecurityRole.eperusteet().crud().build(),
                        DevSecurityRole.eperusteet().crud().oid("1.2.246.562.10.00000000001").build(),
                        DevSecurityRole.eperusteet().crud().oid("1.2.246.562.28.11287634288").build(),
                        DevSecurityRole.eperusteet().crud().oid("1.2.246.562.28.55860281986").build(),
                        DevSecurityRole.eperusteet().crud().oid("1.2.246.562.28.85557110211").build(),
                        DevSecurityRole.eperusteet().read().build(),
                        DevSecurityRole.koto().admin().oid("1.2.246.562.10.54645809036").build(),
                        DevSecurityRole.koto().build(),
                        DevSecurityRole.maarays().crud().build(),
                        DevSecurityRole.maarays().read().build(),
                        DevSecurityRole.tuva().admin().oid("1.2.246.562.10.54645809036").build(),
                        DevSecurityRole.tuva().build(),
                        DevSecurityRole.vst().admin().oid("1.2.246.562.10.54645809036").build(),
                        DevSecurityRole.vst().build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.10.00000000001").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.10.20516711478").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.10.22840843613").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.10.346830761110").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.10.83037752777").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.10.90008375488").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.28.11332956371").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.28.11332956371").build(),
                        DevSecurityRole.ylops().admin().oid("1.2.246.562.28.25927836418").build(),
                        DevSecurityRole.ylops().build(),
                        DevSecurityRole.ylops().crud().build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.00000000001").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.11902547485").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.13649470005").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.22840843613").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.346830761110").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.61057016927").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.68534785412").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.83037752777").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.28.11332956371").build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.81269623245").build()
                )
                .build();
        UserDetails ylops_helsinki = User.withDefaultPasswordEncoder()
                .username("ylopshelsinki")
                .password("test")
                .roles("USER",
                        DevSecurityRole.ylops().build(),
                        DevSecurityRole.ylops().crud().build(),
                        DevSecurityRole.ylops().crud().oid("1.2.246.562.10.346830761110").build(),
                        DevSecurityRole.ylops().read().oid("1.2.246.562.10.00000000001").build()
                )
                .build();
        return new InMemoryUserDetailsManager(
                test,
                ylops_helsinki
        );
    }

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
        cookieCsrfTokenRepository.setCookieHttpOnly(false);
        cookieCsrfTokenRepository.setCookieName("CSRF");
        cookieCsrfTokenRepository.setHeaderName("CSRF");
        cookieCsrfTokenRepository.setCookiePath("/");
        return cookieCsrfTokenRepository;
    }

    @Bean
    public AffirmativeBased affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(List.of(new RoleVoter()));
        affirmativeBased.setAllowIfAllAbstainDecisions(true);
        return affirmativeBased;
    }
}
