package com.mgul.dbrobo.config;

import com.mgul.dbrobo.services.AdminDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AdminDetailsService adminDetailsService;

    @Autowired
    public SecurityConfig(AdminDetailsService adminDetailsService) {
        this.adminDetailsService = adminDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/sosi/edit/**").hasRole("ADMIN");
                    auth.requestMatchers("/sosi/devices/**","/admin/objects/**").hasAnyRole("ADMIN","MODERATOR");
                    auth.requestMatchers("/sosi/**").hasAnyRole("ADMIN","MODERATOR","HELPER");
                    auth.anyRequest().permitAll();
                })
                .formLogin().defaultSuccessUrl("/sosi",true).failureUrl("/login?error")
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling().accessDeniedPage("/sosi/accessDenied")
                .and()
                .userDetailsService(adminDetailsService)
                .headers(headers->headers.frameOptions().sameOrigin())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}
