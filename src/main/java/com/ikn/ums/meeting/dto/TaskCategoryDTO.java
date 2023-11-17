package com.ikn.ums.meeting.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaskCategoryDTO implements Serializable{

	private Long taskCategoryId;
	private String taskCategoryTitle;
	private String taskCategoryDescription;
	private LocalDateTime createdDateTime;
	private LocalDateTime modifiedDateTime;
	private String createdBy;
	private String modifiedBy;
	private String createdByEmailId;
	private String modifiedByEmailId;
	private String taskCategoryStatus;
	
}
