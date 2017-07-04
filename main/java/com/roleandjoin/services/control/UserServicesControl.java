package com.roleandjoin.services.control;

import com.roleandjoin.gcs.UsuarioGCS;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.services.ServiceCodes;
import com.roleandjoin.services.vo.ServicesResp;
import com.roleandjoin.utils.ValidadorDeCadenas;

public class UserServicesControl extends AbstractControl {

	public boolean validatePwd(Usuario user) {
		if (user != null) {
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			Usuario lastUser = usuarioGCS.getUsuario(user.getUserName(),
					user.getLastPwd());
			if (lastUser.getPwd().equals(user.getLastPwd())) {
				return true;
			}
		}
		return false;
	}

	public boolean updateUser(Usuario user) {
		if (user != null) {
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			usuarioGCS.updateUser(user.getUserName(), user);
			return true;
		}
		return false;
	}

	public boolean deleteUserAccount(String userLogged) {
		UsuarioGCS gcs = new UsuarioGCS();
		return gcs.deleteUser(userLogged);
	}

	public boolean userHasActivateAccount(String ownerChannelsUser) {
		return super.userHasActivateAccount(ownerChannelsUser);
	}

	public ServicesResp validate(Usuario usuario, boolean forCreate) {
		ServicesResp servicesResp = validateUserData(usuario);
		if (servicesResp.getId() == 0) {
			servicesResp = emailExists(usuario);
			if (servicesResp.getId() == 0 && !forCreate) {
				servicesResp = userExits(usuario);
			}
		}
		return servicesResp;
	}

	public ServicesResp validateUserData(Usuario usuario) {

		ValidadorDeCadenas validadorCadenas = new ValidadorDeCadenas();
		ServicesResp servicesResp = new ServicesResp();
		usuario.setUserName(usuario.getUserName().toLowerCase());
		usuario.setUserName(ValidadorDeCadenas.addArrobaNombreUsuario(usuario
				.getUserName()));

		if (!validadorCadenas.validarNombreUsuario(usuario.getUserName())) {
			servicesResp.setId(ServiceCodes.COD_USERNAME_NOT_VALID);
			return servicesResp;
		}

		if (!validadorCadenas.validarEmail(usuario.getEmail())) {
			servicesResp.setId(ServiceCodes.COD_EMAIL_NOT_VALID);
			return servicesResp;
		}
		return servicesResp;
	}

	public ServicesResp emailExists(Usuario usuario) {
		ServicesResp servicesResp = new ServicesResp();
		UsuarioGCS gcs = new UsuarioGCS();
		if (gcs.emailExists(usuario.getEmail())) {
			servicesResp.setId(ServiceCodes.COD_EMAIL_EXISTS);
			return servicesResp;
		}
		return servicesResp;
	}

	public ServicesResp userExits(Usuario usuario) {
		ServicesResp servicesResp = new ServicesResp();
		UsuarioGCS gcs = new UsuarioGCS();
		if (gcs.userExists(usuario.getUserName())) {
			servicesResp.setId(ServiceCodes.COD_USERNAME_EXISTS);
			return servicesResp;
		}
		return servicesResp;
	}

}
