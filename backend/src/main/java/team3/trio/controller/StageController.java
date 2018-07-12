package team3.trio.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import team3.trio.exception.ResourceNotFoundException;
import team3.trio.model.Project;
import team3.trio.model.Role;
import team3.trio.model.Stage;
import team3.trio.model.Task;
import team3.trio.model.User;
import team3.trio.model.UserProject;
import team3.trio.model.UserProjectId;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.StageRepository;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserProjectRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.JsonUtils;

@RestController
public class StageController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private StageRepository stageRepository;
	
	@Autowired
	private UserProjectRepository userProjectRepository;
	
	@Autowired
	private TaskRepository taskRepository;

	@RequestMapping(path = "/rest/stage", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public long addNewStage(@RequestBody String jsonString) throws Exception {

		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		Long projectId = (Long) JsonUtils.findElementFromJson(jo, "project_id", "Long");
		
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
		
		Stage stage = new Stage(title, project);
		
		stageRepository.save(stage);
		
		LOG.info(stage.toString() + " successfully saved into DB");

		return stage.getId();
	}
	
	@RequestMapping(path = "/rest/stage/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getStageById(@PathVariable("id") Long id) {
		LOG.info("Reading stage with id " + id + " from database.");
		
		Stage stage = stageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stage", "id", id));

		return stageToJO(stage).toString();
	}
	
	@RequestMapping(path = "/rest/stage", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getStages() {
		LOG.info("Reading all stage from database.");
		List<Stage> stages = stageRepository.findAll();
		JsonArray ja = new JsonArray();
		for (Stage stage : stages) {
			ja.add(stageToJO(stage));
		}
		return ja.toString();
	}

	@RequestMapping(path = "/rest/stage/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void updateStage(@RequestBody String jsonString,
			@PathVariable("id") Long id) throws Exception {
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String"); 

		Stage stage = stageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stage", "id", id));


		if (!StringUtils.isEmpty(title)) {
			stage.setTitle(title);
			stageRepository.saveAndFlush(stage);
		}
		LOG.info("Stage with id " + id + " successfully updated into database.");
	}
	

	@RequestMapping(path = "/rest/stage/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteStage(@PathVariable("id") Long id) {
		stageRepository.deleteById(id);
		LOG.info("Stage with id " + id + " successfully deleted into database.");
	}

	private JsonObject stageToJO(Stage stage) {
		JsonObject jo = new JsonObject();
		jo.addProperty("stage_id", stage.getId());
		jo.addProperty("title", stage.getTitle());
		jo.addProperty("project_id", stage.getProject().getId());

		JsonArray taskList = new JsonArray();
		
		List<Task> tasks = this.taskRepository.findByStageId(stage.getId());
		for (Task task: tasks) {
			taskList.add(task.getId());
		}
		jo.add("tasks", taskList);
		return jo;
	}

}