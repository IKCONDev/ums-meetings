package com.ikn.ums.meeting.service;

import java.util.List;

import com.ikn.ums.meeting.VO.EventVO;
import com.ikn.ums.meeting.entity.Meeting;

public interface MeetingService {
	
	List<EventVO> getUserAttendedMeetings(String email);
	List<EventVO> getUserEventsByEmailId(String userPrincipalName);
	List<Meeting> getAllMeetingsByUserId(String emailId);
	boolean deleteActionItemsOfMeeting(String acItemIds, Integer meetingId);
	void saveAllUserMeetingsListOfCurrentBatchProcess(List<List<Meeting>> currentBatchProcessingUsersMeetingList);
	Integer getUserAttendedMeetingCountByUserId(String emailId);
	Integer getUserOragnizedMeetingCountByUserId(String emailId);

	
}
