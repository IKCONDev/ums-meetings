package com.ikn.ums.meeting.VO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmployeeVO {

	private Integer id;

	private String employeeOrgId;

	private String teamsUserId;

	private String firstName;

	private String lastName;

	private String email;

	private String reportingManager;

	private String designation;

	private DesignationVO empDesignation;

	private Long departmentId;

	private String gender;

	private LocalDateTime createdDateTime;

	private LocalDateTime modifiedDateTime;

	private String createdBy;

	private String modifiedBy;

	private String createdByEmailId;

	private String modifiedByEmailId;

	private boolean isUser;

	private String dateOfJoining;

}
