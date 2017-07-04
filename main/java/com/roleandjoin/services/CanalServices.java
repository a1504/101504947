package com.roleandjoin.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.ChannelJoinGCS;
import com.roleandjoin.gcs.EmailTokenGCS;
import com.roleandjoin.gcs.InvitacionGCS;
import com.roleandjoin.gcs.PostGCS;
import com.roleandjoin.gcs.UserJoinGCS;
import com.roleandjoin.gcs.UsuarioGCS;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.ChannelCounts;
import com.roleandjoin.gcs.entidad.Invitacion;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.services.control.ChannelServicesControl;
import com.roleandjoin.services.vo.ServicesResp;
import com.roleandjoin.utils.Email;
import com.roleandjoin.utils.StringsUtils;
import com.roleandjoin.utils.TokenIdentifierGenerator;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("ch/{userName}")
public class CanalServices extends Services {

	// ch/:userName/:idCanal/:ispublic/:page

	/**
	 * Obtiene los canales de un usuario especifico. Filtrando los canales por
	 * publicos o privados
	 * 
	 * @param auth
	 * @param userNameOwner
	 * @param isPublic
	 * @param page
	 * @return
	 */
	@GET
	@Path("{ispublic}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam("Authorization") String auth,
			@PathParam("userName") String userNameOwner,
			@PathParam("ispublic") Boolean isPublic, @PathParam("page") int page) {
		List<Canal> lstCanales = new ArrayList<Canal>();
		String userLogged = super.tokenIsValid(auth);
		ChannelServicesControl chServicesControl = new ChannelServicesControl();
		if (super.userHasActiveAccount(userNameOwner)
				&& userLogged!=null && super.userHasActiveAccount(userLogged)) {
			CanalGCS canalGCS = new CanalGCS();
			userNameOwner = ValidadorDeCadenas
					.addArrobaNombreUsuario(userNameOwner);
			userNameOwner = userNameOwner.toLowerCase();
			if (userLogged != null && userLogged.equals(userNameOwner)) {
				lstCanales = canalGCS.getCanales(userNameOwner, page);
			} else if (isPublic) {
				lstCanales = chServicesControl.getChannels(userNameOwner, isPublic,
						false, userLogged);
			} else {
				userNameOwner = ValidadorDeCadenas
						.addArrobaNombreUsuario(userNameOwner);
				lstCanales = canalGCS.getChannelsByJoins(userLogged,
						userNameOwner, 1);
			}
		}else if (isPublic){
			lstCanales = chServicesControl.getChannels(userNameOwner, isPublic,
					false, userLogged);
		}
		Gson gson = new Gson();
		return gson.toJson(lstCanales != null ? lstCanales
				: new ArrayList<Canal>());
	}

	/**
	 * Obtiene el datalle de un canal especifico
	 * 
	 * @param auth
	 * @param userName
	 * @param idCanal
	 * @return
	 */
	@GET
	@Path("d/{idCanal}")
	@Produces(MediaType.APPLICATION_JSON)
	public Canal getCanalDetalle(@HeaderParam("Authorization") String auth,
			@PathParam("userName") String userName,
			@PathParam("idCanal") String idCanal) {

		Canal canal = null;

		String userLogged = tokenIsValid(auth);
		CanalGCS canalGCS = new CanalGCS();
		userName = ValidadorDeCadenas.addArrobaNombreUsuario(userName);
		boolean[] resp = canalGCS.getAccess(idCanal, userLogged, userName);

		if (!super.userHasActiveAccount(userName)
				&& !super.userHasActiveAccount(userLogged)) {
			return canal;
		}

		if (resp[0]) {
			canal = canalGCS.getCanalById(userName, idCanal);
			if (canal != null) {
				PostGCS gcs = new PostGCS();
				canal.setPostCount(gcs.getPostCount(canal.getIdCanal()));
				canal.setPostWithCount(gcs.getPostWithCount(canal.getIdCanal(),
						userLogged));
				canal.setCanPost(resp[1]);
				canal.setOwner(false);
				if (canal.getOwnerUser().equals(userLogged)) {
					canal.setOwner(true);
					InvitacionGCS invitacionGCS = new InvitacionGCS();
					canal.setInvitados(invitacionGCS.getInvitadosAsString(
							userLogged, canal.getIdCanal()));
				} else {
					UserJoinGCS joinGCS = new UserJoinGCS();
					Join join = joinGCS.getJoin(canal.getIdCanal(), userLogged);
					canal.setJoin(join != null ? true : false);
				}
			}
		}
		if (canal == null) {
			canal = new Canal();
			canal.setDelete(true);
		}
		return canal;
	}

