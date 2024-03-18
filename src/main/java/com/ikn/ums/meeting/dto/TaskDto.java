package com.ikn.ums.meeting.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TaskDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer taskId;

	private String taskTitle;

	private String taskDescription;

	private String taskPriority;
	
	private LocalDateTime startDate;
	
	private LocalDateTime dueDate;

	private LocalDateTime plannedStartDate;

	private LocalDateTime plannedEndDate;

	private String taskOwner;

	private String status;

	private Integer actionItemId;

	private String emailId;

	private Long departmentId;

	private TaskCategoryDTO taskCategory;

	private LocalDateTime createdDateTime;

	private LocalDateTime modifiedDateTime;

	private String createdBy;

	private String modifiedBy;

	private String createdByEmailId;

	private String modifiedByEmailId;
	
	private String plannedDuration;
	
	private String actualDuration;
}
