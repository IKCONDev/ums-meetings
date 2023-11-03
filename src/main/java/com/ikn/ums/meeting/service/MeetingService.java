package com.ikn.ums.meeting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ikn.ums.meeting.VO.EventVO;
import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;
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
    public Long[] countEmailOccurrences(LocalDateTime startDate, LocalDateTime endDate, String email);
	
}
