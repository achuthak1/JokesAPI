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
import org.jokes.service.JokesServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Path("/jokes")
@ApplicationScoped
public class JokesController {

    @Inject
    private JokesServiceImpl service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getJokes(@QueryParam("count") int count) {
        System.out.println("Inside Controller");
        if (count <= 0) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Count must be greater than zero.").build());
        }

        int batchSize = 10;
        int totalBatches = (int) Math.ceil((double) count / batchSize);

        List<JokesResponseDTO> jokesList = new ArrayList<>();

        return Multi.createFrom().range(0, totalBatches)
                .onItem().transformToUniAndConcatenate(batchIndex -> {
                    int currentBatchSize = Math.min(batchSize, count - (batchIndex * batchSize));
                    return Multi.createFrom().range(0, currentBatchSize)
                            .onItem().transformToUniAndConcatenate(i -> service.callJokesAPI())
                            .collect().asList();
                })
                .collect().asList()
                .onItem().transform(finalJokesList -> Response.ok(finalJokesList).build())
                .onFailure().recoverWithItem(throwable -> {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error fetching jokes: " + throwable.getMessage())
                            .build();
                });
    }


}
