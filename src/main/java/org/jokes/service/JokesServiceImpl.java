package org.jokes.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.streams.operators.spi.Stage;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.entity.JokesAPI;
import org.jokes.entity.OfficialJokes;
import org.jokes.repository.JokesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@ApplicationScoped
public class JokesServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(JokesServiceImpl.class);

    @Inject
    JokesRepository jokesRepository;

    public Uni<JokesResponseDTO> callJokesAPI() {
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        return Uni.createFrom().item(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://official-joke-api.appspot.com/random_joke"))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return objectMapper.readValue(response.body(), OfficialJokes.class);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).onItem().transformToUni(apiResponse -> {
            //apiResponse.setJokesid(String.valueOf(apiResponse.getId()));
            // Save the official joke reactively
            return jokesRepository.saveOfficialJoke(apiResponse)
                    .onItem().transformToUni(v -> {
                        // This part now returns a Uni<JokesResponseDTO> correctly
                        JokesResponseDTO customResponse = formResponse(apiResponse);
                        JokesAPI jokesAPIEntity = new JokesAPI(customResponse.getId(), customResponse.getQuestion(), customResponse.getAnswer(), apiResponse);

                        return jokesRepository.saveJokeAPI(jokesAPIEntity)
                                .onItem().transform(vv -> customResponse); // Ensure this returns the customResponse
                    });
        });
    }


    public JokesResponseDTO formResponse(OfficialJokes apiResponse) {
        String uuid = UUID.randomUUID().toString();
        return new JokesResponseDTO(uuid, apiResponse.getSetup(), apiResponse.getPunchline());
    }
}
