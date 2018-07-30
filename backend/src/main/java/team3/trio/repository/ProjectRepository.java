package team3.trio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team3.trio.model.Channel;
import team3.trio.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}