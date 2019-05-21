package com.login.EndPoint;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.login.facade.LoginFacade;
import com.login.vo.LoginVO;


public class LoginEndpointImpl implements LoginEndpoint {
	private LoginFacade loginFacade;
	public LoginFacade getLoginFacade() {
		return loginFacade;
	}

	public void setLoginFacade(LoginFacade loginFacade) {
		this.loginFacade = loginFacade;
	}

	
	

	@Override
	public Response validateRegID(String regID, HttpServletRequest request, String appTimeZone) {
		try{
			
			
           if (null != appTimeZone) {
        	   appTimeZone = appTimeZone.replace("@", "/");
           }
			
			
			LoginVO loginVO = loginFacade.validateRegIDAndUpdateSettings(regID,  appTimeZone);
			if (null != loginVO) {
				
				return Response.ok().entity(loginVO).build();
			}else {
				LoginVO vo = new LoginVO();
				vo.setErrorMessage("Please log in to authenticate ");
				return Response.status(Response.Status.UNAUTHORIZED).entity(vo).build();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LoginVO vo = new LoginVO();
			vo.setErrorMessage("Internal Server Error ");
			
			return Response.serverError().entity(vo).build();
		}
	}

	
	

}
