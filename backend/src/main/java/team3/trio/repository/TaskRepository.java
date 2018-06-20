package team3.trio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team3.trio.model.Task;
import team3.trio.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	
	List<Task> findByStageId(@Param("stage_id") Long stageId);
}