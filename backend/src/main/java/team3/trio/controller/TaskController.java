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
import team3.trio.model.*;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.StageRepository;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.DateUtils;
import team3.trio.utils.JsonUtils;
import team3.trio.utils.PasswordUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
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
    
    @RequestMapping(path = "/rest/task", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
	public long addNewTaskt(@RequestBody String jsonString) throws Exception {    	
    	
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		String assignedUserEmail = (String) JsonUtils.findElementFromJson(jo, "assigned_user_email", "String");
		Long stageId = (Long) JsonUtils.findElementFromJson(jo, "stage_id", "Long");
		String content = (String) JsonUtils.findElementFromJson(jo, "content", "String");
		String dueDate = (String) JsonUtils.findElementFromJson(jo, "due_date", "String");
		
		List<User> users = userRepository.findByEmail(assignedUserEmail);
		if (users.size()==0) {
			throw new ResourceNotFoundException("User", "email", assignedUserEmail);
		}
		
		User user = users.get(0);
		
    	//User user = userRepository.findById(assignedUserId)
		//		.orElseThrow(() -> new ResourceNotFoundException("User", "id", assignedUserId));
    	
    	Stage stage = stageRepository.findById(stageId)
				.orElseThrow(() -> new ResourceNotFoundException("Stage", "id", stageId));
    	
    	Date dueAt = DateUtils.toDate(dueDate);
    	
    	Task task = new Task(title, content, dueAt, user, stage);
    	
    	taskRepository.save(task);

        LOG.info(task.toString() + " successfully saved into DB");

        return task.getId();
	}
	
	@RequestMapping(path = "/rest/task/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getTaskById(@PathVariable("id") Long id) {
		LOG.info("Reading stage with id " + id + " from database.");
		
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

		return taskToJO(task).toString();
	}
	
	@RequestMapping(path = "/rest/task", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getTasks() {
		LOG.info("Reading all task from database.");
		List<Task> tasks = taskRepository.findAll();
		JsonArray ja = new JsonArray();
		for (Task task : tasks) {
			ja.add(taskToJO(task));
		}
		return ja.toString();
	}

	@RequestMapping(path = "/rest/task/{id}/mark_ready", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8")
	public void markReady( @PathVariable("id") Long id, @RequestBody String jsonString)
	{
		Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String email = (String) JsonUtils.findElementFromJson(jo, "user_email", "String");
		if (task.checkManagerByEmail(email)) {
			task.markReadyForReview();
		}
	}



	@RequestMapping(path = "/rest/task/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void updateTask(@RequestBody String jsonString,
			@PathVariable("id") Long id) throws Exception {
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		String assignedUserEmail = (String) JsonUtils.findElementFromJson(jo, "assigned_user_email", "String");
		Long stageId = (Long) JsonUtils.findElementFromJson(jo, "stage_id", "Long");
		String content = (String) JsonUtils.findElementFromJson(jo, "content", "String");
		String dueDate = (String) JsonUtils.findElementFromJson(jo, "due_date", "String");
		
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));


		if (!StringUtils.isEmpty(title)) {
			task.setTitle(title);
		}
		
		if (!StringUtils.isEmpty(content)) {
			task.setContent(content);
		}
		
		if (!StringUtils.isEmpty(dueDate)) {
			Date dueAt = DateUtils.toDate(dueDate);
			task.setDueAt(dueAt);
		}
		
		if (!StringUtils.isEmpty(assignedUserEmail)) {
			List<User> users = userRepository.findByEmail(assignedUserEmail);
			if (users.size()==0) {
				throw new ResourceNotFoundException("User", "email", assignedUserEmail);
			}
			
			//User user = userRepository.findById(assignedUserId).orElseThrow(() -> new ResourceNotFoundException("User", "id", assignedUserId));
			task.setAssignedUser(users.get(0));
		}
		
		if (stageId != null && stageId > 0) {
			Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new ResourceNotFoundException("Stage", "id", stageId));
			task.setStage(stage);
		}
		
		Date dt = new Date();
		task.setUpdatedAt(dt);
		taskRepository.save(task);
		
		LOG.info("Task with id " + id + " successfully updated into database.");
	}	

	@RequestMapping(path = "/rest/task/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteTask(@PathVariable("id") Long id) {
		taskRepository.deleteById(id);
		LOG.info("Task with id " + id + " successfully deleted into database.");
	}

	private JsonObject taskToJO(Task task) {
		JsonObject jo = new JsonObject();
		jo.addProperty("task_id", task.getId());
		jo.addProperty("title", task.getTitle());
		jo.addProperty("assigned_user_email", task.getAssignedUser().getEmail());
		jo.addProperty("content", task.getContent());
		jo.addProperty("due_date", DateUtils.toString(task.getDueAt()));
		jo.addProperty("create_date", DateUtils.toString(task.getCreatedAt()));
		jo.addProperty("update_date", DateUtils.toString(task.getUpdatedAt()));
		jo.addProperty("stage_id", task.getStage().getId());
		return jo;
	}
    

    
    
}