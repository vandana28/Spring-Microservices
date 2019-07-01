package com.vandanasridhar.photoapp.api.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

// some http requests sent to certain endpoints in the application don't need authorisation such as user registration
// but other end points such as update profile/user details need authorisation for logging in.
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment environment;

    @Autowired
    public WebSecurity(Environment environment) {
        this.environment = environment;
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers(environment.getProperty("api.h2console.url.path")).permitAll() // to access h2 console
                .antMatchers(HttpMethod.POST, environment.getProperty("api.registration.url.path")).permitAll() // to sign up
                .antMatchers(HttpMethod.POST, environment.getProperty("api.login.url.path")).permitAll()// to login
                .anyRequest().authenticated() // any other http requests must be autheticated
                .and()
                .addFilter(new AuthorizationFilter(authenticationManager(), environment));


        //will make api stateless. when client communicates with server, there will be a http session that will be created.
        // This will uniquely identify the client. session and cookie created can cache information,
        // this means that the jwt token can also be cached. hence even without authorisation, the request will be authorised
        //this prevents that.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


    }
}
