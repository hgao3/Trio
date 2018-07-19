package team3.trio.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "issues")
public class Issue extends AuditModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 100)
	private String title;

	@NotNull
	@Size(max = 100)
	@Column(name = "priority_level")
	private String priorityLevel;
	
	@NotNull
	@Size(max = 1000)
	private String content;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "closed_at")
	private Date closedat;

    // User entity is not responsible for this relationship 
    //It should look for a field named assigned_user_id in the Task entity to find the configuration for the JoinColumn/ForeignKey column
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    @JsonIgnore
    private User ownerUser;

    @Column(name = "open_status", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean openStatus = true;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
    public Issue() {}
	public Issue(String title, String content, User user, String priorityLevel, Project project) {
		super();
		Date dt = new Date();
		this.setCreatedAt(dt);
		this.setUpdatedAt(dt);
		
		this.setTitle(title);
		this.setContent(content);
		this.setOwnerUser(user);
		this.setPriorityLevel(priorityLevel);
		this.setProject(project);
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getClosedat() {
		return closedat;
	}

	public void setClosedat(Date closedat) {
		this.closedat = closedat;
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(User ownerUser) {
		this.ownerUser = ownerUser;
	}

	public boolean isOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(boolean openStatus) {
		this.openStatus = openStatus;
	}

	public String getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(String priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}	
}
