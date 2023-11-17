package com.ikn.ums.meeting.VO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionItemVO {

	
    private Integer id;
	
	private Integer eventid;
	
	private String actionTitle;
	
	private String Description;
	
	private String actionOwner;
		
    private String actionPriority;
	
	private String actionStatus;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private String emailId;

	



}
