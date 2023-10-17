package com.ikn.ums.meeting.service;

import java.util.List;
import java.util.Optional;
import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.model.MinutesOfMeeting;

public interface ActionItemService {

	ActionItem saveActionItem(ActionItem actions);
	Optional<ActionItem> getActionItemById(Integer actionItemId);
	ActionItem updateActionItem(ActionItem action);
	Integer deleteActionItemById(Integer actionItemId);
	boolean deleteAllActionItemsById(List<Integer> actionItemIds);
	ActionItemListVO getActionItems();
	List<ActionItem> getActionItemList();
	List<ActionItem> getActionItemsByUserId(String emailId);
	ActionItemListVO getActionItemsByMeetingId(Long meetingId);
    List<Task> convertActionItemsToTasks(List<ActionItem> actionItemList, Long meetingId);
	boolean generateActionItems(List<ActionItem> actionItemList);
	boolean sendMinutesofMeetingEmail(MinutesOfMeeting momObject);

}
