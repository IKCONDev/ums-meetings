package com.ikn.ums.meeting.service;

import java.util.List;
import java.util.Optional;

import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.entity.Task;

public interface TaskService {

	
	Task saveTask(Task task);
	Task updateTask(Task task);
	List<Task> getTasks();
	Integer deleteTaskById(Integer taskId);
	boolean deleteAllTasksById(List<Integer> ids);
	Optional<Task> getTaskById(Integer id);
    List<Task> getTasksByUserId(String emailId);
	List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList, Long meetingId);	
	List<Task> getAssignedTaskListOfUser(String emailId);
}
