package com.ikn.ums.meeting.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ikn.ums.meeting.dto.MeetingDto;
import com.ikn.ums.meeting.entity.AttendanceInterval;
import com.ikn.ums.meeting.entity.AttendanceRecord;
import com.ikn.ums.meeting.entity.AttendanceReport;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.exception.EmptyInputException;
import com.ikn.ums.meeting.exception.EmptyListException;
import com.ikn.ums.meeting.exception.EntityNotFoundException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.model.MeetingModel;
import com.ikn.ums.meeting.repository.MeetingRepository;
import com.ikn.ums.meeting.service.ActionItemService;
import com.ikn.ums.meeting.service.MeetingService;
import com.netflix.servo.util.Strings;

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
		log.info("deleteActionItemsOfMeeting() entered with args - actionItemIds : " + acItemIds + " evenId : "
				+ meetingId);
		boolean isDeleted = Boolean.FALSE;
		if (meetingId <= 0 || meetingId == null) {
			log.info("deleteActionItemsOfMeeting() meeting Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		if (Strings.isNullOrEmpty(acItemIds) || acItemIds.isEmpty()) {
			log.info("deleteActionItemsOfMeeting() actionItems is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ACTIONITEMS_ID_MSG);
		}
		log.info("deleteActionItemsOfMeeting() is under execution...");
		List<Integer> actualAcIds = null;
		if (acItemIds != "") {
			String[] idsFromUI = acItemIds.split(",");
			List<String> idsList = Arrays.asList(idsFromUI);
			// convert string of ids to Integer ids
			actualAcIds = idsList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
			actionItemService.deleteAllActionItemsById(actualAcIds);
		}
		isDeleted = Boolean.TRUE;
		log.info("deleteActionItemsOfMeeting() executed sucessfully");
		return isDeleted;
	}

	@Override
	public List<MeetingDto> getUserAttendedMeetingsByUserId(String emailId) {
		log.info("getUserAttendedMeetingsByUserId() entered with args : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserAttendedMeetingsByUserId() EmptyInputException : user email is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getUserAttendedMeetingsByUserId() calling batch process microservice to get user attended meetings");
		List<Meeting> attendedMeetingList = meetingRepository.findAllAttendedMeetingsByUserId(emailId);
		List<MeetingDto> meetingDTOList = new ArrayList<>();
		attendedMeetingList.forEach(entity -> {
			MeetingDto dto = new MeetingDto();
			modelMapper.map(entity, dto);
			meetingDTOList.add(dto);
		});
		log.info("getUserAttendedMeetingsByUserId() executed successfully");
		return meetingDTOList;
	}

	@Override
	public List<MeetingDto> getUserOrganizedMeetingsByUserId(String emailId) {
		log.info("getUserOrganizedMeetingsByUserId() entered with args - emailId/userId : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserOrganizedMeetingsByUserId() EmptyInputException emailId / userId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getUserOrganizedMeetingsByUserId() is under execution...");
		List<Meeting> meetingList = meetingRepository.findAllMeetingsByUserId(emailId);
		List<MeetingDto> meetingDtoList = new ArrayList<>();
		meetingList.forEach(entity -> {
			MeetingDto dto = new MeetingDto();
			modelMapper.map(entity, dto);
			meetingDtoList.add(dto);
		});
		meetingDtoList.sort((m1, m2) -> {
			return (int) (m2.getMeetingId() - m1.getMeetingId());
		});
		log.info("getUserOrganizedMeetingsByUserId() executed succesfully");
		return meetingDtoList;
	}

	@Transactional
	@Override
	public void saveAllUserMeetingsListOfCurrentBatchProcess(
			List<List<Meeting>> currentBatchProcessingUsersMeetingList) {
		log.info(
				"saveAllUserMeetingsListOfCurrentBatchProcess() entered with args : currentBatchProcessingUsersMeetingList ");
		if (currentBatchProcessingUsersMeetingList.isEmpty()) {
			log.info(
					"saveAllUserMeetingsListOfCurrentBatchProcess() EmptyListException :  Empty meetings list from current batch processing");
			throw new EmptyListException(ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_LIST_EMPTY_MSG);
		}
		log.info("saveAllUserMeetingsListOfCurrentBatchProcess() is under execution...");
		// save each users meetings into db
		currentBatchProcessingUsersMeetingList.forEach(userMeetingList -> {
			userMeetingList.forEach(userMeeting -> {
				// set meeting id's ,attendee id, and transcript id to null which we obtained
				// from batch processing, to auto generate the new meeting id's , attendee id's
				// and
				// transcript id's, for each meeting for this new table.
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
				if(userMeeting.getAttendanceReport() != null) {
					userMeeting.getAttendanceReport().forEach(attendanceReport -> {
						attendanceReport.setId(null);
						Instant startTime = Instant.parse(attendanceReport.getMeetingStartDateTime());
				        Instant endTime = Instant.parse(attendanceReport.getMeetingEndDateTime());
				        Duration duration = Duration.between(startTime, endTime);
				        long secondsDifference = 0;
				        secondsDifference = secondsDifference+duration.getSeconds();
				        String meetingDuration = getMeetingDuration(secondsDifference);
						userMeeting.setActualMeetingDuration(meetingDuration);
						List<AttendanceRecord> attendanceRecordList = attendanceReport.getAttendanceRecords();
						attendanceRecordList.forEach(attendanceRecord -> {
							attendanceRecord.setId(null);
							List<AttendanceInterval> attendanceIntervalList = attendanceRecord.getAttendanceIntervals();
							attendanceIntervalList.forEach(attendanceInterval -> {
								attendanceInterval.setId(null);
							});
						});
					});
				}
			});
			// save each users meeting batch processing records
			List<Meeting> batchProcessedMeetingList = meetingRepository.saveAll(userMeetingList);
			log.info("Batch process Meetings " + batchProcessedMeetingList);
		});
		log.info("saveAllUserMeetingsListOfCurrentBatchProcess() executed successfully");
	}
	
	
	@Override
	public List<MeetingDto> getAllMeetingsByUserId(String emailId) {
		log.info("getAllMeetingsByUserId() entered with args - emailId/userId : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getAllMeetingsByUserId() : userId/emailId is empty");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getAllMeetingsByUserId() is under execution");
		List<Meeting> meetingList = meetingRepository.findAllMeetingsByUserId(emailId);
		List<MeetingDto> meetingDtoList = new ArrayList<>();
		meetingList.forEach(entity -> {
			MeetingDto dto = new MeetingDto();
			modelMapper.map(entity, dto);
			meetingDtoList.add(dto);
		});
		meetingDtoList.sort((m1, m2) -> {
			return (int) (m1.getMeetingId() - m2.getMeetingId());
		});
		log.info("getAllMeetingsByUserId() exiting successfully");
		return meetingDtoList;
	}

	@Override
	public Integer getUserAttendedMeetingCountByUserId(String emailId) {
		log.info("getUserAttendedMeetingCountByUserId() entered with args - emailId/userId : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserAttendedMeetingCountByUserId() : EmptyInputException - emailId/userId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getUserAttendedMeetingCountByUserId() is under execution...");
		var dbAttendedMeetingsCount = meetingRepository.findUserAttendedMeetingCount(emailId);
		log.info("getUserAttendedMeetingCountByUserId() executed succesfully");
		return dbAttendedMeetingsCount;
	}

	@Override
	public Integer getUserOragnizedMeetingCountByUserId(String emailId) {
		log.info("getUserOragnizedMeetingCountByUserId() entered with args - emailId/userId : " + emailId);
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getUserOragnizedMeetingCountByUserId() : EmptyInputException - emailId/userId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getUserOragnizedMeetingCountByUserId() is under execution...");
		var dbCount = meetingRepository.findUserOrganizedMeetingCount(emailId);
		log.info("getUserOragnizedMeetingCountByUserId() executed succesfully");
		return dbCount;
	}

	@Override
	public MeetingDto getMeetingDetails(Long meetingId) {
		log.info("getMeetingDetails() entered with args - meetingId : " + meetingId);
		if (meetingId <= 0 || meetingId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ID_EMPTY_MSG);
		}
		log.info("getMeetingDetails() is under execution...");
		Optional<Meeting> optMeeting = meetingRepository.findById(meetingId);
		if (optMeeting.isEmpty()) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_DBENTITY_ISNULL_CODE,
					ErrorCodeMessages.ERR_MEETINGS_DBENTITY_ISNULL_MSG);
		}
		Meeting meeting = optMeeting.get();
		MeetingDto meetingDto = new MeetingDto();
		modelMapper.map(meeting, meetingDto);
		log.info("getMeetingDetails() executed successfully.");
		return meetingDto;

	}

	@Transactional
	@Override
	public MeetingDto createMeeting(MeetingModel meetingModel) {
		log.info("createMeeting() entered with args : meeting object");
		if (meetingModel == null || meetingModel.equals(null)) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_CODE,
					ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG);
		}

		log.info("createMeeting() is under execution...");
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
		for (int i = 0; i < meetingModel.getAttendees().length; i++) {
			Attendee attendee = new Attendee();
			attendee.setEmail(meetingModel.getAttendees()[i]);
			attendee.setEmailId(meetingModel.getAttendees()[i]);
			attendee.setType("Required");
			attendee.setStatus("Accepted");
			attendeeList.add(attendee);
		}
		// add organizer also as an attendee
