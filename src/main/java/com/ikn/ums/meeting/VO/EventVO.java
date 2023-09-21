package com.ikn.ums.meeting.VO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class EventVO {

	private Integer id;
	
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
    
    private Integer userId;
    
    private boolean isActionItemsGenerated = false;

}
