package com.ikn.ums.meeting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.VO.EventVO;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.service.MeetingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/meetings")
public class MeetingController {
	
	@Autowired
	private MeetingService meetingService;
	
	@GetMapping("/save")
	public ResponseEntity<?> createEvent(){
		//TODO: create event
		return null;
	}
	
	
	/**
	 * delete the action items of an event by commnunicating with action items microservice
	 * @param actionItemIds
	 * @return
	 */
	@GetMapping("/delete/ac-items/{eventId}/{actionItemIds}")
	public ResponseEntity<?> deleteActionItemsOfEvent(@PathVariable Integer eventId, @PathVariable String actionItemIds){
		log.info("EventController.deleteActionItemsOfEvent() entered with args : eventId - "+eventId+" actionItemIds - "+actionItemIds);
		try {
			log.info("EventController.deleteActionItemsOfEvent() is under execution");
			//String[] idsFromUI = actionItemIds.split(",");
			//List<String> idsList =  Arrays.asList(idsFromUI);
			//convert string of ids to Integer ids
			// List<Integer> actualIds = idsList.stream()
              //       .map(s -> Integer.parseInt(s))
                //     .collect(Collectors.toList());
			boolean isAllDeleted = meetingService.removeActionItemsOfEvent(actionItemIds, eventId);
			System.out.println(isAllDeleted);
			if(isAllDeleted) {
				log.info("EventController.deleteActionItemsOfEvent() exiting successfully by returning "+isAllDeleted);
				return new ResponseEntity<Boolean>(isAllDeleted,HttpStatus.OK);
			}else {
				log.info("EventController.deleteActionItemsOfEvent() exiting successfully by returning "+isAllDeleted);
				return new ResponseEntity<Boolean>(isAllDeleted,HttpStatus.OK);
			}
		}
		catch (Exception e) {
			log.info("EventController.deleteActionItemsOfEvent() exited with exception "+e.getMessage());
			ControllerException umsCE = new ControllerException( ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG+" "+e.fillInStackTrace());
			return new ResponseEntity<>(umsCE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("")
	public ResponseEntity<?> editActionItemOfEvent(Integer id){
		return null;
		
	}
	
	/**
	 * get all organized events of a user based on userId(email)
	 * 
	 * @param username
	 * @return list of user organized events
	 */
	@GetMapping(path = "/attended/{userEmailId}")
	public ResponseEntity<?> getUserAttendedMeetings(@PathVariable String userEmailId) {
		if(userEmailId == "" || userEmailId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingController.getUserAttendedMeetings() entered with args : "+userEmailId);
		try {
			log.info("MeetingController.getUserAttendedMeetings() is under excution...");
			List<EventVO> eventList =  meetingService.getUserAttendedMeetings(userEmailId);
			log.info("MeetingController.getUserAttendedMeetings() is executed successfully...");
			return new ResponseEntity<>(eventList, HttpStatus.OK);
		}catch (Exception e) {
			log.info("MeetingController.getUserAttendedMeetings() exited with exception : "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
		
	}

    /*
     * Get Organized Meeting Details of logged-in user
     * @param email id
     */
	@GetMapping("/organized/{userEmailId}")
	public ResponseEntity<?> getUserOrganizedEvents(@PathVariable String userEmailId){
		log.info("MeetingController.getUserEventsByEmailId() entered with args: userEmailId"+userEmailId);
		if(userEmailId.equals("")) {
			log.info("MeetingController.getUserEventsByEmailId() userEmailId: isEmpty");
		}
		log.info("MeetingController.getUserEventsByEmailId() under execution ");
		try {
			
			List<EventVO> meetingList=meetingService.getUserEventsByEmailId(userEmailId);
			log.info("MeetingController.getUserEvnetsByEmailId() exited Successfully");
			return new ResponseEntity<>(meetingList,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.info("MeetingController.getUserEventsByEmailId() exited with Exception: Exception occured while getting user organized meetings"
					+e.getMessage());
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/")
	public ResponseEntity<?> processCurrentBatchProcessingSourceData(@RequestBody List<List<Meeting>> currentBatchProcessUserMeetingsList){
		log.info("MeetingController.processCurrentBatchProcessingSourceData() entered with args : currentBatchProcessUserMeetingsList");
		if(currentBatchProcessUserMeetingsList.size() < 0) {
			log.info("Empty meetings list from current batch processing");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_MESSAGE);
		}
		log.info("MeetingController.processCurrentBatchProcessingSourceData() is under execution");
		try {
			String message = "";
			meetingService.saveAllUserMeetingsListOfCurrentBatchProcess(currentBatchProcessUserMeetingsList);
			message = "Current batch meeting details saved sucessfully";
			log.info("MeetingController.processCurrentBatchProcessingSourceData() executed successfully");
			return new ResponseEntity<>(message, HttpStatus.CREATED);
		}catch (Exception e) {
			log.info("Exception occured while saving current batch process meetings : "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}
	
	@GetMapping("/all/{emailId}")
	public ResponseEntity<?> getAllMeetingsOfUserId(@PathVariable String emailId){
		log.info("MeetingController.getAllMeetingsOfUserId() entered with args : "+emailId);
		if(emailId.equalsIgnoreCase("") || emailId == null) {
			log.info("MeetingsServiceImpl.getAllMeetingsOfUserId() : userId/emailId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("MeetingsServiceImpl.getAllMeetingsOfUserId() is under execution...");
			List<Meeting> userMeetingList = meetingService.getAllMeetingsByUserId(emailId);
			log.info("MeetingsServiceImpl.getAllMeetingsOfUserId() is executed successfully");
			return new ResponseEntity<>(userMeetingList, HttpStatus.OK);
		}catch (Exception e) {
			log.info("Exception occured while fetching user meetings : "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}
	
	
}
