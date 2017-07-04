package com.roleandjoin.services;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.roleandjoin.gcs.ContactGCS;
import com.roleandjoin.gcs.UsuarioGCS;
import com.roleandjoin.gcs.entidad.Contact;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("contact")
public class ContactServices extends Services {

	ContactGCS gcs;

	@PostConstruct
	public void init() {
		gcs = new ContactGCS();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createContact(@HeaderParam("Authorization") String auth,
			Contact contact) {
		if (auth != null) {
			String userLogged = super.tokenIsValid(auth);
			if (userLogged != null) {
				contact.setUserName(userLogged);
				gcs.create(userLogged, contact);
				return Response.ok().build();
			}
		}
		return null;
	}

	@GET
	@Path("{page}")
	public Response getContacts(@HeaderParam("Authorization") String auth,
			@PathParam("page") int page) {
		if (auth != null) {
			String userLogged = super.tokenIsValid(auth);
			if (userLogged != null) {
				List<Contact> lstContact = gcs.getContacts(userLogged, page);
				Gson gson = new Gson();
				return Response.ok(gson.toJson(lstContact)).build();
			}
		}
		return null;
	}

	@DELETE
	@Path("{id}")
	public Response deleteContact(@HeaderParam("Authorization") String auth,
			@PathParam("id") long id) {
		if (auth != null) {
			String userLogged = super.tokenIsValid(auth);
			if (userLogged != null) {
				gcs.delete(userLogged, id);
			}
		}
		return Response.ok().build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("search/{contact}")
	public Contact serachContact(@HeaderParam("Authorization") String auth,
			@PathParam("contact") String contact) {
		if (auth != null) {
			String userLogged = super.tokenIsValid(auth);
			if (userLogged != null) {
				Contact contactObj = gcs.searchContact(userLogged, contact);
				if(contactObj==null){
					UsuarioGCS usuarioGCS = new UsuarioGCS();
					contact = ValidadorDeCadenas.addArrobaNombreUsuario(contact);
					Usuario user = usuarioGCS.getByUserName(contact);
					if(user!=null){
						contactObj = new Contact();
						contactObj.setContact(user.getUserName());
					}
				}else{
					contactObj.setId(1);
				}
				return contactObj;
			}
		}
		return null;
	}
}
