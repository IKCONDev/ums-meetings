package com.ikn.ums.meeting.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusModel {
	Long[] assignedTask;
	List<Long> inprogressTask;
	List<Long> completedTask;
}
