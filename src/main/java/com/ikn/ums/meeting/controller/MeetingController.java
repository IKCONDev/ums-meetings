package com.ikn.ums.meeting.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.MeetingModel;
import com.ikn.ums.meeting.service.MeetingService;

import lombok.extern.slf4j.Slf4j;

/**
 * Meeting Rest Controller 
 */
@Slf4j
@RestController
@RequestMapping("/meetings")
public class MeetingController {
	
	@Autowired
	private MeetingService meetingService;
	
	/**
	 * Creates a manual meeting that is created from UMS application.
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<?> createMeeting(@RequestBody MeetingModel meetingModel){
		log.info("MeetingController.createMeeting() entered with args : meeting object");
		if(meetingModel.equals(null) || meetingModel == null) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG);
			
		}
		try {
			log.info("MeetingController.createMeeting() is under execution...");
			Meeting createdMeeting = meetingService.createMeeting(meetingModel);
			return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
		}catch (Exception e) {
			log.info("MeetingController.createMeeting() : Exception occured while saving meeting "+e.getMessage());
			ControllerException umsCE = new ControllerException( ErrorCodeMessages.ERR_MEETINGS_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CREATE_UNSUCCESS_MSG);
			throw umsCE;
		}
	}
	
	/**
	 * Deletes the selected action items of a single meeting
	 * @param eventId
	 * @param actionItemIds
	 * @return
	 */
	@GetMapping("/delete/ac-items/{id}/{actionItemIds}")
	public ResponseEntity<?> deleteActionItemsOfEvent(@PathVariable("id") Integer meetingId, @PathVariable String actionItemIds){
		log.info("EventController.deleteActionItemsOfEvent() entered with args : eventId - "+meetingId+" actionItemIds - "+actionItemIds);
		try {
			log.info("EventController.deleteActionItemsOfEvent() is under execution...");
			boolean isAllDeleted = meetingService.deleteActionItemsOfMeeting(actionItemIds, meetingId);
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
			throw umsCE;
		}
		
	}
	
