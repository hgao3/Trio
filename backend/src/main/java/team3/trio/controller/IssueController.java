package team3.trio.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import team3.trio.model.Issue;
import team3.trio.model.Project;
import team3.trio.model.Role;
import team3.trio.model.Stage;
import team3.trio.model.Task;
import team3.trio.model.User;
import team3.trio.model.UserProject;
import team3.trio.model.UserProjectId;
import team3.trio.repository.IssueRepository;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.StageRepository;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserProjectRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.DateUtils;
import team3.trio.utils.JsonUtils;

@RestController
public class IssueController {

	private static final Logger LOG = LoggerFactory.getLogger(IssueController.class);

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
	
	@Autowired
	private IssueRepository issueRepository;

	@RequestMapping(path = "/rest/issue", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public long addNewIssue(@RequestBody String jsonString) throws Exception {
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		String content = (String) JsonUtils.findElementFromJson(jo, "content", "String");
		//Boolean openStatus = (Boolean) JsonUtils.findElementFromJson(jo, "open_status", "Boolean");
		String ownerUserEmail = (String) JsonUtils.findElementFromJson(jo, "owner_user_email", "String");
		String priorityLevel = (String) JsonUtils.findElementFromJson(jo, "priority_level", "String");
		Long projectId = (Long) JsonUtils.findElementFromJson(jo, "project_id", "Long");
		//Long taskId = (Long) JsonUtils.findElementFromJson(jo, "task_id", "Long");
		String closeDate = (String) JsonUtils.findElementFromJson(jo, "close_date", "String");
		
		//DateUtils
		
		List<User> users = userRepository.findByEmail(ownerUserEmail);
		if (users.size()==0) {
			throw new ResourceNotFoundException("User", "email", ownerUserEmail);
		}
		
		User user = users.get(0);
		
		
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
		
		Issue issue = new Issue(title, content, user, priorityLevel, project);	
		
		if (!StringUtils.isEmpty(closeDate)) {
			Date closeAt = DateUtils.toDateFromISO(closeDate);
			issue.setClosedat(closeAt);
		}
		
		issueRepository.save(issue);
		
		LOG.info(issue.toString() + " successfully saved into DB");

		return issue.getId();		
	}
	
	@RequestMapping(path = "/rest/issue/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getIssueById(@PathVariable("id") Long id) {
		LOG.info("Reading issue with id " + id + " from database.");
		
		Issue issue = issueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));

