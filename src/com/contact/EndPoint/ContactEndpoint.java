package com.contact.EndPoint;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.contact.vo.Contact;

@Path("/contact")
public interface ContactEndpoint {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addContact( Contact contact, @Context HttpServletRequest request);
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getContacts( @Context HttpServletRequest request );
	
	@DELETE
	@Path("/entry/{entry}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteContact(@PathParam("entry") String entry, @Context HttpServletRequest request );
	
	@GET
	@Path("/checkBalance")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response checkBalance( @Context HttpServletRequest request );
	

}
