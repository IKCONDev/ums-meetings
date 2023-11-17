package com.ikn.ums.meeting.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.dto.TaskCategoryDTO;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.exception.TaskCatagoryTitleExistsException;
import com.ikn.ums.meeting.utils.MeetingConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/taskCategory")
public class TaskCategoryController {
	
//	TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO) {
//	return null;	
//	}
//	
//	TaskCategoryDTO updateTaskCategory(TaskCategoryDTO taskCategoryDTO) {
//		return null;
//	}
//	boolean deleteTaskCategory(List<TaskCategoryDTO> taskCategoryDTOList) {
//		return true;
//	}
//	TaskCategoryDTO getTaskCategoryById(Long taskCategoryId) {
//		return null;
//	}
//	List<TaskCategoryDTO> getAllTaskCategories() {
//		return null;
//	}
//	boolean checkIfTaskCategoryExists(Long taskCategoryId) {
//		return true;
//	}
//	
//	////----
//	
//	@Override
//	public TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO) {
//	}
//
//	@Transactional 
//	@Override
//	public TaskCategoryDTO updateTaskCategory(TaskCategoryDTO taskCategoryDTO) {
//	}
//
//	@Transactional 
//	@Override
//	public boolean deleteTaskCategoryById(Long taskCategoryId) {
//	}
//
//	@Transactional 
//	@Override
//	public boolean deleteSelectedTaskCatgoriesByIds(List<Long> taskCategoriesIds) {
//	}
//	
//	@Override
//	public TaskCategoryDTO getTaskCategoryById(Long taskCategoryId) {
//	}
//
//	@Override
//	public List<TaskCategoryDTO> getAllTaskCategories() {
//	}
//
//	public boolean isTaskCategoryTitleExists(TaskCategoryDTO taskCategoryDTO) {
//	}
	
}
