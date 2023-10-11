package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="task_tab")
public class Task {
	
	@Id
	@SequenceGenerator(name = "taskId_gen", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "taskId_gen")
	@Column(name="taskId")
	private Integer taskId;
	
	@Column(name="taskTitle", nullable = false)
	private String taskTitle;
	
	@Column(name="taskDescription", nullable = false)
	private String taskDescription;
	
	@Column(name="taskPriority", nullable = false)
	private String taskPriority;
	
	@Column(name="startDate", nullable = false)
	private LocalDateTime startDate;
	
	@Column(name="dueDate", nullable = true)
	private LocalDateTime dueDate;
	
	@Column(name="taskOwner", nullable = false)
	private String taskOwner;
	
	@Column(name="status", nullable = true)
	private String status;	
	
	@Column(name="actionItemId", nullable = false)
	private Integer actionItemId;
	
	@Column(name="user_id", nullable = false)
	private String emailId;
	
}
