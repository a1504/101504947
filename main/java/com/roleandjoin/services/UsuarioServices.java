package com.roleandjoin.services;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.gson.Gson;
import com.roleandjoin.gcs.UsuarioGCS;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.services.control.UserServicesControl;
import com.roleandjoin.services.vo.ServicesResp;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("usu")
public class UsuarioServices extends Services {

	ValidadorDeCadenas validadorCadenas;
	ServicesResp servicesResp;
	Gson gson;

	@PostConstruct
	public void ini() {
		validadorCadenas = new ValidadorDeCadenas();
		servicesResp = new ServicesResp();
		gson = new Gson();
	}

	/**
	 * @param usuario
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp crear(Usuario usuario) {

		usuario.setUserName(usuario.getUserName().toLowerCase());
		usuario.setUserName(ValidadorDeCadenas.addArrobaNombreUsuario(usuario
				.getUserName()));

		if (!validadorCadenas.validarNombreUsuario(usuario.getUserName())) {
			servicesResp.setId(COD_USERNAME_NOT_VALID);
			return servicesResp;
		}

		if (!validadorCadenas.validarEmail(usuario.getEmail())) {
			servicesResp.setId(COD_EMAIL_NOT_VALID);
			return servicesResp;
		}
		UsuarioGCS gcs = new UsuarioGCS();

		if (gcs.userExists(usuario.getUserName())) {
			servicesResp.setId(COD_USERNAME_EXISTS);
			return servicesResp;
		}

		if (gcs.emailExists(usuario.getEmail())) {
			servicesResp.setId(COD_EMAIL_EXISTS);
			return servicesResp;
		}

		servicesResp.setId(TRANSACTION_OK);
//		servicesResp.setMsg(gcs.crear(usuario) + "");
		servicesResp.setMsg(gcs.createAndActivatedAccount(usuario) + "");
		Logger.getLogger("").warning("@@@@@new user " + gson.toJson(usuario));
		return servicesResp;
	}

	@GET
	@Path("{token}")
	public void emailValidated(@PathParam("token") String token) {
		UsuarioGCS usuarioGCS = new UsuarioGCS();
		usuarioGCS.updateEmailValidated(token);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp updateUser(@HeaderParam("Authorization") String auth,
			Usuario newUser) {
		ServicesResp servicesResp = new ServicesResp();
		servicesResp.setId(TRANSACTION_NOT_OK);
		if (valideAccess(auth)) {
			String userName = super.tokenIsValid(auth);
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			Usuario userResp = usuarioGCS.getByUserName(userName);
			String email = usuarioGCS.getEmailFromUser(userName);
			// So, do you want change the Email?, ok let's make some validations
			if (!newUser.getEmail().equals(email)) {
				if (newUser.getEmail() == null
						|| newUser.getEmail().trim().equals("")
						|| !validadorCadenas.validarEmail(newUser.getEmail())) {
					servicesResp.setId(COD_EMAIL_NOT_VALID);
					return servicesResp;
				} else if (usuarioGCS.emailExists(newUser.getEmail())) {
					servicesResp.setId(COD_EMAIL_EXISTS);
					return servicesResp;
				}
			} else {
				newUser.setEmail(email);
			}

			String userRespPwd = userResp.getPwd();
			String newUserPwd = newUser.getPwd();
			String newUserNewPwd = newUser.getNewPwd();

			if (newUserPwd != null && !newUserPwd.trim().equals("")
					&& newUserNewPwd != null
					&& !newUserNewPwd.trim().equals("")) {
				if (!userRespPwd.equals(newUserPwd)) {
					servicesResp.setId(COD_PASSWORDS_NO_MATCH);
					return servicesResp;
				} else {
					newUser.setPwd(newUser.getNewPwd());
				}
			} else {
				newUser.setPwd(userResp.getPwd());
			}
			UserServicesControl control = new UserServicesControl();
			boolean resp = control.updateUser(newUser);
			if (resp)
				servicesResp.setId(TRANSACTION_OK);
		}
		return servicesResp;
	}


	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp deleteUserAccount(
			@HeaderParam("Authorization") String auth) {
		ServicesResp servicesResp = new ServicesResp();
		servicesResp.setId(TRANSACTION_NOT_OK);
		if (valideAccess(auth)) {
			String user = super.tokenIsValid(auth);
			UserServicesControl control = new UserServicesControl();
			boolean resp = control.deleteUserAccount(user);
			if (resp)
				servicesResp.setId(TRANSACTION_OK);
		}
		return servicesResp;
	}

	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON)
	public Usuario getUserInfo(@HeaderParam("Authorization") String auth) {
		if (valideAccess(auth)) {
			String userLogged = super.tokenIsValid(auth);
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			Usuario usuario = usuarioGCS.getByUserName(userLogged);
			if (usuario != null) {
				usuario.setEmail(usuarioGCS.getEmailFromUser(userLogged));
				usuario.setPwd(null);
			}
			return usuario;
		}
		return new Usuario();
	}

}
