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
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.VO.ActionItemListVO;
import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Task;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
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
	public ResponseEntity<?> createActionItem(@RequestBody ActionItem actionItem) {
		log.info("ActionItemController.createActionItem() entered with args : actionItem");
			if (actionItem == null) {
				throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_CODE,
						ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_EMPTY_MESSAGE);
			}
		try {
			log.info("ActionItemController.createActionItem() is under execution...");
			ActionItem savedActionItem = actionItemService.saveActionItem(actionItem);
			log.info("ActionItemController.createActionItem() executed successfully");
			return new ResponseEntity<>(savedActionItem, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionItemController.createActionItem() exited with exception : Exception occured while creating action item : "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_SAVE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_SAVE_MESSAGE);
		}
	}

	/**
	 * 
	 * @param actionItemid
	 * @param actionItem
	 * @return
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateActionItem(@PathVariable("id") Integer actionItemId,
			@RequestBody ActionItem actionItem) {
		log.info("ActionItemController.updateActionItem() entered with args : actionItemid " + actionItemId);
		if (actionItemId < 1 || actionItemId == null) {
			log.info("ActionItemController.updateActionItem() EmptyInputException : Empty or invalid actionItemId");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MESSAGE);
		}
		try {
			log.info("ActionItemController.updateActionItem() is under execution...");
			actionItem.setActionItemId(actionItemId);
			ActionItem updatedActionItem = actionItemService.updateActionItem(actionItem);
			log.info("ActionItemController.updateActionItem() executed successfully");
			return new ResponseEntity<>(updatedActionItem, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionItemController.updateActionItem() exited with exception : Exception occured while upadating action item "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_UPDATE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_UPDATE_MESSAGE);
		}

	}

	/**
	 * 
	 * @param actionItemId
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getSingleActionItem(@PathVariable("id") Integer actionItemId) {
		log.info("ActionItemController.getSingleActionItem() entered with args : actionItemId " + actionItemId);
		if (actionItemId < 1 || actionItemId == null) {
			log.info(
					"ActionItemController.getSingleActionItem() Empty Input Exception : Action Item id is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MESSAGE);
		}
		try {
			log.info("ActionItemController.getSingleActionItem() is under execution...");
			Optional<ActionItem> optActionItem = actionItemService.getActionItemById(actionItemId);
			ActionItem actionItem = optActionItem.get();
			log.info("ActionItemController.getSingleActionItem() executed successfully.");
			return new ResponseEntity<>(actionItem, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionItemController.getSingleActionItem() exited with exception : Exception ocuured while fetching action item details : "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_MESSAGE);
		}

	}

	/**
	 * 
	 * @param actionItemid
	 * @return
	 */
	@DeleteMapping("delete/{id}")
	public ResponseEntity<?> deleteActionItem(@PathVariable("id") Integer actionItemid) {
		log.info("ActionItemController.deleteActionItem() entered with args : actionItemid " + actionItemid);
		if (actionItemid == null || actionItemid < 0) {
			log.info(
					"ActionItemController.deleteActionItem() Empty Input Exception : Action Item is empty or invalid.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MESSAGE);
		}
		try {
			log.info("ActionItemController.deleteActionItem() is under execution... ");
			Integer result = actionItemService.deleteActionItemById(actionItemid);
			log.info("ActionItemController.deleteActionItem() executed successfully.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionItemController.deleteActionItem() exited with exception : Exception occured while deleting action item. "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_MESSAGE);
		}

	}

	/**
	 * 
	 * @param actionItemIds
	 * @return
	 */
	@DeleteMapping("/deleteAll/{ids}")
	public ResponseEntity<?> deleteActionItemsById(@PathVariable("ids") String actionItemIds) {
		log.info("ActionItemController.deleteActionItemsById() entered with args actionItemIds : " + actionItemIds);
		if (actionItemIds == "" || actionItemIds == null) {
			log.info("ActionItemController.deleteActionItemsById() Empty Input Exception : Action Item ids are empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MESSAGE);
		}
		List<Integer> actualAcIds = null;
		if (actionItemIds != "") {
			String[] idsFromUI = actionItemIds.split(",");
			List<String> idsList = Arrays.asList(idsFromUI);
			// convert string of ids to Integer ids
			actualAcIds = idsList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
		}
		try {
			log.info("ActionItemController.deleteActionItemsById() is under execution...");
			boolean isAllDeleted = actionItemService.deleteAllActionItemsById(actualAcIds);
			log.info("ActionItemController.deleteActionItemsById() is executed succesfully");
			return new ResponseEntity<>(isAllDeleted, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionItemController.deleteActionItemsById() exited with exception : Exception occured while deleting action items "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_DELETE_MESSAGE);
		}
	}

	/**
	 * 
	 * @param actionItems
	 * @return
	 */
	@PostMapping("/generate-actions")
	public ResponseEntity<?> generateActionItems(@RequestBody List<ActionItem> actionItemList) {
		log.info("ActionItemController.generateActionItems() entered with args : actionItemList ");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info("ActionItemController.generateActionItems() EmptyListException : Action Items list is empty");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MESSAGE);
		}
		try {
			log.info("ActionItemController.generateActionItems() is under execution...");
			boolean isGenerated = actionItemService.generateActionItems(actionItemList);
			log.info("ActionItemController.generateActionItems() executed sucessfully");
			return new ResponseEntity<>(isGenerated, HttpStatus.OK);
		} catch (Exception e) {
			log.info("ActionItemController.generateActionItems() exited with exception : " + e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GENERATE_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GENERATE_MESSAGE);
		}
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public ResponseEntity<?> getActionItems() {
		log.info("ActionItemController.getActionItems() entered");
		try {
			log.info("ActionItemController.getActionItems() is under excecution...");
			List<ActionItem> actionItemList =  actionItemService.getActionItemList();
			log.info("ActionItemController.getActionItems() is executed successfully");
			return new ResponseEntity<>(actionItemList, HttpStatus.OK);
		} catch (Exception e) {
			log.info("ActionItemController.getActionItems() exited with exception: Exception occurred while getting action items : "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_MESSAGE);
		}
	}

	/**
	 * 
	 * @param emailId (userId)
	 * @return
	 */
	@GetMapping("/all/{emailId}")
	public ResponseEntity<?> FetchActionItemsByEmailId(@PathVariable("emailId") String email) {
		log.info("ActionItemController.FetchActionItemsByEmailId() entered with args : " + email);
		if (email.equals("") || email == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("ActionItemController.FetchActionItemsByEmailId() is under execution...");
			List<ActionItem> actionItemList = actionItemService.getActionItemsByUserId(email);
			log.info("ActionItemController.FetchActionItemsByEmailId() executed succesfully");
			return new ResponseEntity<>(actionItemList, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionItemController.FetchActionItemsByEmailId() exited with exception : Exception ocuured while fetching action items of a user : "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_MESSAGE);
		}

	}

	/**
	 * 
	 * @param eventId
	 * @return
	 */
	@GetMapping("/ac-items/{meetingId}")
	public ResponseEntity<?> getActionItemsByMeetingId(@PathVariable Integer meetingId) {
		log.info("ActionItemController.getActionItemsByMeetingId() entered with args  meetingId : " + meetingId);
		if (meetingId == null || meetingId < 1) {
			log.info(
					"ActionItemController.getActionItemsByMeetingId() Empty Input Exception : Meeting id is empty or invalid");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MESSAGE);
		}
		try {
			log.info("ActionItemController.getActionItemsByMeetingId() is under execution");
			ActionItemListVO acItemsListVO = actionItemService.getActionItemsByMeetingId(meetingId);
			log.info("ActionItemController.getActionItemsByMeetingId() executed sucessfully");
			return new ResponseEntity<>(acItemsListVO, HttpStatus.OK);
		} catch (Exception e) {
			log.info("ActionItemController.getActionItemsByMeetingId() exited with exception : " + e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_GET_MESSAGE);
		}
	}

	/**
	 * 
	 * @param actionItemList
	 * @return
	 */
	@PostMapping("/convert-task")
	public ResponseEntity<?> processActionItemsToTasks(@RequestBody List<ActionItem> actionItemList) {
		log.info("ActionsController.processActionItemsToTasks() entered with args : actionItemsList");
		if (actionItemList.size() < 1 || actionItemList == null) {
			log.info(
					"ActionItemController.processActionItemsToTasks() Empty List Exception : Action Items list is empty or null");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_LIST_EMPTY_MESSAGE);
		}
		try {
			log.info("ActionsController.processActionItemsToTasks() is under execution...");
			List<Task> taskList = actionItemService.convertActionItemsToTasks(actionItemList);
			log.info("ActionsController.processActionItemsToTasks() executed successfully");
			return new ResponseEntity<>(taskList, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"ActionsController.processActionItemsToTasks() exited with exception : An Exception occurred while converting action items to tasks "
							+ e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_CONVERTTOTASK_MESSAGE);
		}

	}

}
