package com.ikn.ums.meeting.service;

import java.util.List;
import java.util.Optional;

import com.ikn.ums.meeting.VO.ActionItemVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;

public interface TaskService {

	
	Task SaveTasks(Task task);
    
	List<Task> fetchTaskDetails();
	
	Task updateTask(Task task);
	
	Optional<Task> singleTaskDetails(Integer id);
	
	Integer deleteTaskDetails(Integer taskId);
	
	List<Task> convertToTask(List<ActionItem> actionItemList);
	
	//List<TaskListVO> converTOTask(List<ActionItemVO> actionItemList);
	
	List<Task> fetchUserTasks(String email);
	
	boolean deleteAllTasksById(List<Integer> ids);

}
