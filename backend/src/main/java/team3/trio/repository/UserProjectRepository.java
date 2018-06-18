package team3.trio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import team3.trio.model.UserProject;
import team3.trio.model.UserProjectId;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {


}
