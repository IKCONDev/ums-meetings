package com.ikn.ums.meeting.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.dto.ActionItemDto;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.MinutesOfMeeting;
import com.ikn.ums.meeting.service.ActionItemService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/actions")
@Slf4j
public class ActionItemController {

	@Autowired
	private ActionItemService actionItemService;

	/**
	 * 
	 * @param actions
	 * @return
	 */
	@PostMapping("/save")
	public ResponseEntity<ActionItemDto> createActionItem(@RequestBody ActionItemDto actionItem) {
		log.info("createActionItem() entered with args : actionItem");
		if (actionItem == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_MSG);
		}
		try {
			log.info("createActionItem() is under execution...");
			ActionItemDto savedActionItem = actionItemService.saveActionItem(actionItem);
			log.info("createActionItem() executed successfully");
			return new ResponseEntity<>(savedActionItem, HttpStatus.OK);
		} catch (Exception e) {
			log.error("createActionItem() exited with exception : Exception occured while creating action item : "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_SAVE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_SAVE_MSG);
		}
	}

	/**
	 * 
	 * @param actionItemid
	 * @param actionItem
	 * @return
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<ActionItemDto> updateActionItem(@PathVariable("id") Integer actionItemId,
			@RequestBody ActionItemDto actionItem) {
		log.info("updateActionItem() entered with args : actionItemid " + actionItemId);
		if (actionItemId < 1 || actionItemId == null) {
			log.info("updateActionItem() EmptyInputException : Empty or invalid actionItemId");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		try {
			log.info("updateActionItem() is under execution...");
			actionItem.setActionItemId(actionItemId);
			ActionItemDto updatedActionItem = actionItemService.updateActionItem(actionItem);
			log.info("updateActionItem() executed successfully");
			return new ResponseEntity<>(updatedActionItem, HttpStatus.OK);
		} catch (Exception e) {
			log.error("updateActionItem() exited with exception : Exception occured while upadating action item "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_UPDATE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_UPDATE_MSG);
		}

	}

	/**
	 * 
	 * @param actionItemId
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ActionItem> getSingleActionItem(@PathVariable("id") Integer actionItemId) {
		log.info("getSingleActionItem() entered with args : actionItemId " + actionItemId);
		if (actionItemId < 1 || actionItemId == null) {
			log.info("getSingleActionItem() Empty Input Exception : Action Item id is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		try {
			log.info("getSingleActionItem() is under execution...");
			Optional<ActionItem> optActionItem = actionItemService.getActionItemById(actionItemId);
			ActionItem actionItem = optActionItem.get();
			log.info("getSingleActionItem() executed successfully.");
			return new ResponseEntity<>(actionItem, HttpStatus.OK);
		} catch (Exception e) {
			log.error("getSingleActionItem() exited with exception : Exception ocuured while fetching action item details : "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_MSG);
		}

	}

	/**
	 * 
	 * @param actionItemid
	 * @return
	 */
	@DeleteMapping("delete/{id}")
	public ResponseEntity<Integer> deleteActionItem(@PathVariable("id") Integer actionItemid) {
		log.info("deleteActionItem() entered with args : actionItemid " + actionItemid);
		if (actionItemid == null || actionItemid <= 0) {
			log.info("deleteActionItem() Empty Input Exception : Action Item is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		try {
			log.info("deleteActionItem() is under execution... ");
			Integer result = actionItemService.deleteActionItemById(actionItemid);
			log.info("deleteActionItem() executed successfully.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("deleteActionItem() exited with exception : Exception occured while deleting action item. "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_MSG);
		}

	}

	/**
	 * 
	 * @param actionItemIds
	 * @return
	 */
	@DeleteMapping("/deleteAll/{ids}")
	public ResponseEntity<Boolean> deleteActionItemsById(@PathVariable("ids") String actionItemIds) {
		log.info("deleteActionItemsById() entered with args actionItemIds : " + actionItemIds);
		if (actionItemIds == null || actionItemIds.equals("")) {
			log.info("deleteActionItemsById() Empty Input Exception : Action Item ids are empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		List<Integer> actualAcIds = null;
		if (actionItemIds != "") {
			String[] idsFromUI = actionItemIds.split(",");
			List<String> idsList = Arrays.asList(idsFromUI);
			// convert string of ids to Integer ids
			actualAcIds = idsList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
		}
		try {
			log.info("deleteActionItemsById() is under execution...");
			boolean isAllDeleted = actionItemService.deleteAllActionItemsById(actualAcIds);
			log.info("deleteActionItemsById() is executed succesfully");
			return new ResponseEntity<>(isAllDeleted, HttpStatus.OK);
		} catch (Exception e) {
			log.error("deleteActionItemsById() exited with exception : Exception occured while deleting action items "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_MSG);
		}
	}

	/**
	 * 
	 * @param actionItems
	 * @return
	 */
	@PostMapping("/generate-actions")
	public ResponseEntity<Boolean> generateActionItems(@RequestBody List<ActionItem> actionItemList) {
		log.info("generateActionItems() entered with args : actionItemList ");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info("generateActionItems() EmptyListException : Action Items list is empty");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MSG);
		}
		try {
			log.info("generateActionItems() is under execution...");
			boolean isGenerated = actionItemService.generateActionItems(actionItemList);
			log.info("generateActionItems() executed sucessfully");
			return new ResponseEntity<>(isGenerated, HttpStatus.OK);
		} catch (Exception e) {
			log.error("generateActionItems() exited with exception : " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GENERATE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GENERATE_MSG);
		}
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public ResponseEntity<List<ActionItem>> getActionItems() {
		log.info("getActionItems() is entered");
		try {
			log.info("getActionItems() is under excecution...");
			List<ActionItem> actionItemList = actionItemService.getActionItemList();
			log.info("getActionItems() is executed successfully");
			return new ResponseEntity<>(actionItemList, HttpStatus.OK);
		} catch (Exception e) {
			log.error("getActionItems() exited with exception: Exception occurred while getting action items : "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_MSG);
		}
	}

	/**
	 * 
	 * @param emailId (userId)
	 * @return
	 */
	@GetMapping("/all/{emailId}")
	public ResponseEntity<List<ActionItem>> getActionItemsByEmailId(@PathVariable("emailId") String email,
			@RequestParam(defaultValue = "", required = false) String actionItemTitle,
			@RequestParam(defaultValue = "", required = false) String actionItemOwner,
			@RequestParam(defaultValue = "", required = false) String actionItemStartDate,
			@RequestParam(defaultValue = "", required = false) String actionItemEndDate) {
		log.info("FetchActionItemsByEmailId() entered with args : " + email);
		if (email.equals("") || email == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			if (actionItemTitle.isBlank() && actionItemOwner.isBlank() && actionItemStartDate.isBlank()
					&& actionItemEndDate.isBlank()) {
				log.info("FetchActionItemsByEmailId() is under execution without filters...");
				List<ActionItem> actionItemList = actionItemService.getActionItemsByUserId(email);
				log.info("FetchActionItemsByEmailId() executed succesfully");
				return new ResponseEntity<>(actionItemList, HttpStatus.OK);
			} else {
				log.info("FetchActionItemsByEmailId() is under execution with filters...");
				List<ActionItem> actionItemList = actionItemService.getFilteredActionItems(actionItemTitle,
						actionItemOwner, actionItemStartDate, actionItemEndDate, email);
				log.info("FetchActionItemsByEmailId() executed succesfully");
				return new ResponseEntity<>(actionItemList, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("FetchActionItemsByEmailId() exited with exception : Exception ocuured while fetching action items of a user : "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_MSG);
		}

	}

	/**
	 * 
	 * @param eventId
	 * @return
	 */
	@GetMapping("/ac-items/{meetingId}")
	public ResponseEntity<ActionItemListVO> getActionItemsByMeetingId(@PathVariable Long meetingId) {
		log.info("getActionItemsByMeetingId() entered with args  meetingId : " + meetingId);
		if (meetingId == null || meetingId < 1) {
			log.info("getActionItemsByMeetingId() Empty Input Exception : Meeting id is empty or invalid");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		try {
			log.info("getActionItemsByMeetingId() is under execution");
			ActionItemListVO acItemsListVO = actionItemService.getActionItemsByMeetingId(meetingId);
			log.info("getActionItemsByMeetingId() executed sucessfully");
			return new ResponseEntity<>(acItemsListVO, HttpStatus.OK);
		} catch (Exception e) {
			log.error("getActionItemsByMeetingId() exited with exception : " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_MSG);
		}
	}

	/**
	 * 
	 * @param actionItemList
	 * @return
	 */

	@PostMapping("/convert-task/{meetingId}")
	public ResponseEntity<Boolean> processActionItemsToTasks(@RequestBody List<ActionItem> actionItemList,
			@PathVariable Long meetingId) {
		log.info("processActionItemsToTasks() entered with args : actionItemsList");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info("processActionItemsToTasks() Empty List Exception : Action Items list is empty or null");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MSG);
		}
		try {
			log.info("processActionItemsToTasks() is under execution...");
			boolean isActionItemSubmitted = actionItemService.submitActionItems(actionItemList, meetingId);
			log.info("processActionItemsToTasks() executed successfully");
			return new ResponseEntity<>(isActionItemSubmitted, HttpStatus.OK);
		} catch (Exception e) {
			log.error("processActionItemsToTasks() exited with exception : An Exception occurred while converting action items to tasks "
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_MSG);
		}

	}

	@PostMapping("/send-momdata")
	public ResponseEntity<Boolean> sendMinutesOfMeeting(@RequestBody MinutesOfMeeting momObject) {
		log.info("sendMinutesOfMeeting() entered with args : minutes of meeting object");
		if(momObject == null) {
			log.info("sendMinutesOfMeeting() EmptyInputException : mom object is null / empty.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_ACTIONITEMS_DEPTID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_ACTIONITEMS_DEPTID_EMPTY_CODE);
		}
		log.info("entered the controller of send Minutes of Meeting");
		MinutesOfMeeting momObject1 = new MinutesOfMeeting();
		momObject1.setMeeting(momObject.getMeeting());
		momObject1.setEmailList(momObject.getEmailList());
		momObject1.setDiscussionPoints(momObject.getDiscussionPoints());
		momObject1.setHoursDiff(momObject.getHoursDiff());
		momObject1.setMinutesDiff(momObject.getMinutesDiff());
		boolean resultValue = actionItemService.sendMinutesofMeetingEmail(momObject1);
		log.info("entered the controller of send Minutes of Meeting");
		return new ResponseEntity<>(resultValue, HttpStatus.OK);
	}

	@PostMapping("/send-mom/{meeting}/{emailList}")
	public ResponseEntity<Boolean> sendMinutesOfMeetingObject(@PathVariable("meeting") Meeting meeting,
			@PathVariable("emailList") List<String> emailList) {
		log.info("sendMinutesOfMeetingObject() is entered");
		MinutesOfMeeting momObject = new MinutesOfMeeting();
		momObject.setMeeting(meeting);
		momObject.setEmailList(emailList);
		log.info("sendMinutesOfMeetingObject() is under execution");
		boolean resultValue = actionItemService.sendMinutesofMeetingEmail(momObject);
		log.info("sendMinutesOfMeetingObject() executed successfully)");
		return new ResponseEntity<>(resultValue, HttpStatus.OK);
	}

	@GetMapping("/organized/count/{userId}")
	public ResponseEntity<Long> getActionItemsCountforUser(@PathVariable("userId") String emailId) {
		log.info("getActionItemsCountforUser() is entered)");
		log.info("getActionItemsCountforUser() is under execution");
		Long count = actionItemService.getUserOrganizedActionItemsCount(emailId);
		log.info("getActionItemsCountforUser() executed successfully");
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
	
	@GetMapping("/all/department/{departmentId}")
	public ResponseEntity<List<ActionItem>> getActionItemsByDepartment(@PathVariable Long departmentId){
		log.info("getActionItemsByDepartment() is entered)");
		if(departmentId == 0) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_ACTIONITEMS_DEPTID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_ACTIONITEMS_DEPTID_EMPTY_MSG);
		}
		try {
			log.info("getActionItemsByDepartment() is under execution");
			List<ActionItem> actionItemsListByDepartment = actionItemService.getActionItemsByDepartmentId(departmentId);
			log.info("getActionItemsByDepartment() executed successfully");
			return new ResponseEntity<>(actionItemsListByDepartment, HttpStatus.OK);
		}catch (EmptyInputException businessException) {
			throw businessException;
		}catch (Exception e) {
			log.error("getActionItemsByDepartment() exited with exception : Exception occured while getting the actionItems:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_ACTIONITEMS_GET_BYDEPT_UNSUCCESS_CODE, 
					ErrorCodeMessages.ERR_ACTIONITEMS_GET_BYDEPT_UNSUCCESS_MSG);
		}
	}
	
	@GetMapping("/all/priority/{priority}")
	public ResponseEntity<List<ActionItem>> getActionItemsByDepartment(@PathVariable String priority){
		log.info("getAllActionItemsByDepartment() is entered ");
		if(priority.isBlank() || priority == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_ACTIONITEMS_PRIORITY_EMPTY_CODE, 
					ErrorCodeMessages.ERR_ACTIONITEMS_PRIORITY_EMPTY_MSG);
		}
		try {
			log.info("getAllActionItemsByDepartment() is under execution...");
			List<ActionItem> actionItemsListByDepartment = actionItemService.getActionItemsByPriority(priority);
			return new ResponseEntity<>(actionItemsListByDepartment, HttpStatus.OK);
		}catch (EmptyInputException businessException) {
			throw businessException;
		}catch (Exception e) {
			log.error("getActionItemsByDepartment() exited with exception : Exception occured while getting the actionItems:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_ACTIONITEMS_GET_BYPRIORITY_UNSUCCESS_CODE, 
					ErrorCodeMessages.ERR_ACTIONITEMS_GET_BYPRIORITY_UNSUCCESS_MSG);
		}
	}
	@GetMapping("/department-actions")
	public ResponseEntity<List<Object[]>> getAllActionItemsByDepartment(){
		log.info("getAllActionItemsByDepartment() entered ");
		try { 
			log.info("getAllActionItemsByDepartment() is under execution... ");
			List<Object[]> actionItemList = actionItemService.getAllActionItemsCountByDepartment();
			log.info("getAllActionItemsByDepartment() is executed Successfully");
			return new ResponseEntity<>(actionItemList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getAllActionItemsByDepartment() exited with exception : Exception occured while getting the actionItems:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_IDLIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MSG);
		}
	
	}
	@GetMapping("/meeting-actions/{meetingId}")
	public ResponseEntity<List<ActionItem>> getAllActionItemsByMeetingId(@PathVariable("meetingId") Long meetingId){
		log.info("getAllActionItemsByMeetingId() entered ");
		try { 
			log.info("getAllActionItemsByMeetingId() is under execution... ");
			List<ActionItem> actionItemList = actionItemService.getAllMeetingActionItems(meetingId);
			log.info("getAllActionItemsByMeetingId() is executed Successfully");
			return new ResponseEntity<>(actionItemList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getAllActionItemsByMeetingId() exited with exception : Exception occured while getting the actionItems:"+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_IDLIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MSG);
		}
	
	}

}
