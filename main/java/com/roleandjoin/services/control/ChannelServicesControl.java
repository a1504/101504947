package com.roleandjoin.services.control;

import java.util.List;

import com.roleandjoin.gcs.CanalGCS;
import com.roleandjoin.gcs.entidad.Canal;

public class ChannelServicesControl extends AbstractControl{

	public List<Canal> getChannels(String userNameOwner, boolean isPublic,
			boolean isOwner, String userLogged){
		CanalGCS canalGCS = new CanalGCS(); 
		return canalGCS.getCanales(userNameOwner, isPublic,
				isOwner, userLogged);
	}
	
	
}
