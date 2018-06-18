package team3.trio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team3.trio.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}