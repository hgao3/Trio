package edu.bu.repository;

import edu.bu.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}