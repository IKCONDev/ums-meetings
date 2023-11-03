package com.ikn.ums.meeting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;

import com.ikn.ums.meeting.entity.Attendee;
import com.ikn.ums.meeting.entity.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	
	@Query("FROM Meeting WHERE emailId=:emailId")
	List<Meeting> findAllMeetingsByUserId(String emailId);

	@Query("SELECT COUNT(*) FROM Attendee WHERE emailId=:emailId")
	Integer findUserAttendedMeetingCount(String emailId);
	
	@Query("SELECT COUNT(*) FROM Meeting WHERE emailId=:emailId")
	Integer findUserOrganizedMeetingCount(String emailId);
	
	@Query("SELECT m  FROM Meeting m JOIN m.attendees a WHERE a.email=:emailId")
	List<Meeting> findAllAttendedMeetingsByUserId(String emailId);
	
	@Query(value = "SELECT m.*" +
            "FROM meeting_tab m " +
            "INNER JOIN attendee_tab a ON m.meeting_id = a.meeting_id " +
            "WHERE m.meeting_actual_start_date_time BETWEEN :startDate AND :endDate " +
            "AND a.email_id = :email",
    nativeQuery = true)
List<Object[]> emailsByDateRangeAndEmail(String email,LocalDateTime startDate,LocalDateTime endDate);

}
