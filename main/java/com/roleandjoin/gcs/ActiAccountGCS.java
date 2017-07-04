package com.roleandjoin.gcs;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class ActiAccountGCS {
	DatastoreService datastore;
	public static final String ENTIDAD = "ActivateAccount";
	public static final String PROPIEDAD_MENSAJE = "mensaje";
	
	private final int pageSize = 10;

	public ActiAccountGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	
}
