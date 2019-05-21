package com.contact.vo;

public class Contact {
	private String regID;
	private String userEntry ;
	private String countryCode ;
	private String phoneNumber ;
	private String contactName;
	
	public String getUserEntry() {
		return userEntry;
	}
	public void setUserEntry(String userEntry) {
		this.userEntry = userEntry;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getRegID() {
		return regID;
	}
	public void setRegID(String regID) {
		this.regID = regID;
	}

}
