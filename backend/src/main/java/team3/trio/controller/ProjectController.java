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
import team3.trio.model.Task;
import team3.trio.model.User;
import team3.trio.model.UserProject;
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
public class ProjectController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private StageRepository stageRepository;
    
    @RequestMapping(path = "/project", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public long addNewProject (    		
    		@RequestParam(value = "title", required = false) String title, 
    		@RequestParam(value = "manager_id", required = false) Long managerId) {
    	
    	User user = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", managerId));
    	
    	
    	Project project = new Project(title);
        
        UserProject up = new UserProject(user, project, Role.Manager);
        project.addUserProjects(up);
        
    	projectRepository.save(project);
        LOG.info(project.toString() + " successfully saved into DB");

        return project.getId();
    }

    @RequestMapping(path = "/project/{id}/add_teammate", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addTeammateToProject(@PathVariable("id") Long id,
    		@RequestParam(value = "teammate_id", required = false) Long teammateId) {
        LOG.info("Reading user with id " + id + " from database.");
        
        Project project = this.projectRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        
        User user = userRepository.findById(teammateId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teammateId));
        
        UserProject up = new UserProject(user, project, Role.Teammate);
        project.addUserProjects(up);
        
    	projectRepository.save(project);
        LOG.info("successfully record add teammate "+ user.getEmail()+" to project "+project.getTitle()+" into DB");
    }
    
    @RequestMapping(path = "/project/{id}", method = RequestMethod.PATCH)
    public @ResponseBody void updateUser ( @PathVariable("id") Long id,
    		@RequestParam(value = "title", required = false) String title, 
    		@RequestParam(value = "manager_id", required = false) Long managerId) {
        LOG.info("User with id " + id + " successfully updated into database.");
        
        
        Project project = projectRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (!StringUtils.isEmpty(title)) {
        	project.setTitle(title);
        }
        
        if (managerId >0) {
            User user = userRepository.findById(managerId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", managerId));           
            
            project.getUserProjects().forEach((up) ->{
            	if(up.getRole().equals(Role.Manager)) {
            		up.getId().setUserId(user.getId());
            		up.setUser(user);
            	}
            });
        }

        projectRepository.saveAndFlush(project);
    }
    
    @RequestMapping(path = "/project/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUserById(@PathVariable("id") Long id) {
        LOG.info("Reading project with id " + id + " from database.");
        Project project = projectRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        
        return projectToJO(project).toString();
    }
   
    
    @RequestMapping(path = "/project", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUsers() {
        LOG.info("Reading all project from database.");
        List<Project> projects = projectRepository.findAll();
        JsonArray ja = new JsonArray();       
        for(Project project: projects) {     	
        	ja.add(projectToJO(project));
        }      
        return ja.toString();
    }
    
    @RequestMapping(path = "/project/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public @ResponseBody void deleteUser (@PathVariable("id") Long id) {
        LOG.info("Project with id " + id + " successfully deleted into database.");
        projectRepository.deleteById(id);
    }

    private JsonObject projectToJO(Project project) {
        JsonObject jo = new JsonObject();
        jo.addProperty("project_id", project.getId());
        jo.addProperty("project_title", project.getTitle());
/*        jo.addProperty("manager", project);
        jo.addProperty("teammate", project);
        jo.addProperty("stages", project);*/
        

        
/*        ArrayList<String> projectList = new ArrayList<String>();
        project.getUserProjects().forEach((p) ->{
       	 projectList.add(p.getId().getUserId().toString());
       });
        jo.addProperty("projects", projectList.toString());
        
        ArrayList<String> projectList2 = new ArrayList<String>();
        project.getUserProjects().forEach((s) ->{
        	if (s.getRole().equals(Role.Manager)) {
        		projectList2.add(s.getId().getProjectId().toString());
        	}
       	 
       });
        jo.addProperty("projects_managing", projectList2.toString());*/
        return jo;
    }
    
    
}