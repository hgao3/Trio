package team3.trio.controller;

import java.time.Month;
import java.util.List;
import java.util.Date;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import org.masukomi.aspirin.Aspirin;
import org.masukomi.aspirin.core.AspirinInternal;

import team3.trio.exception.ResourceNotFoundException;
import team3.trio.model.Role;
import team3.trio.model.User;
import team3.trio.repository.UserRepository;
import team3.trio.utils.JsonUtils;
import team3.trio.utils.PasswordUtils;

@RestController
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FirebaseAuth firebaseAuth;

	@RequestMapping(path = "/signup", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public long signup(@RequestParam(value = "first_name", required = false) String firstName,
			@RequestParam(value = "last_name", required = false) String lastName, 
			@RequestParam String email) {
		return addNewUser(firstName, lastName, email);
	}
	
	@RequestMapping(path = "/rest/user", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public long addNewUser(@RequestParam(value = "first_name", required = false) String firstName,
			@RequestParam(value = "last_name", required = false) String lastName, 
			@RequestParam String email) {
		User user = new User(firstName, lastName, email, false);
		userRepository.save(user);

		LOG.info(user.toString() + " successfully saved into DB");

		return user.getId();
	}

	@RequestMapping(path = "/rest/user/{email}/notify", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String notifyUser(@PathVariable("email") String email, @RequestBody String jsonString) throws MessagingException {
		LOG.info("Sending email notification to user " + email + ".");
		List<User> users = userRepository.findByEmail(email);
		if (users.size() == 0) {
			throw new ResourceNotFoundException("User", "email", email);
		}

		class Data {
			public String from;
			public String channel;
			public String body;
			Data(String from, String channel, String body) {
				this.from = from;
				this.channel = channel;
				this.body = body;
			}
			Data() {
				this("", "", "");
			}
		}

		Gson gson = new Gson();
		Data data = gson.fromJson(jsonString, Data.class);

		MimeMessage msg = AspirinInternal.createNewMimeMessage();
		msg.setFrom(new InternetAddress("notifications@trio.com"));
		msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		msg.setSubject("Trio alert: new chat message from " + email);
		String messageBody = "Trio user " + data.from + " posted this message to " + data.channel + ":<br><br>" + data.body;
		msg.setText(messageBody);
		Aspirin.add(msg);
		return "";
	}

	@RequestMapping(path = "/rest/user/{email}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getUserById(@PathVariable("email") String email) {
		LOG.info("Reading user with email " + email + " from database.");
		
		List<User> users = userRepository.findByEmail(email);
		if (users.size()==0) {
			throw new ResourceNotFoundException("User", "email", email);
		}
		
		User user = users.get(0);
		//User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		return userToJO(user).toString();
	}
	
	@RequestMapping(path = "/rest/user/email/{email}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getUserByEmail(@PathVariable("email") String email) throws FirebaseAuthException {
		LOG.info("Reading user with email " + email + " from firebase.");

		UserRecord userRecord = firebaseAuth.getUserByEmail(email);
		System.out.println("Successfully fetched user data: " + userRecord.getEmail());

		JsonObject jo = new JsonObject();
		jo.addProperty("uid", userRecord.getUid());
		
		return jo.toString();
	}
	
	@RequestMapping(path = "/rest/user/uid/{uid}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getUserByUid(@PathVariable("uid") String uid) throws FirebaseAuthException {
		LOG.info("Reading user with uid " + uid + " from firebase.");

		UserRecord userRecord = firebaseAuth.getUser(uid);
		System.out.println("Successfully fetched user data: " + userRecord.getUid());

		List<User> users = userRepository.findByEmail(userRecord.getEmail());
		
		return userToJO(users.get(0)).toString();
	}

	@RequestMapping(path = "/rest/user", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getUsers() {
		LOG.info("Reading all user from database.");
		List<User> users = userRepository.findAll();
		JsonArray ja = new JsonArray();
		for (User user : users) {
			ja.add(userToJO(user));
		}
		return ja.toString();
	}

	@RequestMapping(path = "/rest/user/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteUser(@PathVariable("id") Long id) {
		LOG.info("User with id " + id + " successfully deleted into database.");
		userRepository.deleteById(id);
	}

	@RequestMapping(path = "/rest/user/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8")
	public @ResponseBody void updateUser(@PathVariable("id") Long id,
			@RequestBody String jsonString) throws Exception {
		LOG.info("User with id " + id + " successfully updated into database.");
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String firstName = (String) JsonUtils.findElementFromJson(jo, "first_name", "String");
		String lastName = (String) JsonUtils.findElementFromJson(jo, "last_name", "String");
		String email = (String) JsonUtils.findElementFromJson(jo, "email", "String");
		//String password = (String) JsonUtils.findElementFromJson(jo, "password", "String");
		

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		if (!StringUtils.isEmpty(firstName)) {
			user.setFirstName(firstName);
		}

		if (!StringUtils.isEmpty(lastName)) {
			user.setLastName(lastName);
		}

		if (!StringUtils.isEmpty(email)) {
			user.setEmail(email);
		}

		userRepository.saveAndFlush(user);
	}

	private JsonObject userToJO(User user) {
		JsonObject jo = new JsonObject();
		jo.addProperty("user_id", user.getId());
		jo.addProperty("email", user.getEmail());
		jo.addProperty("first_name", user.getFirstName());
		jo.addProperty("last_name", user.getLastName());
		jo.addProperty("is_admin", user.isAdmin());

		JsonArray projectsAssignedTo = new JsonArray();
		user.getUserProjects().forEach((s) -> {
			projectsAssignedTo.add(s.getId().getProjectId().toString());
		});
		jo.add("projects", projectsAssignedTo);

		JsonArray projectsManaging = new JsonArray();
		user.getUserProjects().forEach((s) -> {
			if (s.getRole().equals(Role.Manager)) {
				projectsManaging.add(s.getId().getProjectId().toString());
			}

		});
		jo.add("projects_managing", projectsManaging);
		return jo;
	}

}