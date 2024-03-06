package com.ikn.ums.meeting.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ActionItemDto {

	private Integer actionItemId;

	private Long meetingId;

	private String emailId;

	private List<String> actionItemOwner;

	private String actionItemTitle;

	private String actionItemDescription;

	private String actionPriority;

	private String actionStatus = "Not Submitted";

	private LocalDate startDate;

	private LocalDate endDate;

	private Long departmentId;

	private LocalDateTime createdDateTime;

	private LocalDateTime modifiedDateTime;

	private String createdBy;

	private String modifiedBy;

	private String createdByEmailId;

	private String modifiedByEmailId;

}
