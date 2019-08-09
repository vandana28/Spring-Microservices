package com.vandanasridhar.photoappdiscovery;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurity  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
        // disable cross site request forgery token
        // any request sent by the user is autheticated
        // http basic authetication
    }
}
