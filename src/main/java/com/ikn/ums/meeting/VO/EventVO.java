package com.ikn.ums.meeting.VO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class EventVO {

	private Integer meetingId;
	
	private String eventId;
	
	private String createdDateTime;
	
	private String originalStartTimeZone;
	
	private String originalEndTimeZone;
	
	private String subject;
	
	private String type;
	
	private String occurrenceId;
	
	private LocalDateTime startDateTime;
	
	private LocalDateTime endDateTime;
	
	private String startTimeZone;
	
	private String endTimeZone;
		
	private String location;
	
    private Set<AttendeeVO> attendees;
    
    private String organizerEmailId;
    
    private String organizerName;
    
	private String onlineMeetingId;
    
	private String onlineMeetingProvider;
	
	private String seriesMasterId;
	
	private String joinUrl;

	private List<TranscriptVO> meetingTranscripts;
	                 
	private String insertedBy = "IKCON UMS";
    
    private String insertedDate = LocalDateTime.now().toString();
    
    private String emailId;
    
    private boolean isActionItemsGenerated = false;

}