	/**
	 * 
	 * 
	 * @param auth
	 * @param userNameOwner
	 * @param nameChannel
	 * @return
	 */
	@GET
	@Path("search/{channel}")
	@Produces(MediaType.APPLICATION_JSON)
	public String searchChannel(@HeaderParam("Authorization") String auth,
			@PathParam("userName") String userNameOwner,
			@PathParam("channel") String nameChannel) {
		if (auth != null && !auth.trim().equals("")) {
			String userLogged = tokenIsValid(auth);
			if (userLogged != null && super.userHasActiveAccount(userLogged)
					&& super.userHasActiveAccount(userNameOwner)) {
				CanalGCS canalGCS = new CanalGCS();
				Gson gson = new Gson();
				return gson.toJson(canalGCS.search(userNameOwner, userLogged,
						nameChannel));
			}
		}
		return null;
	}

	/**
	 * Permite crear un nuevo canal.
	 * 
	 * @param userName
	 * @param auth
	 * @param canal
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ServicesResp crear(@PathParam("userName") String userName,
			@HeaderParam("Authorization") String auth, Canal canal) {

		ServicesResp resp = new ServicesResp();
		resp.setId(TRANSACTION_NOT_OK);

		String userLogged = tokenIsValid(auth);
		if (userLogged != null && super.userHasActiveAccount(userLogged)) {
			CanalGCS canalGCS = new CanalGCS();
			Canal ch = canalGCS.getCanalByLowerCaseName(userLogged,
					canal.getNombre());
			if (ch != null) {
				resp.setId(COD_CHANNEL_EXISTS);
				return resp;
			}
			String idChannel = canalGCS.crear(userLogged, canal);
			if (!idChannel.equals("")) {
				resp.setId(TRANSACTION_OK);
				if (canal.getInvitados() != null
						&& !canal.getInvitados().trim().equals("")) {
					InvitacionGCS invitacionGCS = new InvitacionGCS();
					if (canal.getWhoPost() == CanalGCS.WHO_POST_INVITADOS) {
						invitacionGCS.saveInvitados(idChannel, userLogged,
								canal.getInvitados(), true);
					} else {
						invitacionGCS.saveInvitados(idChannel, userLogged,
								canal.getInvitados(), false);
					}
					UsuarioGCS gcs = new UsuarioGCS();
					Usuario usuario = gcs.getByUserName(userLogged);
					if (usuario != null) {
						String invitados = StringsUtils.removeWhiteSpaces(canal
								.getInvitados());
						String[] arregloInvitados = invitados.split(",");
						String[] arrEmails = getEmailInvited(arregloInvitados);
						String urlChannel = String
								.format("http://roleandjoin.appspot.com/#/email/inv/%s/%s",
										userLogged, idChannel);
						String urlProfile = "http://roleandjoin.appspot.com/"
								+ userLogged;
						String personalName = usuario.getNombre() + " "
								+ usuario.getApellido();
						String htmlContent = Email.getIvitationTemplate(
								personalName, urlProfile, canal.getNombre(),
								urlChannel);
						EmailTokenGCS emailTokenGCS = new EmailTokenGCS();
						String newToken = "";
						for (int i = 0; i < arrEmails.length; i++) {
							newToken = TokenIdentifierGenerator.nextSessionId();
							emailTokenGCS.saveToken(arrEmails[i], newToken,
									userLogged + "/" + idChannel);
							if (arrEmails[i].equals(""))
								continue;
							Email.sendEmail(Email.EMAIL_FROM, personalName,
									canal.getDescrip(), htmlContent,
									arrEmails[i]);
						}
					}
				}
			}
		}
		return resp;
	}

	/**
	 * 
	 * @param arrInvitados
	 * @return
	 */
	private String[] getEmailInvited(String... arrInvitados) {
		String[] emails = new String[arrInvitados.length];
		String email = "";
		UsuarioGCS gcs = new UsuarioGCS();
		ValidadorDeCadenas vdc = new ValidadorDeCadenas();
		for (int i = 0; i < arrInvitados.length; i++) {
			if (vdc.validarEmail(arrInvitados[i].trim())) {
				email = arrInvitados[i];
			} else {
				email = gcs.getEmailFromUser(arrInvitados[i]);
			}
			emails[i] = email;
			email = "";
		}
		return emails;
	}

