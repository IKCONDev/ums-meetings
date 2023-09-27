package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="tasks_tab")
public class Task {
	
	@Id
	@SequenceGenerator(name = "taskId_gen", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "taskId_gen")
	@Column(name="taskId")
	private Integer taskId;
	
	@Column(name="taskTitle")
	private String taskTitle;
	
	@Column(name="taskDescription")
	private String taskDescription;
	
	@Column(name="taskPriority")
	private String taskPriority;
	
	@Column(name="startDate")
	private LocalDateTime startDate;
	
	@Column(name="dueDate")
	private LocalDateTime dueDate;
	
	@Column(name="taskOwner")
	private String taskOwner;
	
	@Column(name="status")
	private String status;	
	
	@Column(name="actionItemId")
	private Integer actionItemId;
	
	@Column(name="user_id")
	private String emailId;
	



}
