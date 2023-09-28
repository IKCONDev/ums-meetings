package com.ikn.ums.meeting.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
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
	public ResponseEntity<?> createTasks(@RequestBody Task task){
		log.info("TaskController.createTasks() entered with args : task");
	    if(task == null) {
	    	log.info("TaskController.createTasks() task : is Empty" );
	    	throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
	    			ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
	    }
		try {
			log.info("TaskController.createTasks(): under execution...");
			Task res = taskService.saveTask(task);
			log.info("TaskController.createTasks(): executed Successfully");
			return new ResponseEntity<>(res, HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			//return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			log.info("An exception occued while saving the Task"+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_CONTROLLER_EXCEPTION_MESSAGE);
		}
	}
	
	/**
	 * 
	 * @param actionItemList
	 * @return
	 */
	@PostMapping("/convert-task")
	public ResponseEntity<?> autoTaskCreation(@RequestBody List<ActionItem> actionItemList){
		System.out.println("TaskController.autoTaskCreation() entered");
		log.info("TaskController.autoTaskCreation() entered with args : actionItemList");
		if(actionItemList == null) {
			log.info("TaskContoller.autoTaskCreation() actionItemList: is Empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
		try {
			log.info("TaskController.autoTaskCreation(): under execution...");
			List<Task> task= taskService.convertActionItemsToTasks(actionItemList);
			System.out.println(task);
			log.info("TaskController.autoTaskCreation(): executed successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			//return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			log.info("An exception occured while converting the actionItem to tasks"+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_CONTROLLER_EXCEPTION_MESSAGE);
			
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public ResponseEntity<?> fetchAllTaskDetails(){
		log.info("TaskController.fetchAllTaskDetails(): entered");
		
		try {
			log.info("TaskController.fetchAllTaskDetails() : under execution... ");
			List<Task> taskList = taskService.getTasks();
			log.info("TaskController.fetchAllTaskDetails() : executed Successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.info("An exception occured while fetching all task details:"+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_CONTROLLER_EXCEPTION_MESSAGE);
		}
		
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	@GetMapping("/get/{emailId}")
	public ResponseEntity<?> fetchUserTasks(@PathVariable String emailId){
		log.info("TaskController.fetchUserTasks() entered with args :"+emailId);
		if(emailId =="" || emailId==null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("TaskController.fetchUserTasks() : under execution...");
			List<Task> task = taskService.getTasksByUserId(emailId);
			log.info("TaskController.fetchUserTasks() : executed Successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			log.info("An exception occured while fetching user tasks :"+e.getMessage());
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> fetchSingleTask(@PathVariable("id") Integer taskId){
		log.info("TaskController.fetchSingleTask() entered with args:"+ taskId);
		if(taskId <1 || taskId== null) {
			log.info("TaskController.fetchSingleTask() taskId is Empty");
		}
		try {
			log.info("TaskController.fetchSingleTask(): under execution...");
			Optional<Task> optTask = taskService.getTaskById(taskId);
			Task task = optTask.get(); 
			return new ResponseEntity<>(task,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.info("An exception occured while getting task:"+e.getMessage());
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * 
	 * @param task
	 * @param id
	 * @return
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateTaskDetails(@RequestBody Task task,@PathVariable("id") Integer id){
		try {
			task.setTaskId(id);
			Task update = taskService.updateTask(task);
			return new ResponseEntity<>(update, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteTaskDetails(@PathVariable("id") Integer id){
		try {
			return new ResponseEntity<>(taskService.deleteTaskById(id),HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	/**
	 * 
	 * @param taskId
	 * @return
	 */
	@DeleteMapping("/deleteAll/{ids}")
	public ResponseEntity<?> deleteTasksById(@PathVariable("ids")String taskId){
		System.out.println(taskId);
		List<Integer> taskIds = null;
		if(taskId != "") {
			String[] idsFromUI = taskId.split(",");
			List<String> idsList =  Arrays.asList(idsFromUI);
			//convert string of ids to Integer ids
			taskIds = idsList.stream()
                     .map(s -> Integer.parseInt(s))
                     .collect(Collectors.toList());
		}
		try {
			boolean isAllDeleted = taskService.deleteAllTasksById(taskIds);
			return new ResponseEntity<>(isAllDeleted, HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>("error while deleting, please try later", HttpStatus.OK);
		}
	}
	
}//class
