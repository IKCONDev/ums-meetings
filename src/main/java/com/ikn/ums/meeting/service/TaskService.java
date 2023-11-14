package com.ikn.ums.meeting.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;

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
	//List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList, Long meetingId);	
	List<Task> getAssignedTaskListOfUser(String emailId);
	void sendMinutesofMeetingEmail(List<String> emailList,List<ActionItem> actionItemList, Long meetingId, String discussionPoints);
	Long getOrganizedTasksCountOfUser(String emailId);
	Long getUserAssignedTasksCountOfUser(String emailId);
	Long[] getTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime, String emailId);
	List<Long> getCompletedTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime, String emailId);
	List<Long> findInProgressTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime, String emailId);
	List<Task> getFilteredTasks(String taskTitle, String taskPriority, String taskOwner, 
			String startDate, String dueDate, String emailId );
	List<Task> getFilteredAssignedTasks(String taskTitle, String taskPriority, 
			String startDate, String dueDate, String emailId);
	 List<Long> findTaskCountsByMonth(LocalDateTime startTime, LocalDateTime endTime, String email);
	 List<Long> findInprogressTaskCountsByMonth(LocalDateTime startTime, LocalDateTime endTime, String email);
	 List<Long> findCompletedTaskCountsByMonth(LocalDateTime startTime, LocalDateTime endTime, String email);
	 
	 //reporting methods
	 List<Task> getTasksByDepartment(Long departmentId);
	 List<Task> getTasksByTaskPriority(String taskPriority);
	 List<Task> getTasksByTaskStatus(String taskStatus);
	 List<Task> getAgedTasks(LocalDateTime dateTime);

}
