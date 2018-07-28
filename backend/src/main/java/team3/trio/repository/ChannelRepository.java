package team3.trio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team3.trio.model.Channel;
import team3.trio.model.Issue;
import team3.trio.model.Task;
import team3.trio.model.User;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
	
	List<Channel> findByProjectId(@Param("project_id") Long projectId);
	
	List<Channel> findByTaskId(@Param("task_id") Long taskId);
	
	List<Channel> findByIssueId(@Param("issue_id") Long issueId);
	
	List<Channel> findByChatId(@Param("chat_id") Long chatId);
	
}