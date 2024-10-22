package com.jokes.test.controller;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.jokes.controller.JokesController;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.service.JokesAPIFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class JokesControllerTest {

    @Mock
    private JokesAPIFetchService jokesAPIService;

    @InjectMocks
    private JokesController jokesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetJokesSuccess() {
        JokesResponseDTO joke = new JokesResponseDTO("1", "Why did the chicken cross the road?", "To get to the other side.");
        when(jokesAPIService.getJokes(5)).thenReturn(Uni.createFrom().item(List.of(joke)));
        Uni<Response> responseUni = jokesController.getJokes(5);
        Response response = responseUni.await().indefinitely();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        List<JokesResponseDTO> jokesList = (List<JokesResponseDTO>) response.getEntity();
        assertEquals(1, jokesList.size());
        assertEquals("Why did the chicken cross the road?", jokesList.get(0).getQuestion());
    }

    @Test
    void testGetJokesInternalServerError() {
        when(jokesAPIService.getJokes(5)).thenReturn(Uni.createFrom().failure(new RuntimeException("API error")));
        Uni<Response> responseUni = jokesController.getJokes(5);
        Response response = responseUni.await().indefinitely();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Error fetching jokes: API error", response.getEntity());
    }

    @Test
    void testGetJokesBadRequest() {
        when(jokesAPIService.getJokes(0)).thenReturn(Uni.createFrom().failure(new IllegalArgumentException("Invalid count")));
        Uni<Response> responseUni = jokesController.getJokes(0);
        Response response = responseUni.await().indefinitely();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Error fetching jokes: Invalid count", response.getEntity());
    }


}
