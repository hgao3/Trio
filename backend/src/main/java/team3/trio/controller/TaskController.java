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
import team3.trio.model.Project;
import team3.trio.model.Role;
import team3.trio.model.Stage;
import team3.trio.model.Task;
import team3.trio.model.User;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.StageRepository;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.DateUtils;
import team3.trio.utils.PasswordUtils;

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
    
    @RequestMapping(path = "/task", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
	public long addNewTaskt(@RequestParam(value = "title") String title,
			@RequestParam(value = "assigned_user_id") Long assignedUserId,
			@RequestParam(value = "stage_id") Long stageId,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "due_date", required = false) String dueDate) throws ParseException {    	
    	
    	User user = userRepository.findById(assignedUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", assignedUserId));
    	
    	Stage stage = stageRepository.findById(stageId)
				.orElseThrow(() -> new ResourceNotFoundException("Stage", "id", stageId));
    	
    	Date dueAt = DateUtils.toDate(dueDate);
    	
    	Task task = new Task(title, content, dueAt, user, stage);
    	
    	taskRepository.save(task);

        LOG.info(task.toString() + " successfully saved into DB");

        return task.getId();
	}
	
	@RequestMapping(path = "/task/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getTaskById(@PathVariable("id") Long id) {
		LOG.info("Reading stage with id " + id + " from database.");
		
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

		return taskToJO(task).toString();
	}
	
	@RequestMapping(path = "/task", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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

	@RequestMapping(path = "/task/{id}", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void updateTask(@PathVariable("id") Long id,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assigned_user_id", required = false) Long assignedUserId,
			@RequestParam(value = "stage_id", required = false) Long stageId,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "due_date", required = false) String dueDate) throws ParseException {
		

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
		
		if (assignedUserId != null && assignedUserId > 0) {
			User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
			task.setAssignedUser(user);
		}
		
		if (stageId != null && stageId > 0) {
			Stage stage = stageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stage", "id", id));
			task.setStage(stage);
		}
		
		LOG.info("Task with id " + id + " successfully updated into database.");
	}	

	@RequestMapping(path = "/task/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteTask(@PathVariable("id") Long id) {
		stageRepository.deleteById(id);
		LOG.info("Task with id " + id + " successfully deleted into database.");
	}

	private JsonObject taskToJO(Task task) {
		JsonObject jo = new JsonObject();
		jo.addProperty("task_id", task.getId());
		jo.addProperty("title", task.getTitle());
		jo.addProperty("assigned_user_id", task.getAssignedUser().getId());
		jo.addProperty("content", task.getContent());
		jo.addProperty("due_date", DateUtils.toString(task.getDueAt()));
		jo.addProperty("create_date", DateUtils.toString(task.getCreatedAt()));
		jo.addProperty("update_date", DateUtils.toString(task.getUpdatedAt()));
		jo.addProperty("stage_id", task.getStage().getId());
		return jo;
	}
    

    
    
}