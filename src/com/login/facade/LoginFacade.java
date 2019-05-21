package com.login.facade;



import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.login.vo.LoginVO;
import com.login.vo.Settings;

import mangodb.MangoDB;

public class LoginFacade {
	private static final Logger log = Logger.getLogger(LoginFacade.class.getName());
	
	public LoginVO validateRegID(String regID) {
		String data = MangoDB.getDocumentWithQuery("sos-services", "registered-users", regID, null,true, null, null);
		 Gson  json = new Gson();
		 return json.fromJson(data, new TypeToken<LoginVO>() {}.getType());
		
	}
	
	public Settings getUserSettings(String email) {
		Gson  json = new Gson();
		String settingsJson = MangoDB.getDocumentWithQuery("sos-service-users-settings", "registered-users-settings", email, null,true, null, null);
		 Settings settings = json.fromJson(settingsJson, new TypeToken<Settings>() {}.getType());
		 if (null == settings ) {
			 settings = new Settings();
			 settings.set_id(email);
		}
		 return settings;
	}
	
	public void updateSettings(Settings settings) {
		Gson  json = new Gson();
		String settingsJson = json.toJson(settings, new TypeToken<Settings>() {}.getType());
		 MangoDB.createNewDocumentInCollection("sos-service-users-settings", "registered-users-settings", settingsJson, null);
	}
	public LoginVO validateRegIDAndUpdateSettings(String regID, String appTimeZone) {
		log.info("regID logged into system "+regID);
		LoginVO result = validateRegID( regID);
		 String email = null;
		 if (null != result) {
			 email =  result.getEmailID();
		 }
		
		 if (null != result && StringUtils.isNotBlank(email)) {
			 Gson  json = new Gson();
			 result.setEmailID(email);
			 result.setAppTimeZone(appTimeZone);
			 result.setLoginTime(new Date().getTime());
			 String data  = json.toJson(result, new TypeToken<LoginVO>() {}.getType());
	
			 MangoDB.updateData("sos-services", "registered-users", data, result.get_id(),null);//Insert loging time stamp
			 //Update settings 
			 Settings settings = getUserSettings( email);
			 
			 settings.setAppTimeZone(appTimeZone);
			 result.setUserSettings(settings);
			 
			 updateSettings( settings);
			 return result;
		 }else {
			 return null;
		 }
		
	}
	
	public LoginVO logout(String regID, HttpSession session) {
		
			MangoDB.deleteDocument("idonot-remember", "registered-users", regID,  null);
			session.invalidate();
		
		return validateRegIDAndUpdateSettings(regID, null);
	}
	
	


}
