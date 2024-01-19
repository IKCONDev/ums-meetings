package com.ikn.ums.meeting.repository;

import java.time.LocalDate;
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
	//@Query("FROM ActionItem WHERE emailId=:emailId AND (actionItemTitle LIKE %:actionItemTitle% OR :actionItemOwner IS NULL OR startDate<=:startDate OR endDate<=:endDate)")
	@Query(value = "SELECT a.* FROM actionitem_tab a " +
	        "JOIN action_item_owners o ON o.action_item_id = a.id " +
	        "WHERE a.user_id = :emailId AND " +
	        "(:actionItemTitle IS NULL OR a.action_item_title LIKE %:actionItemTitle%) AND " +
	        "(:actionItemOwner IS NULL OR o.action_item_owner = :actionItemOwner) AND " +
	        "(CAST(:startDate AS DATE) IS NULL OR a.start_date >= :startDate ) AND " +
	        "(CAST(:endDate AS DATE) IS NULL OR a.start_date <= :endDate ) " +
	        "GROUP BY a.id",
	        nativeQuery = true)
	public List<ActionItem> findAllFilteredActionItemsByUserId(String actionItemTitle, String actionItemOwner,
			LocalDate startDate, LocalDate endDate, String emailId);
	
	//Reports methods
	List<ActionItem> findByDepartmentId(Long departmentId);
	List<ActionItem> findByActionPriority(String actionPriority);
	@Query(value ="select department_Id, count(*) from actionitem_tab Group By department_Id;", nativeQuery = true)
	List<Object[]> getCountOfActionItemsByDepartment();
	
	@Query(value="select * from actionitem_tab where meeting_id=:meetingId", nativeQuery = true)
	List<ActionItem> getAllActionItemsByMeetingId(Long meetingId);

}
