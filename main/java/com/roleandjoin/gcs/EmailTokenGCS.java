package com.roleandjoin.gcs;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class EmailTokenGCS {
	
	DatastoreService datastore;
	public static final String ENTIDAD = "EmailToken";
	public static final String PROPIEDAD_EMAIL = "email";
	public static final String PROPIEDAD_TOKEN = "token";
	public static final String PROPIEDAD_USER_ID_CHANNEL = "userIdChannel";
	
	
	public EmailTokenGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	public long saveToken(String email, String  token,String redirect){
		Entity e = new Entity(ENTIDAD);
		e.setProperty(PROPIEDAD_EMAIL, email);
		e.setProperty(PROPIEDAD_TOKEN, token);
		e.setProperty(PROPIEDAD_USER_ID_CHANNEL, redirect);
		Key k = datastore.put(e);
		if(k.getId()!=0){
			Logger.getLogger("").warning("@@@@@New emailToken "+email+ " " + token);
		}
		return k.getId();
	}
	
	public boolean validateToken(String email, String  token){
		Filter f1 = new  FilterPredicate(PROPIEDAD_EMAIL, FilterOperator.EQUAL, email);
		Filter f2 = new  FilterPredicate(PROPIEDAD_TOKEN, FilterOperator.EQUAL, token);
		Filter f3 =  CompositeFilterOperator.and(f1, f2);
		Query query = new Query(ENTIDAD).setFilter(f3);
		Entity e = datastore.prepare(query).asSingleEntity();
		if(e!=null){
			return true;
		}
		return false;
	}
}
