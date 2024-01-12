package com.ikn.ums.meeting.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	@PostMapping("/create")
	public ResponseEntity<Task> createTask(@RequestBody Task task){
		log.info("createTasks() entered with args : task");
	    if(task == null) {
	    	log.info("createTasks() Empty Input Exception : tasklist is Empty" );
	    	throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
	    			ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
	    }
		try {
			log.info("createTasks() is under execution...");
			Task res = taskService.saveTask(task);
			log.info("createTasks() is executed Successfully");
			return new ResponseEntity<>(res, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("createTask() exited with exception : Exception occured while saving task "+e.getMessage(), e);
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
	public ResponseEntity<List<Task>> fetchAllTasks(){
		log.info("fetchAllTasks() entered ");
		
		try {
			log.info("fetchAllTasks() is under execution... ");
			List<Task> taskList = taskService.getTasks();
			log.info("fetchAllTaskDetails() is executed Successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("fetchAllTasks() exited with exception : Exception occured while getting the tasks:"+ e.getMessage());
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
	public ResponseEntity<List<Task>> fetchTasksByUserId(@PathVariable String emailId,
			@RequestParam(required = false, defaultValue = "") String taskTitle,
			@RequestParam(required = false, defaultValue = "") String taskPriority,
			@RequestParam(required = false, defaultValue = "") String taskOrganizer,
			@RequestParam(required = false, defaultValue = "")String taskStartDate,
			@RequestParam(required = false, defaultValue = "") String taskEndDate){
		log.info("fetchTasksByUserId() entered with args :" +emailId);
		if(emailId =="" || emailId==null) {
			log.info("fetchTaskByUserId() Empty Input Exception : emailId is empty ");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			if (("null".equals(taskTitle) || "".equals(taskTitle)) &&
				    ("null".equals(taskPriority) || "".equals(taskPriority)) &&
				    ("null".equals(taskOrganizer) || "".equals(taskOrganizer)) &&
				    ("null".equals(taskStartDate) || "".equals(taskStartDate)) &&
				    ("null".equals(taskEndDate) || "".equals(taskEndDate))) {
				System.out.println(taskTitle+"---------");
				log.info("fetchTasksByUserId() is under execution without filters...");
				List<Task> taskList = taskService.getTasksByUserId(emailId);
				log.info("fetchTasksByUserId() is executed Successfully without filters");
				return new ResponseEntity<>(taskList,HttpStatus.OK);	
			} else {			
				log.info("fetchTasksByUserId() is under execution with filters...");
				List<Task> taskList = taskService.getFilteredTasks(taskTitle, taskPriority, taskOrganizer, taskStartDate, taskEndDate, emailId);
				log.info("fetchTasksByUserId() is executed Successfully with filters");
				return new ResponseEntity<>(taskList,HttpStatus.OK);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("fetchTasksByUserId() is exited with exception : Exception occured while getting the tasks of the user "+e.getMessage());
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
	public ResponseEntity<Task> fetchTaskByTaskId(@PathVariable("id") Integer taskId){
		log.info("fetchTaskById() entered with args:"+ taskId);
		if(taskId <1 || taskId== null) {
			log.info("fetchTaskById() Empty Input Exception : taskId is Empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		try {
			log.info("fetchTaskById() is under execution...");
			Optional<Task> optTask = taskService.getTaskById(taskId);
			Task task = optTask.get(); 
			log.info("fetchTaskById() executed Successfully");
			return new ResponseEntity<>(task,HttpStatus.OK);
		}catch (Exception e) {
			log.error("fetchTasksById() exited with exception : Exception occured while getting the tasks :" +e.getMessage());
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
	public ResponseEntity<Task> updateTaskDetails(@RequestBody Task task,@PathVariable("id") Integer taskId){
		log.info("updateTaskDetails() entered with args - taskId : " +taskId);
		if(taskId < 1|| taskId == null) {
			log.info("updateTaskDetails() Empty Input Exception : taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		try {
			log.info("updateTaskDetails() is under execution...");
			task.setTaskId(taskId);
			Task update = taskService.updateTask(task);
			log.info("updateTaskDetails() is executed successfully");
			return new ResponseEntity<>(update, HttpStatus.OK);
		}catch (Exception e) {
			log.error("updateTaskDetails() exited with exception : Exception occured while updating : " +e.getMessage(), e);
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
	public ResponseEntity<Integer> deleteTaskByTaskId(@PathVariable("id") Integer id){
		log.info("deleteTaskDetails() entered with args : " +id);
		if(id < 1 || id == null) {
			log.info("deleteTaskDetails() Empty Input Exception : taskId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_ID_EMPTY_MEESAGE);
		}
		try {
			log.info("deleteTaskDetails() is under execution...");
			Integer result = taskService.deleteTaskById(id);
			log.info("deleteTaskDetails() is executed Successfully");
			return new ResponseEntity<>(result,HttpStatus.OK);
		}catch (Exception e) {
			log.error("deleteTaskDetails() exited with Exception : Exception occured while deleting the task " +e.getMessage(), e);
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
	public ResponseEntity<Boolean> deleteAllTasksById(@PathVariable("ids") String taskids){
		log.info("deleteAllTasksById() entered with args : taskIds");
		if(taskids == "" || taskids == null) {
			log.info("deleteAllTasksById() Empty Input Exceptio : taskId's is empty");
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
			log.info("deleteAllTasksById() is under execution...");
			boolean isAllDeleted = taskService.deleteAllTasksById(taskIds);
			log.info("deleteAllTasksBYId() executed Successfully");
			return new ResponseEntity<>(isAllDeleted, HttpStatus.OK);
		}catch (Exception e) {
			log.error("deleteAllTasksById() exited with exception : Exception occured while deleting tasks :" 
					+e.getMessage(), e); 
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_DELETE_MEESAGE);
		}
	}
	
	@GetMapping("/assigned/{emailId}")
	public ResponseEntity<List<Task>> getAssignedTasksByUserId(@PathVariable String emailId,
			@RequestParam(required = false, defaultValue = "") String taskTitle,
			@RequestParam(required = false, defaultValue = "") String taskPriority,
			@RequestParam(required = false, defaultValue = "")String taskStartDate,
			@RequestParam(required = false, defaultValue = "") String taskEndDate){
		log.info("getAssignedTasksByUserId() entered with args :" +emailId);
		if(emailId =="" || emailId==null) {
			log.info("getAssignedTasksByUserId() Empty Input Exception : emailId is empty ");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			if (("null".equals(taskTitle) || "".equals(taskTitle)) &&
				    ("null".equals(taskPriority) || "".equals(taskPriority)) &&
				    ("null".equals(taskStartDate) || "".equals(taskStartDate)) &&
				    ("null".equals(taskEndDate) || "".equals(taskEndDate))) {
				log.info("getAssignedTasksByUserId() is under execution without filters...");
				List<Task> task = taskService.getAssignedTaskListOfUser(emailId);
				log.info("getAssignedTasksByUserId() is executed Successfully");
				return new ResponseEntity<>(task,HttpStatus.OK);
			} else {			
				log.info("getAssignedTasksByUserId() is under execution with filters...");
				List<Task> taskList = taskService.getFilteredAssignedTasks(taskTitle, taskPriority, taskStartDate, taskEndDate, emailId);
				log.info("getAssignedTasksByUserId() is executed Successfully with filters");
				return new ResponseEntity<>(taskList,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			log.error("getAssignedTasksByUserId() is exited with exception : "
					+ "Exception occured while getting the tasks of the user "+e.getMessage(), e);
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
	public ResponseEntity<Long> getOrganizedTasksCountByUserId(@PathVariable("userId") String emailId){
		Long count = taskService.getOrganizedTasksCountOfUser(emailId);
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
	
	@GetMapping("/assigned/count/{userId}")
	public ResponseEntity<Long> getAssignedTasksCountByUserId(@PathVariable("userId") String emailId){
		Long count = taskService.getUserAssignedTasksCountOfUser(emailId);
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
	@GetMapping("/weekTaskCount")
	public ResponseEntity<List<Object>> getWeekTasks(@RequestParam("startdate") String startDate ,
			@RequestParam("endDate") String endDate,String emailId){
		LocalDate startdate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate enddate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		
		List<Long> assignedTask=taskService.getTaskCountsByDayOfWeek(startdate, enddate,emailId);
		List<Long> yetToStartTask=taskService.getYetToStartTaskCountsByDayOfWeek(startdate, enddate,emailId);
		List<Long> inprogressTask=taskService.findInProgressTaskCountsByDayOfWeek(startdate, enddate,emailId);
		List<Long> completedTask=taskService.getCompletedTaskCountsByDayOfWeek(startdate, enddate,emailId);
		List<Object> obj = new LinkedList<>();
		obj.add(assignedTask);
		obj.add(yetToStartTask);
		obj.add(inprogressTask);
		obj.add(completedTask);
		return new ResponseEntity<>(obj,HttpStatus.OK);
	}
	@GetMapping("/TaskCountForYear")
	public ResponseEntity<List<Object>> getTaskCountForYear(@RequestParam("startdate") String startDate ,
			@RequestParam("endDate") String endDate,@RequestParam String emailId){
		LocalDate startdate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate enddate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		
		List<Long> assignedTaskForYear=taskService.findTaskCountsByMonth(startdate, enddate, emailId);
		List<Long> YetToSartTaskForYear= taskService.findYetToStartTaskCountsByMonth(startdate, enddate, emailId);
		List<Long> inprogressTaskForYear= taskService.findInprogressTaskCountsByMonth(startdate, enddate, emailId);
		List<Long> completedTaskCountForYear= taskService.findCompletedTaskCountsByMonth(startdate, enddate, emailId);
		
		List<Object> totalTaskStatusForYear= new LinkedList<>();
		totalTaskStatusForYear.add(assignedTaskForYear);
		totalTaskStatusForYear.add(YetToSartTaskForYear);
		totalTaskStatusForYear.add(inprogressTaskForYear);
		totalTaskStatusForYear.add(completedTaskCountForYear);
		return new ResponseEntity<>(totalTaskStatusForYear,HttpStatus.OK);
		
	}
	
	@GetMapping("department/{departmentId}")
	public ResponseEntity<List<Task>> getTaskListByDepartment(@PathVariable Long departmentId){
		log.info("getTaskListByDepartment() is entered with args: taskstatus");
		if(departmentId == 0 || departmentId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_MSG);
		}
		log.info("getTaskListByDepartment() is under execution... ");
		List<Task> taskList = taskService.getTasksByDepartment(departmentId);
		System.out.println(taskList);
		log.info("getTaskListByDepartment() executed successfully");
		return new ResponseEntity<>(taskList, HttpStatus.OK);
	}
	
	@GetMapping("priority/{taskPriority}")
	public ResponseEntity<List<Task>> getTaskListByPriority(@PathVariable String taskPriority){
		log.info("getTaskListByPriority() is entered with args: taskstatus");
		if(taskPriority == "" || taskPriority == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_MSG);
		}
		log.info("getTaskListByPriority() is under execution... ");
		List<Task> taskList = taskService.getTasksByTaskPriority(taskPriority);
		log.info("getTaskListByPriority() executed Successfully ");
		return new ResponseEntity<>(taskList, HttpStatus.OK);
	}
	
	@GetMapping("status/{taskStatus}")
	public ResponseEntity<List<Task>> getTaskListByTaskStatus(@PathVariable String taskStatus){
		log.info("getTaskListByTaskStatus() is entered with args: taskstatus");
		if(taskStatus == "" || taskStatus == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_TASKS_DEPTID_EMPTY_MSG);
		}
		log.info("getTaskListByTaskStatus() is under execution... ");
		List<Task> taskList = taskService.getTasksByTaskStatus(taskStatus);
		System.out.println(taskList);
		log.info("getTaskListByTaskStatus() executed Successfully ");
		return new ResponseEntity<>(taskList, HttpStatus.OK);
	}
	
	@GetMapping("/aged/{dateTime}")
	public ResponseEntity<List<Task>> getAgedTasksList(@PathVariable String dateTime){
		log.info("getAgedTasksList() is entered with args: dateTime");
		LocalDate currentDateTime = LocalDate.parse(dateTime);
		log.info("getAgedTasksList() is under execution... ");
		List<Task> taskList = taskService.getAgedTasks(currentDateTime);
		log.info("getAgedTasksList() executed Successfully ");
		return new ResponseEntity<>(taskList, HttpStatus.OK);
	}
	
	@GetMapping("/allForYear/{startDate}/{endDate}")
	public ResponseEntity<List<Long>> fetchAllTasksforYear(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,@PathVariable  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate){
		log.info("TaskController.fetchAllTasksforYear() entered ");
		System.out.println(startDate+" "+endDate);
		try { 
			log.info("fetchAllTasksforYear() is under execution... ");
			List<Long> taskList = taskService.getTasksBetweenStartDateAndEndDate(startDate,endDate);
			log.info("fetchAllTaskDetailsfor year () is executed Successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("fetchAllTasks() exited with exception : Exception occured while getting the tasks:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_GET_MSG);
		}
	
	}
	@GetMapping("/department-tasks")
	public ResponseEntity<List<Object[]>> getAllTasksByDepartment(){
		log.info("getAllTasksByDepartment() entered ");
		try { 
			log.info("getAllTasksByDepartment() is under execution... ");
			List<Object[]> taskList = taskService.getAllTasksByDepartment();
			log.info("TaskController.getAllTasksByDepartment() is executed Successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getAllTasksByDepartment() exited with exception : Exception occured while getting the tasks:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
	
	}
	@GetMapping("/taskCategory/{taskCategoryId}")
	public ResponseEntity<List<Task>> getAllTasksByTaskCategoryId(@PathVariable("taskCategoryId") Long taskCategoryId){
		log.info("getAllTasksByTaskCategoryId() entered ");
	
		try { 
			log.info("getAllTasksByTaskCategoryId() is under execution... ");
			List<Task> taskList = taskService.getTasksByCategoryId(taskCategoryId);
			log.info("getAllTasksByTaskCategoryId() is executed Successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getAllTasksByTaskCategoryId() exited with exception : Exception occured while getting the tasks:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
	
	}
	@GetMapping("/taskCategory-count")
	public ResponseEntity<List<Object[]>> getAllTasksByCategoryCount(){
		log.info("getAllTasks() entered ");
		try { 
			log.info("getAllTasks() is under execution... ");
			List<Object[]> taskListCount = taskService.getAllTaskCategoryByCount();
			log.info("getAllTasks() is executed Successfully");
			return new ResponseEntity<>(taskListCount, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getAllTasks() exited with exception : Exception occured while getting the tasks:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_TASKS_LIST_EMPTY_MEESAGE);
		}
	
	}
	
}//class
