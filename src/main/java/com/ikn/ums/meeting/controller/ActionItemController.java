package com.ikn.ums.meeting.controller;

import java.util.Arrays;
import java.util.List;
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
import com.ikn.ums.meeting.service.ActionItemService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/actions")
@Slf4j
public class ActionItemController {

    @Autowired 
    private ActionItemService actionItemService;
    
    //Saving the Action Item in Database
    
    @PostMapping("/save")
	public ResponseEntity<?> createActionItem(@RequestBody ActionItem actions ) {
		
		//ActionsDto str= service.createActionItem(actionModel.getEventid(),actionModel.getActionTitle(),actionModel.getDescription(),actionModel.getActionPriority(),
				//actionModel.getActionStatus(),actionModel.getStartDate(),actionModel.getEndDate());
		try {
			ActionItem str= actionItemService.createActionItem(actions);
			System.out.println(str);
			return new ResponseEntity<>(str,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			
		}

	}
	/*
	 * Generates the Action Items Based on Transcript
	 */
	@PostMapping("/generate-actions")
	public ResponseEntity<?> generateActionItems(@RequestBody List<ActionItem> actionItems){
		
		try {
			boolean value = actionItemService.generateActions(actionItems);
			
			return new ResponseEntity<>(value,HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	//Fetch all Action items
	
	@GetMapping("/all")
	public ResponseEntity<?> getActionItem(){
		try {
		
			return new ResponseEntity<>(actionItemService.fetchActionItemList(),HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//Fetching the Single action item
	@GetMapping("/{id}")
	public ResponseEntity<?> getSingleActionItem(@PathVariable Integer id){
		try {
			return new ResponseEntity<>(actionItemService.getSingleActionItem(id),HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/*
	 *   Fetch the Action Items Based on the user Id 
	 */
	@GetMapping("/all/{email}")
	public ResponseEntity<?> FetchActionItemsByEmailId(@PathVariable ("email") String email){
		
		try {
			
			List<ActionItem> list = actionItemService.fetchActionItemsByEmail(email);
			return new ResponseEntity<>(list,HttpStatus.OK);
			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateActionItem(@PathVariable("id") Integer actionItemid, @RequestBody ActionItem actionItem){ 
		try {
			actionItem.setActionItemId(actionItemid);
			return new ResponseEntity<>(actionItemService.updateActionItem(actionItem),HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<?> deleteActionItem(@PathVariable("id") Integer actionItemid){
		
		try { 
			Integer s= actionItemService.deleteActionItem(actionItemid);
			//String str="ActionItem Deleted Successfully";
			return new ResponseEntity<>(s,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * 
	 * @param eventId
	 * @return
	 */
	@GetMapping("/ac-items/{meetingId}")
	public ResponseEntity<?> getActionItemsByEventId(@PathVariable Integer meetingId){
		ActionItemListVO acItemsListVO = actionItemService.fetchActionItemsOfEvent(meetingId);
		return new ResponseEntity<>(acItemsListVO, HttpStatus.OK);
	}
	
	
	
	@DeleteMapping("/delete/{acItemIds}")
	public ResponseEntity<?> deleteActionItemsById(@PathVariable String acItemIds){
		System.out.println(acItemIds);
		List<Integer> actualAcIds = null;
		if(acItemIds != "") {
			String[] idsFromUI = acItemIds.split(",");
			List<String> idsList =  Arrays.asList(idsFromUI);
			//convert string of ids to Integer ids
			actualAcIds = idsList.stream()
                     .map(s -> Integer.parseInt(s))
                     .collect(Collectors.toList());
		}
		try {
			boolean isAllDeleted = actionItemService.deleteAllActionItemsById(actualAcIds);
			return new ResponseEntity<>(isAllDeleted, HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>("error while deleting, please try later", HttpStatus.OK);
		}
	}
	
	//TODO: get action items based on userid
	
	@PostMapping("/convert-task")
	public ResponseEntity<?> conversionOfTask(@RequestBody List<ActionItem> actionItemList ){
		log.info("ActionsController.conversionOfTask() entered");
		try {
			
			return new  ResponseEntity<>(actionItemService.sendToTasks(actionItemList),HttpStatus.OK);			
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	

}
