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
@Table(name="actionItems_tab")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActionItem {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id")
	private Integer id;
	
	@Column(name="event_id")
	private Integer eventid;
	
	@Column(name="act_title")
	private String actionTitle;
	
	@Column(name="act_desc")
	private String description;
		
	@Column(name="act_prty")
	private String actionPriority;
	
	@Column(name="act_status")
	private String actionStatus;
	
	@Column(name="strt_date")
	private LocalDateTime startDate;
	
	@Column(name="end_date")
	private LocalDateTime endDate;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "act_owner")
	private String actionOwner;
}
