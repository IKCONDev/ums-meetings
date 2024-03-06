package com.ikn.ums.meeting.VO;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DesignationVO {

	private Long id;

	private String designationName;

	private LocalDateTime createdDateTime;

	private LocalDateTime modifiedDateTime;

	private String createdBy;

	private String modifiedBy;

	private String createdByEmailId;

	private String modifiedByEmailId;

}
