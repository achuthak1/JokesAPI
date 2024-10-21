package org.jokes.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jokes.entity.JokesAPI;
import org.jokes.entity.OfficialJokes;

@ApplicationScoped
public class JokesRepository {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    // Save OfficialJoke reactively
    public Uni<Void> saveOfficialJoke(OfficialJokes joke) {
        return sessionFactory.withTransaction((session, transaction) ->
                session.persist(joke)
        ).replaceWithVoid(); // Use replaceWithVoid() to return Uni<Void>
    }

    // Save or update JokesAPI reactively
    public Uni<Void> saveJokeAPI(JokesAPI jokeAPI) {
        return sessionFactory.withTransaction((session, transaction) -> {
            if (jokeAPI.getId() != null) {
                // If the entity exists, merge it (update)
                return session.find(JokesAPI.class, jokeAPI.getId())
                        .onItem().transformToUni(existing -> {
                            if (existing != null) {
                                return session.merge(jokeAPI).replaceWithVoid(); // Update and convert to Uni<Void>
                            } else {
                                return session.persist(jokeAPI).replaceWithVoid(); // Create new and convert to Uni<Void>
                            }
                        });
            } else {
                // If the ID is not set, persist it (create)
                return session.persist(jokeAPI).replaceWithVoid(); // Create new and convert to Uni<Void>
            }
        });
    }

}
