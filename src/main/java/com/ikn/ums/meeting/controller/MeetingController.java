package com.ikn.ums.meeting.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ikn.ums.meeting.dto.MeetingDto;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.ControllerException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.MeetingModel;
import com.ikn.ums.meeting.service.MeetingService;
import com.netflix.servo.util.Strings;

import io.micrometer.core.lang.Nullable;
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
	 * 
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<MeetingDto> createMeeting(@RequestBody MeetingModel meetingModel) {
		log.info("createMeeting() entered with args : meeting object");
		if (meetingModel == null) {
			log.info("createMeeting() EntityNotFoundException : meeting object is null ");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG);

		}
		try {
			log.info("createMeeting() is under execution...");
			MeetingDto createdMeeting = meetingService.createMeeting(meetingModel);
			return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
		} catch (EntityNotFoundException businessEx) {
			log.error("createMeeting()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("createMeeting() : Exception occured while saving meeting " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CREATE_UNSUCCESS_MSG);
		}
	}
	
	/**
	 * update an exsiting teams meeting meeting - because batch process would have updated its attendance report
	 */
	@PutMapping("/update/batchevent")
	public ResponseEntity<Meeting> updateMeetingFromBatchProcess(@RequestBody Meeting updatedMeetingFromBatchProcess){
		log.info("updateMeeting() entered with args : Meeting object");
		if(updatedMeetingFromBatchProcess == null) {
			log.info("updateMeeting() EntityNotFoundException : meeting obejct is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG);
		}
		try {
			Meeting updatedBatchProcessMeeting = meetingService.updateMeetingDetailsFromBatchProcess(updatedMeetingFromBatchProcess);
			return new ResponseEntity<>(updatedBatchProcessMeeting,HttpStatus.PARTIAL_CONTENT);
		}catch (EntityNotFoundException businessException) {
			log.error("createMeeting()  Business Exception occured " + businessException.getMessage(), businessException);
			throw businessException;
		}catch (Exception e) {
			log.error("createMeeting() : Exception occured while saving meeting " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CREATE_UNSUCCESS_MSG);
		}
		
	}

	/**
	 * Deletes the selected action items of a single meeting
	 * 
	 * @param eventId
	 * @param actionItemIds
	 * @return
	 */
	@GetMapping("/delete/ac-items/{id}/{actionItemIds}")
	public ResponseEntity<Boolean> deleteActionItemsOfEvent(@PathVariable("id") Integer meetingId,
			@PathVariable String actionItemIds) {
		log.info("deleteActionItemsOfEvent() entered with args : eventId - " + meetingId + " actionItemIds - "
				+ actionItemIds);
		try {
			log.info("deleteActionItemsOfEvent() is under execution...");
			boolean isAllDeleted = meetingService.deleteActionItemsOfMeeting(actionItemIds, meetingId);
			if (isAllDeleted) {
				log.info("deleteActionItemsOfEvent() exiting successfully by returning " + isAllDeleted);
				return new ResponseEntity<Boolean>(isAllDeleted, HttpStatus.OK);
			} else {
				log.info("deleteActionItemsOfEvent() exiting successfully by returning " + isAllDeleted);
				return new ResponseEntity<Boolean>(isAllDeleted, HttpStatus.OK);
			}
		} catch (EmptyInputException businessEx) {
			log.error("deleteActionItemsOfEvent()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("deleteActionItemsOfEvent() exited with exception " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG + " " + e.fillInStackTrace());
		}

	}

	/**
	 * Get all attended meetings of user based on user's emailId/userId
	 * 
	 * @param userEmailId
	 * @return
	 */
	@GetMapping(path = "/attended/{userId}")
	public ResponseEntity<List<MeetingDto>> getUserAttendedMeetings(@PathVariable("userId") String emailId,
			@RequestParam(defaultValue = "", required = false) String meetingTitle,
			@RequestParam(defaultValue = "", required = false) String startDateTime,
			@RequestParam(defaultValue = "", required = false) String endDateTime) {
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserAttendedMeetings() EmptyInputException : userId / emailId is empty.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getUserAttendedMeetings() entered with args - userId/emailId : " + emailId);
		try {
			if (meetingTitle.isBlank() && startDateTime.isBlank() && endDateTime.isBlank()) {
				log.info("getUserAttendedMeetings() is under excution...");
				List<MeetingDto> attendedMeetingList = meetingService.getUserAttendedMeetingsByUserId(emailId);
				log.info("getUserAttendedMeetings() is executed successfully...");
				return new ResponseEntity<>(attendedMeetingList, HttpStatus.OK);
			} else {
				log.info("getUserAttendedMeetings() is under excution...");
				List<MeetingDto> attendedMeetingList = meetingService.getFilteredAttendedMeetings(meetingTitle,
						startDateTime, endDateTime, emailId);
				log.info("getUserAttendedMeetings() is executed successfully...");
				return new ResponseEntity<>(attendedMeetingList, HttpStatus.OK);
			}
		} catch (EmptyInputException businessEx) {
			log.error("getUserAttendedMeetings()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("getUserAttendedMeetings() exited with exception : " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}

	}

	/**
	 * Get all orgnanized meetings of user based on user's emailId/userId
	 * 
	 * @param userEmailId
	 * @return
	 */
	@GetMapping("/organized/{userId}")
	public ResponseEntity<List<MeetingDto>> getUserOrganizedMeetings(@PathVariable("userId") String emailId,
			@RequestParam(defaultValue = "", required = false) String meetingTitle,
			@Nullable @RequestParam(required = false) String startDate,
			@Nullable @RequestParam(required = false) String endDate) {
		log.info("getUserOrganizedMeetings() entered with args - userId/emailId : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserOrganizedMeetings() userEmailId: isEmpty");
		}
		log.info("getUserOrganizedMeetings() under execution ");
		try {
			if (meetingTitle.isBlank() && startDate == null && endDate == null) {
				List<MeetingDto> meetingList = meetingService.getUserOrganizedMeetingsByUserId(emailId);
				log.info("getUserOrganizedMeetings() exited Successfully without filters");
				return new ResponseEntity<>(meetingList, HttpStatus.OK);
			} else {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

				// Parse the string to LocalDateTime
				LocalDateTime localStartDateTime = startDate.isBlank() ? null
						: LocalDateTime.parse(startDate, formatter);
				LocalDateTime localEndDateTime = endDate.isBlank() ? null : LocalDateTime.parse(endDate, formatter);
				List<MeetingDto> filteredMeetingList = meetingService.getFilteredOrganizedMeetings(meetingTitle,
						localStartDateTime, localEndDateTime, emailId);
				log.info("getUserOrganizedMeetings() exited Successfully with filters");
				return new ResponseEntity<>(filteredMeetingList, HttpStatus.OK);
			}
		} catch (EmptyInputException businessEx) {
			log.error("getUserOrganizedMeetings()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("getUserOrganizedMeetings() General Exception occured while getting user organized meetings"
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}

	/**
	 * Save all meetings obtained from batch process into meetings microservice
	 * tables.
	 * 
	 * @param currentBatchProcessUserMeetingsList
	 * @return
	 */
	@PostMapping("/")
	public ResponseEntity<String> processCurrentBatchProcessingSourceData(
			@RequestBody List<List<Meeting>> currentBatchProcessUserMeetingsList) {
		log.info("processCurrentBatchProcessingSourceData() entered with args : currentBatchProcessUserMeetingsList");
		if (currentBatchProcessUserMeetingsList.isEmpty()) {
			log.info("Empty meetings list from current batch processing");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_MSG);
		}
		log.info("processCurrentBatchProcessingSourceData() is under execution");
		try {
			var message = "";
			meetingService.saveAllUserMeetingsListOfCurrentBatchProcess(currentBatchProcessUserMeetingsList);
			message = "Current batch meeting details saved sucessfully";
			log.info("processCurrentBatchProcessingSourceData() executed successfully");
			return new ResponseEntity<>(message, HttpStatus.CREATED);
		} catch (EmptyListException businessEx) {
			log.error(
					"processCurrentBatchProcessingSourceData()  Business Exception occured " + businessEx.getMessage(),
					businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error(
					"processCurrentBatchProcessingSourceData() General Exception occured while saving current batch process meetings : "
							+ e.getMessage(),
					e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}

	/**
	 * Gets all the meetings of user based on user's emailId/userId
	 * 
	 * @param emailId
	 * @return
	 */
	@GetMapping("/all/{userId}")
	public ResponseEntity<List<MeetingDto>> getAllMeetingsOfUser(@PathVariable("userId") String emailId) {
		log.info("getAllMeetingsOfUser() entered with args : " + emailId);
		if (emailId.equalsIgnoreCase("") || emailId == null) {
			log.info("getAllMeetingsOfUser() : userId/emailId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("getAllMeetingsOfUser() is under execution...");
			List<MeetingDto> userMeetingList = meetingService.getAllMeetingsByUserId(emailId);
			log.info("getAllMeetingsOfUser() is executed successfully");
			return new ResponseEntity<>(userMeetingList, HttpStatus.OK);
		} catch (EmptyInputException businessEx) {
			log.error("getAllMeetingsOfUser()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error(
					"getAllMeetingsOfUser() General Exception occured while fetching user meetings : " + e.getMessage(),
					e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_CONTROLLER_EXCEPTION_MSG);
		}
	}

	/**
	 * Get assigned meetings count based on user's emailId/userId
	 * 
	 * @param emailId
	 * @return
	 */
	@GetMapping(path = "attended/count/{userId}")
	public ResponseEntity<Integer> getUserAttendedEventCount(@PathVariable("userId") String emailId) {
		log.info("getUserAttendedEventCount() entered with args : " + emailId);
		if (emailId.equalsIgnoreCase("") || emailId == null) {
			log.info("getUserAttendedEventCount() userEmailId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("getUserAttendedEventCount() is under execution ");
			Integer count = meetingService.getUserAttendedMeetingCountByUserId(emailId);
			log.info("getUserAttendedEventCount() exited sucessfully by retruning count : " + count);
			return new ResponseEntity<>(count, HttpStatus.OK);
		} catch (EmptyInputException businessEx) {
			log.error("getUserAttendedEventCount()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("getUserAttendedEventCount() Exception occured while getting user attended events count "
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ATTENDED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ATTENDED_COUNT_UNSUCCESS_MSG);
		}
	}

	/**
	 * Gets the orgnanized meetings count based on user's emailId/userId
	 * 
	 * @param emailId
	 * @return
	 */
	@GetMapping(path = "/organized/count/{userId}")
	public ResponseEntity<Integer> getUserOragnizedMeetingCount(@PathVariable("userId") String emailId) {
		log.info("getUserOragnizedEventCount() entered with args : userId / emailId : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserOragnizedEventCount() EmptyInputException : userId / emailId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		try {
			log.info("getUserOragnizedEventCount() is under execution");
			Integer count = meetingService.getUserOragnizedMeetingCountByUserId(emailId);
			log.info("getUserOragnizedEventCount() exited succesfully by returning organizedEventsCount : " + count);
			return new ResponseEntity<>(count, HttpStatus.OK);
		} catch (EmptyInputException businessEx) {
			log.error("getUserOragnizedEventCount()  Business Exception occured " + businessEx.getMessage(),
					businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("getUserOragnizedEventCount() General Exception occured while getting organizedEventsCount "
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_MSG);
		}
	}

	/**
	 * Gets a single meeting based on meetingId (PK)
	 * 
	 * @param meetingId
	 * @return
	 */
	@GetMapping("/{meetingId}")
	public ResponseEntity<MeetingDto> getSingleMeeting(@PathVariable("meetingId") Long meetingId) {
		log.info("getSingleMeeting() entered with args - meetingId");
		try {
			log.info("getSingleMeeting() is under execution...");
			MeetingDto meeting = meetingService.getMeetingDetails(meetingId);
			log.info("getSingleMeeting() executed successfully");
			return new ResponseEntity<>(meeting, HttpStatus.OK);
		} catch (EmptyInputException | EntityNotFoundException businessEx) {
			log.error("getSingleMeeting()  Business Exception occured " + businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {

			log.error("getSingleMeeting()  Business Exception occured " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_MSG);
		}

	}

	@GetMapping("/MeetingsChartData")
	public ResponseEntity<List<Object>> getCountReportOfAttendedAndOrganisedMeetings(
			@RequestParam("startdate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam("emailId") String email) {
		log.info("getCountOfAttendedAndOrganisedMeetings() entered");
		try {
			log.info("getCountOfAttendedAndOrganisedMeetings() is under execution...");
			List<Object> MeetingdatasInWeek = new ArrayList<>();
			List<Long> attendedMeetingInWeek = meetingService.countEmailOccurrences(startDate, endDate, email);
			List<Long> OrganisedMeetingInWeek = meetingService.countOrganisedMeetingOccurrence(startDate, endDate,
					email);
			MeetingdatasInWeek.add(attendedMeetingInWeek);
			MeetingdatasInWeek.add(OrganisedMeetingInWeek);
			log.info("getCountOfAttendedAndOrganisedMeetings() executed successfully.");
			return new ResponseEntity<>(MeetingdatasInWeek, HttpStatus.OK);
		} catch (EmptyInputException businessEx) {
			log.error("getCountOfAttendedAndOrganisedMeetings()  Business Exception occured " + businessEx.getMessage(),
					businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("getCountOfAttendedAndOrganisedMeetings()  Business Exception occured " + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_REPORT_ORG_ATT_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_REPORT_ORG_ATT_COUNT_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/MeetingsChartDataForYear")
	public ResponseEntity<List<Object>> getCountofAttendedAndOrganisedMeetingsInYear(
			@RequestParam("startdate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam("emailId") String email) {
		log.info("getCountofAttendedAndOrganisedMeetingsInYear() entered");
		try {
			log.info("getCountofAttendedAndOrganisedMeetingsInYear() is under execution...");
			List<Object> MeetingdatasInYear = new ArrayList<>();
			List<Long> attendedMeetingInYear = meetingService.countAttendedMeetingForYear(startDate, endDate, email);
			List<Long> OrganisedMeetingInYear = meetingService.countOrganisedMeetingForYear(startDate, endDate, email);
			MeetingdatasInYear.add(attendedMeetingInYear);
			MeetingdatasInYear.add(OrganisedMeetingInYear);
			log.info("getCountofAttendedAndOrganisedMeetingsInYear() executed successfully.");
			return new ResponseEntity<>(MeetingdatasInYear, HttpStatus.OK);
		} catch (EmptyInputException businessEx) {
			log.error("getCountofAttendedAndOrganisedMeetingsInYear()  Business Exception occured "
					+ businessEx.getMessage(), businessEx);
			throw businessEx;
		} catch (Exception e) {
			log.error("getCountofAttendedAndOrganisedMeetingsInYear()  Business Exception occured " + e.getMessage(),
					e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_REPORT_ORG_ATT_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_REPORT_ORG_ATT_COUNT_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/department/{departmentId}")
	public ResponseEntity<List<MeetingDto>> getMeetingsByDepartment(@PathVariable Long departmentId) {
		log.info("getMeetingsByDepartment() entered with args : departmentId - " + departmentId);
		try {
			log.info("getMeetingsByDepartment() is under execution... ");
			List<MeetingDto> departmentMeetingList = meetingService.getMeetingsByDepartment(departmentId);
			log.info("getMeetingsByDepartment() is executed sucessfully.");
			return new ResponseEntity<>(departmentMeetingList, HttpStatus.OK);
		} catch (EmptyInputException businessException) {
			log.error(
					"getMeetingsByDepartment() Business Exception has encountered while fetching meetings by department.");
			throw businessException;
		} catch (Exception e) {
			log.error(
					"getMeetingsByDepartment() General Exception has encountered while fetching meetings by department.");
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_BYDEPT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_BYDEPT_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<MeetingDto>> getAllMeetings() {
		log.info("getAllMeetings() entered with args - meetingId");
		try {
			log.info("MeetingController.getAllMeetings() is under execution...");
			log.info("getAllMeetings() is under execution...");
			List<MeetingDto> meetingObjectList = meetingService.getAllMeetings();
			log.info("getAllMeetings() executed successfully");
			return new ResponseEntity<>(meetingObjectList, HttpStatus.OK);
		} catch (Exception e) {
			log.error(
					"getAllMeetings() exited with execption :General Exception has encountered while fetching meetings"
							+ e.getMessage(),
					e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_GET_ORGANIZED_COUNT_UNSUCCESS_MSG);
		}

	}

	@GetMapping("/dept-count")
	public ResponseEntity<List<Object[]>> getAllDepartmentsMeetingsCount() {
		log.info("getAllDepartmentsMeetingsCount() entered with no args");
		try {
			log.info("getAllDepartmentsMeetingsCount() is under execution...");
			List<Object[]> count = meetingService.getAllDepartmentsMeetingCount();
			log.info("getAllDepartmentsMeetingsCount() executed successfully.");
			return new ResponseEntity<>(count, HttpStatus.OK);
		} catch (Exception e) {
			log.error(
					"getAllDepartmentsMeetingsCount() General Exception occured while fetching meetings by department."
							+ e.getMessage(),
					e);
			throw new ControllerException(ErrorCodeMessages.ERR_MEETINGS_BYDEPT_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_MEETINGS_BYDEPT_UNSUCCESS_MSG);
		}

	}

}
