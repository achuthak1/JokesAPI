package com.jokes.test;

import io.quarkus.test.junit.QuarkusTest;
//import io.rest-assured.RestAssured;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

//import static io.rest-assured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class JokesControllerIntegrationTest {

    @Test
    public void testGetJokesEndpoint() {
        RestAssured.given()
                .queryParam("count", 5)
                .when().get("/jokes")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("$.size()", greaterThanOrEqualTo(1)) // Checks that there is at least one joke
                .body("[0].question", not(emptyOrNullString())) // Checks that the first joke has a question
                .body("[0].answer", not(emptyOrNullString()));  // Checks that the first joke has an answer
    }

    @Test
    public void testGetJokesBadRequest() {
        RestAssured.given()
                .queryParam("count", 0)
                .when().get("/jokes")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(equalTo("Count must be greater than zero."));
    }
}
