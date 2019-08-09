
package com.vandanasridhar.photoapp.api.users.shared;


import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component // automaticallly creates a feign decoder class. no need to create a bean and make it ready to use.
public class FeignErrorDecoder implements ErrorDecoder {


    Environment environment;

    @Autowired
    public FeignErrorDecoder(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Exception decode(String s, Response response) {  // this method will take in the method key and response object as the method arguments.
        // with the response object, we get access to http status quote , response body and headers. also read request object
        // central place to handle errors
        // so for all errors thrown, this method will be called.

        switch (response.status()) {
            case 400:
                break;

            case 404:
            {
                if(s.contains("getAlbums"))
                {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),environment.getProperty("Albums.exception.albums-not-found"));
                }

                break;
            }

            default: return new Exception(response.reason());
        }

        return null;
    }
}


