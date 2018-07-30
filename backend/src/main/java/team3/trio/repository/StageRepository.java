package team3.trio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team3.trio.model.Stage;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
	List<Stage> findByProjectId(@Param("project_id") Long projectId);
}