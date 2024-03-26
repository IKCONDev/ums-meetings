package com.ikn.ums.meeting.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeetingDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long meetingId;
	private String eventId;
	private String emailId;
	private String originalStartTimeZone;
	private String originalEndTimeZone;
	private String subject;
	private String type;
	private String bodyPreview;
	private String occurrenceId;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String startTimeZone;
	private String endTimeZone;
	private String location;
	private Set<AttendeeDto> attendees;
	private String organizerEmailId;
	private String organizerName;
	private Long departmentId;
	private String onlineMeetingId;
	private String onlineMeetingProvider;
	private String seriesMasterId;
	private String joinUrl;
	private List<TranscriptDto> meetingTranscripts;
	private String insertedBy = "AUTO-BATCH-PROCESS";
	private String insertedDate = LocalDateTime.now().toString();
	private boolean isActionItemsGenerated = false;
	private boolean isManualMeeting = false;
	private Integer batchId;
	private String createdDateTime;
	private LocalDateTime modifiedDateTime;
	private String createdBy;
	private String modifiedBy;
	private String createdByEmailId;
	private String modifiedByEmailId;
	private Integer momEmailCount;
	private String actualMeetingDuration;

}
