package com.ikn.ums.meeting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ikn.ums.meeting.entity.ActionItem;

public interface ActionItemRepository extends JpaRepository<ActionItem, Integer>{

	@Query("FROM ActionItem WHERE meetingId=:meetingId")
	public List<ActionItem> findActionItemsByEventId(Long meetingId);
	
	@Query("FROM ActionItem WHERE emailId=:emailId")
	List<ActionItem> findByUserId(String emailId);
	
	@Query("SELECT COUNT(*) FROM ActionItem WHERE emailId=:emailId")
	Long findOrganizedActionItemsCountByUserId(String emailId);
	

	//@Query(value = "select * from actionitem_tab where user_id=:emailId AND (actionitem_title_title='%:actionItemTitle%' OR task_priority=:taskPriority OR start_date>=:startDate OR due_date<=:dueDate)", nativeQuery = true)
	@Query("FROM ActionItem WHERE emailId=:emailId AND (actionItemTitle LIKE %:actionItemTitle% OR :actionItemOwner IS NULL OR startDate<=:startDate OR endDate<=:endDate)")
	public List<ActionItem> findAllFilteredActionItemsByUserId(String actionItemTitle, List<String> actionItemOwner,
			LocalDateTime startDate, LocalDateTime endDate, String emailId);

}
