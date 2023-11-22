package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="meeting_tab")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Meeting {

	@Id
	@SequenceGenerator(name = "meetingId_gen", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "meetingId_gen")
	@Column(name = "meetingId", nullable = false)
	private Long meetingId;
	
	@Column(name = "eventId", nullable = false)
	private String eventId;

	@Column(name = "user_Id", nullable = false)
	private String emailId;

	@Column(name = "originalStartTimeZone")
	private String originalStartTimeZone;
	
	@Column(name = "originalEndTimeZone")
	private String originalEndTimeZone;
	
	@Column(name = "meetingSubject")
	private String subject;
	
	@Column(name = "meetingType")
	private String type;
	
	@Column(name = "meetingOccurrenceId")
	private String occurrenceId;
	
	@Column(name = "meetingActualStartDateTime")
	private LocalDateTime startDateTime;
	
	@Column(name = "meetingActualEndDateTime")
	private LocalDateTime endDateTime;
	
	@Column(name = "startTimeZone")
	private String startTimeZone;
	
	@Column(name = "endTimeZone")
	private String endTimeZone;
	
	@Column(name = "location")
	private String location;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "meeting_id" ,referencedColumnName = "meetingId", nullable = true)
    private Set<Attendee> attendees;
    
	@Column(name = "organizerEmailId")
    private String organizerEmailId;
    
	@Column(name = "organizerName")
    private String organizerName;
	
	@Column(name = "departmentId")
	private Long departmentId;
    
	@Column(name = "onlineMeetingId")
	private String onlineMeetingId;
    
	@Column(name = "onlineMeetingProvider")
	private String onlineMeetingProvider;
	
	/*
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "recurrence_fk_id", referencedColumnName = "id", unique = true, nullable = true)
	private Recurrence recurrence;
	*/
	@Column(name = "seriesMasterId")
	private String seriesMasterId;
	
	@Column(name = "joinUrl")
	private String joinUrl;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "event_fk_id",nullable = true)
	private List<Transcript> meetingTranscripts;
	                 
	@Column(name = "insertedBy")
	private String insertedBy = "AUTO-BATCH-PROCESS";
    
	@Column(name = "insertedDate")
    private String insertedDate = LocalDateTime.now().toString();
    
    @Column(name = "action_items_generated",nullable = true)
    private boolean isActionItemsGenerated = false;
    
    @Column(name = "isManualMeeting", nullable = true)
    private boolean isManualMeeting = false;

    @Column(name = "batch_id")
    private Integer batchId;
    
	@Column(name = "createdDateTime", nullable = false)
	private String createdDateTime;
	
	@Column(name = "modifiedDateTime")
	private LocalDateTime modifiedDateTime;
	
	@Column(name = "createdBy")
	private String createdBy;
	
	@Column(name = "modifiedBy")
	private String modifiedBy;
	
	@Column(name = "createdByEmailId")
	private String createdByEmailId;
	
	@Column(name = "modifiedByEmailId")
	private String modifiedByEmailId;
	
	//other relations
	//@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	//@JoinColumn(name = "meeting_fk_id", nullable = false)
	//private Set<ActionItem> actionItems;
	

}
