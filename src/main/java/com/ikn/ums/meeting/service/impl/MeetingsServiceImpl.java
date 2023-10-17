package com.ikn.ums.meeting.service.impl;

import java.time.LocalDateTime;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.BusinessException;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
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


	@Override
	public boolean deleteActionItemsOfMeeting(String acItemIds, Integer meetingId) {
		System.out.println("EventServiceImpl.removeActionItemsOfEvent()");
		log.info("EventServiceImpl.removeActionItemsOfEvent() entered with args - actionItemIds : "+acItemIds+" evenId : "+meetingId);
		boolean isDeleted = false;
			log.info("EventServiceImpl.removeActionItemsOfEvent() is under execution...");
			List<Integer> actualAcIds = null;
			if(acItemIds != "") {
				String[] idsFromUI = acItemIds.split(",");
				List<String> idsList =  Arrays.asList(idsFromUI);
				//convert string of ids to Integer ids
				actualAcIds = idsList.stream()
	                     .map(s -> Integer.parseInt(s))
	                     .collect(Collectors.toList());
			actionItemService.deleteAllActionItemsById(actualAcIds);
			}
			isDeleted = true;
		log.info("EventServiceImpl.removeActionItemsOfEvent() executed sucessfully by returning "+isDeleted);
		return isDeleted;
	}


	@Override
	public List<Meeting> getUserAttendedMeetingsByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getUserAttendedMeetings() entered with args : "+emailId);
		if(emailId.equals("") || emailId == null) {
			log.info("Exception occured while getting user attended meetings : user email is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
			ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("MeetingsServiceImpl.getUserAttendedMeetings() calling batch process microservice to get user attended meetings");
//		ResponseEntity<List<EventVO>> response = restTemplate
//				.exchange("http://UMS-BATCH-SERVICE/teams/events/attended/"+emailId,
//						HttpMethod.GET,null, new ParameterizedTypeReference<List<EventVO>>() {});
		List<Meeting> attendedMeetingList = meetingRepository.findAllAttendedMeetingsByUserId(emailId);
		log.info("MeetingsServiceImpl.getUserAttendedMeetings() executed successfully");
		return attendedMeetingList;
	}

	

	@Override
	public List<Meeting> getUserOrganizedMeetingsByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getUserEventsByEmailId(): entered");
		String url ="http://UMS-BATCH-SERVICE/teams/events/organized/"+emailId;
//		ResponseEntity<List<EventVO>> response= restTemplate.exchange(url,HttpMethod.GET,null, new ParameterizedTypeReference<List<EventVO>>() {});
//		log.info("MeetingsServiceImpl.getUserEventsByEmailId() : call to batch microsevice is successfull.");
		List<Meeting> meetingList = meetingRepository.findAllMeetingsByUserId(emailId);
		return meetingList;
	}

	@Transactional
	@Override
	public void saveAllUserMeetingsListOfCurrentBatchProcess(List<List<Meeting>> currentBatchProcessingUsersMeetingList) {
		log.info("MeetingsServiceImpl.saveAllUserMeetingsListOfCurrentBatchProcess() entered with args : currentBatchProcessingUsersMeetingList ");
		if(currentBatchProcessingUsersMeetingList.size() < 0) {
			log.info("Empty meetings list from current batch processing ");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_MSG);
		}
		//save each users meetings into db
		currentBatchProcessingUsersMeetingList.forEach(userMeetingList -> {
			meetingRepository.saveAll(userMeetingList);
		});
		log.info("MeetingsServiceImpl.saveAllUserMeetingsListOfCurrentBatchProcess() exiting successfully");
	}


	@Override
	public List<Meeting> getAllMeetingsByUserId(String emailId) {
		log.info("MeetingsServiceImpl.getAllMeetingsByUserId() entered with args : " + emailId);
		if(emailId.equalsIgnoreCase("") || emailId == null) {
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
		try {
			if(emailId.equalsIgnoreCase("")) {
				throw new BusinessException("error code", "Invalid user email id");
			}
			Integer dbAttendedMeetingsCount = meetingRepository.findUserAttendedMeetingCount(emailId);
			return dbAttendedMeetingsCount;
		}catch (Exception e) {
			throw new BusinessException("error code", e.getStackTrace().toString());
		}
	}
	
	@Override
	public Integer getUserOragnizedMeetingCountByUserId(String emailId) {
		try {
			if(emailId == "") {
				throw new EmptyInputException("error code", "email id is empty");
			}
			Integer dbCount = meetingRepository.findUserOrganizedMeetingCount(emailId);
			return dbCount;
		}catch (Exception e) {
			throw new BusinessException("error code", e.getStackTrace().toString());
		}
	}

	@Override
	public Optional<Meeting> getMeetingDetails(Long meetingId) {
		Optional<Meeting> meeting = meetingRepository.findById(meetingId);
		return meeting;
		
	}


	@Override
	public Meeting createMeeting(MeetingModel meetingModel) {
			log.info("MeetingServiceImpl.createMeeting() entered with args : meeting object");
			System.out.println(meetingModel);
			Meeting meeting = new Meeting();
			Set<Attendee> attendeeList = new HashSet<>();
			modelMapper.map(meetingModel, meeting);
			for(int i=0; i< meetingModel.getAttendees().length; i++) {
				Attendee attendee = new Attendee();
				attendee.setEmail(meetingModel.getAttendees()[i]);
				attendee.setEmailId(meetingModel.getAttendees()[i]);
				attendee.setType("Required");
				attendee.setStatus("Accepted");
				attendeeList.add(attendee);
			}
			meeting.setEventId("IKCON UMS MANUAL MEETING "+new Random(9999999).nextInt());
			meeting.setAttendees(attendeeList);
			meeting.setCreatedBy(meetingModel.getCreatedBy());
			meeting.setEmailId(meetingModel.getEmailId());
			meeting.setCreatedByEmailId(meetingModel.getCreatedByEmailId());
			meeting.setCreatedDateTime(LocalDateTime.now().toString());
			Meeting createdMeeting = meetingRepository.save(meeting);
			return createdMeeting;
	}

}
