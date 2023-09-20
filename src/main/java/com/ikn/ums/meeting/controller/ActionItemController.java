package com.ikn.ums.meeting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/api/actions/")
public class ActionItemController {

	
    @Autowired 
	
    private ActionItemService service;
    
    //Saving the Action Item in Database
    
	@PostMapping("/create")
	public ResponseEntity<?> createActionItem(@RequestBody ActionItem actions ) {
		
		try {
			ActionItem str= service.createActionItem(actions);
			System.out.println(str);
			return new ResponseEntity<>(str,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			
		}

	}
	
	//Fetch all Action items
	
	@GetMapping("/get-actions")
	public ResponseEntity<?> getActionItem(){
		try {
		
			return new ResponseEntity<>(service.fetchActionItemList(),HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//Fetching the Single action item
	@CrossOrigin(origins="*")
	@GetMapping("/get-action-item/{id}")
	public ResponseEntity<?> getSingleActionItem(@PathVariable Integer id){
		try {
			return new ResponseEntity<>(service.getSingleActionItem(id),HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PutMapping("/update-action/{id}")
	public ResponseEntity<?> updateActionItem(@PathVariable("id") Integer actionItemid, @RequestBody ActionItem actionItem){ 
		try {
			actionItem.setId(actionItemid);
			return new ResponseEntity<>(service.updateActionItem(actionItem),HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@DeleteMapping("delete-action/{id}")
	public ResponseEntity<?> deleteActionItem(@PathVariable("id") Integer actionItemid){
		
		try {
			Integer s= service.deleteActionItem(actionItemid);
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
	@GetMapping("/ac-items/{eventId}")
	public ResponseEntity<?> getActionItemsByEventId(@PathVariable Integer eventId){
		ActionItemListVO acItemsListVO = service.fetchActionItemsOfEvent(eventId);
		return new ResponseEntity<>(acItemsListVO, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @return all action items
	 */
	@GetMapping("/ac-items")
	public ResponseEntity<?> getAllActionItems(){
		ActionItemListVO acItemsListVO = service.fetchActionItems();
		return new ResponseEntity<>(acItemsListVO, HttpStatus.OK);
	}
	
	//TODO: get action items based on userid
	

}