	/**
	 * Get all attended meetings of user based on user's emailId/userId
	 * @param userEmailId
	 * @return
	 */
	@GetMapping(path = "/attended/{userId}")
	public ResponseEntity<?> getUserAttendedMeetings(@PathVariable("userId") String emailId) {
		if(emailId == "" || emailId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingController.getUserAttendedMeetings() entered with args - userId/emailId : "+emailId);
		try {
			log.info("MeetingController.getUserAttendedMeetings() is under excution...");
			List<Meeting> attendedMeetingList =  meetingService.getUserAttendedMeetingsByUserId(emailId);
			log.info("MeetingController.getUserAttendedMeetings() is executed successfully...");
			return new ResponseEntity<>(attendedMeetingList, HttpStatus.OK);
		}catch (Exception e) {
			log.info("MeetingController.getUserAttendedMeetings() exited with exception : "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
		
	}

    /**
     * Get all orgnanized meetings of user based on user's emailId/userId
     * @param userEmailId
     * @return
     */
	@GetMapping("/organized/{userId}")
	public ResponseEntity<?> getUserOrganizedMeetings(@PathVariable("userId") String emailId){
		log.info("MeetingController.getUserOrganizedMeetings() entered with args - userId/emailId : "+emailId);
		if(emailId.equals("")) {
			log.info("MeetingController.getUserOrganizedMeetings() userEmailId: isEmpty");
		}
		log.info("MeetingController.getUserOrganizedMeetings() under execution ");
		try {
			
			List<Meeting> meetingList=meetingService.getUserOrganizedMeetingsByUserId(emailId);
			log.info("MeetingController.getUserOrganizedMeetings() exited Successfully");
			return new ResponseEntity<>(meetingList,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.info("MeetingController.getUserOrganizedMeetings() exited with Exception: Exception occured while getting user organized meetings"
					+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}
	
	/**
	 * Save all meetings obtained from batch process into meetings microservice tables.
	 * @param currentBatchProcessUserMeetingsList
	 * @return
	 */
	@PostMapping("/")
	public ResponseEntity<?> processCurrentBatchProcessingSourceData(@RequestBody List<List<Meeting>> currentBatchProcessUserMeetingsList){
		log.info("MeetingController.processCurrentBatchProcessingSourceData() entered with args : currentBatchProcessUserMeetingsList");
		if(currentBatchProcessUserMeetingsList.size() < 0) {
			log.info("Empty meetings list from current batch processing");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_MSG);
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
	
	/**
	 * Gets all the meetings of user based on user's emailId/userId
	 * @param emailId
	 * @return
	 */
	@GetMapping("/all/{userId}")
	public ResponseEntity<?> getAllMeetingsOfUser(@PathVariable("userId") String emailId){
		log.info("MeetingController.getAllMeetingsOfUser() entered with args : "+emailId);
		if(emailId.equalsIgnoreCase("") || emailId == null) {
			log.info("MeetingsServiceImpl.getAllMeetingsOfUser() : userId/emailId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("MeetingsServiceImpl.getAllMeetingsOfUser() is under execution...");
			List<Meeting> userMeetingList = meetingService.getAllMeetingsByUserId(emailId);
			log.info("MeetingsServiceImpl.getAllMeetingsOfUser() is executed successfully");
			return new ResponseEntity<>(userMeetingList, HttpStatus.OK);
		}catch (Exception e) {
			log.info("Exception occured while fetching user meetings : "+e.getMessage());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}
	
	/**
	 * Get assigned meetings count based on user's emailId/userId
	 * @param emailId
	 * @return
	 */
	@GetMapping(path = "attended/count/{userId}")
	public ResponseEntity<?> getUserAttendedEventCount(@PathVariable("userId") String emailId) {
		log.info(
				"TeamsSourceDataBatchProcessController.getUserAttendedEventCount() entered with args : " + emailId);
		if (emailId.equalsIgnoreCase("") || emailId == null) {
			log.info("TeamsSourceDataBatchProcessController.getUserAttendedEventCount() userEmailId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("TeamsSourceDataBatchProcessController.getUserAttendedEventCount() is under execution ");
			Integer count = meetingService.getUserAttendedMeetingCountByUserId(emailId);
			log.info(
					"TeamsSourceDataBatchProcessController.getUserAttendedEventCount() exited sucessfully by retruning count : "
							+ count);
			return new ResponseEntity<>(count, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"TeamsSourceDataBatchProcessController.getUserAttendedEventCount() exited with exception : Exception occured while getting user attended evebts count "
							+ e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ATTENDED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ATTENDED_COUNT_UNSUCCESS_MSG);
		}
	}
	
	/**
	 * Gets the orgnanized meetings count based on user's emailId/userId
	 * @param emailId
	 * @return
	 */
	@GetMapping(path = "/organized/count/{userId}")
	public ResponseEntity<?> getUserOragnizedMeetingCount(@PathVariable("userId") String emailId) {
		log.info("TeamsSourceDataBatchProcessController.getUserOragnizedEventCount() entered with args : userEmailId : "
				+ emailId);
		if (emailId.equalsIgnoreCase("") || emailId == null) {
			log.info(
					"TeamsSourceDataBatchProcessController.getUserOragnizedEventCount() exited with exception : userEmailid is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("TeamsSourceDataBatchProcessController.getUserOragnizedEventCount() is under execution");
			Integer count = meetingService.getUserOragnizedMeetingCountByUserId(emailId);
			log.info(
					"TeamsSourceDataBatchProcessController.getUserOragnizedEventCount() exited succesfully by returning organizedEventsCount : "
							+ count);
			return new ResponseEntity<>(count, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"TeamsSourceDataBatchProcessController.getUserOragnizedEventCount() exited with exeception : Exception occured while getting organizedEventsCount "
							+ e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_MSG);
		}
	}
	
	/**
	 * Gets a single meeting based on meetingId (PK)
	 * @param meetingId
	 * @return
	 */
	@GetMapping("/{meetingId}")
	public ResponseEntity<?> getSingleMeeting(@PathVariable("meetingId") Long meetingId){
		log.info("MeetingController.getSingleMeeting() entered with args - meetingId");
		Optional<Meeting> optionalMeetingObject = meetingService.getMeetingDetails(meetingId);
		try {
			log.info("MeetingController.getSingleMeeting() is under execution...");
			Meeting meetingObject = optionalMeetingObject.get();
			log.info("MeetingController.getSingleMeeting() executed successfully");
			return new ResponseEntity<>(meetingObject,HttpStatus.OK);
		}catch (Exception e) {
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_MSG);
		}
		
	}
	
}
