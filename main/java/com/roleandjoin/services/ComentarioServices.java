package com.roleandjoin.services;

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

import com.google.gson.Gson;
import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.ComentarioGCS;
import com.roleandjoin.gcs.entidad.Comentario;
import com.roleandjoin.services.vo.ServicesResp;

@Path("comentario")
public class ComentarioServices extends Services {

	ServicesResp resp;
	private static final int DELETE_OK = 10;

	@PostConstruct
	private void init() {
		resp = new ServicesResp();
	}

	@POST
	@Path("{id_post}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String comentar(@HeaderParam("Authorization") String auth,
			@PathParam("id_post") String id_post, Comentario comentario) {
		String userName = super.tokenIsValid(auth);
		if (userName != null && !comentario.getComentario().trim().equals("")) {
			comentario.setRemitente(userName);
			ComentarioGCS gcs = new ComentarioGCS();
			return "idComentario " + gcs.crear(id_post, comentario);
		}
		return "";
	}

	// @GET
	// @Path("{id_post}")
	// @Produces(MediaType.APPLICATION_JSON)
	// public String getComentarios(@HeaderParam("Authorization") String auth,
	// @PathParam("id_post") long id_post) {
	// ComentarioGCS gcs = new ComentarioGCS();
	// Gson gson = new Gson();
	// return gson.toJson(gcs.getComentarios(id_post));
	// }

	@GET
	@Path("{owner}/{id_channel}/{id_post}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getComentarios(@HeaderParam("Authorization") String auth,
			@PathParam("id_post") String id_post,
			@PathParam("id_channel") String id_channel,
			@PathParam("owner") String userNameOwner,@PathParam("page") int page) {
		if (auth == null)
			auth = "@@";
		String userLogged = tokenIsValid(auth);
		if (userLogged == null)
			userLogged = "@@";
		CanalGCS canalGCS = new CanalGCS();
		boolean[] bln_access = canalGCS.getAccess(id_channel, userLogged,
				userNameOwner);
		if (bln_access[0]/*&& userHasActiveAccount(userLogged)*/ && userHasActiveAccount(userNameOwner)) {
			ComentarioGCS gcs = new ComentarioGCS();
			Gson gson = new Gson();
			return gson.toJson(gcs.getComentarios(id_post,userLogged,page));
		}
		return "";
	}

	@DELETE
	@Path("{id_post}/{id_comment}")
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp delete(@HeaderParam("Authorization") String auth,
			@PathParam("id_post") String id_post,
			@PathParam("id_comment") String id_comment) {

		String userLogged = super.tokenIsValid(auth);
		if (userLogged != null && userHasActiveAccount(userLogged)) {
			ComentarioGCS comentarioGCS = new ComentarioGCS();
			Comentario c = comentarioGCS.get(id_post, id_comment);
			if (c != null && c.getRemitente().equals(userLogged)) {
				comentarioGCS.delete(id_post, id_comment);
			}
			this.resp.setId(DELETE_OK);
			return resp;
		} else {
			this.resp.setId(super.USER_NOT_LOGGED);
		}

		return resp;
	}
	
	@GET
	@Path("count/{owner}/{id_channel}/{id_post}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCommentCount(@HeaderParam("Authorization") String auth,
			@PathParam("id_post") String id_post,
			@PathParam("id_channel") String id_channel,
			@PathParam("owner") String userNameOwner) {
		ServicesResp servicesResp = new ServicesResp();
		servicesResp.setId(0);
		servicesResp.setMsg("go");
		if (auth == null)
			auth = "@@";
		String userLogged = tokenIsValid(auth);
		if (userLogged == null)
			userLogged = "@@";
		CanalGCS canalGCS = new CanalGCS();
		boolean[] bln_access = canalGCS.getAccess(id_channel, userLogged,
				userNameOwner);
		if (bln_access[0]&& userHasActiveAccount(userLogged) && userHasActiveAccount(userNameOwner)) {
			ComentarioGCS gcs = new ComentarioGCS();
			servicesResp.setId(gcs.getCommentsCount(id_post));
			
		}
		Gson gson = new Gson();
		return  gson.toJson(servicesResp);
	}

}
