package com.vandanasridhar.photoapp.api.users.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vandanasridhar.photoapp.api.users.service.UsersService;
import com.vandanasridhar.photoapp.api.users.shared.UserDto;
import com.vandanasridhar.photoapp.api.users.ui.model.LoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter { // spring security will autheticate this.

    private UsersService usersService;
    // importing environemt to access to the application properties for encrytping jwt token.
    private Environment environment;


    public AuthenticationFilter(UsersService usersService, Environment environment, AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager); // calling the base class in the constructor using super keyword

    }

    // attempt authetication will be called by spring framework and takes in two parameters - the request and response.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException { // custom authetication filter class registered with http security.
        // when a user sends http request to perform login, the custom filter will be triggered and the attempt authetication method will be called.

        try {
            // objectmapper maps the username and password to a loginrequestmodel. then this is then passed to username/password authentication token
            // which is a method argument for autheticate and this is passed on object get authetication manager. ( which is a part of spring security)
            LoginRequestModel credentials = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);

            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // the purpose of this method is to take the user details and obtain a JWT token and put that as a part of the  http response header for the http request. application will read
    // this JWT token as use it for subsequent requests
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {

        // read user details
        // produce jwt token
        // put that in the http response header

        String userName = ((User) auth.getPrincipal()).getUsername();
        //loadUserbyUsername does not have userid which needs to be added to the jwt token.
        //usersService.loadUserByUsername(userName).get

        UserDto userDetails = usersService.getUserDetailsByEmail(userName); // finished obtaining the userdetails. now we need to generate the jwt token.

        // this entire code snippet generates the  secure json web token
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId()) //
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time")))) // expiration time on the token, the
                // should mention the milli seconds but the method takes in date. cureent milli seconds plus the milli seconds in which we want the token to expire is
                //modeled into an object.
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret")) // signature algorithm  and secret token with which the jwt token is signed.
                .compact();

        // finally want the token to be attached to a response header and send it back to http client.

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());

        // so when the user logs in using username and password and they are correct, and spring framework was successfully able to perform authentication,
        // the successful authentication method is triggered and the jwt token will be triggered and will be added to the response header along with the userid.


    }
}


