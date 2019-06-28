package com.vandanasridhar.photoapp.api.gateway.security;


// when user sends http request to the protected service, it needs a valid jwt token for authorization in authorization header
//validates jwt token and passe it through if its valid.


import io.jsonwebtoken.Jwts;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    Environment environment;

    public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //reading the authorization header from a request object
        String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

        // checking if the property value is null and does not start with a prefix. called bearer
        if (authorizationHeader == null || !authorizationHeader.startsWith(environment.getProperty("authorization.token.header.prefix"))) {
            chain.doFilter(request, response); // next filter in the change and return
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);


    }

    // takes in the http request object as a parameter , from the http request, it will reader the header
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // header being read.
        String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

        // if that header is empty, it will simply return null.
        if (authorizationHeader == null) {
            return null;
        }

        // stripe out the bearer header. So this line takes in the authorization header and replaces the bearer prefix with an empty string.
        // this provides a clean value of the authorization token.
        String token = authorizationHeader.replace(environment.getProperty("authorization.token.header.prefix"), "");

        // this line parses the jwt token and validate the token
        String userId = Jwts.parser()
                .setSigningKey(environment.getProperty("token.secret")) // same signing key that was used when the token was initialy created
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // subject used when creating the token is publis user id. when User was logging in , the subject was public user id

        if (userId == null) { // checking if that userid is null.
            return null;
        }

        //after obtaining the userid , it is passed onto  UsernamePasswordAuthenticationToken and it can be returned.
        return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

    }
}







