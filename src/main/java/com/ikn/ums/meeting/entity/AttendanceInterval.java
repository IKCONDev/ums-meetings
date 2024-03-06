package com.ikn.ums.meeting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attendee_interval_tab")
public class AttendanceInterval {
	
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long Id;
	@Column(name = "joinDateTime")
	private String joinDateTime;
	@Column(name = "leaveDateTime")
	private String leaveDateTime;
	@Column(name = "attendeeDurationInSeconds")
	private int attendeeDurationInSeconds;

}
