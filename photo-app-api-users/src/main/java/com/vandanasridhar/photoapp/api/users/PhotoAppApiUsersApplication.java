package com.vandanasridhar.photoapp.api.users;

import com.vandanasridhar.photoapp.api.users.shared.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // feign client helps user ms send a http get request to albums ms to get the list of albums
@EnableCircuitBreaker
public class PhotoAppApiUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoAppApiUsersApplication.class, args);
    }

    @Bean  // the object is available as a bean and this can be injected to the service implementation.
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean // this means that this class is available to us as a bean and it can be autowired.
    @LoadBalanced // to allow client side load balancing to our rest template
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate();
    }

    @Bean
    Logger.Level feignLoggerLevel()
    {
        return Logger.Level.FULL; // full is used because the entire logging provided by feign can be viewed.
    }

    /*@Bean
    public FeignErrorDecoder getFeignErrorDecoder()
    {
        return new FeignErrorDecoder();
    }*/

}
