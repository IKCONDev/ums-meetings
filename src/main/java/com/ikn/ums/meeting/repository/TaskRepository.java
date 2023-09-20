package com.ikn.ums.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ikn.ums.meeting.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>{
	@Query("FROM Task WHERE userId=:userId")
	List<Task> findByUserId(String userId);
}
