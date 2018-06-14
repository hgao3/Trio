package team3.trio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team3.trio.model.Project;
import team3.trio.model.Stage;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

}