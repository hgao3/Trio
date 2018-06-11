package team3.trio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import team3.trio.exception.ResourceNotFoundException;
import team3.trio.model.Role;
import team3.trio.model.User;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @RequestMapping(path = "/user", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody long addNewUser (@RequestParam String firstName, @RequestParam String lastName,
                                          @RequestParam String email, @RequestParam String password) {
        User user = new User(firstName, lastName, email, password, false);
        userRepository.save(user);

        LOG.info(user.toString() + " successfully saved into DB");

        return user.getId();
    }

    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUserById(@PathVariable("id") Long id) {
        LOG.info("Reading user with id " + id + " from database.");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return userToJO(user).toString();
    }
    
    @RequestMapping(path = "/user", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUsers() {
        LOG.info("Reading all user from database.");
        List<User> users = userRepository.findAll();
        
        JsonArray ja = new JsonArray();
        
        for(User user: users) {
        	ja.add(userToJO(user));
        }
        
        return ja.toString();
    }
    
    private JsonObject userToJO(User user) {
        JsonObject jo = new JsonObject();
        jo.addProperty("user_id", user.getId());
        jo.addProperty("email", user.getEmail());
        jo.addProperty("first_name", user.getFirstName());
        jo.addProperty("last_name", user.getLastName());
        jo.addProperty("is_admin", user.isAdmin());
        
        ArrayList<String> projectList = new ArrayList<String>();
        user.getUserProjects().forEach((s) ->{
       	 projectList.add(s.getId().getProjectId().toString());
       });
        jo.addProperty("projects", projectList.toString());
        
        ArrayList<String> projectList2 = new ArrayList<String>();
        user.getUserProjects().forEach((s) ->{
        	if (s.getRole().equals(Role.Manager)) {
        		projectList2.add(s.getId().getProjectId().toString());
        	}
       	 
       });
        jo.addProperty("projects_managing", projectList2.toString());
        return jo;
    }

}