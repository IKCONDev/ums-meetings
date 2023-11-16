package com.ikn.ums.meeting.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.BusinessException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.MeetingModel;
import com.ikn.ums.meeting.repository.MeetingRepository;
import com.ikn.ums.meeting.service.ActionItemService;
import com.ikn.ums.meeting.service.MeetingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MeetingsServiceImpl implements MeetingService {

	@Autowired
	private ActionItemService actionItemService;

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	@Override
	public boolean deleteActionItemsOfMeeting(String acItemIds, Integer meetingId) {
		log.info("EventServiceImpl.deleteActionItemsOfMeeting() entered with args - actionItemIds : " + acItemIds
				+ " evenId : " + meetingId);
		if (meetingId.equals(null) || meetingId == null || meetingId == 0) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		if(acItemIds.equals(null) || acItemIds == null || acItemIds == "") {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		boolean isDeleted = false;
		log.info("EventServiceImpl.deleteActionItemsOfMeeting() is under execution...");
		List<Integer> actualAcIds = null;
		if (acItemIds != "") {
			String[] idsFromUI = acItemIds.split(",");
			List<String> idsList = Arrays.asList(idsFromUI);
			// convert string of ids to Integer ids
			actualAcIds = idsList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
			actionItemService.deleteAllActionItemsById(actualAcIds);
		}
		isDeleted = true;
		log.info("EventServiceImpl.deleteActionItemsOfMeeting() executed sucessfully");
		return isDeleted;
	}

	@Override
	public List<Meeting> getUserAttendedMeetingsByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getUserAttendedMeetingsByUserId() entered with args : " + emailId);
		if (emailId.equals("") || emailId == null || emailId.equals(null)) {
			log.info("Exception occured while getting user attended meetings : user email is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info(
				"MeetingsServiceImpl.getUserAttendedMeetingsByUserId() calling batch process microservice to get user attended meetings");
		List<Meeting> attendedMeetingList = meetingRepository.findAllAttendedMeetingsByUserId(emailId);
		log.info("MeetingsServiceImpl.getUserAttendedMeetingsByUserId() executed successfully");
		return attendedMeetingList;
	}

	@Override
	public List<Meeting> getUserOrganizedMeetingsByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getUserOrganizedMeetingsByUserId() entered with args - emailId/userId : "+emailId);
		if(emailId == null || emailId.equals(null) || emailId == "") {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingsServiceImpl.getUserOrganizedMeetingsByUserId() is under execution...");
		List<Meeting> meetingList = meetingRepository.findAllMeetingsByUserId(emailId);
		log.info("MeetingsServiceImpl.getUserOrganizedMeetingsByUserId() executed succesfully");
		return meetingList;
	}

	@Transactional
	@Override
	public void saveAllUserMeetingsListOfCurrentBatchProcess(
			List<List<Meeting>> currentBatchProcessingUsersMeetingList) {
		log.info(
				"MeetingsServiceImpl.saveAllUserMeetingsListOfCurrentBatchProcess() entered with args : currentBatchProcessingUsersMeetingList ");
		if (currentBatchProcessingUsersMeetingList.size() < 0) {
			log.info("Empty meetings list from current batch processing ");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_MSG);
		}
		// save each users meetings into db
		currentBatchProcessingUsersMeetingList.forEach(userMeetingList -> {
			userMeetingList.forEach(userMeeting -> {
				// set meeting id's ,attendee id, and transcript id to null which we obtained
				// from batch processing, to auto generate the new meeting id's , attendee id's and
				// transcript id's, for each meeting.
				userMeeting.setMeetingId(null);
				if (userMeeting.getMeetingTranscripts() != null) {
					userMeeting.getMeetingTranscripts().forEach(transcript -> {
						transcript.setId(null);
					});
				}
				if (userMeeting.getAttendees() != null) {
					userMeeting.getAttendees().forEach(attendee -> {
						attendee.setId(null);
					});
				}
			});
			// save each users meeting batch processing records
			List<Meeting> batchProcessedMeetingList = meetingRepository.saveAll(userMeetingList);
			log.info("Auto Meetings " + batchProcessedMeetingList);
		});
		log.info("MeetingsServiceImpl.saveAllUserMeetingsListOfCurrentBatchProcess() exiting successfully");
	}

	@Override
	public List<Meeting> getAllMeetingsByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getAllMeetingsByUserId() entered with args - emailId/userId : " + emailId);
		if (emailId.equalsIgnoreCase("") || emailId == null) {
			log.info("MeetingsServiceImpl.getAllMeetingsByUserId() : userId/emailId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingsServiceImpl.getAllMeetingsByUserId() is under execution");
		List<Meeting> meetingList = meetingRepository.findAllMeetingsByUserId(emailId);
		log.info("MeetingsServiceImpl.getAllMeetingsByUserId() exiting successfully");
		return meetingList;
	}

	@Override
	public Integer getUserAttendedMeetingCountByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getUserAttendedMeetingCountByUserId() entered with args - emailId/userId : "
				+ emailId);
		if (emailId == "" || emailId.equals(null) || emailId == null) {
			log.info(
					"MeetingsServiceImpl.getUserAttendedMeetingCountByUserId() : EmptyInputException - emailId/userId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingsServiceImpl.getUserAttendedMeetingCountByUserId() is under execution...");
		Integer dbAttendedMeetingsCount = meetingRepository.findUserAttendedMeetingCount(emailId);
		log.info("MeetingsServiceImpl.getUserAttendedMeetingCountByUserId() executed succesfully");
		return dbAttendedMeetingsCount;
	}

	@Override
	public Integer getUserOragnizedMeetingCountByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getUserOragnizedMeetingCountByUserId() entered with args - emailId/userId : "
				+ emailId);
		if (emailId == "" || emailId == null || emailId.equals(null)) {
			log.info(
					"MeetingsServiceImpl.getUserOragnizedMeetingCountByUserId() : EmptyInputException - emailId/userId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingsServiceImpl.getUserOragnizedMeetingCountByUserId() is under execution...");
		Integer dbCount = meetingRepository.findUserOrganizedMeetingCount(emailId);
		log.info("MeetingsServiceImpl.getUserOragnizedMeetingCountByUserId() executed succesfully");
		return dbCount;
	}

	@Override
	public Optional<Meeting> getMeetingDetails(Long meetingId) {
		log.info("MeetingsServiceImpl.getMeetingDetails() entered with args - meetingId : " + meetingId);
		if (meetingId == null || meetingId.equals(null) || meetingId == 0) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		log.info("MeetingsServiceImpl.getMeetingDetails() is under execution...");
		Optional<Meeting> meeting = meetingRepository.findById(meetingId);
		log.info("MeetingsServiceImpl.getMeetingDetails() executed successfully.");
		return meeting;

	}

	@Transactional
	@Override
	public Meeting createMeeting(MeetingModel meetingModel) {
		log.info("MeetingServiceImpl.createMeeting() entered with args : meeting object");
		if (meetingModel == null || meetingModel.equals(null)) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG);
		}
		
		log.info("MeetingsServiceImpl.createMeeting() is under execution...");
		Meeting meeting = new Meeting();
		LocalDateTime startdateUTC = meetingModel.getStartDateTime();
		LocalDateTime endDateTimeUTC = meetingModel.getEndDateTime();
		ZonedDateTime startDateTime = startdateUTC.atZone(ZoneId.systemDefault()); 
		ZonedDateTime endDateTime = endDateTimeUTC.atZone(ZoneId.systemDefault());
		ZonedDateTime utcStartDateTime = startDateTime.withZoneSameInstant(ZoneOffset.UTC);
		ZonedDateTime utcEndDateTime = endDateTime.withZoneSameInstant(ZoneOffset.UTC);
		meetingModel.setStartDateTime(utcStartDateTime.toLocalDateTime());
		meetingModel.setEndDateTime(utcEndDateTime.toLocalDateTime());

		Set<Attendee> attendeeList = new HashSet<>();
		modelMapper.map(meetingModel, meeting);
		for (int i = 0; i < meetingModel.getAttendees().length; i++) {
			Attendee attendee = new Attendee();
			attendee.setEmail(meetingModel.getAttendees()[i]);
			attendee.setEmailId(meetingModel.getAttendees()[i]);
			attendee.setType("Required");
			attendee.setStatus("Accepted");
			attendeeList.add(attendee);
		}
		meeting.setEventId("IKCON UMS MANUAL MEETING " + new Random(9999999).nextInt());
		meeting.setAttendees(attendeeList);
		meeting.setCreatedBy(meetingModel.getCreatedBy());
		meeting.setEmailId(meetingModel.getEmailId());
		meeting.setCreatedByEmailId(meetingModel.getCreatedByEmailId());
		meeting.setCreatedDateTime(LocalDateTime.now().toString());
		meeting.setManualMeeting(true);
		Meeting createdMeeting = meetingRepository.save(meeting);
		// send email to meeting attendees if required.
		log.info("MeetingsServiceImpl.createMeeting() executed successfully");
		return createdMeeting;
	}
	public List<Long> countEmailOccurrences(LocalDateTime startDate, LocalDateTime endDate, String email) {
		List<Object[]> MeetingCountsByDay = meetingRepository.findAttendedMeetingCountsByDayOfWeek(startDate,endDate ,email);
		List<Long> attendedMeetingCounts = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
            attendedMeetingCounts.add(0L);
          
        }
		  for (Object[] result : MeetingCountsByDay) {
	            String dayOfWeek = (String) result[0];
	            System.out.println(dayOfWeek+"day of week");
	            Long completedCount = (Long) result[1];
	            System.out.println(completedCount+"completedCount before setting oin list");
	            int dayIndex = (Integer.parseInt(dayOfWeek)) - 1;
	            System.out.println(dayIndex+"dayIndex");
	            attendedMeetingCounts.set(dayIndex, completedCount);
	           
	            System.out.println(completedCount+"completed count");
	            System.out.println(attendedMeetingCounts+"attended meeting count list");
	        }
	 
		log.info(MeetingCountsByDay.toString());
		return attendedMeetingCounts;
    }

	@Override
	public List<Long> countOrganisedMeetingOccurrence(LocalDateTime startDate, LocalDateTime endDate, String email) {
		// TODO Auto-generated method stub
		List<Object[]> MeetingCountsByDay1 = meetingRepository.findCompletedMeetingCountsByDayOfWeek(startDate,endDate ,email);
		List<Long> OrganisedMeetingCounts = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			OrganisedMeetingCounts.add(0L);
          
        }
		System.out.println(OrganisedMeetingCounts+"before for loop");
	   for (Object[] result : MeetingCountsByDay1) {
	            String dayOfWeek1 = (String) result[0];
	            Long completedCount1 = (Long) result[1];
	            int dayIndex = Integer.parseInt(dayOfWeek1) - 1;
	            OrganisedMeetingCounts.set(dayIndex, completedCount1);
	            System.out.println(dayOfWeek1);
	           
	    }
		log.info(MeetingCountsByDay1.toString()+"++++++");
		return OrganisedMeetingCounts;
	}
	
	public List<Long> countOrganisedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String email){
		List<Object[]> MeetingCountsByMonth = meetingRepository.findOrganisedMeetingCountsByMonth(startDate,endDate ,email);
		List<Long> OrganisedMeetingCountsForYear = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			OrganisedMeetingCountsForYear.add(0L);
          
        }
		for (Object[] result : MeetingCountsByMonth) {
			String monthOfYear = (String) result[0];
	        Long OrganisedMeetings = (Long) result[1]; // Use result[1] for attended meetings count
	        int monthIndex = Integer.parseInt(monthOfYear) - 1;
	        OrganisedMeetingCountsForYear.set(monthIndex, OrganisedMeetings);
           /* System.out.println(dayOfWeek);
            System.out.println(completedCount);*/
        }
 
	
	return OrganisedMeetingCountsForYear;
	}
	public List<Long> countAttendedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String email){
		List<Object[]> MeetingCountsByMonth = meetingRepository.findAttendedMeetingCountsByMonth(startDate,endDate ,email);
		List<Long> AttendedMeetingCountsForYear = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			AttendedMeetingCountsForYear.add(0L);
          
        }
		for (Object[] result : MeetingCountsByMonth) {
			String monthOfYear = (String) result[0];
			System.out.println(monthOfYear+"month of year");
	        Long attendedMeetings = (Long) result[1]; // Use result[1] for attended meetings count
	        System.out.println(attendedMeetings+"attendedMeetings");
	        int monthIndex = Integer.parseInt(monthOfYear) - 1;
            AttendedMeetingCountsForYear.set(monthIndex, attendedMeetings);
            System.out.println(AttendedMeetingCountsForYear+"++++");
           /* System.out.println(dayOfWeek);
            System.out.println(completedCount);*/
        }
 
	
	return AttendedMeetingCountsForYear;
	}

	@Override
	public List<Meeting> getFilteredOrganizedMeetings(String meetingTitle, String startDateTime, String endDateTime,
			String emailId) {
		LocalDateTime actualStartDateTime = null;
		if(!startDateTime.isBlank()) {
			actualStartDateTime = LocalDateTime.parse(startDateTime);
		}
		LocalDateTime actualEndDateTime = null;
		if(!endDateTime.isBlank()) {
			actualEndDateTime = LocalDateTime.parse(endDateTime);
		}
		List<Meeting> filteredMeetingList = meetingRepository.findAllFilteredMeetingsByUserId(meetingTitle.isBlank()?null : meetingTitle, actualStartDateTime, actualEndDateTime,emailId);
		return filteredMeetingList;
	}

	@Override
	public List<Meeting> getFilteredAttendedMeetings(String meetingTitle, String startDateTime, String endDateTime,
			String emailId) {
		LocalDateTime actualStartDateTime = null;
		if(!startDateTime.isBlank()) {
			actualStartDateTime = LocalDateTime.parse(startDateTime);
		}
		LocalDateTime actualEndDateTime = null;
		if(!endDateTime.isBlank()) {
			actualEndDateTime = LocalDateTime.parse(endDateTime);
		}
		List<Meeting> filteredAttendedMeetingList = meetingRepository.findAllFilteredAttendedMeetingsByUserId(meetingTitle.isBlank()?null : meetingTitle, actualStartDateTime, actualEndDateTime,emailId);
		return filteredAttendedMeetingList;
	}

}
    

