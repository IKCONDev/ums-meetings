package com.ikn.ums.meeting.controller;

import java.util.List;

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

import com.ikn.ums.meeting.dto.TaskCategoryDTO;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.exception.TaskCatagoryTitleExistsException;
import com.ikn.ums.meeting.service.TaskCategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/taskCategory")
public class TaskCategoryController {

	@Autowired
	private TaskCategoryService taskCategoryService;
	
	@PostMapping("/create")
	public ResponseEntity<TaskCategoryDTO> createTaskCategory(@RequestBody TaskCategoryDTO	taskCategoryDTO) {
		
		log.info("createTaskCategory() entered ");
		if (taskCategoryDTO == null || taskCategoryDTO.equals(null)) {
			log.info("TaskCategory Entity Not Found Exception has encountered while creating TaskCategory.");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("createTaskCategory() is under execution.");

			TaskCategoryDTO createdTaskCategoryDTO = taskCategoryService.createTaskCategory(taskCategoryDTO);
			log.info("createTaskCategory() executed successfully.");
			return new ResponseEntity<>(createdTaskCategoryDTO, HttpStatus.CREATED);
		} catch (EntityNotFoundException | TaskCatagoryTitleExistsException taskCatagoryTitleExistsException) {
			log.error("createTaskCategory() Business Exception has encountered while creating Task Category. "
		               + taskCatagoryTitleExistsException.getMessage(),taskCatagoryTitleExistsException);
			throw taskCatagoryTitleExistsException;
		} catch (Exception e) {
			log.error("createTaskCategory() General Exception has encountered while creating Task Category. " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_TASK_CATEGORY_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_CREATE_UNSUCCESS_MSG);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<TaskCategoryDTO> updateTaskCategory(@RequestBody TaskCategoryDTO taskCategoryDTO) {
		log.info("updateRole() is entered");
		if (taskCategoryDTO == null || taskCategoryDTO.equals(null)) {
			log.info("Entity Not Found Exception has encountered while updating Task Category.");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("updateRole() is under execution...");
			TaskCategoryDTO updatedTaskCategoryDTO = taskCategoryService.updateTaskCategory(taskCategoryDTO);
			log.info("updateRole() executed successfully.");
			return new ResponseEntity<>(updatedTaskCategoryDTO, HttpStatus.CREATED);
		}catch (EntityNotFoundException taskCategoryBusinessException) {
			log.error("updateRole() Business Exception has encountered while updating Task Category. " + taskCategoryBusinessException.getMessage(), 
					taskCategoryBusinessException);
			throw taskCategoryBusinessException;
		}catch (Exception e) {
			log.error("updateRole() General Exception has encountered while updating Task Category. " + e.getMessage(), e);
			throw  new ControllerException(e.getCause().toString(), e.getMessage());
		}
	}

	/*
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteTaskCategoryById(@PathVariable("id") Long taskCategoryId) {
		log.info("TaskCategoryController.deleteTaskCategoryById() ENTERED : taskCategoryId : " + taskCategoryId);
		if (taskCategoryId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_MSG);
		try {
			taskCategoryService.deleteTaskCategoryById(taskCategoryId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			log.info("TaskCategoryController.deleteTaskCategoryById() : Exception Occured while deleting Task Category !"
					+ e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_TASK_CATEGORY_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_DELETE_UNSUCCESS_MSG);
		}
		
	}
	*/
	
	@DeleteMapping("/delete/{ids}")
	public ResponseEntity<Boolean> deleteSelectedTaskCatgoriesByIds(@PathVariable("ids") List<Long> taskCategoryIds) {
		if (taskCategoryIds.equals(null) || taskCategoryIds == null || taskCategoryIds.size() <= 0 ) {
			log.info("deleteSelectedTaskCatgoriesByIds() entered with args - ids : roleIds size (): " + taskCategoryIds.size());
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_MSG);
		}
		try {
			log.info("deleteSelectedTaskCatgoriesByIds() is under execution...");
			Boolean isDeleted = taskCategoryService.deleteSelectedTaskCatgoriesByIds(taskCategoryIds);
			log.info("deleteSelectedTaskCatgoriesByIds() executed successfully");
			return new ResponseEntity<>(isDeleted, HttpStatus.OK);
		}catch (EmptyListException businessException) {
			log.error("deleteSelectedTaskCatgoriesByIds() exited with business exception : Exception occured while deleting the taskcategory"
					+ businessException.getMessage(), businessException);
			throw businessException;
		} 
		catch (Exception e) {
			log.error("deleteSelectedTaskCatgoriesByIds() exited with General exception : Exception occured while deleting the taskcategory"
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_TASK_CATEGORY_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_DELETE_UNSUCCESS_MSG);
		}
	}
	
	@GetMapping("/{taskCategoryId}")
	public ResponseEntity<TaskCategoryDTO> getTaskCategoryById(@PathVariable Long taskCategoryId) {
		
		if (taskCategoryId <= 0) 
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_MSG);
		try {
			log.info("getTaskCategoryById() is under execution : taskCategoryId : " + taskCategoryId);
			TaskCategoryDTO taskCategoryDTO = taskCategoryService.getTaskCategoryById(taskCategoryId);
			log.info("getTaskCategoryById() executed successfully");
			return new ResponseEntity<>(taskCategoryDTO, HttpStatus.OK);
			
		}catch (EmptyInputException businessException) {
			log.error("getTaskCategoryById() exited with business exception : Exception occured fetching task categories"
					+ businessException.getMessage(), businessException);
			throw businessException;
		}
		catch (Exception e) {
			log.error("getTaskCategoryById() exited with exception : Exception occured fetching task categories"
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_TASK_CATEGORY_GET_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_GET_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<TaskCategoryDTO>> getAllTaskCategories() {
		log.info("getAllTaskCategories() ENTERED.");
		try {
			log.info("getAllTaskCategories() is under execution...");
			List<TaskCategoryDTO> taskCategoryDTOList = taskCategoryService.getAllTaskCategories();
			log.info("getAllTaskCategories() executed successfully");
			return new ResponseEntity<>(taskCategoryDTOList, HttpStatus.OK);
		}catch (EmptyListException businessException) {
			throw businessException;
		} 
		catch (Exception e) {
			log.error("getAllTaskCategories() exited with exception : Exception occured fetching task categories list."
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_TASK_CATEGORY_GET_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_GET_UNSUCCESS_MSG);
		}
	}
	
}
