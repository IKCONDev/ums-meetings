package com.ikn.ums.meeting.VO;

import java.util.List;

import com.ikn.ums.meeting.entity.Task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListVO {
	private List<Task> task;

}
