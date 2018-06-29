package team3.trio.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import team3.trio.Trio;
import team3.trio.model.User;
import team3.trio.utils.JsonUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Trio.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:unit-test.properties")
public class UserControllerTest {

	@LocalServerPort
	private int port;

	@Before
	public void init() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}


	@Test
	public void addNewUser_GetIt_DeleteIt() {
		User newuser = new User("new", "user", "new@bu.edu",  false);

		Long userId = given().queryParam("first_name", newuser.getFirstName())
				.queryParam("last_name", newuser.getLastName()).queryParam("email", newuser.getEmail())
				.when().post("/signup").then()
				.statusCode(is(HttpStatus.SC_CREATED)).extract().body().as(Long.class);
	}

}
