package com.ikn.ums.meeting.VO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptVO {

	private Long id;
	private String transcriptId;
	private String meetingId;
	private String meetingOrganizerId;
	private String transcriptContentUrl;
	private String createdDateTime;
	private String transcriptFilePath;
	private String transcriptContent;
	
}
