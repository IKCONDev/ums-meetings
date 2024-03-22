package com.ikn.ums.meeting.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ikn.ums.meeting.VO.EmployeeVO;
import com.ikn.ums.meeting.VO.Notification;
import com.ikn.ums.meeting.dto.MeetingDto;
import com.ikn.ums.meeting.dto.TaskDto;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.ActionItemModel;
import com.ikn.ums.meeting.repository.TaskRepository;
import com.ikn.ums.meeting.service.ActionItemService;
import com.ikn.ums.meeting.service.MeetingService;
import com.ikn.ums.meeting.service.TaskService;
import com.ikn.ums.meeting.utils.EmailService;
import com.ikn.ums.meeting.utils.MeetingConstants;
import com.ikn.ums.meeting.utils.NotificationService;
import com.netflix.servo.util.Strings;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private MeetingService meetingService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ActionItemService actionItemService;

	@Autowired
	private ModelMapper mapper;

	// @Autowired
	// private DiscoveryClient discoveryClient;

	@Override
	@Transactional
	public TaskDto saveTask(TaskDto task) {
		log.info("saveTask() entered with args - task");
		if (task == null) {
			log.info("saveTask() Empty List Exception : Exception occured while saving the task");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("TaskServiceImpl.saveTask() is under execution...");
		Task saveTask = new Task();
		TaskDto taskDto = new TaskDto();
		mapper.map(task, saveTask);
		String result = getDuartionOfTask(saveTask.getPlannedStartDate(), saveTask.getPlannedEndDate());
		saveTask.setPlannedDuration(result);
		Task createdTask = taskRepository.save(saveTask);
		
		//send email to task owner that a task has been created on behalf of them
				if(createdTask != null) {
					if(!createdTask.getCreatedByEmailId().equalsIgnoreCase(createdTask.getEmailId())) {
						String subject = "Task "+createdTask.getTaskId()+" created by "+createdTask.getCreatedBy()+" on behalf of you";
						String emailBody = 
						"Action Item ID - "+createdTask.getActionItemId()+"\r\n"+
						"Task ID - "+createdTask.getTaskId()+" \r\n"+
						"Task Title - "+createdTask.getTaskTitle()+". \r\n \r\n"+
						"Please be informed that a task has been created on your behalf by "+createdTask.getCreatedBy()+" ("+createdTask.getCreatedByEmailId()+"). \r\n \r\n"+
						"Please click the below link for further details. \r\n"+
						"http://132.145.186.188:4200/#/task"+" \r\n \r\n";
						emailService.sendMail(createdTask.getEmailId(), subject, emailBody, false);
					}
				}
		
		// send email to task Owner
		sendEmailToTaskOwner(createdTask, true);
		mapper.map(createdTask, taskDto);
		// send notification to task owner
		/*
		 * if((discoveryClient.getInstancesById("UMS-NOTIFICATION_SERVICE").size() < 1))
		 * { throw new NotificationServiceUnavailableException(ErrorCodeMessages.
		 * ERR_MEETINGS_NOTIFICATION_SERVICE_NOTFOUND_CODE,
		 * ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG); }
		 */
		EmployeeVO  employee = restTemplate.getForObject("http://UMS-EMPLOYEE-SERVICE/employees/"+saveTask.getEmailId(), EmployeeVO.class);
		Notification notification = new Notification();
		notification.setMessage("The task " + createdTask.getTaskId() + " has been assigned to you by "+employee.getFirstName()+" "+employee.getLastName()+".");
		notification.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
		notification.setNotificationTo(createdTask.getTaskOwner());
		notification.setEmailId(createdTask.getEmailId());
		notificationService.createNotification(notification);
		log.info("saveTask() is executed Successfully");
		return taskDto;
	}

	@Override
	public List<Task> getTasks() {
		log.info("getTasks() entered");
		log.info("getTasks() is under execution...");
		List<Task> taskList = taskRepository.findAll();
		log.info("getTasks() is executed successfully");
		return taskList;
	}

	@Override
	public TaskDto updateTask(TaskDto entity) {
		log.info("updateTask() entered with args - task ");
		if (entity == null) {
			log.info("TaskServiceImpl.updateTask() Empty List Exception : Exception occured while updating the task");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("updateTask() is under execution...");
		var taskUpdatedFrom = entity.getTaskUpdatedFrom();
		Task task = new Task();
		TaskDto resultDto = new TaskDto();
		mapper.map(entity, task);
		Task updatetask = taskRepository.findById(task.getTaskId()).get();
		var dbTaskOwner = updatetask.getTaskOwner();
		updatetask.setTaskTitle(task.getTaskTitle());
		updatetask.setTaskDescription(task.getTaskDescription());
		updatetask.setStartDate(task.getStartDate());
		updatetask.setDueDate(task.getDueDate());
		updatetask.setTaskPriority(task.getTaskPriority());
		updatetask.setActionItemId(task.getActionItemId());
		updatetask.setTaskOwner(task.getTaskOwner());
		updatetask.setStatus(task.getStatus());
		updatetask.setPlannedStartDate(task.getPlannedStartDate());
		updatetask.setPlannedEndDate(task.getPlannedEndDate());
		updatetask.setTaskCategory(task.getTaskCategory());
		updatetask.setModifiedBy(task.getModifiedBy());
		updatetask.setModifiedByEmailId(task.getModifiedByEmailId());
		updatetask.setModifiedDateTime(LocalDateTime.now());
		if(task.getStatus().equals("Completed")) {
			String duration = getDuartionOfTask(task.getStartDate(), task.getDueDate());
			updatetask.setActualDuration(duration);
		}
		if(task.getStatus().equals("Yet to start")) {
			updatetask.setActualDuration(null);
			updatetask.setStartDate(null);
			updatetask.setDueDate(null);
		}
		Task modifiedtask = taskRepository.save(updatetask);
		//send email to task creator that a task has been modified on behalf of them
		if(modifiedtask != null) {
			if(taskUpdatedFrom.equalsIgnoreCase("AssignedTask")) {
				if(!modifiedtask.getModifiedByEmailId().equalsIgnoreCase(modifiedtask.getTaskOwner())) {
					//if create person of the meeting and organizer is not same send email to organizer of meeting that
					//some other person has created a meeting in their account on behalf
					String subject = "Task "+modifiedtask.getTaskId()+" updated by "+modifiedtask.getModifiedBy()+" on behalf of you";
					String emailBody = 
					"Action Item ID - "+modifiedtask.getActionItemId()+"\r\n"+
					"Task ID - "+modifiedtask.getTaskId()+" \r\n"+
					"Task Title - "+modifiedtask.getTaskTitle()+". \r\n \r\n"+
					"Please be informed that a task has been updated on your behalf by "+modifiedtask.getModifiedBy()+" ("+modifiedtask.getModifiedByEmailId()+"). \r\n \r\n"+
					"Please click the below link for further details. \r\n"+
					"http://132.145.186.188:4200/#/task"+" \r\n \r\n";
					emailService.sendMail(new String[] {modifiedtask.getTaskOwner()}, subject, emailBody, false);
				}
			}else {
				//if create person of the meeting and organizer is not same send email to organizer of meeting that
				//some other person has created a meeting in their account on behalf
				String subject = "Task "+modifiedtask.getTaskId()+" updated by "+modifiedtask.getModifiedBy()+" on behalf of you";
				String emailBody = 
				"Action Item ID - "+modifiedtask.getActionItemId()+"\r\n"+
				"Task ID - "+modifiedtask.getTaskId()+" \r\n"+
				"Task Title - "+modifiedtask.getTaskTitle()+". \r\n \r\n"+
				"Please be informed that a task has been updated on your behalf by "+modifiedtask.getModifiedBy()+" ("+modifiedtask.getModifiedByEmailId()+"). \r\n \r\n"+
				"Please click the below link for further details. \r\n"+
				"http://132.145.186.188:4200/#/task"+" \r\n \r\n";
				emailService.sendMail(new String[] {modifiedtask.getEmailId()}, subject, emailBody, false);
			}
		}
		// send notification to task owner
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				//noti to actual owner or assignee of task
				if(modifiedtask.getModifiedByEmailId().equalsIgnoreCase(modifiedtask.getEmailId())) {
					//noti to task organizer
					EmployeeVO  employee = restTemplate.getForObject("http://UMS-EMPLOYEE-SERVICE/employees/"+modifiedtask.getEmailId(), EmployeeVO.class);
					Notification notification1 = new Notification();
					notification1.setMessage("Task " + modifiedtask.getTaskId() + " has been updated by "+employee.getFirstName()+" "+employee.getLastName()+".");
					notification1.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
					notification1.setNotificationTo(modifiedtask.getEmailId());
					notification1.setEmailId(modifiedtask.getEmailId());
					notificationService.createNotification(notification1);
					
					Notification notification = new Notification();
					notification.setMessage("Task " + modifiedtask.getTaskId() + " has been updated by "+employee.getFirstName()+" "+employee.getLastName()+".");
					notification.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
					notification.setNotificationTo(modifiedtask.getTaskOwner());
					notification.setEmailId(modifiedtask.getEmailId());
					notificationService.createNotification(notification);
				}else {
					EmployeeVO  employee = restTemplate.getForObject("http://UMS-EMPLOYEE-SERVICE/employees/"+modifiedtask.getTaskOwner(), EmployeeVO.class);
					//noti to task organizer
					Notification notification1 = new Notification();
					notification1.setMessage("Task " + modifiedtask.getTaskId() + " has been updated by "+employee.getFirstName()+" "+employee.getLastName()+".");
					notification1.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
					notification1.setNotificationTo(modifiedtask.getEmailId());
					notification1.setEmailId(modifiedtask.getEmailId());
					notificationService.createNotification(notification1);
					
					Notification notification = new Notification();
					notification.setMessage("Task " + modifiedtask.getTaskId() + " has been updated by "+employee.getFirstName()+" "+employee.getLastName()+".");
					notification.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
					notification.setNotificationTo(modifiedtask.getTaskOwner());
					notification.setEmailId(modifiedtask.getEmailId());
					notificationService.createNotification(notification);
				}
			}
		}).start();
		
		// send email to task owner
		sendEmailToTaskOwner(modifiedtask, false);
		mapper.map(modifiedtask, resultDto);
		log.info("updateTask() is executed Successfully");
		return resultDto;
	}

	@Override
	public Optional<Task> getTaskById(Integer taskId) {
		log.info("getTaskById() entred with args : " + taskId);
		if (taskId < 1 || taskId == null) {
			log.info("getTaskById() : Empty Input Exception - taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		log.info("getTaskById() is under execution");
		Optional<Task> task = taskRepository.findById(taskId);
		log.info("getTaskById() is executed successfully");
		return task;
	}

	@Override
	@Transactional
	public Integer deleteTaskById(Integer taskId) {
		log.info("deleteTaskById() entered with args : " + taskId);
		if (taskId < 1 || taskId == null) {
			log.info("deleteTaskById() Empty Input Exception : Exception occured while deleting the task");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		Optional<Task> optTask = getTaskById(taskId);
		Task taskToBeDeleted = null;
		if (optTask.isPresent()) {
			taskToBeDeleted = optTask.get();
			taskRepository.delete(taskToBeDeleted);
		}
		log.info("deleteTaskById() is under execution");

		Task deletedTask = taskToBeDeleted;
		// send notification to task owner
		new Thread(new Runnable() {
			@Override
			public void run() {
				// send noti
				Notification notification = new Notification();
				notification.setMessage(
						"Task " + deletedTask.getTaskId() + " has been deleted and it is no longer available.");
				notification.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
				notification.setNotificationTo(deletedTask.getTaskOwner());
				notification.setEmailId(deletedTask.getEmailId());
				notificationService.createNotification(notification);
			}
		}).start();
		log.info("deleteTaskById() is executed successfully");
		return 1;
	}

	/*
	 * @Override
	 * 
	 * @Transactional public List<Task> convertActionItemsToTasks(List<ActionItem>
	 * actionItemList, Long meetingId) { log.
	 * info("TaskServiceImpl.convertActionItemsToTasks() entered with args - actionItemList"
	 * ); System.out.println(actionItemList); if(actionItemList == null ||
	 * actionItemList.size() < 1) { log.
	 * info("TaskServiceImpl.convertActionItemsToTasks() Empty List Exception : Exception occured while converting "
	 * + "Action Item to task"); throw new
	 * EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
	 * ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE); }
	 * log.info("TaskServiceImpl.convertActionItemsToTasks() is under execution..."
	 * ); List<Task> taskList = new ArrayList<>(); actionItemList.forEach(actionitem
	 * ->{ actionitem.getActionItemOwner().forEach(owner -> { Task task = new
	 * Task(); //task.setId(actionitem.getId());
	 * task.setTaskTitle(actionitem.getActionItemTitle());
	 * task.setActionItemId(actionitem.getActionItemId()); task.setTaskOwner(owner);
	 * task.setStartDate(actionitem.getStartDate());
	 * task.setDueDate(actionitem.getEndDate());
	 * task.setTaskDescription(actionitem.getActionItemDescription());
	 * task.setTaskPriority(actionitem.getActionPriority());
	 * task.setEmailId(actionitem.getEmailId());
	 * task.setCreatedBy(task.getCreatedBy());
	 * task.setCreatedDateTime(LocalDateTime.now());
	 * task.setCreatedByEmailId(task.getCreatedByEmailId());
	 * //task.setStatus(actionitem.getActionStatus());
	 * task.setStatus("Yet to start"); taskList.add(task);
	 * log.info("Action items converted to task sucessfully"); }); }); List<Task>
	 * savedTaskList = taskRepository.saveAll(taskList); //send notification to task
	 * owner savedTaskList.forEach(savedtask -> { new Thread(new Runnable() {
	 * 
	 * @Override public void run() { Notification notification = new Notification();
	 * notification.setMessage("Task T000"+savedtask.getTaskId()
	 * +" has been assigned to you."); notification.setModuleType("Tasks");
	 * notification.setNotificationTo(savedtask.getTaskOwner());
	 * notification.setEmailId(savedtask.getEmailId());
	 * notificationService.createNotification(notification); } }).start(); });
	 * //send emails to task owners savedTaskList.forEach(task -> {
	 * sendEmailToTaskOwner(task, true); });
	 * log.info("TaskServiceImpl.convertActionItemsToTasks is executed successfully"
	 * ); return savedTaskList; }
	 */

	@Override
	public boolean deleteAllTasksById(List<Integer> taskIds) {
		log.info("deleteAllTasksById() entered with args : " + taskIds);
		if (taskIds.size() == 0 || taskIds == null) {
			log.info("deleteAllTasksById() Empty List Exception : Exception occured while deleting the tasks");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("deleteAllTasksById() is under execution...");
		boolean isAllDeleted = false;
		List<Task> tasksToBeDeleted = taskRepository.findAllById(taskIds);
		taskRepository.deleteAll(tasksToBeDeleted);
		isAllDeleted = true;

		// send notifications to task owners
		new Thread(new Runnable() {
			List<Notification> notificationList = new ArrayList<>();

			@Override
			public void run() {
				tasksToBeDeleted.forEach(deletedTask -> {
					Notification notification = new Notification();
					notification.setMessage(
							"Task " + deletedTask.getTaskId() + " has been deleted and it is no longer available.");
					notification.setModuleType(MeetingConstants.MODULE_TYPE_TASK);
					notification.setNotificationTo(deletedTask.getTaskOwner());
					notification.setEmailId(deletedTask.getEmailId());
					notificationList.add(notification);
				});
				notificationService.createAllNotifications(notificationList);
			}
		}).start();

		log.info("deleteAllTasksById() is executed successfully");
		return isAllDeleted;
	}

	@Override
	public List<Task> getTasksByUserId(String email) {
		log.info("getTasksByUserId() entered with args : " + email);
		if (email == null || email == "") {
			log.info("getTasksByUserId() Empty Input Exception : Exception occured while fetching the user tasks");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getTasksByUserId() is under execution...");
		List<Task> list = taskRepository.findByUserId(email);
		log.info("getTasksByUserId() is executed successfully");
		return list;
	}

	@Override
	public List<Task> getAssignedTaskListOfUser(String emailId) {
		log.info("getAssignedTaskListOfUser() entered with args - emailId : " + emailId);
		if (emailId == null || emailId.equals("")) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getAssignedTaskListOfUser() is under execution...");
		List<Task> assignedTaskList = taskRepository.findUserAssignedTasksByUserId(emailId);
		log.info("getAssignedTaskListOfUser() is executed successfully");
		return assignedTaskList;
	}

	@Transactional
	@Override
	public void sendMinutesofMeetingEmail(List<String> emailList, List<ActionItem> actionItemList, Long meetingId,
			String discussionPoints, String HoursDiff, String minDiff) {
		log.info("sendMinutesofMeetingEmail() is entered");
		// get meeting object from Repository
		log.info("sendMinutesofMeetingEmail() is under execution...");
		MeetingDto meetingDto = meetingService.getMeetingDetails(meetingId);
		Meeting meeting = new Meeting();
		mapper.map(meetingDto, meeting);
		StringBuilder actionItemBuilder = new StringBuilder();
		StringBuilder attendeeListBuilder = new StringBuilder();
		Set<Attendee> attendeeList = meeting.getAttendees();
		List<String> attendeeEmailList = new ArrayList<>();
		attendeeList.forEach(attendee -> {
			String singleAttendee = attendee.getEmailId();
			attendeeEmailList.add(singleAttendee);
			attendeeListBuilder.append(singleAttendee + ",");
		});
		String emails = attendeeListBuilder.toString();

		// Checking if the string contains a comma
		if (emails.contains(",")) {
			int lastIndex = emails.lastIndexOf(","); // Find the index of the last comma
			if (lastIndex != -1) { // Check if the comma is found
				emails = emails.substring(0, lastIndex) + emails.substring(lastIndex + 1); // Remove the last comma
			}
		}
		String url = "http://UMS-EMPLOYEE-SERVICE/employees/attendees/" + emails;

		// Make the request using exchange method to retrieve a List<EmployeeVO>
		ResponseEntity<List<EmployeeVO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EmployeeVO>>() {
				});
		if (emailList == null) {
			emailList = new ArrayList<>();
			emailList.add(meeting.getOrganizerEmailId());
		} else {
			emailList.add(meeting.getOrganizerEmailId());
		}
		List<EmployeeVO> employeeVOList = responseEntity.getBody();
		String[] emailArrayList = new String[emailList.size()];
		for (int i = 0; i < emailList.size(); i++) {
			if (emailList.get(i) != null) {
				emailArrayList[i] = emailList.get(i);
			}
		}
		List<String> mergedEmailList = new ArrayList<>(emailList);
		mergedEmailList.addAll(attendeeEmailList);
		// LocalDateTime
		LocalDateTime utcDateTime = meeting.getStartDateTime();
		// Convert UTC LocalDateTime to ZonedDateTime in UTC
		ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneOffset.UTC);
		// Convert UTC ZonedDateTime to IST (Indian Standard Time)
		ZonedDateTime istZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
		// Get the equivalent OffsetDateTime in IST
		OffsetDateTime meetingLocalStartDateTime = istZonedDateTime.toOffsetDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy ; HH:MM");
		String formattedDateTimeInIST = meetingLocalStartDateTime.format(formatter);
		// String newformattedDateTimeInIST = formattedDateTimeInIST.replace("am",
		// "AM").replace("pm", "PM");
		String subject = meeting.getSubject() + "/" + "MOM";
		actionItemBuilder.append("<b>" + "Title - " + "</b>" + meeting.getSubject() + "<br/>");
		actionItemBuilder.append("<b>" + "Organizer - " + "</b>" + meeting.getOrganizerName() + "<br/>");
		actionItemBuilder.append("<b>" + "Date & Time - " + "</b>" + formattedDateTimeInIST + "<br/>");
		actionItemBuilder
				.append("<b>" + "Duration of Meeting - " + "</b>" + HoursDiff + "H:" + minDiff + "M" + "<br/>");
		StringBuilder attendeesName = new StringBuilder();
		employeeVOList.forEach(employee -> {
			attendeesName.append(employee.getFirstName() + " " + employee.getLastName() + ", ");
		});
		// actionItemBuilder.append("<h4>").append("Attendees -
		// "+attendeesName).append("</h4>");
		String dislayAttendeeName = attendeesName.toString();
		if (dislayAttendeeName.toString().contains(",")) {
			int lastIndex = dislayAttendeeName.lastIndexOf(","); // Find the index of the last comma
			if (lastIndex != -1) { // Check if the comma is found
				dislayAttendeeName = dislayAttendeeName.substring(0, lastIndex)
						+ dislayAttendeeName.substring(lastIndex + 1); // Remove the last comma
			}
		}
		actionItemBuilder.append("<b>" + "Attendees - " + "</b>" + dislayAttendeeName + "<br/>");
		actionItemBuilder.append("<h4>").append("Discussion Points -").append("</h4>");
		if (discussionPoints == null) {
			actionItemBuilder.append("There are no discussion points" + "<br/><br/>");
		} else {
			// Split the paragraph into sentences based on full stops
			String[] sentences = discussionPoints.split("\\.");

			// Append each sentence as a bulleted point
			for (String sentence : sentences) {
				String trimmedSentence = sentence.trim();
				if (!trimmedSentence.isEmpty()) {
					actionItemBuilder.append("• ").append(trimmedSentence).append("<br/>");
				}
			}
			actionItemBuilder.append("<br/>");
		}
		actionItemBuilder.append("<table border='1'>");
		actionItemBuilder
				.append("<tr><th>Action Item</th><th>Action Item Owner Name</th><th>Action Item Owner Email ID</th></tr>");
		List<ActionItemModel> actionModelList = new ArrayList<>();
		System.out.println("action item list"+ actionItemList);
		//Action Item list
	
		actionItemList.forEach(action -> {
			ActionItemModel actionModel = new ActionItemModel();
			actionModel.setActionTitle(action.getActionItemTitle());
			List<String> actionItemOwnerList = new ArrayList<>();
			action.getActionItemOwner().forEach(owner -> {
				actionItemOwnerList.add(owner);

			});
			//actionModel.setActionOwner(actionItemOwnerList);
			String listString = "";
            System.out.println("the owner list is:"+ actionModel.getActionOwner());
			int size = actionItemOwnerList.size();
			for (int i = 0; i < size; i++) {
				listString += actionItemOwnerList.get(i);

				if (i < size - 1) {
					// Add a comma if it's not the last element
					listString += ",";
				}
			}
			System.out.println(listString);
			String URL = "http://UMS-EMPLOYEE-SERVICE/employees/attendees/" + listString;

			// Make the request using exchange method to retrieve a List<EmployeeVO>
			ResponseEntity<List<EmployeeVO>> res = restTemplate.exchange(URL, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<EmployeeVO>>() {
					});

			List<EmployeeVO> actionOwnerNameList = res.getBody();
			
			// Iterating the actionOwnerNameList
			
            StringBuilder actionOwnerEmail = new StringBuilder();
            List<String> actionItemOwnerEmailList = new LinkedList<>();
    		StringBuilder actionOwnerName = new StringBuilder();
			int sizes = actionOwnerNameList.size();
			int count = 0;
			for (EmployeeVO employee : actionOwnerNameList) {
				actionOwnerName.append(employee.getFirstName()).append(" ").append(employee.getLastName());
				actionOwnerEmail.append(employee.getEmail()+", ");
				actionItemOwnerEmailList.add(employee.getEmail());
                 
				// Check if it's not the last element
				if (count < sizes - 1) {
					actionOwnerName.append(", ");
				}

				count++;
			}
			actionModel.setOwner(actionOwnerName.toString());
			actionModel.setActionOwner(actionItemOwnerEmailList);
			actionModelList.add(actionModel);
			
			// System.out.println(actionModelList);

		});
		
		for (int i = 0; i < actionModelList.size(); i++) {
			actionItemBuilder.append("<tr><td>").append(actionModelList.get(i).getActionTitle()).append("</td>");
			actionItemBuilder.append("<td>").append(actionModelList.get(i).getOwner()).append("</td>");
			actionItemBuilder.append("<td>");
			List<String> owners = actionModelList.get(i).getActionOwner();
			int size = owners.size();
//			actionModelList.get(i).getActionOwner().forEach(owner -> {
//				actionItemBuilder.append(owner+", ");
//	      	});
			for (int j = 0; j < size; j++) {
			    String owner = owners.get(j);
			    actionItemBuilder.append(owner);
			    if (j < size - 1) {
			        actionItemBuilder.append(", ");
			    }
			}
		 actionItemBuilder.append("</td></tr>");
		}
		actionItemBuilder.append("<br/>");
		actionItemBuilder.append("</table>");
		actionItemBuilder.append("<br/>");
		actionItemBuilder.append("<b>").append("Thanks & Regards").append("<br/>");
		actionItemBuilder.append(meeting.getOrganizerName() + "</b>" + "<br/><br/>");
		String[] convertedMergeList = mergedEmailList.toArray(new String[0]);
		emailService.sendMail(convertedMergeList, subject, actionItemBuilder.toString(), true);
		int count = 0;
		MeetingDto updatingMeeting = meetingService.getMeetingDetails(meetingId);
		if(updatingMeeting.getMomEmailCount() == null) {
			count =1;
			updatingMeeting.setMomEmailCount(count);
		}
		else if(updatingMeeting.getMomEmailCount() >= 1){
			count = updatingMeeting.getMomEmailCount();
			count++;
			updatingMeeting.setMomEmailCount(count);
		}
		meetingService.updateMeetingDetails(updatingMeeting);
		log.info("sendMinutesofMeetingEmail() executed successfully");
	}

	private void sendEmailToTaskOwner(Task task, boolean isNew) {
		log.info("sendEmailToTaskOwner() entered with args - taskObject, isNew? : " + isNew);
		// send email to task owner
		log.info("sendEmailToTaskOwner() is under execution...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String to = task.getTaskOwner();
				String subject = null;
				if (isNew) {
					subject = "Alert ! Task Assigned";
				} else {
					subject = "Alert ! Task Updated";
				}
				StringBuilder emailBuilder = new StringBuilder();
				ActionItem actionItem = new ActionItem();
				Optional<ActionItem> optionalActionItem = actionItemService.getActionItemById(task.getActionItemId());
				if (optionalActionItem.isPresent()) {
					actionItem = optionalActionItem.get();
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMMM dd hh:mm a");
				LocalDateTime timestamp = task.getPlannedStartDate();
		        String formattedPlannedStartDateTime = timestamp.format(formatter);
		        LocalDateTime timestamp1 = task.getPlannedEndDate();
		        String formattedPlannedEndDateTime = timestamp1.format(formatter);
		        LocalDateTime timestamp2 = task.getStartDate();
		        String formattedStartDateTime = timestamp2.format(formatter);
		        LocalDateTime timestamp3 = task.getStartDate();
		        String formattedEndDateTime = timestamp3.format(formatter);
		       // var message = String.valueOf(isNew).equals("true")?"assigned to you.":"updated.";
				emailBuilder.append("<b>Meeting ID</b> - " + actionItem.getMeetingId() + "<br/>"
						+ "<b>Action Item ID</b> - " + task.getActionItemId() + "<br/>" + "<b>Task ID</b> - "
						+ task.getTaskId() + "<br/>" + "<b>Task Title</b> - " + task.getTaskTitle()
						+ "<br/><br/>");
				if(!isNew) {
					emailBuilder.append("A task has been updated. <br/><br/>"
							+ "<table width='100%' border='1' align='center'>" + "<tr>"
							+ "<th colspan='3'>Task Details</th>" + "</tr>" + "<tr>" + "<td><b>Assignee</b> : "
							+ task.getTaskOwner() + "</td>" + "<td><b>Organizer</b> : " + task.getEmailId() + "</td>"
							+ "<td><b>Priority</b> : " + task.getTaskPriority() + "</td>" + "</tr>" + "<tr>");
				}else {
					emailBuilder.append("A task has been assigned to you. <br/><br/>"
							+ "<table width='100%' border='1' align='center'>" + "<tr>"
							+ "<th colspan='3'>Task Details</th>" + "</tr>" + "<tr>" + "<td><b>Assignee</b> : "
							+ task.getTaskOwner() + "</td>" + "<td><b>Organizer</b> : " + task.getEmailId() + "</td>"
							+ "<td><b>Priority</b> : " + task.getTaskPriority() + "</td>" + "</tr>" + "<tr>");
				}
				if (task.getStartDate() == null) {
					emailBuilder.append("<td><b>Planned Start Date</b> : " + formattedPlannedStartDateTime + "</td>"
							+ "<td><b>Planned Due Date</b> : " + formattedPlannedEndDateTime + "</td>"
							+ "<td><b>Status</b> : " + task.getStatus() + "</td>" + "</tr>" + "</table><br/>");

				} else {
					emailBuilder.append("<td><b>Start Date</b> : " + formattedStartDateTime + "</td>"
							+ "<td><b>Due Date</b> : " + formattedEndDateTime + "</td>" + "<td><b>Status</b> : "
							+ task.getStatus() + "</td>" + "</tr>" + "</table><br/>");

				}

				log.info("sendEmailToTaskOwner(): Task email sent to " + task.getTaskOwner() + " sucessfully");
				//emailService.sendMail(to, subject, emailBuilder.toString(), true);
				emailService.sendMail(to, subject, emailBuilder.toString(), new String[] {task.getEmailId()}, null, null, true);
			}
		}).start();
		log.info("sendEmailToTaskOwner() executed successfully");

	}

	@Override
	public Long getOrganizedTasksCountOfUser(String emailId) {
		log.info("getOrganizedTasksCountOfUser() is entered");
		if (Strings.isNullOrEmpty(emailId)) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_EMAILID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_EMAILID_IS_EMPTY_MSG);
		}
		log.info("getOrganizedTasksCountOfUser() is under execution...");
		Long count = taskRepository.findOrganizedTaskCountByUserId(emailId);
		log.info("getOrganizedTasksCountOfUser() executed succesfully");
		return count;
	}

	@Override
	public Long getUserAssignedTasksCountOfUser(String emailId) {
		log.info("getUserAssignedTasksCountOfUser() is entered");
		if (Strings.isNullOrEmpty(emailId)) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_EMAILID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_EMAILID_IS_EMPTY_MSG);
		}
		log.info("getUserAssignedTasksCountOfUser() is under execution...");
		Long count = taskRepository.findAssignedTaskCountByUserId(emailId);
		log.info("getUserAssignedTasksCountOfUser() executed succesfully");
		return count;
	}

	@Override
	public List<Long> getTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime, String email) {
		log.info("getTaskCountsByDayOfWeek() is entered");
		log.info("getTaskCountsByDayOfWeek() is under execution...");
		List<Object[]> taskCountsByDay = taskRepository.findTaskCountsByDayOfWeek(startTime, endTime, email);

		// Create an array to store counts for each day, initialized with zeros
		List<Long> totalTaskCount = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			totalTaskCount.add(0L);
		}

		// Process the query result and populate the counts array
		for (Object[] result : taskCountsByDay) {
			String dayOfWeek = (String) result[0];
			Long completedCount = (Long) result[1];
			int dayIndex = Integer.parseInt(dayOfWeek) - 1;
			totalTaskCount.set(dayIndex, completedCount);
		}
		log.info("getTaskCountsByDayOfWeek() executed succesfully");
		return totalTaskCount;
	}

	@Override
	public List<Long> getCompletedTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime, String email) {
		log.info("getCompletedTaskCountsByDayOfWeek() is entered");
		log.info("getCompletedTaskCountsByDayOfWeek() is under execution...");
		List<Object[]> taskCountsByDay = taskRepository.findCompletedTaskCountsByDayOfWeek(startTime, endTime, email);

		// Initialize an array to store completed task counts for each day
		List<Long> completedTaskCounts = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			completedTaskCounts.add(0L);
		}

		// Process the query result and populate the completed task counts array
		for (Object[] result : taskCountsByDay) {
			String dayOfWeek = (String) result[0];
			Long completedCount = (Long) result[1];
			int dayIndex = Integer.parseInt(dayOfWeek) - 1;
			completedTaskCounts.set(dayIndex, completedCount);
		}
		log.info("getCompletedTaskCountsByDayOfWeek() executed succesfully");
		return completedTaskCounts;
	}

	@Override
	public List<Long> findInProgressTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime, String email) {
		log.info("findInProgressTaskCountsByDayOfWeek() is entered");
		log.info("findInProgressTaskCountsByDayOfWeek() is under execution...");
		List<Object[]> taskCountsByDay = taskRepository.findInProgressTaskCountsByDayOfWeek(startTime, endTime, email);

		// Initialize an array to store in-progress task counts for each day
		List<Long> inProgressTaskCounts = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			inProgressTaskCounts.add(0L);
		}

		// Process the query result and populate the in-progress task counts array
		for (Object[] result : taskCountsByDay) {
			String dayOfWeek = (String) result[0];
			Long inProgressCount = (Long) result[1]; // Change [2] to [1]
			int dayIndex = Integer.parseInt(dayOfWeek) - 1;
			inProgressTaskCounts.set(dayIndex, inProgressCount);
		}
		log.info("findInProgressTaskCountsByDayOfWeek() executed successfully");
		return inProgressTaskCounts;
	}

	@Override
	public List<Task> getFilteredTasks(String taskTitle, String taskPriority, String taskOwner, String startDate,
			String dueDate, String emailId) {
		log.info("getFilteredTasks() is entered");
		log.info("getFilteredTasks() is under execution...");
		LocalDate orgStartDateTime = null;
		LocalDate orgDueDateTime = null;
		// DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
		// ");
		if (startDate != null && !startDate.equals("") && !startDate.equals("null")) {
			orgStartDateTime = LocalDate.parse(startDate);
		}
		if (dueDate != null && !dueDate.equals("") && !dueDate.equals("null")) {
			orgDueDateTime = LocalDate.parse(dueDate);
		}
		if (taskTitle.equals("null") || taskTitle == null || taskTitle.isBlank()) {
			taskTitle = null;
		}
		if (taskOwner.equals("null") || taskOwner == null || taskOwner.isBlank()) {
			taskOwner = null;
		}
		if (taskPriority.equals("null") || taskPriority == null || taskPriority.isBlank()) {
			taskPriority = null;
		}
		log.info("getFilteredTasks() executed successfully");
		return taskRepository.findFilteredTasks(taskTitle, taskPriority, taskOwner, orgStartDateTime, orgDueDateTime,
				emailId);
	}

	@Override
	public List<Long> findTaskCountsByMonth(LocalDateTime startTime, LocalDateTime endTime, String email) {
		log.info("findTaskCountsByMonth() is entered");
		log.info("findTaskCountsByMonth() is under execution...");
		List<Object[]> taskCountsByMonth = taskRepository.findTaskCountsByMonth(startTime, endTime, email);
		List<Long> monthlyTaskCounts = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			monthlyTaskCounts.add(0L);
		}
		for (Object[] result : taskCountsByMonth) {
			String month = (String) result[0];
			Long taskCount = (Long) result[1];

			int dayIndex = Integer.parseInt(month) - 1;
			monthlyTaskCounts.set(dayIndex, taskCount);
		}
		log.info("findTaskCountsByMonth() executed successfully");
		return monthlyTaskCounts;
	}

	@Override
	public List<Long> findInprogressTaskCountsByMonth(LocalDateTime startTime, LocalDateTime endTime, String email) {
		log.info("findInprogressTaskCountsByMonth() entered");
		log.info("findInprogressTaskCountsByMonth() is under execution...");
		List<Object[]> inprogressTaskCountsByMonth = taskRepository.findInProgressTaskCountsByMonth(startTime, endTime,
				email);
		List<Long> monthlyInprogressTaskCounts = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			monthlyInprogressTaskCounts.add(0L);
		}
		for (Object[] result : inprogressTaskCountsByMonth) {
			String month = (String) result[0];
			Long taskCount = (Long) result[1];

			int monthIndex = Integer.parseInt(month) - 1;
			monthlyInprogressTaskCounts.set(monthIndex, taskCount);
		}
		log.info("findInprogressTaskCountsByMonth() execueted successfully");
		return monthlyInprogressTaskCounts;
	}

	@Override
	public List<Long> findCompletedTaskCountsByMonth(LocalDateTime startTime, LocalDateTime endTime, String email) {
		log.info("findCompletedTaskCountsByMonth() entered");
		log.info("findCompletedTaskCountsByMonth() is under execution...");
		List<Object[]> completedTaskCountsByMonth = taskRepository.findCompletedTaskCountsByMonth(startTime, endTime,
				email);
		List<Long> completedMonthlyTaskCounts = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			completedMonthlyTaskCounts.add(0L);
		}
		for (Object[] result : completedTaskCountsByMonth) {
			String month = (String) result[0];
			Long taskCount = (Long) result[1];

			int monthIndex = Integer.parseInt(month) - 1;
			completedMonthlyTaskCounts.set(monthIndex, taskCount);
		}
		log.info("findCompletedTaskCountsByMonth() executed successfully");
		return completedMonthlyTaskCounts;
	}

	@Override
	public List<Task> getFilteredAssignedTasks(String taskTitle, String taskPriority, String startDate, String dueDate,
			String emailId) {
		log.info("getFilteredAssignedTasks() entered");
		log.info("getFilteredAssignedTasks() is under execution...");
		LocalDate orgStartDateTime = null;
		LocalDate orgDueDateTime = null;
		// DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
		// ");
		if (startDate != null && !startDate.equals("") && !startDate.equals("null")) {
			orgStartDateTime = LocalDate.parse(startDate);
		}
		if (dueDate != null && !dueDate.equals("") && !dueDate.equals("null")) {
			orgDueDateTime = LocalDate.parse(dueDate);
		}
		if (taskTitle.equals("null") || taskTitle == null || taskTitle.isBlank()) {
			taskTitle = null;
		}
		if (taskPriority.equals("null") || taskPriority == null || taskPriority.isBlank()) {
			taskPriority = null;
		}
		log.info("getFilteredAssignedTasks() executed successfully");
		return taskRepository.findFilteredAssignedTasks(taskTitle, taskPriority, orgStartDateTime, orgDueDateTime,
				emailId);
	}

	@Override
	public List<Task> getTasksByDepartment(Long departmentId) {
		log.info("getTasksByDepartment() entered");
		log.info("getTasksByDepartment() is under execution...");
		if (departmentId == 0 || departmentId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_MSG);
		}
		List<Task> taskList = taskRepository.findByDepartmentId(departmentId);
		log.info("getTasksByDepartment() executed successfully");
		return taskList;
	}

	@Override
	public List<Task> getTasksByTaskPriority(String taskPriority) {
		log.info("findByTaskPriority() entered");
		log.info("getTasksByTaskPriority() is under execution...");
		if (taskPriority == "" || taskPriority == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_MSG);
		}
		List<Task> taskList = taskRepository.findByTaskPriority(taskPriority);
		System.out.println(taskList);
		log.info("getTasksByTaskPriority() executed successfully");
		return taskList;
	}

	@Override
	public List<Task> getTasksByTaskStatus(String taskStatus) {
		log.info("getTasksByTaskStatus() is entered");
		log.info("getTasksByTaskStatus() is under execution...");
		if (taskStatus == null || taskStatus == "") {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_MSG);
		}
		List<Task> taskList = taskRepository.findByStatus(taskStatus);
		log.info("getTasksByTaskStatus() executed successfully");
		return taskList;
	}

	@Override
	public List<Task> getAgedTasks(LocalDate dateTime) {
		log.info("getAgedTasks() is entered");
		log.info("getAgedTasks() is under execution...");
		System.out.println(dateTime);
		List<Task> taskList = taskRepository.findAgedTasks(dateTime);
		log.info("getAgedTasks() executed successfully");
		return taskList;
	}

	@Override
	public List<Long> getTasksBetweenStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate) {
		log.info("getTasksBetweenStartDateAndEndDate() is entered");
		log.info("getTasksBetweenStartDateAndEndDate() is under execution...");
		List<Object[]> taskCountsByMonth = taskRepository.findTaskCountsforYear(startDate, endDate);
		List<Long> monthlyTaskCounts = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			monthlyTaskCounts.add(0L);
		}
		for (Object[] result : taskCountsByMonth) {
			String month = (String) result[0];
			Long taskCount = (Long) result[1];

			int dayIndex = Integer.parseInt(month) - 1;
			monthlyTaskCounts.set(dayIndex, taskCount);
		}
		log.info("getTasksBetweenStartDateAndEndDate() executed successfully");
		return monthlyTaskCounts;
	}

	@Override
	public List<Long> getYetToStartTaskCountsByDayOfWeek(LocalDateTime startDate, LocalDateTime endDate, String emailId) {
		log.info("getYetToStartTaskCountsByDayOfWeek() is entered");
		log.info("getYetToStartTaskCountsByDayOfWeek() is under execution...");

		List<Object[]> YetToStartTaskCountsByDay = taskRepository.findYetToStartTaskCountsByDayOfWeek(startDate,
				endDate, emailId);

		// Initialize an array to store completed task counts for each day
		List<Long> YetToStartTaskCounts = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			YetToStartTaskCounts.add(0L);
		}

		// Process the query result and populate the completed task counts array
		for (Object[] result : YetToStartTaskCountsByDay) {
			String dayOfWeek = (String) result[0];
			Long completedCount = (Long) result[1];
			int dayIndex = Integer.parseInt(dayOfWeek) - 1;
			YetToStartTaskCounts.set(dayIndex, completedCount);
		}
		log.info("getYetToStartTaskCountsByDayOfWeek() executed successfully");
		return YetToStartTaskCounts;
	}

	@Override
	public List<Long> findYetToStartTaskCountsByMonth(LocalDateTime startDate, LocalDateTime endDate, String emailId) {
		log.info("findYetToStartTaskCountsByMonth() is entered");
		log.info("findYetToStartTaskCountsByMonth() is under execution...");
		List<Object[]> YetToStartTaskCountsByMonth = taskRepository.findYetToStartTaskCountsByMonth(startDate, endDate,
				emailId);
		List<Long> monthlyYetToSatrtTaskCounts = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			monthlyYetToSatrtTaskCounts.add(0L);
		}
		for (Object[] result : YetToStartTaskCountsByMonth) {
			String month = (String) result[0];
			Long taskCount = (Long) result[1];

			int monthIndex = Integer.parseInt(month) - 1;
			monthlyYetToSatrtTaskCounts.set(monthIndex, taskCount);
		}
		log.info("findYetToStartTaskCountsByMonth() executed successfully");
		return monthlyYetToSatrtTaskCounts;
	}

	@Override
	public List<Object[]> getAllTasksByDepartment() {
		log.info("getAllTasksByDepartment() is entered");
		log.info("getAllTasksByDepartment() is under execution...");
		List<Object[]> taskList = taskRepository.getAllTasksByDepartment();
		log.info("getAllTasksByDepartment executed successfully");
		return taskList;
	}

	@Override
	public List<Task> getTasksByCategoryId(Long taskCategoryId) {
		log.info("getTasksByCategoryId() is entered");
		if (taskCategoryId == 0 || taskCategoryId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_MSG);
		}
		log.info("getTasksByCategoryId() is under execution...");
		List<Task> taskList = taskRepository.findByTaskCategoryName(taskCategoryId);
		log.info("getTasksByCategoryId() executed successfully");
		return taskList;
	}

	@Override
	public List<Object[]> getAllTaskCategoryByCount() {

		log.info("getAllTaskCategoryByCount() is entered");
		log.info("getAllTaskCategoryByCount() is under execution...");
		List<Object[]> taskCountList = taskRepository.getAllTasksCategoryCount();
		log.info("getAllTaskCategoryByCount() executed successfully");
		return taskCountList;
	}

	@Override
	public List<Task> getAllTasksByOrganizerName(String email) {
		log.info("getAllTasksByOrganizerName() is entered");
		log.info("getAllTasksByOrganizerName() is under execution...");
		List<Task> taskCountList = taskRepository.getTasksByOrganizerName(email);
		log.info("getAllTasksByOrganizerName() executed successfully");
		return taskCountList;
	}

	public String getDuartionOfTask(LocalDateTime startDateTime, LocalDateTime enddateTime) {
		log.info("getDuartionOfTask() is entered");
		log.info("getDuartionOfTask() is under execution...");
		Duration duration = Duration.between(startDateTime, enddateTime);
		long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
       // long seconds = duration.getSeconds() % 60;
		String resultvalue = days +"D - "+hours+"H:"+minutes+"M";
		log.info("getDuartionOfTask() executed successfully");
		return resultvalue;
		
	}
}