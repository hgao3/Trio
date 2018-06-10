package team3.trio.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "messages")
public class Message extends AuditModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 1000)
	private String content;

	// User entity is not responsible for this relationship
	// It should look for a field named assigned_user_id in the Task entity to find
	// the configuration for the JoinColumn/ForeignKey column
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
	private User ownerUser;
	
    @OneToOne
    @JoinColumn(name = "previous_message_id")
	@JsonIgnore
	private Message previousMessage;
	
    @OneToOne
    @JoinColumn(name = "channel_id")
	private Channel channel;
	
    @OneToOne
    @JoinColumn(name = "issue_id")
	@JsonIgnore
	private Issue issue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(User ownerUser) {
		this.ownerUser = ownerUser;
	}

	public Message getPreviousMessage() {
		return previousMessage;
	}

	public void setPreviousMessage(Message previousMessage) {
		this.previousMessage = previousMessage;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}
	
	
	
}