//		Attendee organizerAsAnAttendee = new Attendee();
//		organizerAsAnAttendee.setEmail(meetingModel.getOrganizerEmailId());
//		organizerAsAnAttendee.setEmailId(meetingModel.getOrganizerEmailId());
//		organizerAsAnAttendee.setType("Required");
//		organizerAsAnAttendee.setStatus("Accepted");
//		attendeeList.add(organizerAsAnAttendee);
		// set remaining props
		modelMapper.map(meetingModel, meeting);
		meeting.setEventId("UMS MANUAL MEETING " + new Random(9999999).nextInt());
		meeting.setAttendees(attendeeList);
		meeting.setCreatedBy(meetingModel.getCreatedBy());
		meeting.setEmailId(meetingModel.getEmailId());
		meeting.setCreatedByEmailId(meetingModel.getCreatedByEmailId());
		meeting.setCreatedDateTime(LocalDateTime.now().toString());
		meeting.setManualMeeting(true);
		Meeting createdMeeting = meetingRepository.save(meeting);
		MeetingDto meetingDto = new MeetingDto();
		modelMapper.map(createdMeeting, meetingDto);
		// send email to meeting attendees if required.
		log.info("createMeeting() executed successfully");
		return meetingDto;
	}

	public List<Long> countEmailOccurrences(LocalDateTime startDate, LocalDateTime endDate, String email) {
		log.info("countEmailOccurrences() is entered.");
		if (Strings.isNullOrEmpty(email) || email.isEmpty()) {
			log.info("countEmailOccurrences() EmptyInputException : userId / emailId is empty or null.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("countEmailOccurrences() is under execution...");
		List<Object[]> MeetingCountsByDay = meetingRepository.findAttendedMeetingCountsByDayOfWeek(startDate, endDate,
				email);
		List<Long> attendedMeetingCounts = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			attendedMeetingCounts.add(0L);

		}
		for (Object[] result : MeetingCountsByDay) {
			String dayOfWeek = (String) result[0];
			Long completedCount = Long.parseLong((String) result[1]);
			int dayIndex = (Integer.parseInt(dayOfWeek)) - 1;
			attendedMeetingCounts.set(dayIndex, completedCount);
		}

		log.info("countEmailOccurrences() executed successfully.");
		return attendedMeetingCounts;
	}

	@Override
	public List<Long> countOrganisedMeetingOccurrence(LocalDateTime startDate, LocalDateTime endDate, String email) {
		log.info("countOrganisedMeetingOccurrence() entered.");
		if (Strings.isNullOrEmpty(email) || email.isEmpty()) {
			log.info("countOrganisedMeetingOccurrence() EmptyInputException : userId / emailId is empty or null.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("countOrganisedMeetingOccurrence() is under execution...");
		List<Object[]> MeetingCountsByDay1 = meetingRepository.findCompletedMeetingCountsByDayOfWeek(startDate, endDate,
				email);
		List<Long> OrganisedMeetingCounts = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			OrganisedMeetingCounts.add(0L);

		}
		for (Object[] result : MeetingCountsByDay1) {
			String dayOfWeek1 = (String) result[0];
			Long completedCount1 = Long.parseLong((String) result[1]);
			int dayIndex = Integer.parseInt(dayOfWeek1) - 1;
			OrganisedMeetingCounts.set(dayIndex, completedCount1);
			System.out.println(dayOfWeek1);

		}
		log.info("countOrganisedMeetingOccurrence() executed successfully.");
		return OrganisedMeetingCounts;
	}

	public List<Long> countOrganisedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String email) {
		log.info("countOrganisedMeetingForYear() entered");
		if (Strings.isNullOrEmpty(email) || email.isEmpty()) {
			log.info("countOrganisedMeetingForYear() EmptyInputException : userId / emailId is empty or null.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("countOrganisedMeetingForYear() is under execution...");
		List<Object[]> MeetingCountsByMonth = meetingRepository.findOrganisedMeetingCountsByMonth(startDate, endDate,
				email);
		List<Long> OrganisedMeetingCountsForYear = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			OrganisedMeetingCountsForYear.add(0L);

		}
		for (Object[] result : MeetingCountsByMonth) {
			String monthOfYear = (String) result[0];
			Long OrganisedMeetings = (Long) result[1]; // Use result[1] for attended meetings count
			int monthIndex = Integer.parseInt(monthOfYear) - 1;
			OrganisedMeetingCountsForYear.set(monthIndex, OrganisedMeetings);
		}
		log.info("countOrganisedMeetingForYear() executed successfully.");
		return OrganisedMeetingCountsForYear;
	}

	public List<Long> countAttendedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String email) {
		log.info("countAttendedMeetingForYear() entered");
		if (Strings.isNullOrEmpty(email) || email.isEmpty()) {
			log.info("countAttendedMeetingForYear() EmptyInputException : userId / emailId is empty or null.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("countAttendedMeetingForYear() is under execution...");
		List<Object[]> MeetingCountsByMonth = meetingRepository.findAttendedMeetingCountsByMonth(startDate, endDate,
				email);
		List<Long> AttendedMeetingCountsForYear = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			AttendedMeetingCountsForYear.add(0L);

		}
		for (Object[] result : MeetingCountsByMonth) {
			String monthOfYear = (String) result[0];
			Long attendedMeetings = (Long) result[1]; // Use result[1] for attended meetings count
			int monthIndex = Integer.parseInt(monthOfYear) - 1;
			AttendedMeetingCountsForYear.set(monthIndex, attendedMeetings);
		}
		log.info("countAttendedMeetingForYear() executed successfully");
		return AttendedMeetingCountsForYear;
	}

	@Override
	public List<MeetingDto> getFilteredOrganizedMeetings(String meetingTitle, LocalDateTime startDateTime,
			LocalDateTime endDateTime, String emailId) {
		log.info("getFilteredOrganizedMeetings() entered");
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getFilteredOrganizedMeetings() EmptyInputException : userId / emailId is empty or null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getFilteredOrganizedMeetings() is under execution...");
		List<Meeting> filteredMeetingList = meetingRepository.findAllFilteredMeetingsByUserId(
				meetingTitle.isBlank() ? null : meetingTitle, startDateTime, endDateTime, emailId);
		List<MeetingDto> meetingDtoList = filteredMeetingList.stream().map(meeting -> {
			MeetingDto meetingDto = new MeetingDto();
			modelMapper.map(meeting, meetingDto);
			return meetingDto;
		}).collect(Collectors.toList());
		log.info("getFilteredOrganizedMeetings() executed successfully.");
		return meetingDtoList;
	}

	@Override
	public List<MeetingDto> getFilteredAttendedMeetings(String meetingTitle, String startDateTime, String endDateTime,
			String emailId) {
		log.info("getFilteredAttendedMeetings() entered");
		if (Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getFilteredAttendedMeetings() EmptyInputException : userId / emailId is empty.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_MEETINGS_USERID_EMPTY_EXCEPTION_MSG);
		}
		log.info("getFilteredAttendedMeetings() is under execution...");
		LocalDateTime actualStartDateTime = null;
		if (!startDateTime.isBlank()) {
			actualStartDateTime = LocalDateTime.parse(startDateTime);
		}
		LocalDateTime actualEndDateTime = null;
		if (!endDateTime.isBlank()) {
			actualEndDateTime = LocalDateTime.parse(endDateTime);
		}
		var filteredAttendedMeetingList = meetingRepository.findAllFilteredAttendedMeetingsByUserId(
				meetingTitle.isBlank() ? null : meetingTitle, actualStartDateTime, actualEndDateTime, emailId);
		List<MeetingDto> meetingDtoList = filteredAttendedMeetingList.stream().map(meeting -> {
			MeetingDto meetingDto = new MeetingDto();
			modelMapper.map(meeting, meetingDto);
			return meetingDto;
		}).collect(Collectors.toList());
		log.info("getFilteredAttendedMeetings() executed successfully...");
		return meetingDtoList;
	}

	@Override
	public List<MeetingDto> getMeetingsByDepartment(Long departmentId) {
		log.info("getMeetingsByDepartment() entered with args : departmentId - " + departmentId);
		if (departmentId <= 0) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_MEETINGS_DEPTID_EMPTY_CODE,
					ErrorCodeMessages.ERR_MEETINGS_DEPTID_EMPTY_MSG);
		}
		log.info("getMeetingsByDepartment() is under execution...");
		var meetingsOfDepartmentList = meetingRepository.findByDepartmentId(departmentId);
		List<MeetingDto> meetingsOfDepartmentDtoList = meetingsOfDepartmentList.stream().map(meeting -> {
			MeetingDto meetingDto = new MeetingDto();
			modelMapper.map(meeting, meetingDto);
			return meetingDto;
		}).collect(Collectors.toList());
		log.info("getMeetingsByDepartment() is executed successfully.");
		return meetingsOfDepartmentDtoList;
	}

	@Override
	public List<MeetingDto> getAllMeetings() {
		log.info("getAllMeetings() is entered");
		log.info("getAllMeetings() is under execution...");
		var meetingList = meetingRepository.findAll();
		List<MeetingDto> meetingsDtoList = meetingList.stream().map(meeting -> {
			MeetingDto meetingDto = new MeetingDto();
			return meetingDto;
		}).collect(Collectors.toList());
		log.info("getAllMeetings() is executed successfully.");
		return meetingsDtoList;
	}

	@Override
	public List<Object[]> getAllDepartmentsMeetingCount() {
		log.info("getAllDepartmentsMeetingCount() is entered");
		log.info("getAllDepartmentsMeetingCount() is under execution...");
		List<Object[]> count = meetingRepository.getCountOfMeetingsByDepartment();
		log.info("getAllDepartmentsMeetingCount() is executed successfully.");
		return count;
	}

	long secondsDifference = 0;
	Meeting dbTeamsMeeting = null;
	@Override
	public Meeting updateMeetingDetailsFromBatchProcess(Meeting updatedMeetingFromBatchProcess) {
		log.info("updateMeetingDetailsFromBatchProcess() entered with args : Meeting object");
		if(updatedMeetingFromBatchProcess == null) {
			log.info("updateMeeting() EntityNotFoundException : meeting obejct is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_CODE, 
					ErrorCodeMessages.ERR_MEETINGS_ENTITY_NOTFOUND_MSG);
		}
		log.info("updateMeetingDetailsFromBatchProcess() is under execution...");
		if(updatedMeetingFromBatchProcess.getType().equalsIgnoreCase("singleInstance")) {
			dbTeamsMeeting = meetingRepository.findByEventId(updatedMeetingFromBatchProcess.getEventId());
			//else it is an recurrence meeting
		}else {
			dbTeamsMeeting = meetingRepository.findByOccurrenceId(updatedMeetingFromBatchProcess.getOccurrenceId());
		}
		var dbAttendanceReportList = dbTeamsMeeting.getAttendanceReport();
		var updatedAttendanceReportListFromBatchProcess = updatedMeetingFromBatchProcess.getAttendanceReport();
		var updatedAttendanceReportListFromBatchProcessCopy = updatedMeetingFromBatchProcess.getAttendanceReport();
		
		Iterator<AttendanceReport> dbAttendanceReportIterator = dbAttendanceReportList.iterator();
		//Iterator<AttendanceReport> updatedAttendanceReportListFromBatchProcessIterator = updatedAttendanceReportListFromBatchProcess.iterator();
		while(dbAttendanceReportIterator.hasNext()) {
			var dbAttendanceReport = dbAttendanceReportIterator.next();
			for(int i = 0; i < updatedAttendanceReportListFromBatchProcess.size(); i++) {
				var updatedAttendanceReportFromBatchProcess = updatedAttendanceReportListFromBatchProcess.get(i);
				if(dbAttendanceReport.getAttendanceReportId().equalsIgnoreCase(updatedAttendanceReportFromBatchProcess.getAttendanceReportId())) {
					updatedAttendanceReportListFromBatchProcessCopy.remove(updatedAttendanceReportFromBatchProcess);
					break;
				}
			}
		}
		updatedAttendanceReportListFromBatchProcessCopy.forEach(newReport -> {
			newReport.setId(null);
			dbAttendanceReportList.add(newReport);
		});
		dbAttendanceReportList.forEach(report -> {
			Instant startTime = Instant.parse(report.getMeetingStartDateTime());
	        Instant endTime = Instant.parse(report.getMeetingEndDateTime());
	        Duration duration = Duration.between(startTime, endTime);
	        secondsDifference = secondsDifference+duration.getSeconds();
	        String meetingDuration = getMeetingDuration(secondsDifference);
			dbTeamsMeeting.setActualMeetingDuration(meetingDuration);
		});
		Meeting updatedTeamsMeeting = meetingRepository.save(dbTeamsMeeting);
		log.info("updateMeetingDetailsFromBatchProcess() executed successfully");
		return updatedTeamsMeeting;
	}
	
	public Meeting updateMeetingDetails(MeetingDto meetingDto){
	    Optional<Meeting>meeting = meetingRepository.findById(meetingDto.getMeetingId()) ;  
	    
	    Meeting meetingObject = null;
	    if(meeting.isPresent()) {
	    	meetingObject = meeting.get();
	    }
		meetingObject.setMomEmailCount(meetingDto.getMomEmailCount());
		meetingRepository.save(meetingObject);
		return meetingObject;
    }

	private String getMeetingDuration(Long totalSeconds){
	    if (totalSeconds < 120) {
	        return totalSeconds + " sec";
	    }else if (totalSeconds >= 120 && totalSeconds <= 3600) {
	        long minutes = totalSeconds / 60;
	        long seconds = totalSeconds % 60;
	        if (seconds > 0)
	            return minutes + " min " + seconds + " sec";
	        else
	            return minutes + " min";
	    } else {
	        var hours = totalSeconds / 3600;
	        var remainingSeconds = totalSeconds % 3600;
	        var minutes = remainingSeconds / 60;
	        var seconds = remainingSeconds % 60;
	        if (minutes > 0 && seconds > 0) {
	            return hours + " hour " + minutes + " min " + seconds + " sec";
	        } else if (minutes > 0) {
	            return hours + " hour " + minutes + " min";
	        } else {
	            return hours + " hour";
	        }
	    }
	}

}
