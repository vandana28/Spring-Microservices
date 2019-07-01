package com.vandanasridhar.photoapp.api.users.service;

import com.vandanasridhar.photoapp.api.users.data.UserEntity;
import com.vandanasridhar.photoapp.api.users.data.UsersRepository;
import com.vandanasridhar.photoapp.api.users.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UsersServiceImplemenation implements UsersService { // UserDetails object must have a unique user id. once user details are stored in the db, they have two ids. one is the database  auto
    // generated id and the other one is generated through public user id which is alpha numeric generated id which is unique for every user. it is more safe as well.

    UsersRepository usersRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder; // for encryption of password and decryption as well.

    @Autowired
    public UsersServiceImplemenation(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {

        userDetails.setUserId(UUID.randomUUID().toString()); // generates unique uid.

        userDetails.setEcryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        //maps source object to a destination object.
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // so model mapper might not match the fields correctly so we need to explicitly specify a matching
        //strategy to be strict.
        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class); // the fields in the source must match the ones in the destination. // mapping the dto with the entity

        // userEntity.setEcryptedPassword("test");
        usersRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // when spring framework tries to authenthicate a user, it will look for this method
        // it will rely on this method for returning the userdetails that match the username
        UserEntity userEntity = usersRepository.findByEmail(username); // find by email is available in the usersrepo. and we can use it here. it returns an userentity.

        if (userEntity == null) throw new UsernameNotFoundException(username);
        return new User(userEntity.getEmail(), userEntity.getEcryptedPassword(), true, true, true, true, new ArrayList<>()); // returns a new user by taking in user email, password and if the account is enabled.
        // when email verification is done, the authorities value can be set to false until the user verified the email address.
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {

        UserEntity userEntity = usersRepository.findByEmail(email); // use userrepository to query the database using email to get the userid
        if (userEntity == null) throw new UsernameNotFoundException(email); // "email" not found error


        // need to return a userDTO. need to convert user entity into dto
        //model mapper

        return new ModelMapper().map(userEntity, UserDto.class);
    }


}
