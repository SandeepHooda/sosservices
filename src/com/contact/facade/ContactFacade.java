package com.contact.facade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.communication.email.EmailAddess;
import com.communication.email.MailService;
import com.contact.vo.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.login.facade.LoginFacade;
import com.login.vo.LoginVO;
import com.login.vo.Settings;

import mangodb.MangoDB;

public class ContactFacade {
	private LoginFacade loginFacede = new LoginFacade();
	
	public List<Contact> getContacts(String regID){
		return getContacts( regID, null);
	}
	public List<Contact> getContacts(String regID, String email){
		Settings settings=  getSettings( regID, email);
		if (null != settings && settings.getContactList() != null) {
			return settings.getContactList();
		}else {
			return new ArrayList<Contact>();
		}
	}
	
	public Settings getCallSetings( String email) {
		
			Gson json = new Gson();
			String settingsJson = MangoDB.getDocumentWithQuery("remind-me-on", "registered-users-settings", email, null,true, null, null);
			 Settings settings = json.fromJson(settingsJson, new TypeToken<Settings>() {}.getType());
			 if (settings == null) {
				 settings = new Settings();
				 settings.set_id(email);
			 }
			 
			 return settings;
		
	}
	public String checkBalance(String regID, String email) {
		if (email == null ) {
			LoginVO user = loginFacede.validateRegID(regID);
			if (null != user) {
				email = user.getEmailID();
			}
		}
		
		if (null != email) {
			Gson json = new Gson();
			String settingsJson = MangoDB.getDocumentWithQuery("remind-me-on", "registered-users-settings", email, null,true, null, null);
			 Settings settings = json.fromJson(settingsJson, new TypeToken<Settings>() {}.getType());
			 if (settings == null) {
				 settings = new Settings();
				 settings.set_id(email);
			 }
			 double balance = settings.getCurrentCallCredits();
			 if (balance <=10) {
				 EmailAddess toAddress = new EmailAddess();
				 toAddress.setAddress(email);
				 new  MailService().sendSimpleMail(MailService.prepareEmailVO(toAddress, "Please add call credits to your SOS Services account.",	"Please add cash credit to enable us to call your contacts when you need to. https://sosservices.appspot.com/ui/#/addcash", null, null));
					
			 }
			 return ""+settings.getCurrentCallCredits();
		}
		
		return "";
	}

	
	
	public List<Contact> deleteContact(String regID, String entry, String name){
		Settings settings=  getSettings( regID, null);
		if (null != settings && settings.getContactList() != null) {
			Iterator<Contact> itr = settings.getContactList().iterator();
			while (itr.hasNext()) {
				Contact contact = itr.next();
				if ((name != null && name.equals(contact.getUserEntry()) ) || (entry != null && entry.equals(contact.getUserEntry()) )) {
					itr.remove();
					break;
				}
			}
			return updateContact( settings);
		}
		return new ArrayList<Contact>();
	}
	
	public List<Contact> deleteContact(Contact contact, String email){
		Settings settings=  getSettings( null, email);
		if (null != settings && settings.getContactList() != null) {
			Iterator<Contact> itr = settings.getContactList().iterator();
			while (itr.hasNext()) {
				Contact contactDB = itr.next();
				if (contactDB.getContactName().equalsIgnoreCase(contact.getContactName()) ) {
					itr.remove();
					break;
				}
			}
			return updateContact( settings);
		}
		return new ArrayList<Contact>();
	}
	private List<Contact> updateContact(Settings settings){
		loginFacede.updateSettings(settings);
		return settings.getContactList();
	}
	public Settings  getSettings(String regID, String email){
		
		if (null == email) {
			LoginVO loginVO = loginFacede.validateRegID(regID);
			if (null != loginVO ) {
			 email = loginVO.getEmailID();
			}
		}
		//1. Get email from reg id
		
		
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
		
		return null;
	}
	
	public List<Contact> addContact(Contact contact, String regID){
		contact.setContactName(contact.getContactName().trim());
		contact.setCountryCode(contact.getCountryCode().trim());
		contact.setPhoneNumber(contact.getPhoneNumber().trim());
		return  addContact( contact,  regID,null);
	}
	
	public List<Contact> addContact(Contact contact, String regID,String email){
		Settings settings=  getSettings( regID, email);
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
