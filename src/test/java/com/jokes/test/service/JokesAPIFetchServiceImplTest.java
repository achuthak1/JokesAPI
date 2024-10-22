package com.jokes.test.service;

import io.smallrye.mutiny.Uni;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.service.impl.JokesAPIFetchServiceImpl;
import org.jokes.service.impl.JokesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JokesAPIFetchServiceImplTest {

    @InjectMocks
    private JokesAPIFetchServiceImpl jokesAPIFetchService;

    @Mock
    private JokesServiceImpl jokesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetJokesCountLessThanOrEqualToZero() {
        Uni<List<JokesResponseDTO>> result = jokesAPIFetchService.getJokes(0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> result.await().indefinitely());
        assertEquals("Count must be greater than zero.", exception.getMessage());
    }

    @Test
    void testGetJokesCountGreaterThan100() {
        Uni<List<JokesResponseDTO>> result = jokesAPIFetchService.getJokes(101);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> result.await().indefinitely());
        assertEquals("Count must not be be greater than 100.", exception.getMessage());
    }

    @Test
    void testGetJokesSuccess() {
        JokesResponseDTO joke = new JokesResponseDTO("1", "Why did the chicken cross the road?", "To get to the other side.");
        when(jokesService.callJokesAPI()).thenReturn(Uni.createFrom().item(joke));


        Uni<List<JokesResponseDTO>> result = jokesAPIFetchService.getJokes(5);

        List<JokesResponseDTO> jokesList = result.await().indefinitely();
        assertEquals(5, jokesList.size()); // Adjust based on actual implementation details
        verify(jokesService, times(5)).callJokesAPI();
    }
}
