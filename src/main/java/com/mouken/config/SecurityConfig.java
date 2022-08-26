package com.mouken.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebSecurityCustomizer {


    @Override
    public void customize(WebSecurity web) {
        web.ignoring().mvcMatchers("/", "login", "/signup");
    }
}
