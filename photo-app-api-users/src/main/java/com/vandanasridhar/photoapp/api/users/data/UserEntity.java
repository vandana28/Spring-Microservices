package com.vandanasridhar.photoapp.api.users.data;

import javax.persistence.*;
import java.io.Serializable;

@Entity  //persist the entity object into a database. needs to know the database table.
@Table(name = "users") // db table name
public class UserEntity implements Serializable { // To serialize an object means to convert its state to a byte stream so that the byte stream can be reverted back into a copy of the object.


    private static final long serialVersionUID = -79107004472510110L;

    @Id
    @GeneratedValue // to auto- generate values in the db
    private long id;

    @Column(nullable = false , length = 50)
    private String firstName;

    @Column(nullable = false , length = 50)
    private String lastName;

    //deleted password field  because it needs to be encrypted and then stored.

    @Column(nullable = false, length = 120 , unique = true)
    private String email;

    @Column(nullable = false , unique = true)
    private String userId;

    @Column(nullable = false , unique = true)
    private String encryptedPassword;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEcryptedPassword() {
        return encryptedPassword;
    }

    public void setEcryptedPassword(String ecryptedPassword) {
        this.encryptedPassword = ecryptedPassword;
    }
}
