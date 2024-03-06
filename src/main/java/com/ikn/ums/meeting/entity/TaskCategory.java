package com.ikn.ums.meeting.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "taskcategory_tab")
public class TaskCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "taskCategoryId")
	private Long taskCategoryId;

	@Column(name = "taskCategoryTitle", nullable = false)
	private String taskCategoryTitle;

	@Column(name = "taskCategoryDescription", nullable = false)
	private String taskCategoryDescription;

	@Column(name = "taskCategoryStatus", nullable = true)
	private String taskCategoryStatus;

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

}
