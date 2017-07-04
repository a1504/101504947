package com.roleandjoin.services;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.TokenGCS;
import com.roleandjoin.gcs.entidad.Token;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.services.control.UserServicesControl;
import com.roleandjoin.services.vo.ServicesResp;
import com.roleandjoin.utils.DateUtils;
import com.roleandjoin.utils.Utilidades;

abstract class Services {

	@Context
	UriInfo uri;

	protected final int USER_NOT_LOGGED = 0;
	protected final int USER_LOGGED = 1;
	protected final int TRANSACTION_OK = 2;
	protected final int TRANSACTION_NOT_OK = 3;

	public static final int COD_USERNAME_NOT_VALID = 20;
	public static final int COD_EMAIL_NOT_VALID = 21;
	public static final int COD_USERNAME_EXISTS = 22;
	public static final int COD_EMAIL_EXISTS = 23;
	public static final int COD_PASSWORDS_NO_MATCH = 24;

	protected static final int COD_CHANNEL_EXISTS = 30;

	protected TokenGCS tokenGCS;
	private final int time = 86400;

	protected String URL_HOST = "";
	protected final String APP_NAME = "HelloApp";

	public Services() {
		tokenGCS = new TokenGCS();
	}

	@PostConstruct
	protected void ini() {
		tokenGCS = new TokenGCS();
		this.URL_HOST = uri.getBaseUri().toString();
	};

	protected ServicesResp getResponse(long id) {
		ServicesResp resp = new ServicesResp();
		if (id != 0) {
			resp.setId(TRANSACTION_OK);
			return resp;
		}
		resp.setId(TRANSACTION_NOT_OK);
		return resp;
	}
	
	protected ServicesResp getResponseStr(String id) {
		ServicesResp resp = new ServicesResp();
		if (id!=null  && !id.equals("")) {
			resp.setId(TRANSACTION_OK);
			return resp;
		}
		resp.setId(TRANSACTION_NOT_OK);
		return resp;
	}

	private Usuario decodeBasicAuth(String auth) {
		String[] arr = Utilidades.decodeBasicAuth(auth);
		Usuario usuario = new Usuario();
		usuario.setUserName(arr[0]);
		usuario.setPwd(arr[1]);
		return usuario;
	}

	protected boolean tokenIsValid(Usuario usuario) {
		Token tkn = tokenGCS.get(usuario.getUserName(), usuario.getPwd());
		if (tkn != null) {
			return true;
		}
		return false;
	}

	protected String tokenIsValid(String auth) {
		Usuario usuario = this.decodeBasicAuth(auth);
		Token tkn = tokenGCS.get(usuario.getUserName(), usuario.getPwd());
		if (tkn != null && DateUtils.getSecondsPassed(tkn.getDate()) < time) {
			return usuario.getUserName();
		} else if (tkn != null) {
			tokenGCS.delete(usuario.getUserName(), usuario.getPwd());
		}
		return null;
	}

	protected boolean valideAccess(String auth) {
		if (auth != null) {
			String userLogger = this.tokenIsValid(auth);
			if (userLogger != null) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean userHasActiveAccount(String userName) {
		UserServicesControl userServicesControl = new UserServicesControl();
		return userServicesControl.userHasActivateAccount(userName);
		
	}
}
