package com.ikn.ums.meeting.VO;

import java.time.LocalDate;
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
	
	private String assignee;
	
	private LocalDate startDate;
	
	private LocalDate dueDate;
	
	private LocalDate plannedStartDate;
	
	private LocalDate plannedEndDate;
	
	private String actionTitle;
	
	private Integer taskCategory;
	
	private Long departmentId;


}
