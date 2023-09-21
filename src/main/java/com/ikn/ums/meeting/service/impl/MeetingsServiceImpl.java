package com.ikn.ums.meeting.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ikn.ums.meeting.exception.BusinessException;
import com.ikn.ums.meeting.exception.ErrorCodeMessages;
import com.ikn.ums.meeting.service.ActionItemService;
import com.ikn.ums.meeting.service.MeetingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MeetingsServiceImpl implements MeetingService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ActionItemService actionItemService;

	@Override
	public boolean removeActionItemsOfEvent(String acItemIds, Integer eventId) {
		System.out.println("EventServiceImpl.removeActionItemsOfEvent()");
		log.info("EventServiceImpl.removeActionItemsOfEvent() entered with args : actionItemIds - "+acItemIds+" evenId - "+eventId);
		boolean isDeleted = false;
			log.info("EventServiceImpl.removeActionItemsOfEvent() is under execution");
			List<Integer> actualAcIds = null;
			if(acItemIds != "") {
				String[] idsFromUI = acItemIds.split(",");
				List<String> idsList =  Arrays.asList(idsFromUI);
				//convert string of ids to Integer ids
				actualAcIds = idsList.stream()
	                     .map(s -> Integer.parseInt(s))
	                     .collect(Collectors.toList());
			actionItemService.deleteAllActionItemsById(actualAcIds);
			}
			isDeleted = true;
		log.info("EventServiceImpl.removeActionItemsOfEvent() exited sucessfully by returning "+isDeleted);
		return isDeleted;
	}

}
