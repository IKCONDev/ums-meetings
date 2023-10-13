package com.ikn.ums.meeting.model;

import java.util.List;

import com.ikn.ums.meeting.entity.ActionItem;
import com.ikn.ums.meeting.entity.Meeting;

import lombok.Data;

@Data
public class MinutesOfMeeting {
	
	private Meeting meeting;
	
	private List<ActionItem> actionItemList;
	
	private String[] emailList;

}