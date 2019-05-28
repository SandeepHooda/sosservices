package googleAssistant;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.communication.email.EmailAddess;
import com.communication.email.MailService;
import com.communication.phone.call.MakeACall;
import com.contact.facade.ContactFacade;
import com.contact.vo.Contact;
import com.google.Oauth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.login.vo.ContactUS;
import com.login.vo.Settings;
import com.reminder.vo.CallLogs;

import googleAssistant.util.CountryMap;
import mangodb.MangoDB;
import request.GoogleRequest;
import request.OutputContexts;

/**
 * Servlet implementation class Handler
 */

public class Handler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private Gson gson = new Gson(); 
	 private ContactFacade contactFacade = new ContactFacade();
	
	 private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Handler() {
        super();
        // TODO Auto-generated constructor stub
    }
    
   
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println(" Request from google ");
		boolean needLocation = false;
		 StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
            sb.append(s);
        }
     
        String intent = "";
        String queryText = null;
        String access_token = null;
        GoogleRequest googlerequest = null;
        try {
        	 googlerequest = (GoogleRequest) gson.fromJson(sb.toString(), GoogleRequest.class);
        	intent = googlerequest.getQueryResult().getIntent().getDisplayName();
        	queryText = googlerequest.getQueryResult().getQueryText();
        	access_token = googlerequest.getOriginalDetectIntentRequest().getPayload().getUser().getAccessToken();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        System.out.println(" complete request: "+sb.toString());
        String serviceResponse = "";
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String email = null;
		String name = null;
		String timeZones[] = null;
		boolean continueConversation = true;
		boolean expectUserResponse = true;
		if (null != access_token) {
			Map<String, String> userData = new Oauth().getUserEmailFromMangoD(access_token);
			email = userData.get("emailID");
			name  = userData.get("name");
		}
       
		System.out.println(" got email and name from mango DB "+email+" "+name);
		if ("AddPhone".equalsIgnoreCase(intent) && null != queryText){
			serviceResponse =   name+", I didn't understand what you just said. Please try again.";
			Map<String, String> geoCountry = (Map<String, String>) googlerequest.getQueryResult().getParameters().get("geo-country-code");
			String isd = CountryMap.data.get(geoCountry.get("alpha-2"));
			
				if (isd != null) {
					
					String phoneNumber = (String) googlerequest.getQueryResult().getParameters().get("phone-number");
					phoneNumber = phoneNumber.replaceAll("[^\\d.]", "");
					char[] phoneChars = phoneNumber.toCharArray();
					StringBuilder phoneWithSpaces = new StringBuilder();
					for (char digit: phoneChars) {
						phoneWithSpaces.append(digit +" ");
					}
					if (phoneNumber.length() <7 || phoneNumber.length() >15) {
						serviceResponse =   name+",  "+phoneWithSpaces+" , is not a valid phone number. The minimum length of phone number must be 7 and maximum length must be 15.";
					}else {
						String nameOfContact = (String) googlerequest.getQueryResult().getParameters().get("any");
						
						
						
						Contact contact = new Contact(nameOfContact + " "+isd+ " "+phoneNumber, nameOfContact, isd, phoneNumber);
						contactFacade.addContact(contact, null, email);
						serviceResponse =   name+", I have added "+nameOfContact+" phone number "+phoneWithSpaces+" to your  contacts list country "+isd;
					}
					
					
				}
			
			
		}else if ("DeleteContacts".equalsIgnoreCase(intent) && null != queryText){
			String nameOfContact = (String) googlerequest.getQueryResult().getParameters().get("any");
			Contact contact = findContact( nameOfContact,  email);
			if (contact != null) {
				serviceResponse =   name+", Do you want to delete  "+contact.getContactName()+" ?";
			}else {
				serviceResponse =   name+", I didn't find  "+nameOfContact+" in your contact list. ";
			}
			
			continueConversation = false;
		}else if ("DeleteContacts - yes".equalsIgnoreCase(intent) && null != queryText) {
			if (null != googlerequest.getQueryResult().getOutputContexts()) {
				for (OutputContexts context : googlerequest.getQueryResult().getOutputContexts()) {
					if (context.getName().endsWith("deletecontacts-followup")) {
						String nameOfContact = (String) context.getParameters().get("any");
						
						Contact contact = findContact( nameOfContact,  email);
						contactFacade.deleteContact(contact, email);
						serviceResponse =   name+", I have deleted  "+nameOfContact;
					}
				}
			}
			
			
		}else if ("GetContactList".equalsIgnoreCase(intent) && null != queryText) {
			List<Contact> contactList = contactFacade.getContacts(null,  email);
			serviceResponse =   name+", ";
			if (null != contactList && contactList.size() > 0) {
				serviceResponse += "You have saved the phone number of , ";
				for (Contact contact : contactList) {
					serviceResponse += contact.getContactName() +" , ";
				}
				serviceResponse += " in your contact list.";
			}else {
				serviceResponse =   " You don't have contact in your contact list. To add a new contact please say add new contact.  ";
			}
			
			
		}else if ("MakeACall".equalsIgnoreCase(intent) || "Default Welcome Intent - yes".equalsIgnoreCase(intent) || 
				"Default Fallback Intent - yes".equalsIgnoreCase(intent)){
			double balance = checkbalance(email);
			Settings settings =  contactFacade.getCallSetings(email);
			List<Contact> contactList = contactFacade.getContacts(null,  email);
			if (null != contactList && contactList.size() > 0) {
						if ( balance > -100) {
							if (balance >= 10) {
								
								expectUserResponse = false;
								serviceResponse =   name+", I am calling ";
								for (Contact aContact:contactList ) {
									String phone = aContact.getCountryCode().trim() + aContact.getPhoneNumber().trim();
									phone = phone.replaceAll("[^\\d.]", "");
									serviceResponse +=aContact.getContactName()+ ", ";
									
									String msg = aContact.getContactName()+", I am calling your on behalf of "+name+". "+name+
											"  needs your immediate help. Please get in touch with "+name+" immediately. ";
									makeACall(name, email,  phone,aContact.getContactName(), msg,  settings);
								}
								serviceResponse += " to notify that you need help.";
								expectUserResponse = false;
								continueConversation = false;
								
							}else {
								
								serviceResponse =   name+", Please add cash credit to enable us to call your contacts when you need to. Please check your email for details to add cash to your account."; 
								expectUserResponse = false;
								continueConversation = false;
							}
							
						}else {//it is dummy account
							serviceResponse =   name+", This app can Make a call to your family members that you have saved to the contact list. Also you need to add balance to your account that will be used to make a call. "
									+ "Right now you have saved "+contactList.size()+" contacts in your contact list.  This app is not an emergeny service and will not call 9 1 1 or any other emergency services. "
											+ "You are uising this app as a demo account. Make a call  feature to your contact list  is disabled as a test account. You need to sign in with as real user. ";
							expectUserResponse = true;
							continueConversation = true;
						}
							
						
						
					}else {
						serviceResponse +=
								"I can't call emergency services like 9 1 1 but I can call your family members when you ask me to do for example in emergency situations."
								+ "For that You haven't added  contact person in your contact list. Please add a new contact by saying add a new contact. ";
						expectUserResponse = true;
						continueConversation = false;
					}
					
				
			
			
			
		}else if ("Default Welcome Intent".equalsIgnoreCase(intent)){
			double balance = checkbalance(email);
			if ( balance > -100) {
				if (balance >= 10) {
					serviceResponse =   name+", Do you want me to call your contacts?  ";
				}else {
					serviceResponse =   name+"Please add cash credit to enable us to call your contacts when you need to. Please check your email for details to add cash to your account."; 
				}
				
			}else {//it is dummy account
				serviceResponse =   name+", To add a new contact say add a new contact.  ";
			}
			
			continueConversation = false;
			
			
		}
			else {
			serviceResponse = name+", Should I call your contacts? ";
			continueConversation = false;
			
		}
		
		String continueStr  = "";
				if (continueConversation) {
					continueStr  = ". Anything else I can help you with?";
				}
		/*String responseStr = "{\r\n" + 
		"  \"fulfillmentText\": \"  "+serviceResponse+continueStr+"  \",\r\n" + 
		"  \"outputContexts\": []\r\n" + 
		"}";*/
		String responseStr =  getCompleteResponse( serviceResponse+continueStr , expectUserResponse);
		 
		 System.out.println("intent "+intent+" queryText "+queryText+" serviceResponse "+responseStr);
		 out.print(responseStr );
       
       out.flush();   
	}
   
    private void makeACall(String nameOfCaller, String emailOfCaller, String phoneOfCallee, String nameOfCallee, String msg, Settings settings) {
    	Gson  json = new Gson();
    	
    		CallLogs callLog = new CallLogs();
    		callLog.set_id(""+new Date().getTime());
    		callLog.setFrom(emailOfCaller);
    		
    		callLog.setTo(phoneOfCallee);
    		
    		callLog.setMessage(msg);
    		String logsJson = json.toJson(callLog, new TypeToken<CallLogs>() {}.getType());
    		 MangoDB.createNewDocumentInCollection("remind-me-on", "call-logs", logsJson, null);
    		 
    		 try {
    				if ( settings.getCurrentCallCredits() >=5) {
    					if (!MakeACall.call(callLog.getTo(), callLog.get_id())) {
    						throw new Exception("Couln't not notify user via Phone");
    					}
    					
    					//Make a call above the comment and then update settings
    					 settings.setCurrentCallCredits(settings.getCurrentCallCredits() -5);
    				}
    			}catch(Exception e) {
    				e.printStackTrace();
    			}
    
    	
    	String settingsJson = json.toJson(settings, new TypeToken<Settings>() {}.getType());
    	
    	MangoDB.updateData("remind-me-on", "registered-users-settings", settingsJson, emailOfCaller, null);
    	
    }
    private double checkbalance(String email) {
    	if (email.equalsIgnoreCase("ActionTimeManager1@gmail.com")) {
    		return -100;
    	}
    	try {
    		double balance = Double.parseDouble(contactFacade.checkBalance(null,  email));
    		return balance;
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	 return -101;
    }
    private Contact findContact(String name, String email) {
    	Contact contact = null;
    	List<Contact> contactList = contactFacade.getContacts(null, email);
    	if (null != contactList ) {
    		for (Contact aContact: contactList) {
    			if (aContact.getContactName().contains(name)) {
    				return aContact;
    			}
    		}
    	}
    	return null;
    }
    
    private String getCompleteResponse(String textToSpeak, boolean expectUserResponse) {
    	return responsePre_1+expectUserResponse+responsePre_2+textToSpeak+responsePost;
    	
    }
   
   
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private static final String responsePre_1 = "{\r\n" + 
			"  \"payload\": {\r\n" + 
			"    \"google\": {\r\n" + 
			"      \"expectUserResponse\": ";
	private static final String responsePre_2 = " ,\r\n" + 
			"      \"richResponse\": {\r\n" + 
			"        \"items\": [\r\n" + 
			"          {\r\n" + 
			"            \"simpleResponse\": {\r\n" + 
			"              \"textToSpeech\": \"";
	private static final String responsePost ="\"\r\n" + 
			"            }\r\n" + 
			"          }\r\n" + 
			"        ]\r\n" + 
			"      }\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}";
	
	
	
	public static final String intentPush = "GetToDoPushNotification_9";
	
	
	private static final String location = "{\r\n" + 
			"  \"payload\": {\r\n" + 
			"    \"google\": {\r\n" + 
			"      \"expectUserResponse\": true,\r\n" + 
			"      \"richResponse\": {\r\n" + 
			"        \"items\": [\r\n" + 
			"          {\r\n" + 
			"            \"simpleResponse\": {\r\n" + 
			"              \"textToSpeech\": \"PLACEHOLDER\"\r\n" + 
			"            }\r\n" + 
			"          }\r\n" + 
			"        ]\r\n" + 
			"      },\r\n" + 
			"      \"userStorage\": \"{\\\"data\\\":{}}\",\r\n" + 
			"      \"systemIntent\": {\r\n" + 
			"        \"intent\": \"actions.intent.PLACE\",\r\n" + 
			"        \"data\": {\r\n" + 
			"          \"@type\": \"type.googleapis.com/google.actions.v2.PermissionValueSpec\",\r\n" + 
			"          \"optContext\": \"To set reminder at correct time zone.\",\r\n" + 
			"          \"permissions\": [\r\n" + 
			"            \"NAME\",\r\n" + 
			"            \"DEVICE_PRECISE_LOCATION\"\r\n" + 
			"          ]\r\n" + 
			"        }\r\n" + 
			"      }\r\n" + 
			"    }\r\n" + 
			"  },\r\n" + 
			"  \"outputContexts\": [\r\n" + 
			"    {\r\n" + 
			"      \"name\": \"/contexts/_actions_on_google\",\r\n" + 
			"      \"lifespanCount\": 99,\r\n" + 
			"      \"parameters\": {\r\n" + 
			"        \"data\": \"{}\"\r\n" + 
			"      }\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";

}
