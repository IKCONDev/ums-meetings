package com.ikn.ums.meeting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ikn.ums.meeting.dto.TaskCategoryDTO;

@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategoryDTO, Long> {

	//TODO : Check This
	 Optional<TaskCategoryDTO> findByTaskCategoryTitle(String taskCategoryTitle);
	 
	 @Query ("FROM Role WHERE taskCategoryStatus=:taskCategoryStatus")
	 List<TaskCategoryDTO> findAllTaskCategories(String taskCategoryStatus);
	 
}
