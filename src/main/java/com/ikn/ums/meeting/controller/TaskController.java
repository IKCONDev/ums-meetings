package com.ikn.ums.meeting.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.TaskStatusModel;
import com.ikn.ums.meeting.service.TaskService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

	
	@Autowired
	private TaskService taskService;
	
  /**
  * 
  * @param task
  * @return
  */
	@PostMapping("/save")
	public ResponseEntity<?> createTask(@RequestBody Task task){
		log.info("TaskController.createTasks() entered with args : task");
	    if(task == null) {
	    	log.info("TaskController.createTasks() Empty Input Exception : tasklist is Empty" );
	    	throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
	    			ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
	    }
		try {
			log.info("TaskController.createTasks() is under execution...");
			Task res = taskService.saveTask(task);
			log.info("TaskController.createTasks() is executed Successfully");
			return new ResponseEntity<>(res, HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			//return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			log.info("TaskController.createTask() exited with exception : Exception occured while saving task "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_SAVE_CODE,
					ErrorCodeMessages.ERR_MEETINS_TASKS_SAVE_MSG);
		}
	}
	
	/**
	 * 
	 * @param actionItemList
	 * @return
	 */
	/*
	@PostMapping("/convert-task")
	public ResponseEntity<?> autoTaskCreation(@RequestBody List<ActionItem> actionItemList, @RequestBody Long meetingId){
		System.out.println("TaskController.autoTaskCreation() entered");
		log.info("TaskController.autoTaskCreation() entered with args : actionItemList");
		if(actionItemList == null || actionItemList.size() < 1) {
			log.info("TaskContoller.autoTaskCreation() Empty List Exception : actionItemList is Empty");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		try {
			log.info("TaskController.autoTaskCreation() is under execution...");
			List<Task> task= taskService.convertActionItemsToTasks(actionItemList,meetingId);
			System.out.println(task);
			log.info("TaskController.autoTaskCreation() is executed successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			//return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			log.info("TaskController.autoTaskCreation() exited with exception : Exception occured while creating the task"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_CONVERTTASK_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_CONVERTTASK_MSG);
		}
	}
	*/
	
	/**
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public ResponseEntity<?> fetchAllTasks(){
		log.info("TaskController.fetchAllTasks() entered ");
		
		try {
			log.info("TaskController.fetchAllTasks() is under execution... ");
			List<Task> taskList = taskService.getTasks();
			log.info("TaskController.fetchAllTaskDetails() is executed Successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.info("TaskController.fetchAllTasks() exited with exception : Exception occured while getting the tasks:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_MSG);
		}
		
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	@GetMapping("/getall/{emailId}")
	public ResponseEntity<?> fetchTasksByUserId(@PathVariable String emailId){
		log.info("TaskController.fetchTasksByUserId() entered with args :" +emailId);
		if(emailId =="" || emailId==null) {
			log.info("TaskController.fetchTaskByUserId() Empty Input Exception : emailId is empty ");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("TaskController.fetchTasksByUserId() is under execution...");
			List<Task> task = taskService.getTasksByUserId(emailId);
			log.info("TaskController.fetchTasksByUserId() is executed Successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			log.info("TaskController.fetchTasksByUserId() is exited with exception : Exception occured while getting the tasks of the user "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_MSG);
		}
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> fetchTaskByTaskId(@PathVariable("id") Integer taskId){
		log.info("TaskController.fetchTaskById() entered with args:"+ taskId);
		if(taskId <1 || taskId== null) {
			log.info("TaskController.fetchTaskById() Empty Input Exception : taskId is Empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		try {
			log.info("TaskController.fetchTaskById() is under execution...");
			Optional<Task> optTask = taskService.getTaskById(taskId);
			Task task = optTask.get(); 
			log.info("TaskController.fetchTaskById() executed Successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.info("TaskController.fetchTasksById() exited with exception : Exception occured while getting the tasks :" +e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_MSG);
		}
		
	}
	
	/**
	 * 
	 * @param task
	 * @param id
	 * @return
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateTaskDetails(@RequestBody Task task,@PathVariable("id") Integer taskId){
		log.info("TaskController.updateTaskDetails() entered with args - taskId : " +taskId);
		if(taskId < 1|| taskId == null) {
			log.info("TaskController.updateTaskDetails() Empty Input Exception : taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		try {
			log.info("TaskController.updateTaskDetails() is under execution...");
			task.setTaskId(taskId);
			Task update = taskService.updateTask(task);
			log.info("TaskController.updateTaskDetails() is executed successfully");
			return new ResponseEntity<>(update, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.info("TaskController.updateTaskDetails() exited with exception : Exception occured while updating : " +e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_UPDATE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_UPDATE_MSG);	
		}
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteTaskByTaskId(@PathVariable("id") Integer id){
		log.info("TaskController.deleteTaskDetails() entered with args : " +id);
		if(id < 1 || id == null) {
			log.info("TaskController.deleteTaskDetails() Empty Input Exception : taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		try {
			log.info("TaskController.deleteTaskDetails() is under execution...");
			Integer result = taskService.deleteTaskById(id);
			log.info("TaskController.deleteTaskDetails() is executed Successfully");
			return new ResponseEntity<>(result,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.info("TaskController.deleteTaskDetails() exited with Exception : Exception occured while deleting the task " +e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_DELETE_MEESAGE);
		}	
	}
	
	/**
	 * 
	 * @param taskId
	 * @return
	 */
	@DeleteMapping("/deleteAll/{ids}")
	public ResponseEntity<?> deleteAllTasksById(@PathVariable("ids") String taskids){
		log.info("TaskController.deleteAllTasksById() entered with args : taskIds");
		if(taskids == "" || taskids == null) {
			log.info("TaskController.deleteAllTasksById() Empty Input Exceptio : taskId's is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		List<Integer> taskIds = null;
		if(taskids != "") {
			String[] idsFromUI = taskids.split(",");
			List<String> idsList =  Arrays.asList(idsFromUI);
			//convert string of ids to Integer ids
			taskIds = idsList.stream()
                     .map(s -> Integer.parseInt(s))
                     .collect(Collectors.toList());
		}
		try {
			log.info("TaskController.deleteAllTasksById() is under execution...");
			boolean isAllDeleted = taskService.deleteAllTasksById(taskIds);
			log.info("TaskController.deleteAllTasksBYId() executed Successfully");
			return new ResponseEntity<>(isAllDeleted, HttpStatus.OK);
		}catch (Exception e) {
			log.info("TaskController.deleteAllTasksById() exited with exception : Exception occured while deleting tasks :" 
					+e.getMessage()); 
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_DELETE_MEESAGE);
		}
	}
	
	@GetMapping("/assigned/{emailId}")
	public ResponseEntity<?> getAssignedTasksByUserId(@PathVariable String emailId){
		log.info("TaskController.getAssignedTasksByUserId() entered with args :" +emailId);
		if(emailId =="" || emailId==null) {
			log.info("TaskController.getAssignedTasksByUserId() Empty Input Exception : emailId is empty ");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("TaskController.getAssignedTasksByUserId() is under execution...");
			List<Task> task = taskService.getAssignedTaskListOfUser(emailId);
			log.info("TaskController.getAssignedTasksByUserId() is executed Successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			log.info("TaskController.getAssignedTasksByUserId() is exited with exception : Exception occured while getting the tasks of the user "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_MSG);
		}
		
	}
	
	
	/**
	 * 
	 * @param actionItemList
	 * @return
	 */
	/*
	@PostMapping("/convert-task/{meetingId}")
	public ResponseEntity<?> processActionItemsToTasks(@RequestBody List<ActionItem> actionItemList, @PathVariable Long meetingId) {
		log.info("ActionsController.processActionItemsToTasks() entered with args : actionItemsList");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info(
					"ActionItemController.processActionItemsToTasks() Empty List Exception : Action Items list is empty or null");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MSG);
		}
		try {
			log.info("ActionsController.processActionItemsToTasks() is under execution...");
			List<Task> taskList = taskService.convertActionItemsToTasks(actionItemList,meetingId);
			log.info("ActionsController.processActionItemsToTasks() executed successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionsController.processActionItemsToTasks() exited with exception : An Exception occurred while converting action items to tasks "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_MSG);
		}

	}
	*/
	
	@GetMapping("/organized/count/{userId}")
	public ResponseEntity<?> getOrganizedTasksCountByUserId(@PathVariable("userId") String emailId){
		Long count = taskService.getOrganizedTasksCountOfUser(emailId);
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
	
	@GetMapping("/assigned/count/{userId}")
	public ResponseEntity<?> getAssignedTasksCountByUserId(@PathVariable("userId") String emailId){
		Long count = taskService.getUserAssignedTasksCountOfUser(emailId);
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
	@GetMapping("/weekTaskCount")
	public ResponseEntity<?> getWeekTasks(@RequestParam("startdate")@DateTimeFormat(iso =ISO.DATE_TIME) LocalDateTime startDate ,
			@RequestParam("endDate")@DateTimeFormat(iso =ISO.DATE_TIME) LocalDateTime endDate){
		
		Long[] assignedTask=taskService.getTaskCountsByDayOfWeek(startDate, endDate);
		List<Long> inprogressTask=taskService.findInProgressTaskCountsByDayOfWeek(startDate, endDate);
		List<Long> completedTask=taskService.getCompletedTaskCountsByDayOfWeek(startDate, endDate);
		List<Object> obj = new LinkedList<>();
		obj.add(assignedTask);
		obj.add(inprogressTask);
		obj.add(completedTask);
		return new ResponseEntity<>(obj,HttpStatus.OK);
	}
}//class
