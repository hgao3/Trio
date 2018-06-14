package team3.trio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import team3.trio.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}