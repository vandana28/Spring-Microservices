package com.vandanasridhar.photoapp.api.users.data;

import com.vandanasridhar.photoapp.api.users.ui.model.AlbumResponseModel;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "albums-ws" , fallbackFactory = AlbumsFallbackFactory.class)
public interface AlbumsServiceClient {


// specify the method by which the feign client will fetch the list of albums
    // when a get request is set to this below mentioned path, the getalbums() method is used to retrieve the list of albums.
    // since id is provided as a argument , the id is provided as a path variable for the method.

    @GetMapping("/users/{id}/albums") // need to specify the url which can be queried to get the list of albums, need to specify the web service end point.
    public List<AlbumResponseModel> getAlbums(@PathVariable String id); // feign is a declarative client so it needs to be properly implemented. annotate it properly to provide the details of the request/response.

    // so the getAlbums method brings back a response in the form of a json object which has to be converted into a java object, so there should be a class in place that has the
    // same fields of the json reponse object. which is the albumresponsemodel.

    // to display error messages , use the try and catch block. this way, we can handle the feign exception




}

/*@Component
class AlbumsFallback implements AlbumsServiceClient {

    @Override
    public List<AlbumResponseModel> getAlbums(String id) { // need to match the method signature of feign client
        return new ArrayList<>();
    }
}*/

// sometimes for developers it is better if they know that a specific microservice is down, makes them aware.
// refactor the feign client - get hold of the error message , log it and still let the fallback method provide the default values.

@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient>
{
    @Override
    public AlbumsServiceClient create(Throwable throwable) { // override create to get access to throwable which intern provides access to the error message
        // so you want to handle an error message and at the same time you want to return a list of albums
        return new AlbumsServiceClientFallback(throwable);
    }


}

class AlbumsServiceClientFallback implements AlbumsServiceClient {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Throwable throwable;

    public AlbumsServiceClientFallback(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public List<AlbumResponseModel> getAlbums(String id) {

        if (throwable instanceof FeignException && ((FeignException) throwable).status() == 404) { // if the error is an instance of feign exception and if the status number is 404,
            // then the error is logged with the id
            logger.error("404 error took place when getAlbums was called with userId: " + id + ". Error message: "
                    + throwable.getLocalizedMessage());
        } else { // else if the error is another number , then the other error is logged.
            logger.error("Other error took place: " + throwable.getLocalizedMessage());
        }

        return new ArrayList<>();
    }
}

