package com.contact.facade;

import java.util.ArrayList;
import java.util.List;

import com.contact.vo.Contact;
import com.login.facade.LoginFacade;
import com.login.vo.LoginVO;
import com.login.vo.Settings;

public class ContactFacade {
	private LoginFacade loginFacede = new LoginFacade();
	
	public List<Contact> getContacts(String regID){
		Settings settings=  getSettings( regID);
		if (null != settings && settings.getContactList() != null) {
			return settings.getContactList();
		}else {
			return new ArrayList<Contact>();
		}
	}
	
	public Settings  getSettings(String regID){
		
		//1. Get email from reg id
		LoginVO loginVO = loginFacede.validateRegID(regID);
		if (null != loginVO ) {
			String email = loginVO.getEmailID();
			if (email != null) {
				//2. Get setting via email
				Settings settings = loginFacede.getUserSettings( email);
				//3. Add contact to array list
				List<Contact> contactList = new ArrayList<Contact>();
				if (null != settings.getContactList()) {
					contactList = settings.getContactList();
				}
				settings.setContactList(contactList);
				return settings;
			}
		}
		return null;
	}
	
	
	public List<Contact> addContact(Contact contact, String regID){
		Settings settings=  getSettings( regID);
		if (null != settings) {
			List<Contact> contactList = settings.getContactList();
			if (null == contactList) {
				contactList = new ArrayList<Contact>();
			}
			contactList.add(contact);
			loginFacede.updateSettings(settings);
			return contactList;
		}else {
			return new ArrayList<Contact>();
		}
		
	}

}
