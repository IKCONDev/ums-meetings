package com.ikn.ums.meeting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.ikn.ums.meeting.dto.TaskDto;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;

public interface TaskService {

	TaskDto saveTask(TaskDto task);

	TaskDto updateTask(TaskDto task);

	List<Task> getTasks();

	Integer deleteTaskById(Integer taskId);

	boolean deleteAllTasksById(List<Integer> ids);

	Optional<Task> getTaskById(Integer id);

	List<Task> getTasksByUserId(String emailId);

	// List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList, Long
	// meetingId);
	List<Task> getAssignedTaskListOfUser(String emailId);

	void sendMinutesofMeetingEmail(List<String> emailList, List<ActionItem> actionItemList, Long meetingId,
			String discussionPoints, String HoursDiff, String minDiff);

	Long getOrganizedTasksCountOfUser(String emailId);

	Long getUserAssignedTasksCountOfUser(String emailId);

	List<Long> getTaskCountsByDayOfWeek(LocalDateTime startDate, LocalDateTime endDate, String emailId);

	List<Long> getCompletedTaskCountsByDayOfWeek(LocalDateTime startdate, LocalDateTime enddate, String emailId);

	List<Long> findInProgressTaskCountsByDayOfWeek(LocalDateTime startdate, LocalDateTime enddate, String emailId);

	List<Task> getFilteredTasks(String taskTitle, String taskPriority, String taskOwner, String startDate,
			String dueDate, String emailId);

	List<Task> getFilteredAssignedTasks(String taskTitle, String taskPriority, String startDate, String dueDate,
			String emailId);

	List<Long> findTaskCountsByMonth(LocalDateTime startdate, LocalDateTime enddate, String email);

	List<Long> findInprogressTaskCountsByMonth(LocalDateTime startdate, LocalDateTime enddate, String email);

	List<Long> findCompletedTaskCountsByMonth(LocalDateTime startdate, LocalDateTime enddate, String email);

	// reporting methods
	List<Task> getTasksByDepartment(Long departmentId);

	List<Task> getTasksByTaskPriority(String taskPriority);

	List<Task> getTasksByTaskStatus(String taskStatus);

	List<Task> getAgedTasks(LocalDateTime currentDateTime);

	List<Long> getTasksBetweenStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate);

	List<Long> getYetToStartTaskCountsByDayOfWeek(LocalDateTime startdate, LocalDateTime enddate, String emailId);

	List<Long> findYetToStartTaskCountsByMonth(LocalDateTime startdate, LocalDateTime enddate, String emailId);

	List<Object[]> getAllTasksByDepartment();

	List<Task> getTasksByCategoryId(Long taskCategoryId);

	// List<Task> getAllTasks();
	List<Object[]> getAllTaskCategoryByCount();

	List<Task> getAllTasksByOrganizerName(String email);

}
