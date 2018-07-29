package team3.trio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "projects")
/*@NaturalIdCache
@Cache(
    usage = CacheConcurrencyStrategy.READ_WRITE
)*/
public class Project implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 100)
	private String title;

	@OneToMany(mappedBy = "project",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<UserProject> userProjects = new HashSet<UserProject>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Project p = (Project) o;
		return Objects.equals(title, p.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title);
	}

	//Getters and setters omitted for brevity
    
/*    public void addUser(User user) {
    	UserProject userProject = new UserProject(user, this, );
    	userProjects.add(userProject);
    	user.getUserProjects().add(userProject);
    }

    public void removeUser(User user) {
        for (Iterator<UserProject> iterator = userProjects.iterator();
             iterator.hasNext(); ) {
        	UserProject userProject = iterator.next();

            if (userProject.getProject().equals(this) &&
            		userProject.getUser().equals(user)) {
                iterator.remove();
                userProject.getUser().getUserProjects().remove(userProject);
                userProject.setProject(null);
                userProject.setUser(null);
            }
        }
    }*/

	// Hibernate requires a no-arg constructor
	public Project() {}

	public Project(String title) {
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

	public Set<UserProject> getUserProjects() {
		return userProjects;
	}

	public void setUserProjects(Set<UserProject> userProjects) {
		this.userProjects = userProjects;
	}

	public void addUserProjects(UserProject userProject) {
		this.userProjects.add(userProject);
	}
	
	public void removeUserProjects(UserProject userProject) {
		this.userProjects.remove(userProject);
	}
}