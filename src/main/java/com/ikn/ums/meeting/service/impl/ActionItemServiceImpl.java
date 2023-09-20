package com.ikn.ums.meeting.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.repository.ActionItemRepository;

public class ActionItemServiceImpl implements com.ikn.ums.meeting.service.ActionItemService {

	@Autowired
	private ActionItemRepository actionItemRepository;
	
	@Override
	public ActionItem createActionItem(ActionItem actions) {
		return actionItemRepository.save(actions);
	}

	@Override
	public List<ActionItem> fetchActionItemList() {
		List<ActionItem>actions = actionItemRepository.findAll();
		return actions;
	}

	@Override
	public Optional<ActionItem> getSingleActionItem(Integer id) {
		Optional<ActionItem> actionItem = actionItemRepository.findById(id);
		return actionItem;
	}

	@Override
	public ActionItem updateActionItem(ActionItem action) {
		ActionItem existingAction = actionItemRepository.findById(action.getId()).get();
		existingAction.setEventid(action.getEventid());
		existingAction.setActionTitle(action.getActionTitle());
		existingAction.setDescription(action.getDescription());
		existingAction.setActionPriority(action.getActionPriority());
		existingAction.setActionStatus(action.getActionStatus());
		existingAction.setStartDate(action.getStartDate());
		existingAction.setEndDate(action.getEndDate());
		ActionItem updateAction = actionItemRepository.save(existingAction);
		return updateAction;
	}

	@Override
	public Integer deleteActionItem(Integer actionId) {
		actionItemRepository.deleteById(actionId);
		return 1;
	}

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

}
