package com.ikn.ums.meeting.dto;

import java.io.Serializable;
import java.time.LocalDate;
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

	private LocalDate startDate;

	private LocalDate dueDate;

	private LocalDate plannedStartDate;

	private LocalDate plannedEndDate;

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
}
