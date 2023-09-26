package com.ikn.ums.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ikn.ums.meeting.entity.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

}
