package org.jokes.service;

import io.smallrye.mutiny.Uni;
import org.jokes.dto.JokesResponseDTO;

import java.util.List;

public interface JokesAPIFetchService {
    Uni<List<JokesResponseDTO>> getJokes(int count);
}
