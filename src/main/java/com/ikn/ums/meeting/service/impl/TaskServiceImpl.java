package com.ikn.ums.meeting.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.repository.TaskRepository;
import com.ikn.ums.meeting.service.ActionItemService;
import com.ikn.ums.meeting.service.MeetingService;
import com.ikn.ums.meeting.service.TaskService;
import com.ikn.ums.meeting.utils.EmailService;

import lombok.extern.slf4j.Slf4j;

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
	    Task modifiedtask = taskRepository.save(updatetask );
	    log.info("TaskServiceImpl.updateTask() is executed Successfully");
	    return modifiedtask;
		
	}

	@Override
	public Optional<Task> getTaskById(Integer taskId) {
		// TODO Auto-generated method stub
		log.info("TaskServiceImpl.getTaskById() entred with args : " + taskId);
		if(taskId < 1 || taskId == null) {
			log.info("TaskServiceImpl.getTaskById() Empty Input Exception taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		log.info("TaskServiceImpl.getTaskById() is under execution");
		Optional<Task>   task = taskRepository.findById(taskId);
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
		log.info("TaskServiceImpl.deleteTaskById() is under execution");
		taskRepository.deleteById(taskId);
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
			Task task = new Task();
			//task.setId(actionitem.getId());
			task.setTaskTitle(actionitem.getActionItemTitle());
			task.setActionItemId(actionitem.getActionItemId());
			task.setTaskOwner(actionitem.getActionItemOwner());
			task.setStartDate(actionitem.getStartDate());
			task.setDueDate(actionitem.getEndDate());
			task.setTaskDescription(actionitem.getActionItemDescription());
			task.setTaskPriority(actionitem.getActionPriority());
			task.setEmailId(actionitem.getEmailId());
			task.setCreatedBy(task.getCreatedBy());
			task.setCreatedDateTime(LocalDateTime.now());
			task.setCreatedByEmailId(task.getCreatedByEmailId());
			//task.setStatus(actionitem.getActionStatus());
			task.setStatus("Yet to Start");
			taskList.add(task);
			log.info("Action items converted to task sucessfully");
		});
		List<Task> savedTaskList = taskRepository.saveAll(taskList);
		//List<ActionItem> updatedActionItemList = actionItemService
		log.info(null);
		String[] emailList = {"pamarthi.bharat1234@gmail.com","Bharat@ikcontech.com"};
		// send MOM email
		//sendMinutesofMeetingEmail(emailList,actionItemList, meetingId);
		log.info(null);
		//send emails to task owners
		sendEmailsToTaskOwners(taskList);
		log.info(null);
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
		taskRepository.deleteAllById(taskIds);
		isAllDeleted = true;		
		System.out.println(isAllDeleted);
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
		
		actionItemList.forEach(actionItem->{
			 int i=0;
	    	 String singleActionItem = actionItem.getActionItemTitle();
	    	 i=i+1;
	    	 actionItemBuilder.append(i+". "+singleActionItem+"\r\n");
	      });
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
		System.out.println("emailId to send mom Email:"+emailArrayList);
		String subject ="Minutes of Meeting Email";
		
		//String body = "<b>Meeting Title:</b>"+""<br/>"
		//String OrganizeremailId = meeting.getOrganizerEmailId();
		String textBody ="Hi Team," +"\r\n"+"\r\n"+"please find the Below Meeting Details and Action Items"+"\r\n"+"\r\n"+
             "Meeting Title : " + meeting.getSubject() +"\r\n"+""+
             "Meeting Organizer : " + meeting.getOrganizerName()+"\r\n"+" "+
			 "Meeting Attendees : " + attendeeListBuilder+"\r\n"+ " "+
		     "Meeting StartDate : " + meeting.getStartDateTime()+"\r\n"+" "+
		     "Meeting EndDate : " + meeting.getEndDateTime()+"\r\n"+
		     "Meeting Action Items : "+actionItemBuilder+"\r\n"+" ";
		//emailService.sendMail(OrganizeremailId, subject, textBody, true);
	   emailService.sendMail(emailArrayList, subject, textBody,true);	
	}
	
	private void sendEmailsToTaskOwners(List<Task> taskList) {
		//send email to task owner
				taskList.forEach(task -> {
					new Thread(new Runnable() {
						@Override
						public void run() {
							String to = task.getTaskOwner();
							String subject = "Alert ! Task Assigned";
							
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
//							String body = "Hi , you have been assigned with  a task: Please see below"+"\r\n"+"\r\n"
//							+"Task : "+task.getTaskTitle()+"\r\n"
//							+"Start Date : "+task.getStartDate()+"\r\n"
//							+"End Date : "+task.getDueDate()+"\r\n"
//							+"Priority : "+task.getTaskPriority()+"\r\n"
//							+"Status : "+task.getStatus()+"\r\n"
//							+"Description: "+task.getTaskDescription();
							log.info("TaskServiceImpl.convertActionItemsToTasks() task email sent sucessfully");
							emailService.sendMail(to,subject, body,true);
						}
					}).start();
				});
	}

}
