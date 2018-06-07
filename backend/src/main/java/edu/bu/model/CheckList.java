package edu.bu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "check_list")
public class CheckList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
	private Task task;
	
	private String item;
	
    @Column(name = "check_status", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean checkStatus = true;

    public CheckList() {}
    
	public CheckList(String item, boolean checkStatus) {
		this.item = item;
		this.checkStatus = checkStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public boolean isCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

}
