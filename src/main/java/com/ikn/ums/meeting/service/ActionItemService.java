package com.ikn.ums.meeting.service;

import java.util.List;
import java.util.Optional;
import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.entity.Task;

public interface ActionItemService {

	ActionItem saveActionItem(ActionItem actions);
	Optional<ActionItem> getActionItemById(Integer actionItemId);
	ActionItem updateActionItem(ActionItem action);
	Integer deleteActionItemById(Integer actionItemId);
	boolean deleteAllActionItemsById(List<Integer> actionItemIds);
	ActionItemListVO getActionItems();
	List<ActionItem> getActionItemList();
	List<ActionItem> getActionItemsByUserId(String emailId);
	ActionItemListVO getActionItemsByMeetingId(Integer meetingId);
    List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList);
	boolean generateActionItems(List<ActionItem> actionItemList);
	boolean sendMinutesofMeetingEmail(List<ActionItem> actionItemList, Meeting meeting);

}
