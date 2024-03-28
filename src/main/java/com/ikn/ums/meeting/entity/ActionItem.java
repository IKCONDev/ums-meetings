package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "actionitem_tab")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActionItem {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer actionItemId;

	@Column(name = "meetingId", nullable = false)
	private Long meetingId;

	@Column(name = "user_id", nullable = false)
	private String emailId;

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "actionItemOwners", joinColumns = @JoinColumn(name = "actionItemId"))
	@Column(name = "actionItemOwner", nullable = false)
	private List<String> actionItemOwner;

	@Column(name = "actionItemTitle", nullable = false)
	private String actionItemTitle;

	@Column(name = "actionItemDescription", nullable = false)
	private String actionItemDescription;

	@Column(name = "actionPriority", nullable = true)
	private String actionPriority;

	@Column(name = "actionStatus", nullable = false)
	private String actionStatus = "Not Submitted";

	@Column(name = "startDate", nullable = true)
	private LocalDateTime startDate;

	@Column(name = "endDate", nullable = true)
	private LocalDateTime endDate;

	@Column(name = "departmentId")
	private Long departmentId;

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

	// relation
	// @OneToOne(fetch = FetchType.LAZY)
	// private Meeting meeting;

}
