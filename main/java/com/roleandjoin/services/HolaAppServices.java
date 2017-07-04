package com.roleandjoin.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.UsuarioGCS;

@Path("hello")
public class HolaAppServices {

//	@GET
//	@Path("crear/{userName}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String crear(@PathParam("userName") String userName) {
//		CanalGCS canalGCS = new CanalGCS();
//		return "Hola un saludo " + canalGCS.crear(userName);
//	}
	
//	@GET
//	@Path("{name}")
//	@Produces(MediaType.TEXT_HTML)
//	public Response saludo(@PathParam("name") String name) {
//		Logger logger = Logger.getLogger("com.hola");
//		logger.setLevel(Level.INFO);
//		
//		for (int i = 0; i < 100; i++) {
//			Logger.getLogger("com.hola").info("@@@@@INFO " + i);;
//		}
//		
//		java.net.URI location = null;
//		try {
//			location = new java.net.URI("../#/"+name);
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Logger.getLogger("com.hola").info("@@@@@INFO " + e.toString());
//		}
//	    return Response.temporaryRedirect(location).build();
//	}
//	
//	@GET
//	@Path("email/{e1}/{e2}")
//	@Produces(MediaType.TEXT_HTML)
//	public String log(@PathParam("e1") String e1, @PathParam("e2") String e2) {
//		Logger logger = Logger.getLogger("com.hola");
//		logger.setLevel(Level.INFO);
//		Email.sendEmail("Alejandro Lozano","@NewUser",Email.TYPE_MAIL_NOTIFY_INVITATION,e1,e2);
////		for (int i = 0; i < 100; i++) {
////			Logger.getLogger("com.hola").info("@@@@@INFO " + i);;
////		}
//		
//		return "log e2";
//	}
//	
//	@GET
//	@Path("email/{e1}")
//	@Produces(MediaType.TEXT_HTML)
//	public String log(@PathParam("e1") String e1) {
//		Logger logger = Logger.getLogger("com.hola");
//		logger.setLevel(Level.INFO);
//				
//		Email.sendEmail("Alejandro Lozano","@NewUser",Email.TYPE_MAIL_ACTIVATE_ACCOUNT,e1);
//		
////		for (int i = 0; i < 100; i++) {
////			Logger.getLogger("com.hola").info("@@@@@INFO " + i);;
////		}
//		
//		return "log e1";
//	}
//
//	
	@GET
	@Path("add/{add}/{user}/{idCh}")
	@Produces(MediaType.TEXT_HTML)
	public String add(@PathParam("user") String user, @PathParam("idCh") String idChannel,  @PathParam("add") Boolean add) {
		CanalGCS canalGCS = new CanalGCS();
		canalGCS.updateJoinersCount(user, idChannel, add);
		return "ok";
	}
//
//	@GET
//	@Path("bestch")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getBestCh() {
//		CanalGCS canalGCS = new CanalGCS();
//		List<Canal> lst = canalGCS.getChannelsWithMoreJoiners(true,1);
//		Gson gson = new Gson();
//		return gson.toJson(lst);
//	}
//	
	@GET
	@Path("activate/{userName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getBestCh(@PathParam("userName") String userName) {
		UsuarioGCS gcs = new UsuarioGCS();
		gcs.activateAccount(userName);
		return "ok";
	}
	
	@GET
	@Path("saludo")
	@Produces(MediaType.TEXT_PLAIN)
	public String saludo() {
		return "saludo!";
	}


}
