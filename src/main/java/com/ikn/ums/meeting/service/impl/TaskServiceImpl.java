package com.ikn.ums.meeting.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.VO.Notification;
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
import com.ikn.ums.meeting.utils.NotificationService;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice.Local;

@Service
@Slf4j
public class TaskServiceImpl implements  TaskService{
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MeetingService meetingService;
	
	private ActionItemService actionItemService;
	
	@Autowired
	private NotificationService notificationService;

	@Override
	@Transactional
	public Task saveTask(Task task) {
		log.info("TaskServiceImpl.saveTask() entered with args - task");
		if(task == null ) {
			log.info("TaskServiceImpl.saveTask() Empty List Exception : Exception occured while saving the task");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("TaskServiceImpl.saveTask() is under execution...");
		Task taskObject= taskRepository.save(task);
		log.info("TaskServiceImpl.saveTask() is executed Successfully");
		return taskObject;
	}

	@Override
	public List<Task> getTasks() {
		// TODO Auto-generated method stub
		log.info("TaskServiceImpl.getTasks() entered");
		log.info("TaskServiceImpl.getTasks() is under execution...");
		List<Task> taskList= taskRepository.findAll();
		log.info("TaskServiceImpl.getTasks() is executed successfully");
		return taskList;
	}

	@Override
	public Task updateTask(Task task) {
		log.info("TaskServiceImpl.updateTask() entered with args - task ");
		// TODO Auto-generated method stub
		if(task == null ) {
			log.info("TaskServiceImpl.updateTask() Empty List Exception : Exception occured while updating the task");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("TaskServiceImpl.updateTask() is under execution...");
	    Task updatetask = taskRepository.findById( task.getTaskId() ).get();
	    updatetask.setTaskTitle(task.getTaskTitle());
	    updatetask.setTaskDescription(task.getTaskDescription());
	    updatetask.setStartDate(task.getStartDate());
	    updatetask.setDueDate(task.getDueDate());
	    updatetask.setTaskPriority(task.getTaskPriority());
	    updatetask.setActionItemId(task.getActionItemId());
	    updatetask.setTaskOwner(task.getTaskOwner());
	    updatetask.setStatus(task.getStatus());
	    Task modifiedtask = taskRepository.save(updatetask);
	    
	  //send notification to task owner
	    new Thread(new Runnable() {
			@Override
			public void run() {
			    Notification notification = new Notification();
			    notification.setMessage("Task T000"+modifiedtask.getTaskId()+" has been updated for you.");
			    notification.setModuleType("Tasks");
			    notification.setNotificationTo(modifiedtask.getTaskOwner());
			    notification.setEmailId(modifiedtask.getEmailId());
			    notificationService.createNotification(notification);
			}
		}).start();
	    
	    //send email to task owner
	    sendEmailToTaskOwner(modifiedtask, false);
	    log.info("TaskServiceImpl.updateTask() is executed Successfully");
	    return modifiedtask;
	}

	@Override
	public Optional<Task> getTaskById(Integer taskId) {
		log.info("TaskServiceImpl.getTaskById() entred with args : " + taskId);
		if(taskId < 1 || taskId == null) {
			log.info("TaskServiceImpl.getTaskById() : Empty Input Exception - taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		log.info("TaskServiceImpl.getTaskById() is under execution");
		Optional<Task>  task = taskRepository.findById(taskId);
		log.info("TaskServiceImpl.getTaskById() is executed successfully");
		return task;
	}

	@Override
	@Transactional
	public Integer deleteTaskById(Integer taskId) {
		log.info("TaskServiceImpl.deleteTaskById() entered with args : " + taskId);
		if(taskId < 1 || taskId == null) {
			log.info("TaskServiceImpl.deleteTaskById() Empty Input Exception : Exception occured while deleting the task");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		Optional<Task> optTask = getTaskById(taskId);
		Task taskToBeDeleted = null;
		if(optTask.isPresent()) {
			taskToBeDeleted = optTask.get();
			taskRepository.delete(taskToBeDeleted);
		}
		log.info("TaskServiceImpl.deleteTaskById() is under execution");
		
		Task deletedTask = taskToBeDeleted;
		//send notification to task owner
		new Thread(new Runnable() {
			@Override
			public void run() {
				//send noti
				Notification notification = new Notification();
				notification.setMessage("The task T000"+deletedTask.getTaskId()+" has been deleted and it is no more available");
				notification.setModuleType("Tasks");
				notification.setNotificationTo(deletedTask.getTaskOwner());
				notification.setEmailId(deletedTask.getEmailId());
				notificationService.createNotification(notification);
			}
		}).start();
		
		log.info("TaskServiceImpl.deleteTaskById() is executed successfully");
		return 1;

	}

	@Override
	@Transactional
	public List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList, Long meetingId) {
		log.info("TaskServiceImpl.convertActionItemsToTasks() entered with args - actionItemList");
		System.out.println(actionItemList);
		if(actionItemList == null || actionItemList.size() < 1)
		{
			log.info("TaskServiceImpl.convertActionItemsToTasks() Empty List Exception : Exception occured while converting "
					+ "Action Item to task");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("TaskServiceImpl.convertActionItemsToTasks() is under execution...");
		List<Task> taskList = new ArrayList<>();
		actionItemList.forEach(actionitem ->{
			actionitem.getActionItemOwner().forEach(owner -> {
				Task task = new Task();
				//task.setId(actionitem.getId());
				task.setTaskTitle(actionitem.getActionItemTitle());
				task.setActionItemId(actionitem.getActionItemId());
				task.setTaskOwner(owner);
				task.setStartDate(actionitem.getStartDate());
				task.setDueDate(actionitem.getEndDate());
				task.setTaskDescription(actionitem.getActionItemDescription());
				task.setTaskPriority(actionitem.getActionPriority());
				task.setEmailId(actionitem.getEmailId());
				task.setCreatedBy(task.getCreatedBy());
				task.setCreatedDateTime(LocalDateTime.now());
				task.setCreatedByEmailId(task.getCreatedByEmailId());
				//task.setStatus(actionitem.getActionStatus());
				task.setStatus("Yet to start");
				taskList.add(task);
				log.info("Action items converted to task sucessfully");
			});
		});
		List<Task> savedTaskList = taskRepository.saveAll(taskList);
		//send notification to task owner
	    savedTaskList.forEach(savedtask -> {
	    	 new Thread(new Runnable() {
	 			@Override
	 			public void run() {
	 			    Notification notification = new Notification();
	 			    notification.setMessage("Task T000"+savedtask.getTaskId()+" has been assigned to you.");
	 			    notification.setModuleType("Tasks");
	 			    notification.setNotificationTo(savedtask.getTaskOwner());
	 			    notification.setEmailId(savedtask.getEmailId());
	 			    notificationService.createNotification(notification);
	 			}
	 		}).start();
	    });
		//send emails to task owners
		savedTaskList.forEach(task -> {
			sendEmailToTaskOwner(task, true);
		});
		log.info("TaskServiceImpl.convertActionItemsToTasks is executed successfully");
	    return savedTaskList;
	}

    
	@Override
	public boolean deleteAllTasksById(List<Integer> taskIds) {
		log.info("TaskServiceImpl.deleteAllTasksById() entered with args : " +taskIds);
		if(taskIds.size() == 0 || taskIds == null) {
			log.info("TaskServiceImpl.deleteAllTasksById() Empty List Exception : Exception occured while deleting the tasks");
		    throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
		    		ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("TaskServiceImpl.deleteAllTasksById() is under execution...");
		boolean isAllDeleted = false;
		List<Task> tasksToBeDeleted = taskRepository.findAllById(taskIds);
		taskRepository.deleteAll(tasksToBeDeleted);
		isAllDeleted = true;		
		
		//send notifications to task owners
		new Thread(new Runnable() {
			List<Notification> notificationList = new ArrayList<>();
			@Override
			public void run() {
				tasksToBeDeleted.forEach(deletedTask -> {
					Notification notification = new Notification();
					notification.setMessage("The task T000"+deletedTask.getTaskId()+" has been deleted and it is no more available");
	 			    notification.setModuleType("Tasks");
	 			    notification.setNotificationTo(deletedTask.getTaskOwner());
	 			    notification.setEmailId(deletedTask.getEmailId());
	 			    notificationList.add(notification);	
				});
				notificationService.createAllNotifications(notificationList);
			}
		}).start();
		
		log.info("TaskServiceImpl.deleteAllTasksById() is executed successfully");
		return isAllDeleted;
	}

	@Override
	public List<Task> getTasksByUserId(String email) {
		log.info("TaskServiceImpl.getTasksByUserId() entered with args : " +email);
		if(email == "" || email == null) {
			log.info("TaskServiceImpl.getTasksByUserId() Empty Input Exception : Exception occured while fetching the user tasks");
		    throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
		    		ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("TaskServiceImpl.getTasksByUserId() is under execution...");
		List<Task> list = taskRepository.findByUserId(email);
		log.info("TaskServiceImpl.getTasksByUserId() is executed successfully");
		return list;
	}
	@Override
	public List<Task> getAssignedTaskListOfUser(String emailId) {
		log.info("TaskServiceImpl.getAssignedTaskListOfUser() entered with args - emailId : "+emailId);
		if(emailId == null || emailId.equals("")) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("TaskServiceImpl.getAssignedTaskListOfUser() is under execution...");
		List<Task> assignedTaskList = taskRepository.findUserAssignedTasksByUserId(emailId);
		log.info("TaskServiceImpl.getAssignedTaskListOfUser() is executed successfully");
		return assignedTaskList;
	}
		
	public void sendMinutesofMeetingEmail(List<String> emailList, List<ActionItem> actionItemList, Long meetingId) {
		
		//get meeting object from Repository
		Optional<Meeting> optMeeting = meetingService.getMeetingDetails(meetingId);
		Meeting meeting = null;
		if(optMeeting.isPresent()) {
		     meeting = optMeeting.get();
		}
		
		// TODO Auto-generated method stub
		StringBuilder actionItemBuilder = new StringBuilder();
		StringBuilder attendeeListBuilder = new StringBuilder();
		Set<Attendee> attendeeList =  meeting.getAttendees();
		attendeeList.forEach(attendee->{
			String singleAttendee = attendee.getEmailId();
			attendeeListBuilder.append(singleAttendee+"\r\n");
		});
		
		String[] emailArrayList = new String[emailList.size()];
		for(int i=0; i<emailList.size(); i++) {
			emailArrayList[i] = emailList.get(i);
			System.out.println("filtered email is:"+emailArrayList[i]);
			
		}
		String subject ="MoM-"+meeting.getSubject()+" "+meeting.getStartDateTime();
		actionItemBuilder.append("<h4>").append("Meeting Description - "+meeting.getSubject()).append("</h4>");
		actionItemBuilder.append("<table border='1'>");
		actionItemBuilder.append("<tr><th>Action Item</th><th>Action Owner</th></tr>");		
		List<ActionItemModel> actionModelList = new ArrayList<>();
 	    actionItemList.forEach(action ->{
	    	ActionItemModel actionModel = new ActionItemModel();
	        actionModel.setActionTitle(action.getActionItemTitle());
	        List<String> actionItemOwnerList = new ArrayList<>();
	        		action.getActionItemOwner().forEach(owner -> {
	        			actionItemOwnerList.add(owner);
	        	
	        });
	        actionModel.setActionOwner(actionItemOwnerList);
	        actionModelList.add(actionModel);
	        System.out.println(actionModelList);
			    
		});
 	   System.out.println(actionModelList);
 	   
 	   for(int i= 0; i<actionModelList.size();i++) {
 		 actionItemBuilder.append("<tr><td>").append(actionModelList.get(i).getActionTitle()).append("</td>");
 		 //actionItemBuilder.append("<td>").append(actionModelList.get(i).getActionOwner().toString()).append("</td></tr>");
 		 actionItemBuilder.append("<td>");
 		 actionItemList.get(i).getActionItemOwner().forEach(owner->{
 			actionItemBuilder.append(owner+" ").append("</td></tr>");
 		 });
 			 
 	   }
 	   actionItemBuilder.append("</table>");
	   emailService.sendMail(emailArrayList, subject, actionItemBuilder.toString(),true);	
	}
	
	private void sendEmailToTaskOwner(Task task, boolean isNew) {
		log.info("TaskServiceImpl.sendEmailToTaskOwner() entered with args - taskObject, isNew? : "+isNew);
		//send email to task owner
				
					new Thread(new Runnable() {
						@Override
						public void run() {
							String to = task.getTaskOwner();
							String subject = null;
							if(isNew) {
								subject = "Alert ! Task Assigned";
							}else {
								subject = "Alert ! Task Updated";
							}
							
							String body = "<b>Meeting ID</b> - MXXX"+"<br/>"
							+"<b>Action Item ID</b> - A000"+task.getActionItemId()+"<br/>"
							+"<b>Task ID</b> - T000"+task.getTaskId()+"<br/>"
							+"A task has been assigned to you: Please see below"+"<br/>"
							+"Task Description: "+task.getTaskDescription()+"<br/>"
							+ "<table width='100%' border='1' align='center'>"
							+"<tr>"
							+"<th colspan='3'>Task Details</th>"
							+"<th></th>"
							+"</tr>"
							+"<tr>"
							+"<td><b>Assignee</b> : "+task.getTaskOwner()+"</td>"
							+"<td><b>Organizer</b> : "+task.getEmailId()+"</td>"
							+"<td><b>Priority</b> : "+task.getTaskPriority()+"</td>"
							+"</tr>"
							+"<tr>"
							+"<td><b>Start Date</b> : "+task.getStartDate()+"</td>"
							+"<td><b>Due Date</b> : "+task.getDueDate()+"</td>"
							+"<td><b>Status</b> : "+task.getStatus()+"</td>"
							+"</tr>"
							+ "</table>";
							log.info("TaskServiceImpl.sendEmailToTaskOwner(): Task email sent to "+task.getTaskOwner()+" sucessfully");
							emailService.sendMail(to,subject, body,true);
						}
					}).start();	
					System.out.println("TaskServiceImpl.sendEmailToTaskOwner() executed succesfully");
	}
	
	@Override
	public Long getOrganizedTasksCountOfUser(String emailId) {
		Long count = taskRepository.findOrganizedTaskCountByUserId(emailId);
		return count;
	}

	@Override
	public Long getUserAssignedTasksCountOfUser(String emailId) {
		Long count = taskRepository.findAssignedTaskCountByUserId(emailId);
		return count;
	}
	
	@Override
	 public Long[] getTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime) {
       List<Object[]> taskCountsByDay = taskRepository.findTaskCountsByDayOfWeek(startTime, endTime);
 
        // Create an array to store counts for each day, initialized with zeros
        Long[] countsArray = new Long[7];
        for (int i = 0; i < 7; i++) {
            countsArray[i] = 0L;
        }
 
        // Process the query result and populate the counts array
        for (Object[] result : taskCountsByDay) {
            String dayOfWeek = (String) result[0];
            Long count = (Long) result[1];
 
            // Map the day name to its DayOfWeek enum value
            DayOfWeek dayEnum = getDayOfWeekFromDayName(dayOfWeek);
 
            // Map the DayOfWeek to an array index
            int index = dayEnum.getValue() - 1;
 
            // Update the counts array with the count
            countsArray[index] = count;
        }
 
        return countsArray;
    }
	private DayOfWeek getDayOfWeekFromDayName(String dayName) {
        switch (dayName) {
            case "0":
                return DayOfWeek.SUNDAY;
            case "1":
                return DayOfWeek.MONDAY;
            case "2":
                return DayOfWeek.TUESDAY;
            case "3":
                return DayOfWeek.WEDNESDAY;
            case "4":
                return DayOfWeek.THURSDAY;
            case "5":
                return DayOfWeek.FRIDAY;
            case "6":
                return DayOfWeek.SATURDAY;
            default:
                // Handle any unexpected values
                return null;
        }
    }
	 @Override
	    public List<Long> getCompletedTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime) {
	        List<Object[]> taskCountsByDay = taskRepository.findCompletedTaskCountsByDayOfWeek(startTime, endTime);
	 
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
	 
	        return completedTaskCounts;
	    }
	 
	    @Override
	    public List<Long> findInProgressTaskCountsByDayOfWeek(LocalDateTime startTime, LocalDateTime endTime) {
	        List<Object[]> taskCountsByDay = taskRepository.findInProgressTaskCountsByDayOfWeek(startTime, endTime);
	 
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
	 
	        return inProgressTaskCounts;
	    }

	
	 
	    

}
