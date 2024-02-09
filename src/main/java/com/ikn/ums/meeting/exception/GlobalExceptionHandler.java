package com.ikn.ums.meeting.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


	/**
	 * EntityNotFoundException handles the exception when the Object is NUll
	 * @param emptyInputException
	 * @return
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
		log.info("GlobalExceptionHandler.handleEntityNotFoundException() ENTERED :" + entityNotFoundException.getMessage());
		log.info("Entity Object is NUll." );
		return new ResponseEntity<String>(entityNotFoundException.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The EmptyInputException is a Custom Exception (created by us) written for
	 * handling the Business Scenarios. defined
	 * @param emptyInputException
	 * @return
	 */
	@ExceptionHandler(EmptyInputException.class)
	public ResponseEntity<String> handleEmptyInput(EmptyInputException emptyInputException) {
		log.info("GlobalExceptionHandler.handleEmptyInput() ENTERED" + emptyInputException.getMessage());
		log.info("Empty Input Value." );
		return new ResponseEntity<String>(emptyInputException.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmployeeExistsException.class)
	public ResponseEntity<String> handleEmployeeExistsException(EmployeeExistsException employeeExistsException) {
		log.info("GlobalExceptionHandler.handleEmployeeExistsException() ENTERED" + employeeExistsException.getMessage());
		log.info("Employee already exists!" );
		return new ResponseEntity<String>(employeeExistsException.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The NoSuchElementException is a Pre-defined default handler for the
	 * SpringBoot. No class required to be created for pre-defined.
	 * 
	 * @param noSuchElementException
	 * @return
	 */
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException noSuchElementException) {
		log.info("GlobalExceptionHandler.handleNoSuchElementException() ENTERED" + noSuchElementException.getMessage());
		log.info("No Value is Present in DB." );
		return new ResponseEntity<String>(noSuchElementException.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * Handling the Business Exceptions global to reduce boiler plate code
	 * @param noSuchElementException
	 * @return
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<String> handleBusinessException(BusinessException businessException) {
		log.info("GlobalExceptionHandler.handleBusinessException() ENTERED" + businessException.getMessage());
		log.info("Business Exception Occurred.");
		return new ResponseEntity<String>(businessException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handling the Controller Exceptions global to reduce boiler plate code
	 * @param noSuchElementException
	 * @return
	 */
	@ExceptionHandler(ControllerException.class)
	public ResponseEntity<String> handleControllerException(ControllerException controllerException) {
		log.info("GlobalExceptionHandler.handleControllerException() ENTERED" + controllerException.getMessage());
		log.info("Controller Exception Occurred" + controllerException.getMessage());
		return new ResponseEntity<String>(controllerException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NotificationServiceUnavailableException.class)
	public ResponseEntity<String> handleNotificationServiceUnavailableException(NotificationServiceUnavailableException notificationServiceException) {
		log.info("GlobalExceptionHandler.handleControllerException() ENTERED" + notificationServiceException.getMessage());
		log.info("Controller Exception Occurred" + notificationServiceException.getMessage());
		return new ResponseEntity<String>(notificationServiceException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.info("GlobalExceptionHandler.handleEmptyInput() ENTERED" + ex.getMessage());
		return new ResponseEntity<Object>("Please change your http method type.", HttpStatus.NOT_FOUND);
	}
	
	/**
	 * TaskCatagoryTitleExistsException handles the exception when the Role Name is exists
	 * @param taskCatagoryTitleExistsException
	 * @return
	 */
	@ExceptionHandler(TaskCatagoryTitleExistsException.class)
	public ResponseEntity<String> handleTaskCatagoryTitleExistsException(TaskCatagoryTitleExistsException taskCatagoryTitleExistsException) {
		log.info("GlobalExceptionHandler.handleTaskCatagoryTitleExistsException() ENTERED" + taskCatagoryTitleExistsException.getMessage());
		return new ResponseEntity<String>("Task Catagory title already exists.", HttpStatus.FOUND);
	}
	
	/**
	 * TaskCatagoryTitleExistsException handles the exception when the Role Name is exists
	 * @param taskCatagoryTitleExistsException
	 * @return
	 */
	@ExceptionHandler(TaskCatagoryInUsageException.class)
	public ResponseEntity<String> handleTaskCatagoryInUsageException(TaskCatagoryInUsageException taskCatagoryInUsageException) {
		log.info("GlobalExceptionHandler.handleTaskCatagoryInUsageException() ENTERED" + taskCatagoryInUsageException.getMessage());
		return new ResponseEntity<String>("Task Catagory is in usage, cannot be deleted.", HttpStatus.IM_USED);
	}

}
