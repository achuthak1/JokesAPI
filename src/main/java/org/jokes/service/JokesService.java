package org.jokes.service;

import io.smallrye.mutiny.Uni;
import org.jokes.dto.JokesResponseDTO;

public interface JokesService {
    Uni<JokesResponseDTO> callJokesAPI();
}
