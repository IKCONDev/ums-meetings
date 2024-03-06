package com.ikn.ums.meeting.model;

import java.util.List;

import com.ikn.ums.meeting.entity.Meeting;

import lombok.Data;

@Data
public class MinutesOfMeeting {

	private Meeting meeting;

	private List<String> emailList;

	private String discussionPoints;

	private String hoursDiff;

	private String minutesDiff;

}
