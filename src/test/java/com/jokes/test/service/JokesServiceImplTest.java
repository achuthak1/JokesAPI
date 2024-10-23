package com.jokes.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.entity.JokesAPI;
import org.jokes.model.JokesAPIResponseModel;
import org.jokes.repository.JokesRepository;
import org.jokes.service.impl.JokesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JokesServiceImplTest {

    @InjectMocks
    private JokesServiceImpl jokesService;

    @Mock
    private JokesRepository jokesRepository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCallJokesAPIMocked() {
        JokesAPIResponseModel mockedJoke = new JokesAPIResponseModel();
        mockedJoke.setSetup("Why did the chicken cross the road?");
        mockedJoke.setPunchline("To get to the other side.");

            //Generate an uuid for mocking
        String uuid = UUID.randomUUID().toString();
        JokesResponseDTO expectedResponse = new JokesResponseDTO(uuid, mockedJoke.getSetup(), mockedJoke.getPunchline());
        JokesServiceImpl mockJokesService = mock(JokesServiceImpl.class);
        when(mockJokesService.callJokesAPI()).thenReturn(Uni.createFrom().item(expectedResponse));

            Uni<JokesResponseDTO> result = mockJokesService.callJokesAPI();
        JokesResponseDTO responseDTO = result.await().indefinitely();
        assertNotNull(responseDTO);
        assertEquals(expectedResponse.getAnswer(), responseDTO.getAnswer());
        assertEquals(expectedResponse.getQuestion(), responseDTO.getQuestion());
        verify(mockJokesService).callJokesAPI();
    }

    @Test
    void testCallJokesAPIHandlesException() {
        JokesServiceImpl mockJokesService = mock(JokesServiceImpl.class);
        when(mockJokesService.callJokesAPI()).thenThrow(new RuntimeException("Network error"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            mockJokesService.callJokesAPI().await().indefinitely();
        });
        assertEquals("Network error", thrown.getMessage());
        verify(mockJokesService).callJokesAPI();
    }

}
