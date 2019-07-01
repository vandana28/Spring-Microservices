package com.vandanasridhar.photoapp.api.users.security;

import com.vandanasridhar.photoapp.api.users.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration  // these annotations are for providing spring security.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Environment environment;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment environment, UsersService usersService , BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.environment = environment;  // created an environment object and this can read values from property files.
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {  // configure takes in a httpsecurity object as a method argument and the object needs to be configured to allow requests
        // sent to a certain url path and this url path is the path to users rest controller.
        // going to use JWT authetication token , disable Cross-Site Request Forgery (CSRF) token.

        http.csrf().disable();

         // configure in such a way that the microservice can receive http requests only from certain ip addresses.

        http.headers().frameOptions().disable(); // this is because the h2 console runs within a frame and it will show an empty page after clicking on connect.



        //http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip")).and().addFilter(getAuthenticationFilter());

        http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip")).and().addFilter(getAuthenticationFilter());



    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager());
        //authenticationFilter.setAuthenticationManager(authenticationManager());

        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
    }


}
