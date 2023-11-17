package com.ikn.ums.meeting.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ikn.ums.meeting.dto.TaskCategoryDTO;
import com.ikn.ums.meeting.entity.TaskCategory;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.exception.TaskCatagoryTitleExistsException;
import com.ikn.ums.meeting.repository.TaskCategoryRepository;
import com.ikn.ums.meeting.service.TaskCategoryService;
import com.ikn.ums.meeting.utils.MeetingConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskCategoryServiceImpl implements TaskCategoryService {

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;
    
    @Autowired
    private ModelMapper mapper;
    
	@Override
	public TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO) {
		log.info("TaskCategoryServiceImpl.createTaskCategory() ENTERED");

		if (taskCategoryDTO == null) 
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_MSG);
		if (isTaskCategoryTitleExists(taskCategoryDTO))
			throw new TaskCatagoryTitleExistsException(ErrorCodeMessages.ERR_TASK_CATEGORY_TITLE_EXISTS_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_TITLE_EXISTS_EXCEPTION_MSG);
		log.info("TaskCategoryServiceImpl.createTaskCategory() is under execution...");
		taskCategoryDTO.setCreatedDateTime(LocalDateTime.now());
		taskCategoryDTO.setTaskCategoryStatus(MeetingConstants.STATUS_ACTIVE);
		TaskCategory taskCategory = new TaskCategory();
		mapper.map(taskCategoryDTO, taskCategory);
		TaskCategory savedTaskCategory = taskCategoryRepository.save(taskCategory);
		TaskCategoryDTO savedTaskCategoryDTO = new TaskCategoryDTO();
		mapper.map(savedTaskCategory, savedTaskCategory);
		log.info("TaskCategoryServiceImpl.createTaskCategory() executed successfully");
		return savedTaskCategoryDTO;
	}

	@Transactional 
	@Override
	public TaskCategoryDTO updateTaskCategory(TaskCategoryDTO taskCategoryDTO) {
		log.info("TaskCategoryServiceImpl.updateTaskCategory() entered with args - taskCategoryDTO");
		if(taskCategoryDTO == null || taskCategoryDTO.equals(null)) {
			log.info("TaskCategoryServiceImpl.updateTaskCategory() EntityNotFoundException : user object is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_CODE, 
					ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_MSG);
		}
		log.info("TaskCategoryServiceImpl.updateTaskCategory() is under execution.");
		Optional<TaskCategory> optTaskCategory = taskCategoryRepository.findById(taskCategoryDTO.getTaskCategoryId());
		TaskCategory dbTaskCategory = null;
		if(optTaskCategory.isPresent()) {
			dbTaskCategory = optTaskCategory.get();
		}
		//set modified date time
		dbTaskCategory.setModifiedDateTime(LocalDateTime.now());
		TaskCategory updatedTaskCategory =  taskCategoryRepository.save(dbTaskCategory);
		TaskCategoryDTO updatedTaskCategoryDTO = new TaskCategoryDTO();
		mapper.map(updatedTaskCategory, taskCategoryDTO);
		log.info("TaskCategoryServiceImpl.updateTaskCategory() executed successfully.");
		return updatedTaskCategoryDTO;
	}

	@Transactional 
	@Override
	public boolean deleteTaskCategoryById(Long taskCategoryId) {
		log.info("TaskCategoryServiceImpl.deleteTaskCategory() ENTERED ");
		boolean deleteTaskCategory = false;
		if (taskCategoryId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_MSG);

		log.info("TaskCategoryServiceImpl.deleteTaskCategory() is under execution...");
		Optional<TaskCategory> optTaskCategory = taskCategoryRepository.findById(taskCategoryId);
		
		if ( !optTaskCategory.isPresent() || optTaskCategory == null ) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_MSG);
		} else {
			TaskCategory taskCategory = null;
			taskCategory = optTaskCategory.get();
			taskCategory.setTaskCategoryStatus(MeetingConstants.STATUS_IN_ACTIVE);
			TaskCategoryDTO taskCategoryDTO = new TaskCategoryDTO();
			mapper.map(taskCategory, taskCategoryDTO);
			updateTaskCategory(taskCategoryDTO);
			deleteTaskCategory = true;
			log.info("TaskCategoryServiceImpl.deleteTaskCategory() executed successfully");
		}
		return deleteTaskCategory;
	}

	@Transactional 
	@Override
	public boolean deleteSelectedTaskCatgoriesByIds(List<Long> taskCategoriesIds) {
		log.info("TaskCategoryServiceImpl.deleteTaskCategoryById() ENTERED : taskCategoriesIds Size : " + taskCategoriesIds.size() );
		boolean deleteSelectedTaskCatgoriesByIds = false;
		if ( taskCategoriesIds.size() <= 0 )
			throw new EmptyListException(ErrorCodeMessages.ERR_TASK_CATEGORY_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_LIST_IS_EMPTY_MSG);		
			
			List<TaskCategory> taskCategoryDTOList = taskCategoryRepository.findAllById(taskCategoriesIds);
			if(taskCategoryDTOList.size() > 0) {
				taskCategoryDTOList.forEach(taskCategoryDTO -> {
					taskCategoryDTO.setTaskCategoryStatus(MeetingConstants.STATUS_IN_ACTIVE);
				});
				deleteSelectedTaskCatgoriesByIds = true;
			}
		return deleteSelectedTaskCatgoriesByIds;
	}
	
	@Override
	public TaskCategoryDTO getTaskCategoryById(Long taskCategoryId) {
		log.info("TaskCategoryServiceImpl.getTaskCategoryById() ENTERED : taskCategoryId : " + taskCategoryId);
		log.info("TaskCategoryServiceImpl.getTaskCategoryById() is under execution...");
		if (taskCategoryId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ID_IS_EMPTY_MSG);
		TaskCategory taskCategory  = taskCategoryRepository.findById(taskCategoryId).get(); //return TaskCategoryDTO
		TaskCategoryDTO taskCategoryDTO = new TaskCategoryDTO();
		mapper.map(taskCategory, taskCategoryDTO);
		log.info("TaskCategoryServiceImpl.getTaskCategoryById() executed successfully");
		return taskCategoryDTO;
	}

	@Override
	public List<TaskCategoryDTO> getAllTaskCategories() {
		log.info("TaskCategoryServiceImpl.getAllTaskCategories() ENTERED.");
		List<TaskCategory> taskCategoryList = null;
		log.info("TaskCategoryServiceImpl.getAllTaskCategories() is under execution...");
		taskCategoryList = taskCategoryRepository.findAllTaskCategories(MeetingConstants.STATUS_ACTIVE);
		if ( taskCategoryList == null || taskCategoryList.isEmpty() || taskCategoryList.size() == 0 )
			throw new EmptyListException(ErrorCodeMessages.ERR_TASK_CATEGORY_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_LIST_IS_EMPTY_MSG);
		log.info("getAllTaskCategories() : Total Task Categories Count : " + taskCategoryList.size());
		List<TaskCategoryDTO> taskCategoryDTOList = new ArrayList<>();
		mapper.map(taskCategoryDTOList, taskCategoryDTOList);
		log.info("getAllTaskCategories() executed successfully");
		return taskCategoryDTOList;
	}

	public boolean isTaskCategoryTitleExists(TaskCategoryDTO taskCategoryDTO) {
		log.info("TaskCategoryServiceImpl.isTaskCategoryExists() ENTERED");
		boolean isTaskCategoryTitleExists = false;
		if (taskCategoryDTO == null) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_TASK_CATEGORY_ENTITY_IS_NULL_MSG);
		} else {
			log.info("TaskCategoryServiceImpl  : Task Category Id : " + taskCategoryDTO.getTaskCategoryId() + " || Task Category Title : " + taskCategoryDTO.getTaskCategoryTitle());
			Optional<TaskCategory> optTaskCategory = taskCategoryRepository.findByTaskCategoryTitle(taskCategoryDTO.getTaskCategoryTitle());
			isTaskCategoryTitleExists = optTaskCategory.isPresent();
			log.info("TaskCategoryServiceImpl  : isTaskCategoryTitleExists : " + isTaskCategoryTitleExists);
		}
		log.info("TaskCategoryServiceImpl.isTaskCategoryTitleExists() executed successfully" );
		return isTaskCategoryTitleExists;
	}

}