	/**
	 * Permite eliminar un canal epecifico
	 * 
	 * @param userName
	 * @param idCanal
	 * @param auth
	 * @return
	 */
	@DELETE
	@Path("{idCh}")
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp delete(@PathParam("userName") String userName,
			@PathParam("idCh") String idCanal,
			@HeaderParam("Authorization") String auth) {
		String userN = tokenIsValid(auth);
		ServicesResp resp = new ServicesResp();
		resp.setId(TRANSACTION_NOT_OK);

		if (userN != null && super.userHasActiveAccount(userN)) {
			CanalGCS canalGCS = new CanalGCS();
			canalGCS.delete(userN, idCanal);
			resp.setId(TRANSACTION_OK);
			return resp;
		}
		return resp;
	}

	/**
	 * Permite editar un canal
	 * 
	 * @param userName
	 * @param idChannel
	 * @param canal
	 * @param auth
	 * @return
	 */
	@PUT
	@Path("{idCh}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp edit(@PathParam("userName") String userName,
			@PathParam("idCh") String idChannel, Canal canal,
			@HeaderParam("Authorization") String auth) {
		ServicesResp servicesResp = new ServicesResp();
		servicesResp.setId(TRANSACTION_NOT_OK);
		String userN = tokenIsValid(auth);
		if (userN != null && super.userHasActiveAccount(userN)) {
			CanalGCS canalGCS = new CanalGCS();
			long resp = canalGCS.edit(userN, canal);
			if (resp != 0) {
				servicesResp.setId(TRANSACTION_OK);
			}
		}
		return servicesResp;
	}

	/**
	 * Obtine las invitaciones a los canales a los cuales el usuario a sio
	 * invitado
	 * 
	 * @param userNameOwner
	 * @param auth
	 * @param page
	 * @return
	 */
	@GET
	@Path("get/inv/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvitations(@PathParam("userName") String userNameOwner,
			@HeaderParam("Authorization") String auth,
			@PathParam("page") int page) {
		if (auth == null || auth.trim().equals(""))
			return "";

		String userLogged = tokenIsValid(auth);
		Gson gson = new Gson();
		if (userLogged != null && super.userHasActiveAccount(userLogged)) {
			CanalGCS canalGCS = new CanalGCS();
			List<Canal> lstCanal = canalGCS.getInvitations(userLogged, page);
			return gson.toJson(lstCanal != null ? lstCanal
					: new ArrayList<Canal>());
		}
		return null;
	}

	/**
	 * Obtiene el numero de registros de canales creados
	 * 
	 * @param auth
	 * @param userNameOwner
	 * @return
	 */
	@GET
	@Path("counts")
	@Produces(MediaType.APPLICATION_JSON)
	public ChannelCounts getCount(@HeaderParam("Authorization") String auth,
			@PathParam("userName") String userNameOwner) {
		String userLogged = tokenIsValid(auth);
		ChannelCounts counts = new ChannelCounts();

		if (userLogged != null && super.userHasActiveAccount(userLogged)) {
			CanalGCS canalGCS = new CanalGCS();
			InvitacionGCS invitacionGCS = new InvitacionGCS();
			UserJoinGCS joinGCS = new UserJoinGCS();
			int countCh = canalGCS.getCountChannels(userLogged);
			int countInv = invitacionGCS.getCountInvitations(userLogged);
			int countJoins = joinGCS.getCountJoins(userLogged);
			counts = new ChannelCounts(countCh, countInv, countJoins);
		}
		return counts;
	}

	/**
	 * Permite cerrar un canal
	 * 
	 * @param userName
	 * @param idChannel
	 * @param auth
	 * @return
	 */
	@PUT
	@Path("close/{idCh}")
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp close(@PathParam("userName") String userName,
			@PathParam("idCh") String idChannel,
			@HeaderParam("Authorization") String auth) {
		String userN = tokenIsValid(auth);
		ServicesResp resp = new ServicesResp();
		resp.setId(TRANSACTION_NOT_OK);

		if (userN != null && super.userHasActiveAccount(userN)) {
			CanalGCS canalGCS = new CanalGCS();
			canalGCS.close(userN, idChannel);
			resp.setId(TRANSACTION_OK);
			return resp;
		}
		return resp;
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("bestch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBestCh() {
		CanalGCS canalGCS = new CanalGCS();
		List<Canal> lst = canalGCS.getChannelsWithMoreJoiners(true, 1);
		Gson gson = new Gson();
		return gson.toJson(lst);
	}

}
