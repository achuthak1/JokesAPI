package org.jokes.service.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jokes.dto.JokesResponseDTO;
import org.jokes.service.JokesAPIFetchService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class JokesAPIFetchServiceImpl implements JokesAPIFetchService {

    @Inject
    JokesServiceImpl jokesService;

    @Override
    public Uni<List<JokesResponseDTO>> getJokes(int count) {
        if (count <= 0) {
            return Uni.createFrom().failure(new IllegalArgumentException("Count must be greater than zero."));
        }
        else if(count>100){
            return Uni.createFrom().failure(new IllegalArgumentException("Count must not be be greater than 100."));
        }

        int batchSize = 10;
        int totalBatches = (int) Math.ceil((double) count / batchSize);

        return Multi.createFrom().range(0, totalBatches)
                .onItem().transformToUniAndConcatenate(batchIndex -> {
                    int currentBatchSize = Math.min(batchSize, count - (batchIndex * batchSize));
                    return Multi.createFrom().range(0, currentBatchSize)
                            .onItem().transformToUniAndConcatenate(i -> jokesService.callJokesAPI())
                            .collect().asList();
                })
                .collect().asList()
                .onItem().transform(this::flattenLists);
    }

    private List<JokesResponseDTO> flattenLists(List<List<JokesResponseDTO>> listOfLists) {
        return listOfLists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
