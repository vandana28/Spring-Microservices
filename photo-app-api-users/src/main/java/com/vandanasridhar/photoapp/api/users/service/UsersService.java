package com.vandanasridhar.photoapp.api.users.service;

import com.vandanasridhar.photoapp.api.users.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {

    UserDto createUser(UserDto userDetails); // takes in user details as a data transfer object and returns a DTO as well.

    UserDto getUserDetailsByEmail(String email); // this method is to get userid using the email. this method takes in a string and returns a userDTO object.

    UserDto getUserByUserId(String userId); // accepts public user id
}
