package com.ikn.ums.meeting.service;

import java.util.List;

import com.ikn.ums.meeting.dto.TaskCategoryDTO;

public interface TaskCategoryService {

	TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO);
	TaskCategoryDTO updateTaskCategory(TaskCategoryDTO taskCategoryDTO);
	boolean deleteTaskCategoryById(Long taskCategoryId);
	boolean deleteSelectedTaskCatgoriesByIds(List<Long> ids);
	TaskCategoryDTO getTaskCategoryById(Long taskCategoryId);
	List<TaskCategoryDTO> getAllTaskCategories();

}
