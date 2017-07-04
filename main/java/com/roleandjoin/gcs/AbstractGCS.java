package com.roleandjoin.gcs;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

abstract class AbstractGCS {

	protected DatastoreService datastore;
	protected AbstractGCS(){
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	
	
}
