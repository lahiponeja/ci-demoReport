package com.ricoh.cidemo.report.portlet.dto;

public class UserDto {
	
	 private String userId;
	 private String fullName;
	 private String screenName;
	 private String	emailAddress;
	 
	public UserDto(String userId, String fullName, String screenName, String middleName) {
		super();
		this.userId = userId;
		this.fullName = fullName;
		this.screenName = screenName;
		this.emailAddress = emailAddress;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
