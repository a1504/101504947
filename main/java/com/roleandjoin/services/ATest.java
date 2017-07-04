package com.roleandjoin.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hola")
public class ATest {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String saludo(){
		return "Hola Mundo!!!";
	}
	
}
