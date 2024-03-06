package com.ikn.ums.meeting.VO;

import lombok.Data;

@Data
public class AttendeeVO {

	private Integer id;
	private String type;
	private String status;
	private String email;
	private EventVO event;
	private String emailId;

}
