package com.ikn.ums.meeting.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.dto.TaskCategoryDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/taskCategory")
public class TaskCategoryController {
	
	TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO) {
	return null;	
	}
	
	TaskCategoryDTO updateTaskCategory(TaskCategoryDTO taskCategoryDTO) {
		return null;
	}
	boolean deleteTaskCategory(List<TaskCategoryDTO> taskCategoryDTOList) {
		return true;
	}
	TaskCategoryDTO getTaskCategoryById(Long taskCategoryId) {
		return null;
	}
	List<TaskCategoryDTO> getAllTaskCategories() {
		return null;
	}
	boolean checkIfTaskCategoryExists(Long taskCategoryId) {
		return true;
	}
	
	
}
