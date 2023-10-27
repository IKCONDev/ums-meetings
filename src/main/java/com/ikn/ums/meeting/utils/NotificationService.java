package com.ikn.ums.meeting.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ikn.ums.meeting.VO.Notification;

@Component
public class NotificationService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * creates a new notification
	 * @param notification
	 * @return
	 */
	public Notification createNotification(Notification notification) {
		String createNotificationUrl = "http://UMS-NOTIFICATION-SERVICE/notification/create";
		HttpEntity<Notification> httpEntity = new HttpEntity<Notification>(notification);
		ResponseEntity<Notification> response = restTemplate.exchange(createNotificationUrl,
				HttpMethod.POST,httpEntity, Notification.class);
		Notification createdNotification = response.getBody();
		return createdNotification;
	}
	
	/**
	 * creates new notifications
	 * @param notificationList
	 * @return
	 */
	public List<Notification> createAllNotifications(List<Notification> notificationList) {
		String createNotificationUrl = "http://UMS-NOTIFICATION-SERVICE/notification/createAll";
		HttpEntity<List<Notification>> httpEntity = new HttpEntity<List<Notification>>(notificationList);
		ResponseEntity<List<Notification>> response = restTemplate.exchange(createNotificationUrl,
				HttpMethod.POST,httpEntity, new ParameterizedTypeReference<List<Notification>>(){});
		List<Notification> createdNotificationList = response.getBody();
		return createdNotificationList;
	}

}
