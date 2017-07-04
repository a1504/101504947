package com.roleandjoin.services.user;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.PostGCS;
import com.roleandjoin.gcs.TestGCS;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.services.UsuarioServices;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("")
public class UserServices {
	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_HTML)
	public Response get(@PathParam("name") String name) {
		Logger.getLogger("").warning("@@@@@"+UsuarioServices.class.getCanonicalName());
		name = ValidadorDeCadenas.addArrobaNombreUsuario(name);
		java.net.URI location = null;
		try {
			location = new java.net.URI("../#/" + name);
		} catch (URISyntaxException e) {
			Logger.getLogger("com.hola").info("@@@@@INFO " + e.toString());
		}
		return Response.temporaryRedirect(location).build();
	}

	@GET
	@Path("{name}/{ch}")
	@Produces(MediaType.TEXT_HTML)
	public Response getChannel(@PathParam("name") String name,
			@PathParam("ch") String channel) {
		name = ValidadorDeCadenas.addArrobaNombreUsuario(name);
		channel = ValidadorDeCadenas.addNumeralCanal(channel);
		CanalGCS canalGCS = new CanalGCS();
		Canal canal = canalGCS.getCanalByLowerCaseName(name, channel);
		String idCanal = "";
		if (canal != null) {
			idCanal = canal.getIdCanal();
		}
		name = ValidadorDeCadenas.addArrobaNombreUsuario(name);
		java.net.URI location = null;
		try {
			location = new java.net.URI("../#/" + name + "/" + idCanal);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.getLogger("com.hola").info("@@@@@INFO " + e.toString());
		}
		return Response.temporaryRedirect(location).build();
	}
//
//	@GET
//	@Path("post/{idpost}")
//	@Produces(MediaType.TEXT_HTML)
//	public void getPost(@PathParam("idpost") long idPost) {
//		TestGCS gcs = new TestGCS();
//		Canal ch = new Canal();
//		ch.setNombre("Canal");
//		long id = gcs.crear(ch);
//		long idP = gcs.crear(id);
//		gcs.get(idP);
//		System.out.println();
//	}
}
