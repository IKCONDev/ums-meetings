package com.ikn.ums.meeting.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "attendee_tab")
@AllArgsConstructor
@NoArgsConstructor
public class Attendee {

	@Id
	@SequenceGenerator(name = "attendeeId_gen", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "attendeeId_gen")
	private Integer id;
	private String type;
	private String status;
	private String email;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "meet_Id", referencedColumnName = "meetingId", nullable = true)
	private Meeting meeting;
	/*
	 * @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	 * 
	 * @JoinColumn(name = "user_fk_id",nullable = true)
	 */
	private String emailId; // userId

}
