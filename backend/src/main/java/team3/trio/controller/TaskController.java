package team3.trio.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import team3.trio.exception.ResourceNotFoundException;
import team3.trio.model.Role;
import team3.trio.model.Task;
import team3.trio.model.User;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.StageRepository;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class TaskController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private StageRepository stageRepository;
    
    @RequestMapping(path = "/task", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public long addNewUser () {
    	
    	Task task = new Task();
    	task.setTitle("Set up Project");
    	task.setContent("Set up backend and frontend of project.");
    	Date dt = new Date();
    	
    	task.setCreatedAt(dt);
    	task.setUpdatedAt(dt);
    	task.setAssignedUser(userRepository.findById((long) 1).get());
    	task.setStage(stageRepository.findById((long)1).get());
    	
    	taskRepository.save(task);

        LOG.info(task.toString() + " successfully saved into DB");

        return task.getId();
    }
}