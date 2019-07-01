package com.vandanasridhar.photoapp.api.users.data;

import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository <UserEntity, Long> {
    // data type of the  entity object that needs to be stored,  data type of the id that the entity has which is the database id

    // to write a query to find by username
    UserEntity findByEmail(String email);



}
