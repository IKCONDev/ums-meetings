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
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.repository.ActionItemRepository;
import com.ikn.ums.meeting.service.TaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ActionItemServiceImpl implements com.ikn.ums.meeting.service.ActionItemService {

	@Autowired
	private ActionItemRepository actionItemRepository;
	
	@Autowired
	private TaskService taskService;
	
	@Override
	@Transactional
	public ActionItem saveActionItem(ActionItem actionItem) {	
		log.info("ActionItemServiceImpl.saveActionItem() entered with args - actionItem object");
		if (actionItem == null) {
			log.info("ActionItemServiceImpl.saveActionItem() Empty Input Exception : Exception occured while saving actionItem");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_MESSAGE);
		}
		log.info("ActionItemServiceImpl.saveActionItem() is under execution...");
		ActionItem savedActionItem = actionItemRepository.save(actionItem);
		log.info("ActionItemServiceImpl.saveActionItem() executed successfully...");
		return savedActionItem;
	}
	
	@Transactional
	@Override
	public ActionItem updateActionItem(ActionItem actionItem) {
		log.info("ActionItemServiceImpl.updateActionItem() entered with args - actionItem");
		if (actionItem == null) {
			log.info(
					"ActionItemService.updateActionItem() Empty Input Exception : ActionItem object is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_MESSAGE);
		}
		log.info("ActionItemServiceImpl.updateActionItem() is under execution...");
		ActionItem dbActionItem = actionItemRepository.findById(actionItem.getActionItemId()).get();
		dbActionItem.setMeetingId(actionItem.getMeetingId());
		dbActionItem.setActionItemTitle(actionItem.getActionItemTitle());
		dbActionItem.setActionItemDescription(actionItem.getActionItemDescription());
		dbActionItem.setActionPriority(actionItem.getActionPriority());
		dbActionItem.setActionStatus(actionItem.getActionStatus());
		dbActionItem.setStartDate(actionItem.getStartDate());
		dbActionItem.setEndDate(actionItem.getEndDate());
		ActionItem updateAction= actionItemRepository.save(dbActionItem);
		log.info("ActionItemServiceImpl.updateActionItem() executed successfully...");
		return updateAction;
	}
	
	@Override
	@Transactional
	public Integer deleteActionItemById(Integer actionItemId) {
		log.info("ActionItemServiceImpl.deleteActionItemById() entered with args - actionItemId : "+actionItemId);
		if (actionItemId == null || actionItemId < 1) {
			log.info(
					"ActionItemService.deleteActionItemById() Empty Input Exception : Action Item Id is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MESSAGE);
		}
		log.info("ActionItemServiceImpl.deleteActionItemById() is under execution...");
		actionItemRepository.deleteById(actionItemId);
		log.info("ActionItemServiceImpl.deleteActionItemById() executed successfully");
		return 1;
	}
	
	@Override
	public boolean deleteAllActionItemsById(List<Integer> actionItemIds) {
		log.info("ActionItemServiceImpl.deleteAllActionItemsById() entered with args - actionItemIds");
		if(actionItemIds.size() == 0 || actionItemIds == null) {
			log.info(
					"ActionItemService.deleteAllActionItemsById() Empty List Exception : Action Item Ids List is empty.");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_IDLIST_EMPTY_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_IDLIST_EMPTY_MESSAGE);
		}
		log.info("ActionItemService.deleteAllActionItemsById() is under execution...");
		boolean isAllDeleted = false;
		actionItemRepository.deleteAllById(actionItemIds);
		isAllDeleted = true;
		log.info("ActionItemService.deleteAllActionItemsById() executed successfully");
		return isAllDeleted;
	}

	@Override
	public List<ActionItem> getActionItemList() {
		log.info("ActionItemServiceImpl.getActionItemList() entered");
		log.info("ActionItemServiceImpl.getActionItemList() is under execution...");
		List<ActionItem> actionItemList =actionItemRepository.findAll();
		log.info("ActionItemServiceImpl.getActionItemList() executed succesfully");
		return actionItemList;
	}

	@Override
	public Optional<ActionItem> getActionItemById(Integer actionItemId) {
		log.info("ActionItemServiceImpl.getActionItemById() entered with args - actionItemId : "+actionItemId);
		if (actionItemId == null || actionItemId < 0) {
			log.info(
					"ActionItemService.getActionItemById() Empty Input Exception : Action Item Id is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MESSAGE);
		}
		log.info("ActionItemServiceImpl.getActionItemById() is under execution...");
		Optional<ActionItem> actionItem = actionItemRepository.findById(actionItemId);
		log.info("ActionItemServiceImpl.getActionItemById() executed successfully.");
		return actionItem;
	}

	@Override
	public ActionItemListVO getActionItemsByMeetingId(Integer meetingId) {
		log.info("ActionItemServiceImpl.getActionItemsByMeetingId() entered with args - meetingId : "+meetingId);
		if(meetingId < 1 || meetingId == null) {
			log.info("ActionItemServiceImpl.getActionItemsByMeetingId() Empty Input Exception : Exception occured while"
					+ "fetching the ActionItems");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MESSAGE);
		}
		log.info("ActionItemServiceImpl.getActionItemsByMeetingId() is under execution...");
		ActionItemListVO acItemsVO = new ActionItemListVO();
		List<ActionItem> actionItemsList = actionItemRepository.findActionItemsByEventId(meetingId);
		acItemsVO.setActionItemList(actionItemsList);
		log.info("ActionItemServiceImpl.getActionItemsByMeetingId() executed succesfully");
		return acItemsVO;
	}

	@Override
	public ActionItemListVO getActionItems() {
		log.info("ActionItemServiceImpl.getActionItems() entered");
		ActionItemListVO acItemsVO = new ActionItemListVO();
		log.info("ActionItemServiceImpl.getActionItems() is under execution...");
		List<ActionItem> actionItemsList = actionItemRepository.findAll();
		acItemsVO.setActionItemList(actionItemsList);
		log.info("ActionItemServiceImpl.getActionItems() executed successfully");
		return acItemsVO;
	}
	
	@Transactional
	@Override
	public List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList) {
		log.info("ActionItemServiceImpl.convertActionItemsToTasks() entered with args : actionItemList");
		if(actionItemList == null || actionItemList.size()<1) {
			
		}
		log.info("ActionItemServiceImpl.convertActionItemsToTasks() is under execution...");
		List<Task> taskList = taskService.convertActionItemsToTasks(actionItemList);
		actionItemList.stream().forEach(action ->{
			action.setActionStatus("Converted");
		});
        actionItemRepository.saveAll(actionItemList);
        log.info("ActionItemServiceImpl.convertActionItemsToTasks() executed succesfully");
		return taskList;
	}

	@Override
	public boolean generateActionItems(List<ActionItem> actionItemList) {
		log.info("ActionItemServiceImpl.generateActionItems() entered with args - actionItemList");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info("ActionItemServiceImpl.generateActionItems() EmptyListException : Action Items list is empty or null");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MESSAGE);
		}
		log.info("ActionItemServiceImpl.generateActionItems() is under execution...");
		List<ActionItem> newActionItemList = new ArrayList<>();
		actionItemList.forEach(actionItem->{
			ActionItem newActionItem = new ActionItem();
			newActionItem.setActionItemTitle(actionItem.getActionItemTitle());
			newActionItem.setActionItemDescription(actionItem.getActionItemDescription());
			newActionItem.setStartDate(actionItem.getStartDate());
			newActionItem.setActionPriority(actionItem.getActionPriority());
			newActionItem.setActionStatus(actionItem.getActionStatus());
			newActionItem.setEndDate(actionItem.getEndDate());
			newActionItem.setMeetingId(actionItem.getMeetingId());
			newActionItem.setEmailId(actionItem.getEmailId()); //UserId details
			newActionItemList.add(newActionItem);
			
		});
		actionItemRepository.saveAll(newActionItemList);
		log.info("ActionItemServiceImpl.generateActionItems() executed successfully");
		return true;
	}

	@Override
	public List<ActionItem> getActionItemsByUserId(String emailId) {
		log.info("ActionItemServiceImpl.getActionItemsByUserId() entered with args - emailId : "+emailId);
		if(emailId == null || emailId.equals("")) {
			log.info("ActionItemServiceImpl.generateActionItems() EmptyInputException : UserId/EmailId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("ActionItemServiceImpl.getActionItemsByUserId() is under execution...");
		List<ActionItem> actionItemList =actionItemRepository.findByUserId(emailId);
		log.info("ActionItemServiceImpl.getActionItemsByUserId() executed successfully");
		return actionItemList;
	}
	
}
