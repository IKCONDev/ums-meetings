package com.ikn.ums.meeting.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.dto.ActionItemDto;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.MinutesOfMeeting;
import com.ikn.ums.meeting.repository.ActionItemRepository;
import com.ikn.ums.meeting.service.TaskService;
import com.ikn.ums.meeting.utils.EmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ActionItemServiceImpl implements com.ikn.ums.meeting.service.ActionItemService {

	@Autowired
	private ActionItemRepository actionItemRepository;

	@Autowired
	private TaskService taskService;

	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private Environment env;

	@Override
	@Transactional
	public ActionItemDto saveActionItem(ActionItemDto actionItem) {
		log.info("saveActionItem() entered with args - actionItem object");
		if (actionItem == null) {
			log.info("saveActionItem() Empty Input Exception : Exception occured while saving actionItem");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_MSG);
		}
		log.info("saveActionItem() is under execution...");
		actionItem.setActionStatus("Not Submitted");
		ActionItem entity = new ActionItem();
		mapper.map(actionItem, entity);
		ActionItem savedActionItem = actionItemRepository.save(entity);
		ActionItemDto savedDto = new ActionItemDto();
		mapper.map(savedActionItem, savedDto);
		//send email, if createdBy and action item owner is diffrent
		if(savedActionItem != null) {
			if(!savedActionItem.getCreatedByEmailId().equalsIgnoreCase(savedActionItem.getEmailId())) {
				//if create person of the action item and organizer is not same, send email to organizer of action item that
				//some other person has created a action item in their account on behalf
				String subject = "Action Item "+savedActionItem.getActionItemId()+" created by "+savedActionItem.getCreatedBy()+" on behalf of you";
				String emailBody = "Meeting ID - "+savedActionItem.getMeetingId()+" \r\n"+
				"Action Item ID - "+savedActionItem.getActionItemId()+" \r\n"+
				"Action Item Title - "+savedActionItem.getActionItemTitle()+". \r\n \r\n"+
				"Please be informed that an action item has been created on your behalf by "+savedActionItem.getCreatedBy()+" ("+savedActionItem.getCreatedByEmailId()+"). \r\n \r\n"+
				"Please click the below link for further details. \r\n"+
				env.getProperty("default.domain.url")+"#/actions"+" \r\n \r\n";
				emailService.sendMail(savedActionItem.getEmailId(), subject, emailBody, false);
			}
		}
		log.info("saveActionItem() executed successfully...");
		return savedDto;
	}

	@Transactional
	@Override
	public ActionItemDto updateActionItem(ActionItemDto actionItem) {
		log.info("updateActionItem() entered with args - actionItem");
		if (actionItem == null) {
			log.info("ActionItemService.updateActionItem() Empty Input Exception : ActionItem object is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_MSG);
		}
		log.info("updateActionItem() is under execution...");
		ActionItem entity = new ActionItem();
		mapper.map(actionItem, entity);
		ActionItem dbActionItem = actionItemRepository.findById(entity.getActionItemId()).get();
		dbActionItem.setMeetingId(actionItem.getMeetingId());
		dbActionItem.setActionItemTitle(actionItem.getActionItemTitle());
		dbActionItem.setActionItemDescription(actionItem.getActionItemDescription());
		dbActionItem.setActionPriority(actionItem.getActionPriority());
		dbActionItem.setActionStatus(actionItem.getActionStatus());
		dbActionItem.setActionItemOwner(actionItem.getActionItemOwner());
		dbActionItem.setStartDate(actionItem.getStartDate());
		dbActionItem.setEndDate(actionItem.getEndDate());
		dbActionItem.setModifiedBy(actionItem.getModifiedBy());
		dbActionItem.setModifiedByEmailId(actionItem.getModifiedByEmailId());
		dbActionItem.setModifiedDateTime(LocalDateTime.now());
		ActionItem updateAction = actionItemRepository.save(dbActionItem);
		ActionItemDto updatedActionDto = new ActionItemDto();
		mapper.map(updateAction, updatedActionDto);
		//send email, if createdBy and action item owner is diffrent
				if(updateAction != null) {
					if(!updateAction.getModifiedByEmailId().equalsIgnoreCase(updateAction.getEmailId())) {
						//if create person of the action item and organizer is not same, send email to organizer of action item that
						//some other person has created a action item in their account on behalf
						String subject = "ActionItem "+updateAction.getActionItemId()+" updated by "+updateAction.getModifiedBy();
						String emailBody = "ActionItemID - "+updateAction.getActionItemId()+" \r\n"+
						"ActionItemTitle - "+updateAction.getActionItemTitle()+". \r\n \r\n"+
						"Please be informed that an action item has been updated on your behalf by "+updateAction.getModifiedBy()+" ("+updateAction.getModifiedByEmailId()+"). \r\n \r\n"+
						"Kindly visit the provided link for further details. \r\n"+
						env.getProperty("default.domain.url")+"#/actions"+" \r\n \r\n";
						emailService.sendMail(updateAction.getEmailId(), subject, emailBody, false);
					}
				}
		log.info("updateActionItem() executed successfully...");
		return updatedActionDto;
	}

	@Override
	@Transactional
	public Integer deleteActionItemById(Integer actionItemId) {
		log.info("deleteActionItemById() entered with args - actionItemId : " + actionItemId);
		if (actionItemId == null || actionItemId < 1) {
			log.info("deleteActionItemById() Empty Input Exception : Action Item Id is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		log.info("deleteActionItemById() is under execution...");
		actionItemRepository.deleteById(actionItemId);
		log.info("deleteActionItemById() executed successfully");
		return 1;
	}

	@Override
	public boolean deleteAllActionItemsById(List<Integer> actionItemIds) {
		log.info("deleteAllActionItemsById() entered with args - actionItemIds");
		if (actionItemIds.size() == 0 || actionItemIds == null) {
			log.info("deleteAllActionItemsById() Empty List Exception : Action Item Ids List is empty.");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_IDLIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_IDLIST_EMPTY_MSG);
		}
		log.info("deleteAllActionItemsById() is under execution...");
		boolean isAllDeleted = false;
		actionItemRepository.deleteAllById(actionItemIds);
		isAllDeleted = true;
		log.info("deleteAllActionItemsById() executed successfully");
		return isAllDeleted;
	}

	@Override
	public List<ActionItem> getActionItemList() {
		log.info("getActionItemList() entered");
		log.info("getActionItemList() is under execution...");
		List<ActionItem> actionItemList = actionItemRepository.findAll();
		log.info("getActionItemList() executed succesfully");
		return actionItemList;
	}

	@Override
	public Optional<ActionItem> getActionItemById(Integer actionItemId) {
		log.info("getActionItemById() entered with args - actionItemId : " + actionItemId);
		if (actionItemId == null || actionItemId < 0) {
			log.info("getActionItemById() Empty Input Exception : Action Item Id is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		log.info("getActionItemById() is under execution...");
		Optional<ActionItem> actionItem = actionItemRepository.findById(actionItemId);
		log.info("getActionItemById() executed successfully.");
		return actionItem;
	}

	@Override
	public ActionItemListVO getActionItemsByMeetingId(Long meetingId) {
		log.info("getActionItemsByMeetingId() entered with args - meetingId : " + meetingId);
		if (meetingId < 1 || meetingId == null) {
			log.info("getActionItemsByMeetingId() Empty Input Exception : Exception occured while"
					+ "fetching the ActionItems");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		log.info("getActionItemsByMeetingId() is under execution...");
		ActionItemListVO acItemsVO = new ActionItemListVO();
		List<ActionItem> actionItemsList = actionItemRepository.findActionItemsByEventId(meetingId);
		acItemsVO.setActionItemList(actionItemsList);
		log.info("getActionItemsByMeetingId() executed succesfully");
		return acItemsVO;
	}

	@Override
	public ActionItemListVO getActionItems() {
		log.info("getActionItems() is entered");
		ActionItemListVO acItemsVO = new ActionItemListVO();
		log.info("getActionItems() is under execution...");
		List<ActionItem> actionItemsList = actionItemRepository.findAll();
		acItemsVO.setActionItemList(actionItemsList);
		log.info("getActionItems() executed successfully");
		return acItemsVO;
	}

	@Transactional
	@Override
	public boolean submitActionItems(List<ActionItem> actionItemList, Long meetingId) {
		boolean isActionItemsSubmitted = false;
		log.info("submitActionItems() entered with args : actionItemList");
		if (actionItemList == null || actionItemList.size() < 1) {

		}
		log.info("submitActionItems() is under execution...");
		// List<Task> taskList = taskService.convertActionItemsToTasks(actionItemList,
		// meetingId);
		actionItemList.stream().forEach(action -> {
			action.setActionStatus("Submitted");
		});
		isActionItemsSubmitted = true;
		actionItemRepository.saveAll(actionItemList);
		log.info("submitActionItems() executed succesfully");
		return isActionItemsSubmitted;
	}

	@Override
	public boolean generateActionItems(List<ActionItem> actionItemList) {
		log.info("generateActionItems() entered with args - actionItemList");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info("generateActionItems() EmptyListException : Action Items list is empty or null");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MSG);
		}
		log.info("generateActionItems() is under execution...");
		List<ActionItem> newActionItemList = new ArrayList<>();
		actionItemList.forEach(actionItem -> {
			ActionItem newActionItem = new ActionItem();
			newActionItem.setActionItemTitle(actionItem.getActionItemTitle());
			newActionItem.setActionItemDescription(actionItem.getActionItemDescription());
			newActionItem.setStartDate(actionItem.getStartDate());
			newActionItem.setActionPriority(actionItem.getActionPriority());
			newActionItem.setActionStatus(actionItem.getActionStatus());
			newActionItem.setEndDate(actionItem.getEndDate());
			newActionItem.setMeetingId(actionItem.getMeetingId());
			newActionItem.setEmailId(actionItem.getEmailId()); // UserId details
			newActionItemList.add(newActionItem);

		});
		actionItemRepository.saveAll(newActionItemList);
		log.info("generateActionItems() executed successfully");
		return true;
	}

	@Override
	public List<ActionItem> getActionItemsByUserId(String emailId) {
		log.info("getActionItemsByUserId() entered with args - emailId : " + emailId);
		if (emailId == null || emailId.equals("")) {
			log.info("generateActionItems() EmptyInputException : UserId/EmailId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getActionItemsByUserId() is under execution...");
		var actionItemStatus = "Submitted";
		List<ActionItem> actionItemList = actionItemRepository.findActionItemsByUserId(emailId, actionItemStatus);
		actionItemList.sort((o1, o2) -> {
			return o2.getActionItemId() - o1.getActionItemId();
		});
		log.info("getActionItemsByUserId() executed successfully.");
		return actionItemList;
	}

	@Override
	public boolean sendMinutesofMeetingEmail(MinutesOfMeeting momObject) {
		log.info("sendMinutesofMeetingEmail() entered with args -actionItemList :");
		log.info("sendMinutesofMeetingEmail() is under execution...");
		Long meetingId = momObject.getMeeting().getMeetingId();
		List<String> emailList = momObject.getEmailList();
		List<ActionItem> actionItemList = actionItemRepository.findActionItemsByEventId(meetingId);
		String discussionPoints = momObject.getDiscussionPoints();
		String hoursDiff = momObject.getHoursDiff();
		String minsDiff = momObject.getMinutesDiff();
		taskService.sendMinutesofMeetingEmail(emailList, actionItemList, meetingId, discussionPoints, hoursDiff,
				minsDiff);
		log.info("sendMinutesofMeetingEmail() executed successfully");
		return true;
	}

	@Override
	public Long getUserOrganizedActionItemsCount(String emailId) {
		log.info("getUserOrganizedActionItemsCount() entered with args - emailId/userId");
		if (emailId == null || emailId == "" || emailId.equals(null)) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getUserOrganizedActionItemsCount() is under execution...");
		Long count = actionItemRepository.findOrganizedActionItemsCountByUserId(emailId);
		log.info("getUserOrganizedActionItemsCount() executed successfully");
		return count;
	}

	@Override
	public List<ActionItem> getFilteredActionItems(String actionItemTitle, String actionItemOwner,
			String actionItemStartDate, String actionItemEndDate, String emailId) {
		log.info("getFilteredActionItems() is entered");
		log.info("getFilteredActionItems() is under execution...");
		var actionItemStatus = "Submitted";
		LocalDate actualStartDate = null;
		if (!actionItemStartDate.isBlank()) {
			actualStartDate = LocalDate.parse(actionItemStartDate);
		}
		LocalDate actualEndDate = null;
		if (!actionItemEndDate.isBlank()) {
			actualEndDate = LocalDate.parse(actionItemEndDate);
		}
		List<ActionItem> filteredActionItemList = actionItemRepository.findAllFilteredActionItemsByUserId(
				actionItemTitle.isBlank() ? null : actionItemTitle, actionItemOwner.isBlank() ? null : actionItemOwner,
				actualStartDate, actualEndDate, emailId, actionItemStatus);
		log.info("getFilteredActionItems() executed successfully");
		return filteredActionItemList;
	}

	@Override
	public List<ActionItem> getActionItemsByDepartmentId(Long departmentId) {
		log.info("getActionItemsByDepartmentId() is entered");
		log.info("getActionItemsByDepartmentId() is under execution...");
		if (departmentId == 0) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_ACTIONITEMS_DEPTID_EMPTY_CODE,
					ErrorCodeMessages.ERR_ACTIONITEMS_DEPTID_EMPTY_MSG);
		}
		List<ActionItem> actionItemsListOfDepartment = actionItemRepository.findByDepartmentId(departmentId);
		log.info("getActionItemsByDepartmentId() executed successfully");
		return actionItemsListOfDepartment;
	}

	@Override
	public List<ActionItem> getActionItemsByPriority(String priority) {
		log.info("getActionItemsByPriority() is entered");
		log.info("getActionItemsByPriority() is under execution...");
		if (priority.isBlank()) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_ACTIONITEMS_PRIORITY_EMPTY_CODE,
					ErrorCodeMessages.ERR_ACTIONITEMS_PRIORITY_EMPTY_MSG);
		}
		List<ActionItem> actionItemsListOfDepartment = actionItemRepository.findByActionPriority(priority);
		log.info("getActionItemsByPriority() executed successfully");
		return actionItemsListOfDepartment;
	}

	@Override
	public List<Object[]> getAllActionItemsCountByDepartment() {
		log.info("getAllActionItemsCountByDepartment() is entered");
		log.info("getAllActionItemsCountByDepartment() is under execution...");
		List<Object[]> actionItemsList = actionItemRepository.getCountOfActionItemsByDepartment();
		log.info("getAllActionItemsCountByDepartment() executed successfully");
		return actionItemsList;

	}

	@Override
	public List<ActionItem> getAllMeetingActionItems(Long meetingId) {
		log.info("getAllMeetingActionItems() is entered");
		log.info("getAllMeetingActionItems() is under execution...");
		List<ActionItem> actionItemList = actionItemRepository.getAllActionItemsByMeetingId(meetingId);
		log.info("getAllMeetingActionItems() executed successfully");
		return actionItemList;
	}

}
