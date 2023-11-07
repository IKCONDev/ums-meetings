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
	
	@Query("SELECT TO_CHAR(m.startDateTime, 'D'), " +
	           "SUM(CASE WHEN m.emailId=:email THEN 1 ELSE 0 END) " +
	           "FROM Meeting m " +
	           "INNER JOIN m.attendees a " +
	           "WHERE m.startDateTime BETWEEN :startDate AND :endDate " +
	           "AND a.emailId = :email " +
	           "GROUP BY TO_CHAR(m.startDateTime, 'D')")
	    List<Object[]> findAttendedMeetingCountsByDayOfWeek(LocalDateTime startDate,LocalDateTime endDate,String email
	    );
	    
	    @Query("SELECT TO_CHAR(m.startDateTime, 'D'), "
	    	       + "SUM(CASE WHEN m.emailId = :email THEN 1 ELSE 0 END) "
	    	       + "FROM Meeting m "
	    	       + "WHERE m.startDateTime BETWEEN :startDate AND :endDate "
	    	       + "AND m.organizerEmailId = :email "
	    	       + "GROUP BY TO_CHAR(m.startDateTime, 'D')")
	 List<Object[]> findCompletedMeetingCountsByDayOfWeek(LocalDateTime startDate,LocalDateTime endDate,String email);
	 
	 @Query("SELECT TO_CHAR(m.startDateTime, 'MM'), " +
		        "SUM(CASE WHEN m.emailId = :email THEN 1 ELSE 0 END) " +
		        "FROM Meeting m " +
		        "WHERE m.startDateTime BETWEEN :startDate AND :endDate " +
		        "AND m.emailId = :email " +
		        "GROUP BY TO_CHAR(m.startDateTime, 'MM')")
		List<Object[]> findAttendedMeetingCountsByMonth(LocalDateTime startDate, LocalDateTime endDate, String email);
		
		@Query("SELECT TO_CHAR(m.startDateTime, 'MM'), " +
		        "SUM(CASE WHEN m.emailId = :email THEN 1 ELSE 0 END) " +
		        "FROM Meeting m " +
		        "WHERE m.startDateTime BETWEEN :startDate AND :endDate " +
		        "AND m.organizerEmailId = :email " +
		        "GROUP BY TO_CHAR(m.startDateTime, 'MM')")
		List<Object[]> findOrganisedMeetingCountsByMonth(LocalDateTime startDate, LocalDateTime endDate, String email);

}
