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
	private TaskRepository repo;

	@Override
	@Transactional
	public Task SaveTasks(Task task) {
		
		return repo.save(task);
	}

	@Override
	public List<Task> fetchTaskDetails() {
		// TODO Auto-generated method stub
		List<Task> task= repo.findAll();
		return task;
	}

	@Override
	public Task updateTask(Task task) {
		// TODO Auto-generated method stub
	   
	    Task updatetask = repo.findById(task.getId()).get();
	    updatetask.setTaskTitle(task.getTaskTitle());
	    updatetask.setTaskDescription(task.getTaskDescription());
	    updatetask.setStartDate(task.getStartDate());
	    updatetask.setDueDate(task.getDueDate());
	    updatetask.setTaskPriority(task.getTaskPriority());
	    updatetask.setOrganizer(task.getOrganizer());
	    updatetask.setActionItemId(task.getActionItemId());
	    updatetask.setAssignee(task.getAssignee());
	    updatetask.setStatus(task.getStatus());
	    Task modifiedtask = repo.save(updatetask );
	    return modifiedtask;
		
	}

	@Override
	public Optional<Task> singleTaskDetails(Integer id) {
		// TODO Auto-generated method stub
		Optional<Task>   task = repo.findById(id);
		return task;
	}

	@Override
	@Transactional
	public Integer deleteTaskDetails(Integer taskId) {
		// TODO Auto-generated method stub
		repo.deleteById(taskId);
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
		
	    return repo.saveAll(taskList);
	}
    
	@Override
	public boolean deleteAllTasksById(List<Integer> ids) {
		boolean isAllDeleted = false;
		try {
			repo.deleteAllById(ids);
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
		List<Task> list = repo.findByUserId(email);
		return list;
	}
	


}
