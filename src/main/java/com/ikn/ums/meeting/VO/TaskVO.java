package com.ikn.ums.meeting.VO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskVO {

	private Integer id;

	private String taskTitle;

	private String taskDescription;

	private String taskPriority;

	private String status;

	private Integer actionItemId;
	
	private Long meetingId;

	private String assignee;

	private LocalDateTime startDate;

	private LocalDateTime dueDate;

	private LocalDateTime plannedStartDate;

	private LocalDateTime plannedEndDate;

	private String actionTitle;

	private Integer taskCategory;

	private Long departmentId;

}
