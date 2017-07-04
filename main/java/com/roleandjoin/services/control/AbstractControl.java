package com.roleandjoin.services.control;

import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.UsuarioGCS;

abstract class AbstractControl {
	
	protected boolean userHasActivateAccount(String userName) {
		UsuarioGCS usuarioGCS = new UsuarioGCS();
		return usuarioGCS.accountIsActivate(userName);
	}

}
