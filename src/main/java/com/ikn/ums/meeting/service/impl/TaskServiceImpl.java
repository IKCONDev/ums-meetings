package com.ikn.ums.meeting.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.ikn.ums.meeting.VO.ActionItemVO;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.repository.TaskRepository;
import com.ikn.ums.meeting.service.TaskService;

public class TaskServiceImpl implements  TaskService{


	
	@Autowired
	private TaskRepository taskRepo;

	@Override
	@Transactional
	public Task SaveTasks(Task task) {
		
		return taskRepo.save(task);
	}

	@Override
	public List<Task> fetchTaskDetails() {
		// TODO Auto-generated method stub
		List<Task> task= taskRepo.findAll();
		return task;
	}

	@Override
	public Task updateTask(Task task) {
		// TODO Auto-generated method stub
	   
	    Task updatetask = taskRepo.findById(task.getId()).get();
	    updatetask.setTaskTitle(task.getTaskTitle());
	    updatetask.setTaskDescription(task.getTaskDescription());
	    updatetask.setStartDate(task.getStartDate());
	    updatetask.setDueDate(task.getDueDate());
	    updatetask.setTaskPriority(task.getTaskPriority());
	    updatetask.setOrganizer(task.getOrganizer());
	    updatetask.setActionItemId(task.getActionItemId());
	    updatetask.setAssignee(task.getAssignee());
	    updatetask.setStatus(task.getStatus());
	    Task modifiedtask = taskRepo.save(updatetask );
	    return modifiedtask;
		
	}

	@Override
	public Optional<Task> singleTaskDetails(Integer id) {
		// TODO Auto-generated method stub
		Optional<Task>   task = taskRepo.findById(id);
		return task;
	}

	@Override
	@Transactional
	public Integer deleteTaskDetails(Integer taskId) {
		// TODO Auto-generated method stub
		taskRepo.deleteById(taskId);
		return 1;

	}

	@Override
	@Transactional
	public List<Task> convertToTask(List<ActionItemVO> actionItemList) {
		// TODO Auto-generated method stub
		System.out.println(actionItemList);
		List<Task> taskList = new ArrayList<>();
		actionItemList.forEach(actionitem ->{
			Task task = new Task();
			task.setId(actionitem.getId());
			task.setTaskTitle(actionitem.getActionTitle());
			task.setActionItemId(actionitem.getId());
			task.setAssignee(actionitem.getActionOwner());
			task.setStartDate(actionitem.getStartDate());
			task.setDueDate(actionitem.getEndDate());
			task.setTaskDescription(actionitem.getDescription());
			task.setTaskPriority(actionitem.getActionPriority());
			task.setUserId(actionitem.getUserId());
			//task.setStatus(actionitem.getActionStatus());
			task.setStatus("Yet to Start");
			taskList.add(task);
			System.out.println(taskList);
		
		});
		
	    return taskRepo.saveAll(taskList);
	}
    
	@Override
	public boolean deleteAllTasksById(List<Integer> ids) {
		boolean isAllDeleted = false;
		try {
			taskRepo.deleteAllById(ids);
			isAllDeleted = true;
		}catch (Exception e) {
			isAllDeleted = false;
		}
		System.out.println(isAllDeleted);
		return isAllDeleted;
	}

	@Override
	public List<Task> fetchUserTasks(String email) {
		// TODO Auto-generated method stub
		List<Task> list = taskRepo.findByUserId(email);
		return list;
	}
	


}
