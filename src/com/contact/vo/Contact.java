package com.contact.vo;

public class Contact {
	
	private String userEntry ;
	private String countryCode ;
	private String phoneNumber ;
	private String contactName;
	
	public Contact() {
		
	}
	
	public Contact(String userEntry, String contactName, String countryCode, String phoneNumber) {
		this.contactName = contactName;
		this.userEntry = userEntry;
		this.countryCode = countryCode;
		this.phoneNumber = phoneNumber;
		
	}
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
	

}
