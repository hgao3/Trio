package team3.trio.controller;

import java.util.ArrayList;
import java.util.Date;
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
import team3.trio.model.Channel;
import team3.trio.model.Issue;
import team3.trio.model.Project;
import team3.trio.model.Role;
import team3.trio.model.Stage;
import team3.trio.model.Task;
import team3.trio.model.User;
import team3.trio.model.UserProject;
import team3.trio.model.UserProjectId;
import team3.trio.repository.ChannelRepository;
import team3.trio.repository.IssueRepository;
import team3.trio.repository.ProjectRepository;
import team3.trio.repository.StageRepository;
import team3.trio.repository.TaskRepository;
import team3.trio.repository.UserProjectRepository;
import team3.trio.repository.UserRepository;
import team3.trio.utils.DateUtils;
import team3.trio.utils.JsonUtils;

@RestController
public class ChannelController {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelController.class);

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
	
	@Autowired
	private ChannelRepository channelRepository;

	@RequestMapping(path = "/rest/channel", method = RequestMethod.POST, 
			consumes = "application/json;charset=UTF-8", 
			produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public long addNewChannel(@RequestBody String jsonString) throws Exception {
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String chatId = (String) JsonUtils.findElementFromJson(jo, "chat_id", "String");
		String ownerUserEmail = (String) JsonUtils.findElementFromJson(jo, "owner_user_email", "String");
		Long projectId = (Long) JsonUtils.findElementFromJson(jo, "project_id", "Long");
		Long taskId = (Long) JsonUtils.findElementFromJson(jo, "task_id", "Long");
		Long issueId = (Long) JsonUtils.findElementFromJson(jo, "issue_id", "Long");
		
		List<User> users = userRepository.findByEmail(ownerUserEmail);
		if (users.size()==0) {
			throw new ResourceNotFoundException("User", "email", ownerUserEmail);
		}
		
		User user = users.get(0);
		
		Channel channel = new Channel(chatId, user);
		
		if (projectId != null && projectId > 0) {
			Project project = projectRepository.findById(projectId)
					.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
			
			channel.setProject(project);
		}
		if (taskId != null && taskId > 0) {
			Task task = taskRepository.findById(taskId)
					.orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
			
			channel.setTask(task);
		}
		if (issueId != null && issueId > 0) {
			Issue issue = this.issueRepository.findById(issueId)
					.orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));
			
			channel.setIssue(issue);
		}	
		
		channelRepository.save(channel);
		
		
		LOG.info(channel.toString() + " successfully saved into DB");

		return channel.getId();		
	}
	
	@RequestMapping(path = "/rest/channel/task_id/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getChannelByTaskId(@PathVariable("id") Long id) {
		LOG.info("Reading channel with task id " + id + " from database.");
		
		List<Channel> channels = channelRepository.findByTaskId(id);
		
		if (channels.size()>0) {
			return channels.get(0).getChatId();
		} else {
			return null;
		}
	}
	
	@RequestMapping(path = "/rest/channel/project_id/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getChannelByProjectId(@PathVariable("id") Long id) {
		LOG.info("Reading channel with project id " + id + " from database.");
		
		List<Channel> channels = channelRepository.findByProjectId(id);
		
		if (channels.size()>0) {
			return channels.get(0).getChatId();
		} else {
			return null;
		}
	}
	
	@RequestMapping(path = "/rest/channel/issue_id/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getChannelByIssueId(@PathVariable("id") Long id) {
		LOG.info("Reading channel with issue id " + id + " from database.");
		
		List<Channel> channels = channelRepository.findByIssueId(id);
		
		if (channels.size()>0) {
			return channels.get(0).getChatId();
		} else {
			return null;
		}
	}
	
	@RequestMapping(path = "/rest/channel/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getChannelById(@PathVariable("id") Long id) {
		LOG.info("Reading channel with id " + id + " from database.");
		
		Channel channel = channelRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Channel", "id", id));

		return channelToJO(channel).toString();
	}
	
	@RequestMapping(path = "/rest/channel/chat_id/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getChannelByChatId(@PathVariable("id") Long id) {
		LOG.info("Reading channel with id " + id + " from database.");
		
		List<Channel> channels = channelRepository.findByIssueId(id);
		
		if (channels.size()==0) {
			throw new ResourceNotFoundException("Channel", "chat_id", id);
		}
		
		Channel channel = channels.get(0);

		return channelToJO(channel).toString();
	}
	
	@RequestMapping(path = "/rest/channel", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getIssues() {
		LOG.info("Reading all channel from database.");
		List<Channel> channels = channelRepository.findAll();
		JsonArray ja = new JsonArray();
		for (Channel channel: channels) {
			ja.add(channelToJO(channel));
		}
		return ja.toString();
	}

	@RequestMapping(path = "/rest/channel/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void updateIssue(@RequestBody String jsonString,
			@PathVariable("id") Long id) throws Exception {
		
		// json
		JsonObject jo = JsonUtils.toJsonObject(jsonString);
		String chatId = (String) JsonUtils.findElementFromJson(jo, "chat_id", "String");
		String ownerUserEmail = (String) JsonUtils.findElementFromJson(jo, "owner_user_email", "String");
		Long projectId = (Long) JsonUtils.findElementFromJson(jo, "project_id", "Long");
		Long taskId = (Long) JsonUtils.findElementFromJson(jo, "task_id", "Long");
		Long issueId = (Long) JsonUtils.findElementFromJson(jo, "issue_id", "Long");
		
		
		Channel channel = channelRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Channel", "id", id));
		
		channel.setChatId(chatId);
		
		if (!StringUtils.isEmpty(ownerUserEmail)) {
			List<User> users = userRepository.findByEmail(ownerUserEmail);
			if (users.size()==0) {
				throw new ResourceNotFoundException("User", "email", ownerUserEmail);
			}
			
			User user = users.get(0);
			channel.setOwnerUser(user);
		}
		
		if (projectId != null && projectId > 0) {
			Project project = projectRepository.findById(projectId)
					.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
			
			channel.setProject(project);
		}
		if (taskId != null && taskId > 0) {
			Task task = taskRepository.findById(taskId)
					.orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
			
			channel.setTask(task);
		}
		if (issueId != null && issueId > 0) {
			Issue issue = this.issueRepository.findById(issueId)
					.orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));
			
			channel.setIssue(issue);
		}	
		
		channelRepository.save(channel);
		
		LOG.info("Channel with id " + id + " successfully updated into database.");
	}
	

	@RequestMapping(path = "/rest/channel/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody void deleteIssue(@PathVariable("id") Long id) {
		issueRepository.deleteById(id);
		LOG.info("Issue with id " + id + " successfully deleted into database.");
	}

	private JsonObject channelToJO(Channel channel) {
		
		JsonObject jo = new JsonObject();
		jo.addProperty("channel_id", channel.getId());
		jo.addProperty("chat_id", channel.getChatId());
		jo.addProperty("owner_user_email", channel.getOwnerUser().getEmail());
		
		if (channel.getTask()!=null) {
			jo.addProperty("project_id", channel.getProject().getId());
		} else {
			jo.addProperty("project_id", "");
		}
		
		if (channel.getTask()!=null) {
			jo.addProperty("task_id", channel.getTask().getId());
		} else {
			jo.addProperty("task_id", "");
		}
		
		if (channel.getTask()!=null) {
			jo.addProperty("issue_id", channel.getIssue().getId());
		} else {
			jo.addProperty("issue_id", "");
		}

		return jo;
	}	
}