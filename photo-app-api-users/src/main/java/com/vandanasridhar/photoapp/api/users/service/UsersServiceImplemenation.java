package com.vandanasridhar.photoapp.api.users.service;

import com.vandanasridhar.photoapp.api.users.data.UserEntity;
import com.vandanasridhar.photoapp.api.users.data.UsersRepository;
import com.vandanasridhar.photoapp.api.users.shared.UserDto;
import com.vandanasridhar.photoapp.api.users.ui.model.AlbumResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersServiceImplemenation implements UsersService { // UserDetails object must have a unique user id. once user details are stored in the db, they have two ids. one is the database  auto
    // generated id and the other one is generated through public user id which is alpha numeric generated id which is unique for every user. it is more safe as well.

    UsersRepository usersRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder; // for encryption of password and decryption as well.

    RestTemplate restTemplate;
    Environment environment;

    @Autowired
    public UsersServiceImplemenation(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder,RestTemplate restTemplate,Environment environment) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.restTemplate = restTemplate;
        this.environment = environment;

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

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException("user not found");


        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class); // takes the entity and converts it into a user dto and returns it.
        String albumsUrl = String.format(environment.getProperty("albums.url"),userId); // since the port number isnt constant and a new port number is initialised each time the albums
        // microservice is started, the name under which the microservice is registered can be used instead.
        // albumsurl - url to the album's microservice to return the list of albums
        // http.GET - reuqest
        // requestEntity: null - http entity, information included with the request which is null because no information is included.
        // fourth paramter - response type, since a list of albums is gonna be obtained as a response, a list of album response model will be the response type.
        // when the exchange method is called on the rest template object, it will send a httpGET request to the albums URL, and the response from the url should be an albums list
        //hence it is of type album response model. That response is stored in albumsListResponse. Now to extract the albums, the body of the response is extracted (getBody())

        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        });
        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
        // now this albumslist response must be converted into a userDTO so that it can be transferred from a service class to a controller class.

        userDto.setAlbums(albumsList);

        return userDto;


    }
}
