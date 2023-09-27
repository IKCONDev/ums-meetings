package com.ikn.ums.meeting.controller;

import java.util.Arrays;
import java.util.List;
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

import com.ikn.ums.meeting.VO.ActionItemVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

	
	@Autowired
	private TaskService taskService;

	@PostMapping("/save")
	public ResponseEntity<?> createTasks(@RequestBody Task task){
		Task res = taskService.SaveTasks(task);
		try {
			return new ResponseEntity<>(res, HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/convert-task")
	public ResponseEntity<?> autoTaskCreation(@RequestBody List<ActionItem> actionItemList){
		System.out.println("TaskController.autoTaskCreation() entered");
		try {
			List<Task> task= taskService.convertToTask(actionItemList);
			System.out.println(task);
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> fetchAllTaskDetails(){
		
		try {
			 
			return new ResponseEntity<>(taskService.fetchTaskDetails(), HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/get/{email}")
	public ResponseEntity<?> fetchUserTasks(@PathVariable String email){
		
		try {
			List<Task> task = taskService.fetchUserTasks(email);
			return new ResponseEntity<>(task,HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> fetchSingleTask(@PathVariable Integer id){
		try {
			
			return new ResponseEntity<>(taskService.singleTaskDetails(id),HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
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
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteTaskDetails(@PathVariable("id") Integer id){
		try {
			
			return new ResponseEntity<>(taskService.deleteTaskDetails(id),HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
	}
	
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
	


}
