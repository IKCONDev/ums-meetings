package com.ikn.ums.meeting.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ikn.ums.meeting.dto.MeetingDto;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.model.MeetingModel;

public interface MeetingService {
	
	List<MeetingDto> getUserAttendedMeetingsByUserId(String emailId);
	List<MeetingDto> getUserOrganizedMeetingsByUserId(String emailId);
	List<MeetingDto> getAllMeetingsByUserId(String emailId);
	boolean deleteActionItemsOfMeeting(String acItemIds, Integer meetingId);
	void saveAllUserMeetingsListOfCurrentBatchProcess(List<List<Meeting>> currentBatchProcessingUsersMeetingList);
	Integer getUserAttendedMeetingCountByUserId(String emailId);
	Integer getUserOragnizedMeetingCountByUserId(String emailId);
	MeetingDto getMeetingDetails(Long meetingId);
	MeetingDto createMeeting(MeetingModel meetingModel);
    public List<Long> countEmailOccurrences(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    public List<Long> countOrganisedMeetingOccurrence(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    public List<Long> countOrganisedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    public List<Long> countAttendedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    List<MeetingDto> getFilteredOrganizedMeetings(String meetingTitle, LocalDateTime localStartDateTime, LocalDateTime localEndDateTime, String emailId);
    List<MeetingDto> getFilteredAttendedMeetings(String meetingTitle, String startDate, String endDate, String emailId);
    List<MeetingDto> getMeetingsByDepartment(Long departmentId);
    List<MeetingDto> getAllMeetings();
    //List<DepartmentMeetingCount> getAllDepartmentsMeetingCount();
    List<Object[]> getAllDepartmentsMeetingCount();
    
}
