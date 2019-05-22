package com.contact.EndPoint;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.contact.facade.ContactFacade;
import com.contact.vo.Contact;
import com.login.vo.LoginVO;

public class ContactEndpointImpl implements ContactEndpoint{
	private ContactFacade contactFacade;

	public ContactFacade getContactFacade() {
		return contactFacade;
	}

	public void setContactFacade(ContactFacade contactFacade) {
		this.contactFacade = contactFacade;
	}

	@Override
	public Response addContact(Contact contact,HttpServletRequest request) {
		try{
			String regID = request.getHeader("Authorization");
			System.out.println(" regID from header "+regID );
			return Response.ok().entity(contactFacade.addContact(contact,regID)).build();
		}catch(Exception e){
			e.printStackTrace();
			LoginVO vo = new LoginVO();
			vo.setErrorMessage("Internal Server Error ");
			
			return Response.serverError().entity(vo).build();
		}
	}

	@Override
	public Response getContacts(HttpServletRequest request) {
		try{
			String regID = request.getHeader("Authorization");
			System.out.println(" regID from header "+regID );
			return Response.ok().entity(contactFacade.getContacts(regID)).build();
		}catch(Exception e){
			e.printStackTrace();
			LoginVO vo = new LoginVO();
			vo.setErrorMessage("Internal Server Error ");
			
			return Response.serverError().entity(vo).build();
		}
	}

	@Override
	public Response deleteContact(String entry, HttpServletRequest request) {
		try{
			String regID = request.getHeader("Authorization");
			System.out.println(" regID from header "+regID );
			return Response.ok().entity(contactFacade.deleteContact(regID, entry, null)).build();
		}catch(Exception e){
			e.printStackTrace();
			LoginVO vo = new LoginVO();
			vo.setErrorMessage("Internal Server Error ");
			
			return Response.serverError().entity(vo).build();
		}
	}

	@Override
	public Response checkBalance(HttpServletRequest request) {
		try{
			String regID = request.getHeader("Authorization");
			System.out.println(" regID from header "+regID );
			 
			return Response.ok().entity(contactFacade.checkBalance(regID)).build();
		}catch(Exception e){
			e.printStackTrace();
			LoginVO vo = new LoginVO();
			vo.setErrorMessage("Internal Server Error ");
			
			return Response.serverError().entity(vo).build();
		}
	}

}
