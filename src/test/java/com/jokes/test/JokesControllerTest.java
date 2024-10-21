package com.jokes.test;

import org.jokes.controller.JokesController;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.service.JokesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JokesControllerTest {

    @Mock
    private JokesServiceImpl jokesService;

    @InjectMocks
    private JokesController jokesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetJokes() {
        JokesResponseDTO joke = new JokesResponseDTO("1", "Why did the chicken cross the road?", "To get to the other side.");
        when(jokesService.callJokesAPI()).thenReturn(Uni.createFrom().item(joke));

        Uni<Response> responseUni = jokesController.getJokes(5);
        Response response = responseUni.await().indefinitely();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testGetJokesBadRequest() {
        Uni<Response> responseUni = jokesController.getJokes(0);
        Response response = responseUni.await().indefinitely();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
