package team3.trio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team3.trio.model.Issue;
import team3.trio.model.Task;
import team3.trio.model.User;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
	
	List<Issue> findByProjectId(@Param("project_id") Long projectId);
	
	List<Issue> findByOpenStatus(@Param("open_status") boolean status);
	
	List<Issue> findByProjectIdAndOpenStatus(@Param("project_id") Long projectId, @Param("open_status") boolean status);
	
	List<Issue> findByTaskId(@Param("task_id") Long taskId);
}