package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="tasks_tab")
public class Task {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id")
	private Integer id;
	
	@Column(name="tsk_title")
	private String taskTitle;
	
	@Column(name="tsk_desc")
	private String taskDescription;
	
	@Column(name="tsk_priority")
	private String taskPriority;
	
	@Column(name="tsk_startdt")
	private LocalDateTime startDate;
	
	@Column(name="tsk_duedt")
	private LocalDateTime dueDate;
	
	@Column(name="tsk_assign")
	private String assignee;
	
	@Column(name="organizer")
	private String organizer;
	
	@Column(name="status")
	private String status;	
	
	@Column(name="act_id")
	private Integer actionItemId;
	
	@Column(name="actn_title")
	private String actionTitle;
	
	@Column(name="user_email")
	private String userId;
	



}
