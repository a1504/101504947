package com.roleandjoin.services;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.roleandjoin.gcs.EmailTokenGCS;
import com.roleandjoin.services.vo.ServicesResp;

@Path("email")
public class Email extends Services {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{email}/{token}")
	public ServicesResp validateEmailToken(@PathParam("email") String email, @PathParam("token") String token){
		EmailTokenGCS gcs = new EmailTokenGCS();
		boolean resp = gcs.validateToken(email, token);
		ServicesResp servicesResp = new ServicesResp();
		servicesResp.setMsg(resp+"");
		return servicesResp;
	}
}
