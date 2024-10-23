package org.jokes.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jokes.entity.JokesAPI;

@ApplicationScoped
public class JokesRepository {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    /*public Uni<Void> saveOfficialJoke(OfficialJokes joke) {
        return sessionFactory.withTransaction((session, transaction) -> {
            return session.createQuery("SELECT COUNT(j) FROM OfficialJokes j WHERE j.id = :id", Long.class)
                    .setParameter("id", joke.getId())
                    .getSingleResult()
                    .onItem().transform(existingIdCount -> {
                        if (existingIdCount > 0) {
                            return Uni.createFrom().voidItem();
                        }
                        session.persist(joke);
                        return Uni.createFrom().voidItem();
                    });
        }).flatMap(result -> result);
    }*/



    public Uni<Void> saveJokeAPI(JokesAPI jokeAPI) {
        return sessionFactory.withTransaction((session, transaction) -> {
            if (jokeAPI.getId() != null) {
                // If the entity exists, merge it (update)
                return session.find(JokesAPI.class, jokeAPI.getId())
                        .onItem().transformToUni(existing -> {
                            if (existing != null) {
                                return session.merge(jokeAPI).replaceWithVoid();
                            } else {
                                return session.persist(jokeAPI).replaceWithVoid();
                            }
                        });
            } else {
                return session.persist(jokeAPI).replaceWithVoid();
            }
        });
    }

}