		return issueToJO_AvailableUserProject(issue).toString();
	}
	
	@RequestMapping(path = "/rest/issue", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getIssues() {
		LOG.info("Reading all issue from database.");
		
		List<Issue> issues = new ArrayList<Issue>();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName(); //get logged in username
		List<User> users = userRepository.findByEmail(email);
		if (users.size() == 0) {
			throw new ResourceNotFoundException("User", "email", email);
		}
		User user = users.get(0);
		for(UserProject up: user.getUserProjects()) {
			issues.addAll(issueRepository.findByProjectIdAndOpenStatus(up.getProject().getId(), true));
		}
		
		issues.sort(new Comparator<Issue>() {
			@Override
			public int compare(Issue m1, Issue m2) {
				String p1 = m1.getPriorityLevel();
				String p2 = m2.getPriorityLevel();
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				if (p1.equals(p2))
					return 0;
				if (p1.equals("Low") && (p2.equals("Medium") || p2.equals("High")))
					return 1;
				if (p1.equals("Medium") && p2.equals("High"))
					return 1;
				return -1;
			}
		}.reversed().thenComparing(Issue::getCreatedAt).reversed());
		
		
		JsonArray ja = new JsonArray();
		for (Issue issue : issues) {
			ja.add(issueToJO(issue));
		}
		return ja.toString();
	}
	
	@RequestMapping(path = "/rest/issue/project_id/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getIssueByProjectId(@PathVariable("id") Long id) {
		LOG.info("Reading issue with project id " + id + " from database.");
		
		List<Issue> issues = issueRepository.findByProjectIdAndOpenStatus(id, true);
		
		issues.sort(new Comparator<Issue>() {
			@Override
			public int compare(Issue m1, Issue m2) {
				String p1 = m1.getPriorityLevel();
				String p2 = m2.getPriorityLevel();
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				if (p1.equals(p2))
					return 0;
				if (p1.equals("Low") && (p2.equals("Medium") || p2.equals("High")))
					return 1;
				if (p1.equals("Medium") && p2.equals("High"))
					return 1;
				return -1;
			}
		}.reversed().thenComparing(Issue::getCreatedAt).reversed());
		
		JsonArray ja = new JsonArray();
		for (Issue issue : issues) {
			ja.add(issueToJO(issue));
		}
		return ja.toString();
	}

	@RequestMapping(path = "/rest/issue/convertToTask/{id}", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void convertIssueToTask(@RequestBody String jsonString,
			@PathVariable("id") Long id) throws Exception {
		
		Issue issue = issueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));
		
		

		String title = "Issue: " + issue.getTitle();
		User manager = findManager(issue.getProject().getUserProjects());
		if (manager == null) {
			throw new ResourceNotFoundException("Project", "Role", "Manager");
		}
		
		List<Stage> stages = stageRepository.findByProjectId(issue.getProject().getId());
		if (stages.size()==0) {
			throw new ResourceNotFoundException("Stage", "project_id", issue.getProject().getId());
		}

		String content = issue.getContent();
		
		Task task = new Task(title, content, null, manager, stages.get(0));    	
    	taskRepository.save(task);

        LOG.info(task.toString() + " successfully saved into DB");
        
        issue.setTask(task);
        issueRepository.save(issue);
		LOG.info("Issue with id " + id + " successfully updated into database.");
	}
	
	@RequestMapping(path = "/rest/issue/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void updateIssue(@RequestBody String jsonString,
			@PathVariable("id") Long id) throws Exception {
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String title = (String) JsonUtils.findElementFromJson(jo, "title", "String");
		String content = (String) JsonUtils.findElementFromJson(jo, "content", "String");
		Boolean openStatus = (Boolean) JsonUtils.findElementFromJson(jo, "open_status", "Boolean");
		String ownerUserEmail = (String) JsonUtils.findElementFromJson(jo, "owner_user_email", "String");
		String priorityLevel = (String) JsonUtils.findElementFromJson(jo, "priority_level", "String");
		Long projectId = (Long) JsonUtils.findElementFromJson(jo, "project_id", "Long");
		Long taskId = (Long) JsonUtils.findElementFromJson(jo, "task_id", "Long");
		String closeDate = (String) JsonUtils.findElementFromJson(jo, "close_date", "String");
		
		Issue issue = issueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));
		
		
		List<User> users = userRepository.findByEmail(ownerUserEmail);
		if (users.size()==0) {
			throw new ResourceNotFoundException("User", "email", ownerUserEmail);
		}
		
		User user = users.get(0);
		
		
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));		
		
		if (!StringUtils.isEmpty(title)) {
			issue.setTitle(title);
		}
		
		if (!StringUtils.isEmpty(content)) {
			issue.setContent(content);
		}
		
		issue.setOpenStatus(openStatus);
		issue.setOwnerUser(user);		
		
		if (!StringUtils.isEmpty(priorityLevel)) {
			issue.setPriorityLevel(priorityLevel);
		}
		
		issue.setProject(project);
		
		if (taskId != null && taskId > 0) {
			Task task = taskRepository.findById(taskId)
					.orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
			
			issue.setTask(task);
		}
		
		if (!StringUtils.isEmpty(closeDate)) {
			Date closeAt = DateUtils.toDateFromISO(closeDate);
			issue.setClosedat(closeAt);
		}
		
		Date dt = new Date();
		issue.setUpdatedAt(dt);
		issueRepository.save(issue);
		
		LOG.info("Issue with id " + id + " successfully updated into database.");
	}
	

	@RequestMapping(path = "/rest/issue/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteIssue(@PathVariable("id") Long id) {
		issueRepository.deleteById(id);
		LOG.info("Issue with id " + id + " successfully deleted into database.");
	}

	private JsonObject issueToJO(Issue issue) {
		JsonObject jo = new JsonObject();
		jo.addProperty("issue_id", issue.getId());
		jo.addProperty("title", issue.getTitle());
		jo.addProperty("content", issue.getContent());
		jo.addProperty("open_status", issue.isOpenStatus());
		jo.addProperty("owner_user_email", issue.getOwnerUser().getEmail());
		jo.addProperty("owner_user_name", issue.getOwnerUser().getFullName());
		jo.addProperty("priority_level", issue.getPriorityLevel());
		jo.addProperty("project_id", issue.getProject().getId());
		jo.addProperty("project_title", issue.getProject().getTitle());
		if (issue.getTask()!=null) {
			jo.addProperty("task_id", issue.getTask().getId());
			jo.addProperty("task_owner_user_name", issue.getTask().getAssignedUser().getFullName());
		} else {
			jo.addProperty("task_id", "");
			jo.addProperty("task_owner_user_name", "");
		}
		if (issue.getClosedat()!=null) {
			jo.addProperty("close_date", DateUtils.toIsoString(issue.getClosedat()));
		} else {
			jo.addProperty("close_date", "");
		}
		jo.addProperty("create_date", DateUtils.toIsoString(issue.getCreatedAt()));
		jo.addProperty("update_date", DateUtils.toIsoString(issue.getUpdatedAt()));
		return jo;
	}
	private JsonObject issueToJO_AvailableUserProject(Issue issue) {
		JsonObject jo = new JsonObject();
		jo.addProperty("issue_id", issue.getId());
		jo.addProperty("title", issue.getTitle());
		jo.addProperty("content", issue.getContent());
		jo.addProperty("open_status", issue.isOpenStatus());
		jo.addProperty("owner_user_email", issue.getOwnerUser().getEmail());
		jo.addProperty("owner_user_name", issue.getOwnerUser().getFullName());
		jo.addProperty("priority_level", issue.getPriorityLevel());
		jo.addProperty("project_id", issue.getProject().getId());
		jo.addProperty("project_title", issue.getProject().getTitle());
		if (issue.getTask()!=null) {
			jo.addProperty("task_id", issue.getTask().getId());
			jo.addProperty("task_owner_user_name", issue.getTask().getAssignedUser().getFullName());
			jo.addProperty("task_title", issue.getTask().getTitle());
		} else {
			jo.addProperty("task_id", "");
			jo.addProperty("task_owner_user_name", "");
			jo.addProperty("task_title", "");
		}
		if (issue.getClosedat()!=null) {
			jo.addProperty("close_date", DateUtils.toIsoString(issue.getClosedat()));
		} else {
			jo.addProperty("close_date", "");
		}
		jo.addProperty("create_date", DateUtils.toIsoString(issue.getCreatedAt()));
		jo.addProperty("update_date", DateUtils.toIsoString(issue.getUpdatedAt()));
		
		JsonArray userJA = new JsonArray();
		
		for(UserProject up: issue.getProject().getUserProjects()) {
			JsonObject jo2 = new JsonObject();
			jo2.addProperty("user_name", up.getUser().getFullName());
			jo2.addProperty("user_email", up.getUser().getEmail());
			userJA.add(jo2);	
		}
		jo.add("availableUsers", userJA);
		
		JsonArray projectJA = new JsonArray();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName(); //get logged in username
		
		List<User> users = userRepository.findByEmail(email);
		if (users.size() == 0) {
			throw new ResourceNotFoundException("User", "email", email);
		}
		User user = users.get(0);
		
		for (UserProject up : user.getUserProjects()) {
			JsonObject jo2 = new JsonObject();
			jo2.addProperty("project_title", up.getProject().getTitle());
			jo2.addProperty("project_id", up.getProject().getId());
			projectJA.add(jo2);	
		}
		
		jo.add("availableProject", projectJA);   
		return jo;
	}
	
	public User findManager(Set<UserProject> userProjects) {
		for (UserProject up : userProjects) {
			if (up.getRole().equals(Role.Manager)) {
				return userRepository.findById(up.getId().getUserId())
						.orElseThrow(() -> new ResourceNotFoundException("User", "id", up.getId().getUserId()));
			}
		}
		return null;
	}
}