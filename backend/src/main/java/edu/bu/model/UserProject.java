package edu.bu.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users_projects")
public class UserProject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")  
	private User user;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")  
	private Project project;
	
	@Enumerated(EnumType.STRING)
    @Column(length = 10)
	private Role role;
	
	public UserProject() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
	
	
}
