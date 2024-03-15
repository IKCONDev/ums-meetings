package com.ikn.ums.meeting.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingModel {

	private String emailId;
	private String subject;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String location;
	private String[] attendees;
	private String organizerEmailId;
	private String organizerName;
	private Long departmentId;
	private String insertedBy = "MANUAL-ENTRY";
	private String insertedDate = LocalDateTime.now().toString();
	private String originalStartTimeZone;
	private String originalEndTimeZone;

	private String createdDateTime;

	private LocalDateTime modifiedDateTime;

	private String createdBy;

	private String modifiedBy;

	private String createdByEmailId;

	private String modifiedByEmailId;

	// other relations
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval
	// = true)
	// @JoinColumn(name = "meeting_fk_id", nullable = false)
	// private Set<ActionItem> actionItems;

}
