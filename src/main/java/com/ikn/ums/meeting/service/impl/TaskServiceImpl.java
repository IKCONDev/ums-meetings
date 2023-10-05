package com.ikn.ums.meeting.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.repository.TaskRepository;
import com.ikn.ums.meeting.service.TaskService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskServiceImpl implements  TaskService{
	
	@Autowired
	private TaskRepository taskRepository;

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
	public List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList) {
		log.info("TaskServiceImpl.convertActionItemsToTasks() entered with args - actionItemList");
		System.out.println(actionItemList);
		if(actionItemList == null || actionItemList.size() < 1)
		{
			log.info("TaskServiceImpl.convertActionItemsToTasks() Empty List Exception : Exception occured while converting "
					+ "Action Item to task");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		log.info("TaskServiceInmpl.convertActionItemsToTasks() is under execution...");
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
			//task.setStatus(actionitem.getActionStatus());
			task.setStatus("Yet to Start");
			taskList.add(task);
			System.out.println(taskList);
		
		});
		log.info("TaskServiceImpl.convertActionItemsToTasks is executed successfully");
	    return taskRepository.saveAll(taskList);
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
	
	


}
