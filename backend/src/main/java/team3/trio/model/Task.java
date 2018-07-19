package team3.trio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gson.JsonObject;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import team3.trio.utils.JsonUtils;

@Entity
@Table(name = "tasks")
public class Task extends AuditModel {    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;
    
    @NotNull
    @Size(max = 1000)
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "due_at")
    private Date dueAt;

    @Column(name = "ready_for_review")
	private Boolean readyForReview = false;

    private Boolean completed = false;

    
    // User entity is not responsible for this relationship 
    //It should look for a field named assigned_user_id in the Task entity to find the configuration for the JoinColumn/ForeignKey column
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;
    
    // declare that it has a many-to-one relationship
    // declare the foreign key column.
    // if project get delete, all stage that have this project_id should also delete
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Stage stage;
    
	public Task() {		
		super();
	}
	
	public Task(String title, String content, Date dueAt,
			User assignedUser, Stage stage) {		
		super();
		Date dt = new Date();
		this.setCreatedAt(dt);
		this.setUpdatedAt(dt);
		
		this.title = title;
		this.content = content;
		this.dueAt = dueAt;
		this.assignedUser = assignedUser;
		this.stage = stage;
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

	public Date getDueAt() {
		return dueAt;
	}

	public void setDueAt(Date dueAt) {
		this.dueAt = dueAt;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}

	public Boolean isReadyForReview() {
		return readyForReview;
	}

	public void markReadyForReview() {
		this.readyForReview = true;
	}

	public void markNotReadyForReview() {
		this.readyForReview = false;
	}

	public Boolean isCompleted() {
		return this.completed;
	}

	public void markCompleted() {
		this.completed = true;
	}

	public void markIncomplete() {
		this.completed = false;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Boolean checkManagerByEmail(String email) {
		Stage stage = this.getStage();
		Project project = stage.getProject();
		User manager = null;
		for (UserProject up : project.getUserProjects()) {
			if (up.getRole().equals(Role.Manager)) {
				manager = up.getUser();
				break;
			}
		}
		if (manager == null) { return false; }
		return manager.getEmail().equals(email);
	}
	
	
}
