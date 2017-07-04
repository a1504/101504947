package com.roleandjoin.services.user;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.roleandjoin.gcs.PostGCS;
import com.roleandjoin.services.vo.ServicesResp;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("test")
public class Test {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp get() {
		ServicesResp servicesResp = new ServicesResp();
		servicesResp.setMsg((new Date()).toString());
		return servicesResp;
	}
	
}
