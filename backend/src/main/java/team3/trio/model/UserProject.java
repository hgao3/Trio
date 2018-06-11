package team3.trio.model;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "users_projects")
public class UserProject {
    @EmbeddedId
    private UserProjectId id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
	private User user;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
	private Project project;
	
	@Enumerated(EnumType.STRING)
    @Column(length = 10)
	private Role role;
	
	public UserProject() {}

	public UserProject(User user, Project project) {
		this.user = user;
		this.project = project;
		this.id = new UserProjectId(user.getId(), project.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        UserProject that = (UserProject) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(project, that.project);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(user, project);
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

	public UserProjectId getId() {
		return id;
	}

	public void setId(UserProjectId id) {
		this.id = id;
	}
	
	
	
	
}
