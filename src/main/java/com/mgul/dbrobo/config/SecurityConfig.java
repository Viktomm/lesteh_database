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
                    auth.requestMatchers("/admin/edit/**").hasRole("ADMIN");
                    auth.requestMatchers("/admin/devices/**","/admin/objects/**").hasAnyRole("ADMIN","MODERATOR");
                    auth.requestMatchers("/admin/**").hasAnyRole("ADMIN","MODERATOR","HELPER");
                    auth.anyRequest().permitAll();
                })
                .formLogin()
                    // .loginPage("") отвечает за url, куда перенаправят пользователя для аутентификации
                    // при использовании директивы, spring забивает хуй на всю обработку по-умолчанию и делегирует работу приложению
                    // очень похоже на то, что происходит в классе с конструктором по-умолчанию, когда создаем какой-то свой
                    .loginProcessingUrl("/login") // директива отвечает за url куда будет послан POST-запрос с введенными в форму кредами
                    // не ставим тут /mongo, т.к. директива отвечает за url, куда будет послан запрос, и где spring ожидает его поймать
                    // т.к. /mongo срезается nginx'ом на входе, spring никогда не получит креды по /mongo/login
                    // иначе говоря, посылаются креды по /mongo/login а ловятся уже по /login, но обработчик висит на /mongo/login
                    // вместо этого замутили ещё один location в nginx
                    .defaultSuccessUrl("/mongo/admin",true)
                    .failureUrl("/login?error") // аналогично не ставим /mongo тут
                .and()
                    .logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/mongo")
                .and()
                    .exceptionHandling()
                    .accessDeniedPage("/admin/accessDenied")
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
