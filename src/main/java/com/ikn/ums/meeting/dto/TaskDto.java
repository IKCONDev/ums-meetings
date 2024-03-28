package com.ikn.ums.meeting.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

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
	
	private String taskReviewer;

	private String status;

	private Integer actionItemId;
	
	private Long meetingId;

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
	
	private String taskUpdatedFrom;
}
