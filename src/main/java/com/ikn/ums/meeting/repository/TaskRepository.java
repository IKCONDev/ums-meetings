package com.ikn.ums.meeting.repository;

import java.time.LocalDate;
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
	
	@Query("SELECT TO_CHAR(t.plannedStartDate, 'D'), COUNT(*) " +
		       "FROM Task t " +
		       "WHERE t.plannedStartDate BETWEEN :startTime AND :endTime " +
		       "AND t.taskOwner= :email " +
		       "GROUP BY TO_CHAR(t.plannedStartDate, 'D')")
	List<Object[]> findTaskCountsByDayOfWeek(LocalDate startTime,LocalDate endTime, String email);
	
	@Query("SELECT TO_CHAR(t.startDate, 'D'), " +
		           "SUM(CASE WHEN t.status = 'Completed' THEN 1 ELSE 0 END) " +
		           "FROM Task t " +
		           "WHERE t.startDate BETWEEN :startTime AND :endTime " +
		           "AND t.taskOwner= :email " +
		           "GROUP BY TO_CHAR(t.startDate, 'D')")
	List<Object[]> findCompletedTaskCountsByDayOfWeek( LocalDate startTime,LocalDate endTime, String email);
		 
	@Query("SELECT TO_CHAR(t.startDate, 'D'), " +
		           "SUM(CASE WHEN t.status = 'Inprogress' THEN 1 ELSE 0 END) " +
		           "FROM Task t " +
		           "WHERE t.startDate BETWEEN :startTime AND :endTime " +
		           "AND t.taskOwner= :email " +
		           "GROUP BY TO_CHAR(t.startDate, 'D')")
	List<Object[]> findInProgressTaskCountsByDayOfWeek(LocalDate startTime,LocalDate endTime, String email);
	
//	@Query(value = "select * from task_tab where user_id=:emailId AND (task_title='%:taskTitle%' or task_priority=:taskPriority or task_owner=:taskOwner or start_date>=:startDate or due_date<=:dueDate)", nativeQuery = true)
	//@Query("FROM Task WHERE emailId = :emailId AND ((:taskTitle IS NULL OR taskTitle LIKE %:taskTitle%) AND (:taskPriority IS NULL OR taskPriority = :taskPriority) AND (:taskOwner IS NULL OR taskOwner = :taskOwner) AND (:startDate IS NULL OR startDate >= :startDate) AND (:dueDate IS NULL OR dueDate <= :dueDate))")
	@Query(value = "SELECT * FROM task_tab WHERE user_id = :emailId AND " +
            "(:taskTitle IS NULL OR task_title LIKE %:taskTitle%) AND " +
            "(:taskPriority IS NULL OR task_priority = :taskPriority) AND " +
            "(:taskOwner IS NULL OR task_owner = :taskOwner) AND " +
            "(CAST(:startDate AS DATE) IS NULL OR start_date >= :startDate) AND " +
            "(CAST(:dueDate AS DATE) IS NULL OR due_date <= :dueDate)",
     nativeQuery = true)
	List<Task> findFilteredTasks(String taskTitle, String taskPriority, String taskOwner, LocalDate startDate, LocalDate dueDate, String emailId);
	
	@Query(value = "SELECT * FROM task_tab WHERE task_owner = :emailId AND " +
            "(:taskTitle IS NULL OR task_title LIKE %:taskTitle%) AND " +
            "(:taskPriority IS NULL OR task_priority = :taskPriority) AND " +
            "(CAST(:startDate AS DATE)IS NULL OR start_date >= :startDate) AND " +
            "(CAST(:dueDate AS DATE)IS NULL OR due_date <= :dueDate)",
     nativeQuery = true)
	//@Query("FROM Task WHERE emailId = :emailId AND ((:taskTitle IS NULL OR taskTitle LIKE %:taskTitle%) AND (:taskPriority IS NULL OR taskPriority = :taskPriority) AND (:startDate IS NULL OR startDate >= :startDate) AND (:dueDate IS NULL OR dueDate <= :dueDate))")
	List<Task> findFilteredAssignedTasks(String taskTitle, String taskPriority, LocalDate startDate, LocalDate dueDate, String emailId);
	
	@Query("SELECT TO_CHAR(t.plannedStartDate, 'MM'), COUNT(*) " +
	        "FROM Task t " +
	        "WHERE t.plannedStartDate BETWEEN :startTime AND :endTime " +
	        "AND t.taskOwner = :email " +  
	        "GROUP BY TO_CHAR(t.plannedStartDate, 'MM')")
	List<Object[]> findTaskCountsByMonth(LocalDate startTime, LocalDate endTime, String email);
	
	@Query("SELECT TO_CHAR(t.startDate, 'MM'), " +
	        "SUM(CASE WHEN t.status = 'Completed' THEN 1 ELSE 0 END) " +
	        "FROM Task t " +
	        "WHERE t.startDate BETWEEN :startTime AND :endTime " +
	        "AND t.taskOwner = :email " +  
	        "GROUP BY TO_CHAR(t.startDate, 'MM')")
	List<Object[]> findCompletedTaskCountsByMonth(LocalDate startTime, LocalDate endTime, String email);

	@Query("SELECT TO_CHAR(t.startDate, 'MM'), " +
	        "SUM(CASE WHEN t.status = 'Inprogress' THEN 1 ELSE 0 END) " +
	        "FROM Task t " +
	        "WHERE t.startDate BETWEEN :startTime AND :endTime " +
	        "AND t.taskOwner = :email " +  
	        "GROUP BY TO_CHAR(t.startDate, 'MM')")
	List<Object[]> findInProgressTaskCountsByMonth(LocalDate startTime, LocalDate endTime, String email);
	
	List<Task> findByDepartmentId(Long departmentId);
	
	List<Task> findByTaskPriority(String taskPriority);
	
	List<Task> findByStatus(String status);
	
	@Query("FROM Task WHERE plannedEndDate < :currentDate")
	List<Task> findAgedTasks(LocalDate currentDate);
	
	@Query("SELECT TO_CHAR(t.plannedStartDate, 'MM'), COUNT(*) " +
	        "FROM Task t " +
	        "WHERE t.plannedStartDate BETWEEN :startDate AND :endDate " +
	        "GROUP BY TO_CHAR(t.plannedStartDate, 'MM')")
	List<Object[]> findTaskCountsforYear(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query("SELECT TO_CHAR(t.plannedStartDate, 'D'), " +
	           "SUM(CASE WHEN t.status = 'Yet to start' THEN 1 ELSE 0 END) " +
	           "FROM Task t " +
	           "WHERE t.plannedStartDate BETWEEN :startTime AND :endTime " +
	           "AND t.taskOwner= :email " +
	           "GROUP BY TO_CHAR(t.plannedStartDate, 'D')")
	List<Object[]> findYetToStartTaskCountsByDayOfWeek( LocalDate startTime,LocalDate endTime, String email);

	@Query("SELECT TO_CHAR(t.plannedStartDate, 'MM'), " +
     "SUM(CASE WHEN t.status = 'Yet to start' THEN 1 ELSE 0 END) " +
     "FROM Task t " +
     "WHERE t.plannedStartDate BETWEEN :startTime AND :endTime " +
     "AND t.taskOwner = :email " +  
     "GROUP BY TO_CHAR(t.plannedStartDate, 'MM')")
	List<Object[]> findYetToStartTaskCountsByMonth(LocalDate startTime, LocalDate endTime, String email);
	 
	
}
