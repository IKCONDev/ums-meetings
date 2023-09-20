package com.ikn.ums.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ikn.ums.meeting.entity.ActionItem;

public interface ActionItemRepository extends JpaRepository<ActionItem, Integer>{

	@Query("FROM ActionItem WHERE eventid=:eventId")
	public List<ActionItem> findActionItemsByEventId(Integer eventId);
}
