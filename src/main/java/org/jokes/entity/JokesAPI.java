package org.jokes.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Cacheable
@AllArgsConstructor
@NoArgsConstructor
public class JokesAPI extends PanacheEntityBase {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) // Use identity or other strategy as per your requirement
    private String id;

    private String question;
    private String answer;

    @OneToOne
    @JoinColumn(name = "official_id", referencedColumnName = "jokesid")
    private OfficialJokes officialJoke;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public OfficialJokes getOfficialJoke() {
        return officialJoke;
    }

    public void setOfficialJoke(OfficialJokes officialJoke) {
        this.officialJoke = officialJoke;
    }
}
