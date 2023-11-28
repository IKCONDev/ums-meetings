package com.ikn.ums.meeting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ikn.ums.meeting.VO.EventVO;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
import com.ikn.ums.meeting.model.DepartmentMeetingCount;
import com.ikn.ums.meeting.model.MeetingModel;

public interface MeetingService {
	
	List<Meeting> getUserAttendedMeetingsByUserId(String emailId);
	List<Meeting> getUserOrganizedMeetingsByUserId(String emailId);
	List<Meeting> getAllMeetingsByUserId(String emailId);
	boolean deleteActionItemsOfMeeting(String acItemIds, Integer meetingId);
	void saveAllUserMeetingsListOfCurrentBatchProcess(List<List<Meeting>> currentBatchProcessingUsersMeetingList);
	Integer getUserAttendedMeetingCountByUserId(String emailId);
	Integer getUserOragnizedMeetingCountByUserId(String emailId);
    Optional<Meeting> getMeetingDetails(Long meetingId);
    Meeting createMeeting(MeetingModel meetingModel);
    public List<Long> countEmailOccurrences(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    public List<Long> countOrganisedMeetingOccurrence(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    public List<Long> countOrganisedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    public List<Long> countAttendedMeetingForYear(LocalDateTime startDate, LocalDateTime endDate, String emailId);
    List<Meeting> getFilteredOrganizedMeetings(String meetingTitle, LocalDateTime localStartDateTime, LocalDateTime localEndDateTime, String emailId);
    List<Meeting> getFilteredAttendedMeetings(String meetingTitle, String startDate, String endDate, String emailId);
    List<Meeting> getMeetingsByDepartment(Long departmentId);
    List<Meeting> getAllMeetings();
    //List<DepartmentMeetingCount> getAllDepartmentsMeetingCount();
    List<Object[]> getAllDepartmentsMeetingCount();
    
}
