package com.ikn.ums.meeting.exception;

public class NotificationServiceUnavailableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMessage;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public NotificationServiceUnavailableException(String errorCode, String errorMessage) {
		super(errorMessage, new Throwable(errorCode));
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public NotificationServiceUnavailableException() {
		super();
	}
}
