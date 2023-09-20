package com.ikn.ums.meeting.service;

import java.util.List;
import java.util.Optional;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;

public interface ActionItemService {

	// save Action Item
	ActionItem createActionItem(ActionItem actions);

	// Fetch all Action Items
	List<ActionItem> fetchActionItemList();

	// Fetch ActionItem based on Id
	Optional<ActionItem> getSingleActionItem(Integer id);

	// Update ActionItem based on Id
	ActionItem updateActionItem(ActionItem action);

	// Delete ActionItem based on Id
	Integer deleteActionItem(Integer actionId);

	// get action items of an event based on eventId
	ActionItemListVO fetchActionItemsOfEvent(Integer eventId);

	ActionItemListVO fetchActionItems();

}
