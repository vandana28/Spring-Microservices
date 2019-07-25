package com.vandanasridhar.photoapp.api.users.ui.model;

public class AlbumResponseModel { // this class will have fields that will represent the albums json response such as albumid, userid, name and description


    private String albumId;
    private String userId;
    private String name;
    private String description;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
