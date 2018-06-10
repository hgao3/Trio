package team3.trio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
}
