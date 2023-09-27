package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="actionitems_tab")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActionItem {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id")
	private Integer actionItemId;
	
	@Column(name="meetingId")
	private Integer meetingId;

	@Column(name="user_id")
	private String emailId;
	
	@Column(name="actionItemOwner")
	private String actionItemOwner;
	
	@Column(name="actionItemTitle")
	private String actionItemTitle;
	
	@Column(name="actionItemDescription")
	private String actionItemDescription;
		
	@Column(name="actionPriority")
	private String actionPriority;
	
	@Column(name="actionStatus")
	private String actionStatus;
	
	@Column(name="startDate")
	private LocalDateTime startDate;
	
	@Column(name="endDate")
	private LocalDateTime endDate;
	
	//relation
	//@OneToOne(fetch = FetchType.LAZY)
	//private Meeting meeting;
	
}
