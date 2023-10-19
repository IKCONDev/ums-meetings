package com.ikn.ums.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
