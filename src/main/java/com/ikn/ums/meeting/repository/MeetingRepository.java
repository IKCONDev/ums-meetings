package com.ikn.ums.meeting.repository;

import java.time.LocalDateTime;
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

	@Query("SELECT m  FROM Meeting m JOIN m.attendees a WHERE LOWER(a.email)=:emailId")
	//@Query(name = "SELECT * FROM meetings_tab as m JOIN attendance_report_tab a WHERE LOWER(a.meeting_id)=:emailId", nativeQuery = true)
	List<Meeting> findAllAttendedMeetingsByUserId(String emailId);
	
	@Query(value = "SELECT TO_CHAR(meeting_actual_start_date_time + INTERVAL '5 hours 30 minutes', 'D'), " +
		       "CAST(SUM(CASE WHEN attendee_tab.email_id = :email THEN 1 ELSE 0 END) AS text) " +
		       "FROM meeting_tab " +
		       "INNER JOIN attendee_tab ON meeting_tab.meeting_id = attendee_tab.meeting_id " +
		       "WHERE DATE(meeting_actual_start_date_time) >= DATE(:startDate) AND DATE(meeting_actual_start_date_time) <= DATE(:endDate) " +
		       "AND email_id = :email " +
		       "GROUP BY TO_CHAR(meeting_actual_start_date_time + INTERVAL '5 hours 30 minutes', 'D')", nativeQuery = true)
		List<Object[]> findAttendedMeetingCountsByDayOfWeek(
		    LocalDateTime startDate, LocalDateTime endDate, String email
		);
	    
		@Query(value = "SELECT TO_CHAR(meeting_actual_start_date_time + INTERVAL '5 hours 30 minutes', 'D'), "
		           + "CAST(SUM(CASE WHEN user_Id = :email THEN 1 ELSE 0 END) AS text) "
		           + "FROM meeting_tab "
		           + "WHERE DATE(meeting_actual_start_date_time) >= DATE(:startDate) AND DATE(meeting_actual_start_date_time) <= DATE(:endDate) "
		           + "AND organizer_email_id = :email "
		           + "GROUP BY TO_CHAR(meeting_actual_start_date_time + INTERVAL '5 hours 30 minutes', 'D')", nativeQuery = true)
	 List<Object[]> findCompletedMeetingCountsByDayOfWeek(LocalDateTime startDate,LocalDateTime endDate,String email);
	
	 @Query("SELECT TO_CHAR(m.startDateTime, 'MM'), " +
		       "SUM(CASE WHEN a.emailId = :email THEN 1 ELSE 0 END) " +
		       "FROM Meeting m " +
		       "INNER JOIN m.attendees a " +
		       "WHERE m.startDateTime BETWEEN :startDate AND :endDate " +
		       "AND a.emailId = :email " +
		       "GROUP BY TO_CHAR(m.startDateTime, 'MM')")
		List<Object[]> findAttendedMeetingCountsByMonth(
		    LocalDateTime startDate, LocalDateTime endDate, String email);

	@Query("SELECT TO_CHAR(m.startDateTime, 'MM'), " + "SUM(CASE WHEN m.emailId = :email THEN 1 ELSE 0 END) "
			+ "FROM Meeting m " + "WHERE m.startDateTime BETWEEN :startDate AND :endDate "
			+ "AND m.organizerEmailId = :email " + "GROUP BY TO_CHAR(m.startDateTime, 'MM')")
	List<Object[]> findOrganisedMeetingCountsByMonth(LocalDateTime startDate, LocalDateTime endDate, String email);

	@Query("FROM Meeting " + "WHERE emailId = :emailId "
			+ "AND (:meetingTitle IS NULL OR lower(subject) LIKE lower(concat('%', :meetingTitle, '%'))) "
			+ "AND (CAST(:startDateTime as timestamp) IS NULL OR startDateTime >= CAST(:startDateTime as timestamp)) "
			+ "AND (CAST(:endDateTime as timestamp) IS NULL OR startDateTime <= CAST(:endDateTime as timestamp))")
	List<Meeting> findAllFilteredMeetingsByUserId(String meetingTitle, LocalDateTime startDateTime,
			LocalDateTime endDateTime, String emailId);

	@Query("FROM Meeting m JOIN m.attendees a WHERE a.emailId = :emailId AND "
			+ "((:meetingTitle IS NULL OR lower(m.subject) LIKE lower(concat('%', :meetingTitle, '%'))) "
			+ "AND (cast(:startDateTime as timestamp) IS NULL OR m.startDateTime >= cast(:startDateTime as timestamp)) "
			+ "AND (cast(:endDateTime as timestamp) IS NULL OR m.startDateTime <= cast(:endDateTime as timestamp)))")
	// List<Meeting> findAllFilteredAttendedMeetingsByUserId(String meetingTitle,
	// LocalDateTime startDateTime, LocalDateTime endDateTime, String emailId);
	List<Meeting> findAllFilteredAttendedMeetingsByUserId(String meetingTitle, LocalDateTime startDateTime,
			LocalDateTime endDateTime, String emailId);

	List<Meeting> findByDepartmentId(Long departmentId);

	@Query(value = "select department_Id, count(*) from meeting_tab Group By department_Id;", nativeQuery = true)
	List<Object[]> getCountOfMeetingsByDepartment();
	
	Meeting findByEventId(String eventId);
	Meeting findByOccurrenceId(String occurrenceId);

}
