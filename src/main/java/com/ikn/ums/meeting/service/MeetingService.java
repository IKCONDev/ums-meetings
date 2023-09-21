package com.ikn.ums.meeting.service;

import java.util.List;

import com.ikn.ums.meeting.VO.EventVO;

public interface MeetingService {
	
	boolean removeActionItemsOfEvent(String acItemIds, Integer eventId);
	
	//get user attended meetings count
	List<EventVO> getUserAttendedMeetings(String email);
	
	List<EventVO> getUserEventsByEmailId(String userPrincipalName);

	
}
