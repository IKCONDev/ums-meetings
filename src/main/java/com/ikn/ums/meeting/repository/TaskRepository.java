package com.ikn.ums.meeting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import com.ikn.ums.meeting.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>{
	@Query("FROM Task WHERE emailId=:emailId")
	List<Task> findByUserId(String emailId);
	
	@Query("FROM Task WHERE taskOwner=:emailId")
	List<Task> findUserAssignedTasksByUserId(String emailId);
	
	@Query("SELECT COUNT(*) FROM Task WHERE emailId=:emailId")
	Long findOrganizedTaskCountByUserId(String emailId); 
	
	@Query("SELECT COUNT(*) FROM Task WHERE taskOwner=:emailId") 
	Long findAssignedTaskCountByUserId(String emailId);
	
	@Query("SELECT TO_CHAR(t.startDate, 'D'), COUNT(*) " +
		       "FROM Task t " +
		       "WHERE t.startDate BETWEEN :startTime AND :endTime " +
		       "GROUP BY TO_CHAR(t.startDate, 'D')")
	List<Object[]> findTaskCountsByDayOfWeek(LocalDateTime startTime,LocalDateTime endTime);
	
	@Query("SELECT TO_CHAR(t.startDate, 'D'), " +
		           "SUM(CASE WHEN t.status = 'Completed' THEN 1 ELSE 0 END) " +
		           "FROM Task t " +
		           "WHERE t.startDate BETWEEN :startTime AND :endTime " +
		           "GROUP BY TO_CHAR(t.startDate, 'D')")
	List<Object[]> findCompletedTaskCountsByDayOfWeek( LocalDateTime startTime,LocalDateTime endTime);
		 
	@Query("SELECT TO_CHAR(t.startDate, 'D'), " +
		           "SUM(CASE WHEN t.status = 'Inprogress' THEN 1 ELSE 0 END) " +
		           "FROM Task t " +
		           "WHERE t.startDate BETWEEN :startTime AND :endTime " +
		           "GROUP BY TO_CHAR(t.startDate, 'D')")
	List<Object[]> findInProgressTaskCountsByDayOfWeek(LocalDateTime startTime,LocalDateTime endTime);
	
	//@Query(value = "select * from task_tab where user_id=:emailId AND (task_title='%:taskTitle%' or task_priority=:taskPriority or task_owner=:taskOwner or start_date=:startDate or due_date=:dueDate)", nativeQuery = true)
	@Query("FROM Task WHERE emailId = :emailId AND (taskTitle='%:taskTitle%' OR taskPriority = :taskPriority OR taskOwner = :taskOwner OR startDate = :startDate OR dueDate = :dueDate)")
	List<Task> findFilteredTasks(String taskTitle, String taskPriority, String taskOwner, LocalDateTime startDate, LocalDateTime dueDate, String emailId);
	

}
