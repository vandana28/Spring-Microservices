package com.vandanasridhar.photoapp.api.users.ui;

import com.vandanasridhar.photoapp.api.users.service.UsersService;
import com.vandanasridhar.photoapp.api.users.shared.UserDto;
import com.vandanasridhar.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.vandanasridhar.photoapp.api.users.ui.model.CreateUserResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private Environment env;

    @Autowired
    UsersService usersService;


    @GetMapping("/status/check")  // first endpoint
    public String status()
    {
        return "Working on port" + env.getProperty("local.server.port") + "with token = " +  env.getProperty("token.secret"); // to access the local server port number.
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity <CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) // the body of the request will have a json payload and this will be converted into a java object of CreateUserRequestModel
            //annotating with request body says that request comes with the body.
            //instead of returning a string, return a response entity. A status code
            // also the response entity should not return sensitive information such as db id , encrypted password etc. so a new java object needs to be created.

    {


        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userDetails,UserDto.class);

        UserDto createdUser =  usersService.createUser(userDto);

        CreateUserResponseModel returnValue = modelMapper.map(createdUser,CreateUserResponseModel.class);

        return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
}
