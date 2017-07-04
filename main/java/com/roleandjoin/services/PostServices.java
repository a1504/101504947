package com.roleandjoin.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.ChannelStatisticGCS;
import com.roleandjoin.gcs.PostGCS;
import com.roleandjoin.gcs.PostStatisticGCS;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.Post;
import com.roleandjoin.services.control.PostServicesControl;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("post/{idCanal}")
public class PostServices extends Services {

	@POST
	@Path("{owner}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String crear(@HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel,
			@PathParam("owner") String owner, Post post) {
		Gson gson = new Gson();
		String jsonLogger = gson.toJson(post);
		Logger.getLogger(PostServices.class.getName()).warning("@@@@@crear Post " + jsonLogger + " idChannel " +idChannel);
		String userName = super.tokenIsValid(auth);
		if (userName != null && post != null && userHasActiveAccount(userName) && userHasActiveAccount(owner)) {
			CanalGCS canalGCS = new CanalGCS();
			boolean[] resp = canalGCS.getAccess(idChannel, userName, owner);
			if (resp[0] && resp[1]) {
				post.setRemitente(userName);
				post.setDate(new Date());
				PostGCS gcs = new PostGCS();
				return gcs.crear(idChannel, post, owner) + "";
			}
		}
		return "";
	}
	
	@POST
	@Path("{owner}/{slack}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String crear(@HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel,
			@PathParam("owner") String owner, Post post, @PathParam("slack") String slack) {
		Gson gson = new Gson();
		CanalGCS canalGCS = new CanalGCS();
		PostServicesControl posControl = new PostServicesControl();
		String[] userOwnerAndCh = posControl.decodeSlack(slack);
		Canal ch = canalGCS.getCanalByName(userOwnerAndCh[0], userOwnerAndCh[1]);
		if(ch!=null){
			idChannel = ch.getIdCanal();
			owner = ch.getOwnerUser();
		}
		
		String jsonLogger = gson.toJson(post);
		Logger.getLogger(PostServices.class.getName()).warning("@@@@@crear Post " + jsonLogger + " idChannel " +idChannel);
		String userName = super.tokenIsValid(auth);
		if (userName != null && post != null && userHasActiveAccount(userName) && userHasActiveAccount(owner)) {
			
			boolean[] resp = canalGCS.getAccess(idChannel, userName, owner);
			if (resp[0] && resp[1]) {
				post.setRemitente(userName);
				post.setDate(new Date());
				PostGCS gcs = new PostGCS();
				return gcs.crear(idChannel, post, owner) + "";
			}
		}
		return "";
	}

	@GET
	@Path("{owner}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPosts(@HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel,
			@PathParam("owner") String owner, @PathParam("page") int page) {
		String userName = super.tokenIsValid(auth);
		CanalGCS canalGCS = new CanalGCS();
		boolean[] resp = canalGCS.getAccess(idChannel, userName, owner);
		if (resp[0] && !resp[2]/*&& userHasActiveAccount(userName)*/ && userHasActiveAccount(owner)) {
			PostGCS gcs = new PostGCS();
			Gson gson = new Gson();
			return gson.toJson(gcs.getPosts(idChannel, owner, userName, page));
		}
		return "";
	}
	
	

	@GET
	@Path("{owner}/{page}/{haswith}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPosts(@Context HttpServletRequest request, @HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel,
			@PathParam("owner") String owner, @PathParam("page") int page,
			@PathParam("haswith") Boolean hasWith) {
		String userLogged = super.tokenIsValid(auth);
		CanalGCS canalGCS = new CanalGCS();
		owner = ValidadorDeCadenas.addArrobaNombreUsuario(owner);
		boolean[] resp = canalGCS.getAccess(idChannel, userLogged, owner);
		if (resp[0] && !resp[2]/*&& userHasActiveAccount(userLogged)*/ && userHasActiveAccount(owner)) {
			PostGCS gcs = new PostGCS();
			Gson gson = new Gson();
			List<Post> lstPost = new ArrayList<Post>();
			if (!hasWith) {
				lstPost = gcs.getPosts(idChannel, owner, userLogged,
						page);
			} else if(userLogged!=null){
				lstPost = gcs.getPostWith(idChannel, userLogged, owner,page);
			}
			String ipAddress = request.getRemoteAddr();
			ChannelStatisticGCS channelStatisticGCS = new ChannelStatisticGCS();
			channelStatisticGCS.createPostStatistic(owner, idChannel, ipAddress);
			return gson.toJson(lstPost);
		}
		return "";
	}

