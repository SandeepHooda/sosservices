package googleAssistant;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.Oauth;
import com.google.gson.Gson;

import googleAssistant.util.CountryMap;
import request.GoogleRequest;

/**
 * Servlet implementation class Handler
 */

public class Handler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private Gson gson = new Gson(); 
	
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
					String nameOfContact = (String) googlerequest.getQueryResult().getParameters().get("any");
					
					char[] phoneChars = phoneNumber.toCharArray();
					StringBuilder phoneWithSpaces = new StringBuilder();
					for (char digit: phoneChars) {
						phoneWithSpaces.append(digit +" ");
					}
					serviceResponse =   name+", I have added "+nameOfContact+" phone number "+phoneWithSpaces+" to your  contacts list country "+isd;
					
				}
			
			
		}else if ("DeleteContacts".equalsIgnoreCase(intent) && null != queryText){
			String nameOfContact = (String) googlerequest.getQueryResult().getParameters().get("any");
			
			serviceResponse =   name+", Do you want to delete  "+nameOfContact;
			continueConversation = false;
		}else if ("DeleteContacts - yes".equalsIgnoreCase(intent) && null != queryText) {
			String nameOfContact = (String) googlerequest.getQueryResult().getParameters().get("any");
			
			serviceResponse =   name+", I have deleted  "+nameOfContact;
			
		}else if ("GetContactList".equalsIgnoreCase(intent) && null != queryText) {
			serviceResponse =   name+", I will get your contact list  ";
			
		}else if ("MakeACall".equalsIgnoreCase(intent) || "Default Welcome Intent - yes".equalsIgnoreCase(intent) || 
				"Default Fallback Intent - yes".equalsIgnoreCase(intent)){
			serviceResponse =   name+", I will make a call  ";
			continueConversation = false;
			expectUserResponse = false;
			
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
