package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="actionitem_tab")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActionItem {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id")
	private Integer actionItemId;
	
	@Column(name="meetingId",nullable = false)
	private Long meetingId;

	@Column(name="user_id", nullable = false)
	private String emailId;
	
	@Column(name="actionItemOwner",nullable = false)
	private String actionItemOwner;
	
	@Column(name="actionItemTitle",nullable = false)
	private String actionItemTitle;
	
	@Column(name="actionItemDescription",nullable = false)
	private String actionItemDescription;
		
	@Column(name="actionPriority",nullable = true)
	private String actionPriority;
	
	@Column(name="actionStatus",nullable = false)
	private String actionStatus = "Not Converted";
	
	@Column(name="startDate",nullable = true)
	private LocalDateTime startDate;
	
	@Column(name="endDate",nullable = true)
	private LocalDateTime endDate;
	
	@Column(name = "createdDateTime")
	private LocalDateTime createdDateTime;
	
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
	
	//relation
	//@OneToOne(fetch = FetchType.LAZY)
	//private Meeting meeting;
	
}
