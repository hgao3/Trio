package team3.trio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "channels")
public class Channel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;
    
    // User entity is not responsible for this relationship 
    //It should look for a field named assigned_user_id in the Task entity to find the configuration for the JoinColumn/ForeignKey column
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @OneToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;
    
    @OneToMany(cascade = CascadeType.ALL, 
            orphanRemoval = true)
    private Set<User> usersInChannel = new HashSet<User>();
    
    // Hibernate requires a no-arg constructor
    public Channel() {}
    
	public Channel(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(User ownerUser) {
		this.ownerUser = ownerUser;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	public Set<User> getUsersInChannel() {
		return usersInChannel;
	}

	public void setUsersInChannel(Set<User> usersInChannel) {
		this.usersInChannel = usersInChannel;
	}

	public void addUsersInChannel(User user) {
		this.usersInChannel.add(user);
	}

}