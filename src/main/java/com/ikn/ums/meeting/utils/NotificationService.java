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

import lombok.extern.slf4j.Slf4j;
@Slf4j
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
		log.info("createNotification() is entered");
		String createNotificationUrl = "http://UMS-NOTIFICATION-SERVICE/notification/create";
		HttpEntity<Notification> httpEntity = new HttpEntity<Notification>(notification);
		log.info("createNotification() is calling notification microservice under execution...");
		ResponseEntity<Notification> response = restTemplate.exchange(createNotificationUrl,
				HttpMethod.POST,httpEntity, Notification.class);
		Notification createdNotification = response.getBody();
		log.info("createNotification() is calling notification microservice executed successfully");
		return createdNotification;
	}
	
	/**
	 * creates new notifications
	 * @param notificationList
	 * @return
	 */
	public List<Notification> createAllNotifications(List<Notification> notificationList) {
		log.info("createAllNotifications() is entered");
		String createNotificationUrl = "http://UMS-NOTIFICATION-SERVICE/notification/createAll";
		HttpEntity<List<Notification>> httpEntity = new HttpEntity<List<Notification>>(notificationList);
		log.info("createAllNotifications() is calling notification microservice under execution...");
		ResponseEntity<List<Notification>> response = restTemplate.exchange(createNotificationUrl,
				HttpMethod.POST,httpEntity, new ParameterizedTypeReference<List<Notification>>(){});
		List<Notification> createdNotificationList = response.getBody();
		log.info("createAllNotifications() is calling notification microservice executed successfully");
		return createdNotificationList;
	}

}
