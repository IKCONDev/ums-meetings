package com.ikn.ums.meeting.service;

import java.util.List;
import java.util.Optional;
import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;

public interface ActionItemService {

	ActionItem createActionItem(ActionItem actions);
	Optional<ActionItem> getSingleActionItem(Integer id);
	ActionItem updateActionItem(ActionItem action);
	Integer deleteActionItem(Integer actionId);
	boolean deleteAllActionItemsById(List<Integer> ids);
	ActionItemListVO fetchActionItems();
	List<ActionItem> fetchActionItemList();
	List<ActionItem> fetchActionItemsByEmail(String email);
	ActionItemListVO fetchActionItemsOfEvent(Integer eventId);
    List<Task> sendToTasks(List<ActionItem> actionItem);
	boolean generateActions(List<ActionItem> actionItems);

}
