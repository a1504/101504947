package com.roleandjoin.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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
import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.UserJoinGCS;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.services.vo.ServicesResp;

@Path("join")
public class UserJoinServices extends Services {

	UserJoinGCS gcs;
	ServicesResp resp;

	@PostConstruct
	public void init() {
		gcs = new UserJoinGCS();
		resp = new ServicesResp();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response join(@HeaderParam("Authorization") String auth, Join join) {
		Logger.getLogger("").warning("@@@@@"+UsuarioServices.class.getCanonicalName()+" " + join.getIdChannel());
		resp.setId(TRANSACTION_NOT_OK);
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				join.setUserName(userLogger);
				String idJoin = gcs.join(join);
				return Response.ok(getResponseStr(idJoin)).build();
			}
		}
		return Response.ok(resp).build();
	}

	@GET
	@Path("get/{page}")
	public Response getJoins(@HeaderParam("Authorization") String auth,
			@PathParam("page") int page) {
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				List<Join> lstJoin = gcs.getJoins(userLogger, page);
				List<Canal> lstCanal = new ArrayList<Canal>();
				CanalGCS canalGCS = new CanalGCS();
				Canal ch = null;
				for (Join join : lstJoin) {
					ch = canalGCS.getCanalById(join.getOwnerChannel(),
							join.getIdChannel());
					if (ch != null) {
						lstCanal.add(ch);
					}
				}
				Gson gson = new Gson();
				return Response.ok(gson.toJson(lstCanal)).build();
			}
		}
		return null;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public ServicesResp unjoin(@HeaderParam("Authorization") String auth,
			@PathParam("id") String idChannel) {
		ServicesResp resp = new ServicesResp();
		resp.setId(TRANSACTION_NOT_OK);
		if (auth != null) {
			String userLogger = super.tokenIsValid(auth);
			if (userLogger != null) {
				gcs.delete(userLogger, idChannel);
				resp.setId(TRANSACTION_OK);
			}
		}
		return resp;
	}
}
