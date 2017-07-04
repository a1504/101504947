package com.roleandjoin.services;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.roleandjoin.gcs.TokenGCS;
import com.roleandjoin.gcs.UsuarioGCS;
import com.roleandjoin.gcs.entidad.Token;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.services.vo.ServicesResp;
import com.roleandjoin.utils.TokenIdentifierGenerator;
import com.roleandjoin.utils.Utilidades;
import com.roleandjoin.utils.ValidadorDeCadenas;

@Path("login")
public class LoginServices extends Services {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Usuario auth(@HeaderParam("Authorization")String auth){
		String[] arr = Utilidades.decodeBasicAuth(auth);
		arr[0] = ValidadorDeCadenas.addArrobaNombreUsuario(arr[0]);
		UsuarioGCS usuarioGCS = new UsuarioGCS();
		Usuario usuario =  usuarioGCS.getUsuario(arr[0], arr[1]);
		if(usuario!=null){
			Token token = new Token();
			token.setToken(TokenIdentifierGenerator.nextSessionId());
			TokenGCS tokenGCS = new TokenGCS();
			tokenGCS.crear(arr[0], token);
			usuario.setPwd(token.getToken());
			return usuario;
		}else{
			if(usuarioGCS.userExists(arr[0]) && !usuarioGCS.accountIsActivate(arr[0])){
				usuario=new Usuario();
				usuario.setUserName(arr[0]);
			}
		}
		
//		byte[] message = "hello world".getBytes();
//		String encoded = DatatypeConverter.printBase64Binary(message);
//		byte[] decoded = DatatypeConverter.parseBase64Binary(encoded);
		
		return usuario;
	}
	
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public String logOut(@HeaderParam("Authorization")String auth){
		String[] arr = Utilidades.decodeBasicAuth(auth);
		String userName = arr[0];
		String token = arr[1];
		TokenGCS tokenGCS = new TokenGCS();
		tokenGCS.delete(userName, token);
		return "ok";
	}
	
	@GET
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ServicesResp sessionIsValid(@HeaderParam("Authorization")String auth){
		ServicesResp resp = new ServicesResp();
		resp.setId(super.USER_NOT_LOGGED);
		String userLogged = super.tokenIsValid(auth);
		if(userLogged!=null){
			resp.setId(super.USER_LOGGED);
		}
		return resp;
	}
}
