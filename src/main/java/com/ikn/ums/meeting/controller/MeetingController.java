package com.ikn.ums.meeting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.VO.EventVO;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
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
	
	/*
	 *  Get all user Events based on Login
	 */
	
	
	
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
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USER_EMPTY_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_USER_EMPTY_EXCEPTION_MSG);
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

}
