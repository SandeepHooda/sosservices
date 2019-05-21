package com.login.vo;

import java.io.Serializable;
import java.util.List;

import com.contact.vo.Contact;

public class Settings implements Serializable {
	private String _id;
	private String appTimeZone;
	private String userSuppliedTimeZone;
	private double currentCallCredits ;
	private List<Contact> contactList ;

	public String getAppTimeZone() {
		return appTimeZone;
	}

	public void setAppTimeZone(String appTimeZone) {
		this.appTimeZone = appTimeZone;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUserSuppliedTimeZone() {
		return userSuppliedTimeZone;
	}

	public void setUserSuppliedTimeZone(String userSuppliedTimeZone) {
		this.userSuppliedTimeZone = userSuppliedTimeZone;
	}

	public double getCurrentCallCredits() {
		return currentCallCredits;
	}

	public void setCurrentCallCredits(double currentCallCredits) {
		this.currentCallCredits = currentCallCredits;
	}

	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}

}