	@GET
	@Path("detail/{owner}/{idPost}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPost(@HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel,
			@PathParam("idPost") String idPost, @PathParam("owner") String owner, @Context HttpServletRequest request) {
		String userName = "@";
		if (auth != null)
			userName = super.tokenIsValid(auth);

		CanalGCS canalGCS = new CanalGCS();
		boolean[] resp = canalGCS.getAccess(idChannel, userName, owner);
		if (resp[0]/*&& userHasActiveAccount(userName)*/ && userHasActiveAccount(owner)) {
			PostGCS postGCS = new PostGCS();
			Gson gson = new Gson();
			Post post = postGCS.getPostByIdPost(idPost, idChannel, owner);
			if(post!=null){
				PostStatisticGCS statisticGCS = new PostStatisticGCS();
				String ipAddress = request.getRemoteAddr();
				statisticGCS.createPostStatistic(owner,idChannel, idPost, ipAddress);
			}
			return gson.toJson(post);
		}
		return null;
	}

	@GET
	@Path("sh/{owner}/{idPost}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getShared(@HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel,
			@PathParam("idPost") String idPost, @PathParam("owner") String owner) {
		String userName = "@";
		if (auth != null)
			userName = super.tokenIsValid(auth);

		CanalGCS canalGCS = new CanalGCS();
		boolean[] resp = canalGCS.getAccess(idChannel, userName, owner);
		if (resp[0]&& userHasActiveAccount(userName) && userHasActiveAccount(owner)) {
			Canal canal = canalGCS.getCanalById(owner, idChannel);
			PostGCS postGCS = new PostGCS();
			Post post = postGCS.getPostByIdPost(idPost, idChannel, null);
			if (canal != null && post != null) {
				String title = canal.getNombre() + " by "
						+ canal.getOwnerUser();
				String content = post.getMsg();
				String img = "";
				if (post.getBlobs() != null) {
					img = this.URL_HOST + "file/" + post.getBlobs()[0];
				}
				String url = createUrlViewPost(idChannel, idPost, owner);
				String fbContent = createFacebookContent(title, content, img);
				return Response.ok(fbContent).build();
			}
		}
		return null;
	}

	@DELETE
	@Path("{idPost}")
	@Produces(MediaType.APPLICATION_JSON)
	public Post delete(@HeaderParam("Authorization") String auth,
			@PathParam("idCanal") String idChannel, @PathParam("idPost") String idPost) {
		String userName = super.tokenIsValid(auth);
		PostGCS gcs = new PostGCS();
		Post p = gcs.getPostByIdPost(idPost, idChannel, null);
		if (p != null&& userHasActiveAccount(userName)) {
			if (p.getRemitente().equals(userName)) {
				gcs.delete(idPost, idChannel);
			} else {
				CanalGCS canalGCS = new CanalGCS();
				Canal c = canalGCS.getCanalById(userName, idChannel);
				if (c != null) {
					if (c.isOwner()) {
						gcs.delete(idPost, idChannel);
					}
				}
			}
		}
		return null;
	}

//	@Deprecated
//	private String createUrlViewPost(long idCanal, long idPost, String owner) {
//		String post_url = String.format("post/%s/detail/%s/%s", idCanal, owner,
//				idPost);
//		String urlViewPost = this.URL_HOST + post_url;
//		return urlViewPost;
//	}
	
	private String createUrlViewPost(String idChannel, String idPost, String owner) {
		String post_url = String.format("post/%s/detail/%s/%s", idChannel, owner,
				idPost);
		String urlViewPost = this.URL_HOST + post_url;
		return urlViewPost;
	}

	private String createFacebookContent(String title, String content,
			String img) {

		String html = String
				.format("<html><head><meta charset=\"ISO-8859-1\"><title>%s</title><meta property=\"fb:app_id\" content=\"1465076340422379\" />"
						+ "<meta property=\"og:title\" content=\"%s\" />"
						+ "<meta property=\"og:description\"	content=\"Workday,\" />"
						+ "<meta property=\"og:image\" content=\"%s\" /> "
						+ "<meta property=\"og:url\"	content=\"http://www.google.com\" />"
						+ "</head><body >View</body></html>", this.APP_NAME,
						title, content, img);
		return html;
	}
}
