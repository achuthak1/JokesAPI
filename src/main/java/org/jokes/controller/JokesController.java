package org.jokes.controller;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.service.JokesAPIFetchService;
import org.jokes.service.JokesService;
import org.jokes.service.impl.JokesServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Path("/jokes")
@ApplicationScoped
public class JokesController {

    @Inject
    private JokesAPIFetchService jokesAPIService;  // Use the service interface

    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public Uni<Response> getJokes(@QueryParam("count") int count) {
        return jokesAPIService.getJokes(count)
                .onItem().transform(jokesList -> Response.ok(jokesList).build())
                .onFailure(IllegalArgumentException.class)
                .recoverWithItem(throwable -> Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Error fetching jokes: " + throwable.getMessage())
                        .build())
                    .onFailure()
                 .recoverWithItem(throwable -> Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error fetching jokes: " + throwable.getMessage())
                        .build());
    }


}
