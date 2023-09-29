package com.ikn.ums.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ikn.ums.meeting.entity.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	
	@Query("FROM Meeting WHERE emailId=:emailId")
	List<Meeting> findAllMeetingsByUserId(String emailId);

	@Query("SELECT COUNT(*) FROM Attendee WHERE emailId=:emailId")
	Integer findUserAttendedMeetingCount(String emailId);
	
	@Query("SELECT COUNT(*) FROM Meeting WHERE emailId=:emailId")
	Integer findUserOrganizedMeetingCount(String emailId);

}
