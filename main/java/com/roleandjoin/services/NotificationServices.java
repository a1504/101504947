package com.roleandjoin.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.ChannelJoinGCS;
import com.roleandjoin.gcs.FileAccessGCS;
import com.roleandjoin.gcs.FileGCS;
import com.roleandjoin.gcs.PostGCS;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.File;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.gcs.entidad.Post;
import com.roleandjoin.services.vo.ServicesResp;

@Path("notify")
public class NotificationServices extends Services {

	@PostConstruct
	public void init() {
	}

	@GET
	@Path("{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getChannels(@HeaderParam("Authorization") String auth,
			@PathParam("page") int page) {
		ServicesResp servicesResp = new ServicesResp();
		Gson gson = new Gson();
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				CanalGCS canalGCS = new CanalGCS();
				List<Canal> lstChannels = canalGCS.getChannelsByLastPost(
						userLogger, page);
				if (lstChannels == null) {
					lstChannels = new ArrayList<Canal>();
				}
				String json = gson.toJson(lstChannels);
				return json;
			} else {
				servicesResp.setId(USER_NOT_LOGGED);
				return gson.toJson(servicesResp);
			}
		}
		return "";
	}

	@GET
	@Path("joins/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getJoins(@HeaderParam("Authorization") String auth,
			@PathParam("page") int page) {
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				ChannelJoinGCS chjoinGCS = new ChannelJoinGCS();
				List<Join> lstJoins = chjoinGCS.getJoins(userLogger, page);
				if (lstJoins != null && !lstJoins.isEmpty()) {
					List<Canal> lstChannels = null;
					lstChannels = new ArrayList<Canal>();
					CanalGCS canalGCS = new CanalGCS();
					Canal ch;
					PostGCS postGCS = new PostGCS();
					Post post = null;
					for (Join join : lstJoins) {
						ch = canalGCS.getCanalById(join.getOwnerChannel(),
								join.getIdChannel());
						if (ch != null) {
							post = postGCS.getLastPost(ch.getIdCanal(),
									ch.isPublic());
							if (post != null) {
								ch.setLastPost(post);
								lstChannels.add(ch);
							}
						} else {
							ch = new Canal();
							ch.setNombre(join.getChannelName());
							ch.setIdCanal(join.getIdChannel());
							ch.setOwnerUser(join.getOwnerChannel());
							ch.setDelete(true);
							ch.setLastPost(new Post());
							lstChannels.add(ch);
						}
					}
					Gson gson = new Gson();
					String json = gson.toJson(lstChannels != null ? lstChannels
							: new ArrayList<Canal>());
					return json;
				}
			}
		}
		return null;
	}

	@GET
	@Path("count/{joins}")
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp getChannelsCount(
			@HeaderParam("Authorization") String auth,
			@PathParam("joins") Boolean joins) {
		ServicesResp resp = new ServicesResp();
		resp.setId(0);
		int count = 0;
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				if (joins) {
					ChannelJoinGCS chjoinGCS = new ChannelJoinGCS();
					count = chjoinGCS.getCountJoins(userLogger);
				} else {
					CanalGCS canalGCS = new CanalGCS();
					count = canalGCS.getCountChannelsByLastPost(userLogger);
				}
			}
		}
		resp.setId(count);
		return resp;
	}

	@GET
	@Path("joins/{idChannel}/{idLastPost}/{joins}")
	@Produces(MediaType.APPLICATION_JSON)
	public Canal existsNewPost(@HeaderParam("Authorization") String auth,
			@PathParam("idChannel") String idChannel,
			@PathParam("idLastPost") String idLastPost,
			@PathParam("joins") Boolean joins) {

		Canal canal = null;
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				if (joins) {
					canal = existsNewPostOnJoins(userLogger, idChannel,
							idLastPost);
				} else {
					canal = existsNewPostOnChannels(userLogger, idChannel,
							idLastPost);
				}
			}
		}
		canal = canal == null ? new Canal() : canal;
		return canal;
	}

	public Canal existsNewPostOnChannels(String userLogger, String idChannel,
			String idLastPost) {
		CanalGCS canalGCS = new CanalGCS();
		List<Canal> lstChannels = canalGCS.getChannelsByLastPost(userLogger, 1);
		if (lstChannels != null && !lstChannels.isEmpty()) {
			Canal ch = lstChannels.get(0);
			String sender = ch.getLastPost().getRemitente();
			if (!ch.getLastPost().getIdPost().equals(idLastPost)
					&& !sender.equals(userLogger)) {
				return ch;
			}
		}
		return null;
	}

	public Canal existsNewPostOnJoins(String userLogger, String idChannel,
			String idLastPost) {
		ChannelJoinGCS chjoinGCS = new ChannelJoinGCS();
		List<Join> lstJoins = chjoinGCS.getJoins(userLogger, 1);
		if (lstJoins != null && !lstJoins.isEmpty()) {
			Join joinFirst = lstJoins.get(0);
			// if (joinFirst.getIdChannel() != idChannel) {

			CanalGCS canalGCS = new CanalGCS();
			PostGCS postGCS = new PostGCS();
			Canal ch = null;
			Post post = null;
			ch = canalGCS.getCanalById(joinFirst.getOwnerChannel(),
					joinFirst.getIdChannel());
			if (ch != null) {
				post = postGCS.getLastPost(ch.getIdCanal(), ch.isPublic());
				if (post != null) {
					if (!post.getIdPost().equals(idLastPost)) {
						ch.setLastPost(post);
						return ch;
					}
				}
			}
			return null;
		}

		// }
		return null;
	}

	public boolean existsNewPostOnJoins2(String userLogger, String idChannel,
			String idLastPost) {
		ChannelJoinGCS chjoinGCS = new ChannelJoinGCS();
		List<Join> lstJoins = chjoinGCS.getJoins(userLogger, 1);
		if (lstJoins != null && !lstJoins.isEmpty()) {
			Join joinFirst = lstJoins.get(0);
			if (!joinFirst.getIdChannel().equals(idChannel)) {
				return true;
			} else {
				CanalGCS canalGCS = new CanalGCS();
				PostGCS postGCS = new PostGCS();
				Canal ch = null;
				Post post = null;
				ch = canalGCS.getCanalById(joinFirst.getOwnerChannel(),
						joinFirst.getIdChannel());
				if (ch != null) {
					post = postGCS.getLastPost(ch.getIdCanal(), ch.isPublic());
					if (post != null) {
						if (!post.getIdPost().equals(idLastPost)) {
							return true;
						}
					}
				}
				return false;
			}

		}
		return false;
	}
}
