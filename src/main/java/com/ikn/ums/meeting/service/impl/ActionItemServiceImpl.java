package com.ikn.ums.meeting.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.BusinessException;
import com.ikn.ums.meeting.repository.ActionItemRepository;
import com.ikn.ums.meeting.service.TaskService;

@Service
public class ActionItemServiceImpl implements com.ikn.ums.meeting.service.ActionItemService {

	@Autowired
	private ActionItemRepository actionItemRepository;
	
	@Autowired
	private TaskService taskService;
	
	@Override
	@Transactional
	public ActionItem createActionItem(ActionItem actions) {
		// TODO Auto-generated method stub
		//ActionsEntity entity =repo.saveAll(actionmodel);
		//ModelMapper mapper =new ModelMapper();
		//mapper.map(entity,ActionsDto.class);
		
		return actionItemRepository.save(actions);
	}

	@Override
	public List<ActionItem> fetchActionItemList() {
		// TODO Auto-generated method stub
		List<ActionItem>actions =actionItemRepository.findAll();
		return actions;
	}

	@Override
	public Optional<ActionItem> getSingleActionItem(Integer id) {
		// TODO Auto-generated method stub
		Optional<ActionItem> actionItem = actionItemRepository.findById(id);
		return actionItem;
	}

	@Override
	public ActionItem updateActionItem(ActionItem action) {
		// TODO Auto-generated method stub
		ActionItem existingAction = actionItemRepository.findById(action.getActionItemId()).get();
		existingAction.setMeetingId(action.getMeetingId());
		existingAction.setActionItemTitle(action.getActionItemTitle());
		existingAction.setActionItemDescription(action.getActionItemDescription());
		existingAction.setActionPriority(action.getActionPriority());
		existingAction.setActionStatus(action.getActionStatus());
		existingAction.setStartDate(action.getStartDate());
		existingAction.setEndDate(action.getEndDate());
		ActionItem updateAction= actionItemRepository.save(existingAction);
		return updateAction;
	}

	@Override
	@Transactional
	public Integer deleteActionItem(Integer actionId) {
		// TODO Auto-generated method stub
	
		actionItemRepository.deleteById(actionId);
		return 1;
		
	}

	//fetches action items based on event id
	@Override
	public ActionItemListVO fetchActionItemsOfEvent(Integer eventId) {
		ActionItemListVO acItemsVO = new ActionItemListVO();
		List<ActionItem> actionItemsList = actionItemRepository.findActionItemsByEventId(eventId);
		acItemsVO.setActionItemList(actionItemsList);
		return acItemsVO;
	}

	@Override
	public ActionItemListVO fetchActionItems() {
		ActionItemListVO acItemsVO = new ActionItemListVO();
		List<ActionItem> actionItemsList = actionItemRepository.findAll();
		acItemsVO.setActionItemList(actionItemsList);
		return acItemsVO;
	}

	@Override
	public boolean deleteAllActionItemsById(List<Integer> ids) {
		boolean isAllDeleted = false;
		try {
			actionItemRepository.deleteAllById(ids);
			isAllDeleted = true;
		}catch (Exception e) {
			isAllDeleted = false;
		}
		System.out.println(isAllDeleted);
		return isAllDeleted;
	}
	
	@Transactional
	@Override
	public List<Task> sendToTasks(List<ActionItem> actionItemList) {
		try {
			System.out.println("ActionsServiceImpl.sendToTasks() entered "+actionItemList);
			
			/*
			String URL="http://localhost:8012/task/convert-task";
			HttpEntity<?> httpEntity = new HttpEntity<>(actionItems,null);
			
			ResponseEntity<List<TaskVO>> responseEntity = restTemplate.exchange(
			        URL, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<List<TaskVO>>() {});
			List<TaskVO> taskList = responseEntity.getBody();
			System.out.println(responseEntity.getBody());
			*/
			List<Task> taskList = taskService.convertToTask(actionItemList);
			//change the action item status to Converted
			actionItemList.stream().forEach(action ->{
				action.setActionStatus("Converted");
			});
			
			//updates only the status of action item in db
            actionItemRepository.saveAll(actionItemList);
			return taskList;
		}catch (Exception e) {
			throw new BusinessException("error code", "Service Exception");
		}
	}

	@Override
	public boolean generateActions(List<ActionItem> actionItems) {
		// TODO Auto-generated method stub
		List<ActionItem> actionItemList = new ArrayList<>();
		actionItems.forEach(actionItem->{
			ActionItem newActionItem = new ActionItem();
			newActionItem.setActionItemTitle(actionItem.getActionItemTitle());
			newActionItem.setActionItemDescription(actionItem.getActionItemDescription());
			newActionItem.setStartDate(actionItem.getStartDate());
			newActionItem.setActionPriority(actionItem.getActionPriority());
			newActionItem.setActionStatus(actionItem.getActionStatus());
			newActionItem.setEndDate(actionItem.getEndDate());
			newActionItem.setMeetingId(actionItem.getMeetingId());
			newActionItem.setEmailId(actionItem.getEmailId()); //UserId details
			actionItemList.add(newActionItem);
			
		});
		actionItemRepository.saveAll(actionItemList);
		System.out.println(actionItemList);
		return true;
	}

	@Override
	public List<ActionItem> fetchActionItemsByEmail(String emailId) {
		// TODO Auto-generated method stub
	    System.out.println(emailId);
		List<ActionItem> list =actionItemRepository.findByUserId(emailId);
		System.out.println(list);
		return list;
	}
	
}
