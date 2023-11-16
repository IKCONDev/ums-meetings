package com.ikn.ums.meeting.entity;

import java.time.LocalDate;
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
	
	@Column(name="startDate", nullable = true)
	private LocalDate startDate;
	
	@Column(name="dueDate", nullable = true)
	private LocalDate dueDate;
	
	@Column(name = "plannedStartDate")
	private LocalDate plannedStartDate;
	
	@Column(name = "plannedEndDate")
	private LocalDate plannedEndDate;
	
	@Column(name="taskOwner", nullable = false)
	private String taskOwner;
	
	@Column(name="status", nullable = true)
	private String status;	
	
	@Column(name="actionItemId", nullable = false)
	private Integer actionItemId;
	
	@Column(name="user_id", nullable = false)
	private String emailId;
	
	@Column(name = "departmentId", nullable = false)
	private Long departmentId;
	
	@Column(name = "createdDateTime")
	private LocalDateTime createdDateTime;
	
	@Column(name = "modifiedDateTime")
	private LocalDateTime modifiedDateTime;
	
	@Column(name = "createdBy")
	private String createdBy;
	
	@Column(name = "modifiedBy")
	private String modifiedBy;
	
	@Column(name = "createdByEmailId")
	private String createdByEmailId;
	
	@Column(name = "modifiedByEmailId")
	private String modifiedByEmailId;
}
