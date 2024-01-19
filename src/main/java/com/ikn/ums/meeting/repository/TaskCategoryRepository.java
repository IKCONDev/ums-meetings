package com.ikn.ums.meeting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ikn.ums.meeting.entity.TaskCategory;

@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {

	//TODO : Check This
	 Optional<TaskCategory> findByTaskCategoryTitle(String taskCategoryTitle);
	 
	 @Query ("FROM TaskCategory WHERE taskCategoryStatus=:taskCategoryStatus")
	 List<TaskCategory> findAllTaskCategories(String taskCategoryStatus);
	
	 
}
