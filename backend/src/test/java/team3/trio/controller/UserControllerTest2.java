package team3.trio.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Field;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import team3.trio.Trio;
import team3.trio.model.User;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.JsonUtils;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserControllerTest2 {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository users;
    
	UserController controller;
	
	@Before
	public void init() throws Exception {
		controller = new UserController();
		
		Field field = UserController.class.getDeclaredField("userRepository");
		field.setAccessible(true);
		field.set(controller, users);
	}

	@Test
	public void getUsers() {

		String response = controller.getUsers();
		
		JsonArray ja = JsonUtils.toJsonArray(response);
		Assert.assertTrue(ja.size() == 5);

	}

	@Test
	public void addNewUser_GetIt_DeleteIt() {
		User newuser = new User("new", "user", "new@bu.edu",  false);

		Long userId = controller.addNewUser(newuser.getFirstName(), newuser.getLastName(), newuser.getEmail());				
		
		String response = controller.getUserById(newuser.getEmail());
		JsonObject jo = JsonUtils.toJsonObject(response);
		Assert.assertTrue(jo.get("first_name").getAsString().equalsIgnoreCase(newuser.getFirstName()));
		Assert.assertTrue(jo.get("last_name").getAsString().equalsIgnoreCase(newuser.getLastName()));

		controller.deleteUser(userId);	
		
	}

}
