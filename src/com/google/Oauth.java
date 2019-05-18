package com.google;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.login.vo.LoginVO;

import mangodb.MangoDB;


/**
 * Servlet implementation class Oauth
 */
public class Oauth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static FetchOptions lFetchOptions = FetchOptions.Builder.doNotValidateCertificate().setDeadline(300d);
	private static URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
	
	private static final Logger log = Logger.getLogger(Oauth.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Oauth() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Important notes
	 */
    //https://console.cloud.google.com/apis/credentials?project=sosservices
    //Also change redirect URI
   private static String client_secret = "X3ko-70RN-K3zIBIohcHTL09";
   private static String client_id = "566574600254-tdors8is4eicg8a4869v57a2rh848nck.apps.googleusercontent.com";
   //Enanle people.googleapis.com from https://console.cloud.google.com/apis/library?project=sosservices
   private static String googleActionsRedirectURI = "https://oauth-redirect.googleusercontent.com/r/findmythings-350ac?code=ACCESS_TOKEN_VALUE&state=";
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String code = request.getParameter("code");
		String client_id = request.getParameter("client_id");
		String state = request.getParameter("state");
		String refresh_token = request.getParameter("refresh_token");
		log.info("Oauth called state : "+state +" client_id : "+client_id+" code : "+code+" refresh_token "+refresh_token);
		
		if (null == client_id) {//Just a safety check - It don't happen
			client_id = Oauth.client_id;
		
		}
		if (null != refresh_token) {//Step 3
			Map<String,String>	map  = getAccesstokenFromRefreshToken(request, response,  refresh_token,  client_id);
			if (null == map.get("access_token")) {
		    	   response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    	   return;
		     }
			PrintWriter out = response.getWriter();
			String responseStr = "{\r\n" + 
					"\"token_type\": \"Bearer\",\r\n" + 
					"\"access_token\": \""+map.get("access_token")+"\",\r\n" + 
					"\"refresh_token\": \""+refresh_token+"\",\r\n" + 
					"\"expires_in\": 3599 \r\n" + 
					"}";
			       out.print(responseStr );
			       out.flush(); 
			       
			       
		}else
		 if (null != code) {//Step 2. get access token from auth code
			 
			if (null != state ) {
				System.out.println(" Will get access token "+code);
				Map<String,Object>	map = 	getAccesstoken(request, response, code, client_id);
				String access_token = (String)map.get("access_token");
				String refreshToken = (String)map.get("id_token");
				log.info("refreshToken :  "+map.get("access_token"));
				  addCookiedToResponseAndRecordLoginInDB(request, response,  access_token,refreshToken);
				 String redirectTo = googleActionsRedirectURI.replaceAll("ACCESS_TOKEN_VALUE", access_token)+state;
				 redirectTo = "/ui/index.html";
				 System.out.println(" being redirected to "+redirectTo);
				response.sendRedirect(redirectTo);
			}else {
				String loginVOStr = MangoDB.getDocumentWithQuery("sos-services", "registered-users", code, "accessToken", false, null,null);
				Gson  json = new Gson();
				LoginVO loginVO = json.fromJson(loginVOStr, new TypeToken<LoginVO>() {}.getType());
				PrintWriter out = response.getWriter();
				System.out.println(" code "+code);
				System.out.println(" loginVO.getRefreshToken() "+loginVO.getRefreshToken());
				String responseStr = "{\r\n" + 
						"\"token_type\": \"Bearer\",\r\n" + 
						"\"access_token\": \""+code+"\",\r\n" + 
						"\"refresh_token\": \""+loginVO.getRefreshToken()+"\",\r\n" + 
						"\"expires_in\": 3599 \r\n" + 
						"}";
				       out.print(responseStr );
				       out.flush(); 
			}
			
		}else {
		
			//Step 1. get auth code
			getAuthCode(request, response,client_id, state);
		}
		
	}
	
	public class UserObj{
		public String email;
		public String name;
	}
	 
	
	private UserObj addCookiedToResponseAndRecordLoginInDB(HttpServletRequest request, HttpServletResponse response,  String access_token, String refreshToken) throws IOException {
		
		
		//Set cookies
		Map<String, String> userData = getUserEmail(access_token);
		String email = userData.get("email");
		String name  = userData.get("name");
		UserObj userObj = new UserObj();
		userObj.email = email;
		userObj.name = name;
		addCookie("email", email,request, response );
		addCookie("name" , name,request, response );
		addCookie("cookieAccess" , access_token,request, response );
		addCookie("userdetails" , "{\"name\":\""+name+"\",\"avatar_url\":\"https://avatars0.githubusercontent.com/u/24775543?v=4\"}",request, response );
		
		//Insert in DB
		LoginVO loginVO = new LoginVO();
		loginVO.setEmailID(email);
		loginVO.setName(name);
		loginVO.setRefreshToken(refreshToken);
		loginVO.setAccessToken(access_token);
		addCookie("regID" , loginVO.getRegID(),request, response );
		 Gson  json = new Gson();
        String data = json.toJson(loginVO, new TypeToken<LoginVO>() {}.getType());
		
        MangoDB.createNewDocumentInCollection("sos-services", "registered-users", data, null);
		
		return userObj;
	}
	private void addCookie(String cookieName, String cookieValue ,HttpServletRequest request, HttpServletResponse response){
		Cookie cookie = new Cookie(cookieName,cookieValue);
	      cookie.setMaxAge(60*60*24); 
	      response.addCookie(cookie);
	      request.getSession().setAttribute(cookieName, cookieValue);
	}
	private void getAuthCode(HttpServletRequest request, HttpServletResponse response, String client_id, String state){
		//Client id + redirect url + scope + response type
	
			String redirectUrl = "https://accounts.google.com/o/oauth2/auth?response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&client_id="+client_id+"&state="+state+"&redirect_uri=https%3A%2F%2Fsosservices.appspot.com%2FOauth&output=embed";
			try {
				response.sendRedirect(redirectUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	private  Map<String,String> getAccesstokenFromRefreshToken(HttpServletRequest request, HttpServletResponse res, String refreshToken, String client_id) throws IOException{
		Map<String, String > map = new HashMap<String, String>();
		String loginVOStr = MangoDB.getDocumentWithQuery("sos-services", "registered-users", refreshToken, "refreshToken", false, null,null);
		Gson  json = new Gson();
		LoginVO loginVO = json.fromJson(loginVOStr, new TypeToken<LoginVO>() {}.getType());
		if (null != loginVO) {
			map.put("access_token", loginVO.getAccessToken());
		}
		return map;
	}
	
	private  Map<String,Object> getAccesstoken(HttpServletRequest request, HttpServletResponse res, String code, String client_id) throws IOException{
		log.info("Got auth code , now try to get access token  "+code);
		
		
	
		
		
		String urlParameters  = "grant_type=authorization_code&client_id="+client_id+"&client_secret="+client_secret+"&redirect_uri=https%3A%2F%2Fsosservices.appspot.com%2FOauth&code="+code+"&output=embed";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;
		
		
	    URL url = new URL("https://accounts.google.com/o/oauth2/token" );
	    
	    HTTPRequest req = new HTTPRequest(url, HTTPMethod.POST, lFetchOptions);
	    HTTPHeader contentType = new HTTPHeader("Content-type", "application/x-www-form-urlencoded");
	    HTTPHeader charset = new HTTPHeader("charset", "utf-8");
	    HTTPHeader contentLength = new HTTPHeader( "Content-Length", Integer.toString( postDataLength ));
	    req.setHeader(contentType);
	    req.setHeader(charset);
	    req.setHeader(contentLength);
	    req.setPayload(postData);
	    HTTPResponse resp= fetcher.fetch(req);
	    
	    
	    
	    
	        	
	  
	    
	    int respCode = resp.getResponseCode();
	    log.info("respCode "+respCode);
	    if (respCode == HttpURLConnection.HTTP_OK || respCode == HttpURLConnection.HTTP_NOT_FOUND ) {
	    	request.setAttribute("error", "");
	      String response = new String(resp.getContent());
	      

	     
	      log.info("Got access_token response  "+response);
	      Gson gson = new Gson(); 
	      String json = response;
	      Map<String,Object> map = new HashMap<String,Object>();
	      map = (Map<String,Object>) gson.fromJson(json, map.getClass());
	      
	      log.info("Extract access token "+map.get("access_token"));
	      
	     return map;//(String)map.get("access_token");
	    }
	     return null;
	}
	
	public Map<String, String> getUserEmailFromMangoD(String accessToken) throws IOException{
		String loginVOStr = MangoDB.getDocumentWithQuery("sos-services", "registered-users", accessToken, "accessToken", false, null,null);
		Gson  json = new Gson();
		LoginVO loginVO = json.fromJson(loginVOStr, new TypeToken<LoginVO>() {}.getType());
		Map<String, String> map = new HashMap<String, String>();
		if (null != loginVO) {
			map.put("emailID", loginVO.getEmailID());
			map.put("name", loginVO.getName());
			return map;
		}else {
			return null;
		}
		
		
	}

	public Map<String, String> getUserEmail(String accessToken) throws IOException{
		Map<String, String> userProfile = new HashMap<String, String>();
		log.info("Will get getUserEmail for access token "+accessToken);
		URL url = new URL("https://people.googleapis.com/v1/people/me?personFields=emailAddresses,names" );
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    conn.setRequestProperty("Authorization",  "Bearer "+accessToken);
	    
	    BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		Gson gson = new Gson(); 
		log.info("code:  "+conn.getResponseCode()+" response "+response.toString());
	      Map<String,Object> map = new HashMap<String,Object>();
	      map = (Map<String,Object>) gson.fromJson(response.toString(), map.getClass());
	      
	     Map emailMap = (Map) ((List<Object>)map.get("emailAddresses")).get(0);
	     Map nameMap = (Map) ((List<Object>)map.get("names")).get(0);
	     
		log.info("response  "+emailMap.get("value"));
		userProfile.put("email", (String)emailMap.get("value"));
		userProfile.put("name", (String)nameMap.get("displayName"));
		return userProfile;

	}
	 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	

}
