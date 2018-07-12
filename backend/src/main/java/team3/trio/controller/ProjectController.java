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
public class ProjectController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private StageRepository stageRepository;
	
	@Autowired
	private UserProjectRepository userProjectRepository;

	@RequestMapping(path = "/rest/project", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public long addNewProject(@RequestBody String jsonString) throws Exception {

		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		Long managerId = (Long) JsonUtils.findElementFromJson(jo, "manager_id", "Long");
		
		User user = userRepository.findById(managerId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", managerId));

		Project project = new Project(title);

		UserProject up = new UserProject(user, project, Role.Manager);
		project.addUserProjects(up);

		projectRepository.save(project);
		LOG.info(project.toString() + " successfully saved into DB");

		return project.getId();
	}

	@RequestMapping(path = "/rest/project/{id}/add_teammate", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public void addTeammateToProject(@PathVariable("id") Long id,
			@RequestBody String jsonString) throws Exception {
		LOG.info("Reading user with id " + id + " from database.");
		
		//json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		Long teammateId = (Long) JsonUtils.findElementFromJson(jo, "teammate_id", "Long");

		Project project = this.projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

		User user = userRepository.findById(teammateId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", teammateId));

		UserProject up = new UserProject(user, project, Role.Teammate);
		project.addUserProjects(up);

		projectRepository.save(project);
		LOG.info("successfully record add teammate " + user.getEmail() + " to project " + project.getTitle()
				+ " into DB");
	}

	@RequestMapping(path = "/rest/project/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void updateProject(@PathVariable("id") Long id,
			@RequestBody String jsonString) throws Exception {
		LOG.info("User with id " + id + " successfully updated into database.");
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		Long managerId = (Long) JsonUtils.findElementFromJson(jo, "manager_id", "Long");

		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

		if (!StringUtils.isEmpty(title)) {
			project.setTitle(title);
			projectRepository.saveAndFlush(project);
		}

		if (managerId != null && managerId > 0) {
			User user = userRepository.findById(managerId)
					.orElseThrow(() -> new ResourceNotFoundException("User", "id", managerId));
			
			userProjectRepository.findAll().forEach((up) -> {
				System.out.println("project id: " + project.getId());
				if (up.getRole().equals(Role.Manager)) System.out.println("role");
				if (up.getId().getProjectId()==project.getId()) System.out.println("id");
				if (up.getRole().equals(Role.Manager) && up.getId().getProjectId()==project.getId()) {
					userProjectRepository.delete(up);
				}
			});
			UserProject currentManger = new UserProject(user, project, Role.Manager);
			userProjectRepository.saveAndFlush(currentManger);		
		}
	}

	@RequestMapping(path = "/rest/project/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getProjectById(@PathVariable("id") Long id) {
		LOG.info("Reading project with id " + id + " from database.");
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

		return projectToJO(project).toString();
	}

	@RequestMapping(path = "/rest/project", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getProjects() {
		LOG.info("Reading all project from database.");
		List<Project> projects = projectRepository.findAll();
		JsonArray ja = new JsonArray();
		for (Project project : projects) {
			ja.add(projectToJO(project));
		}
		return ja.toString();
	}

	@RequestMapping(path = "/rest/project/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteProject(@PathVariable("id") Long id) {
		LOG.info("Project with id " + id + " successfully deleted into database.");
		projectRepository.deleteById(id);
	}

	private JsonObject projectToJO(Project project) {
		JsonObject jo = new JsonObject();
		jo.addProperty("project_id", project.getId());
		jo.addProperty("project_title", project.getTitle());

		JsonArray manager = new JsonArray();
		JsonArray teammateList = new JsonArray();

		project.getUserProjects().forEach((p) -> {
			if (p.getRole().equals(Role.Manager)) {
				manager.add(p.getId().getUserId().toString());
			} else if (p.getRole().equals(Role.Teammate)) {
				teammateList.add(p.getId().getUserId().toString());
			}
		});

		jo.add("manager", manager);
		jo.add("teammate", teammateList);

		JsonArray stageList = new JsonArray();

		stageRepository.findAll().forEach((s) -> {
			if (s.getProject().getId().equals(project.getId())) {
				stageList.add(s.getId());
			}
		});

		jo.add("stages", stageList);

		return jo;
	}

}