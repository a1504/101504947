package com.roleandjoin.gcs;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.utils.ValidadorDeCadenas;

public class TestGCS {
	
	DatastoreService datastore;
	public static final String ENTIDAD = "Test";
	public static final String PROPIEDAD_NOMBRE = "nombre";
	public static final String PROPIEDAD_DESCRIP = "descrip";
	public static final String ENTIDAD_HIJO = "TestHijo";

	public TestGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	public long crear(Canal canal) {
		long rsp = 0;
		Entity entity = new Entity(ENTIDAD);
		entity.setProperty(PROPIEDAD_DESCRIP, canal.getDescrip());
		entity.setProperty(PROPIEDAD_NOMBRE, canal.getNombre());
		Key kresp = datastore.put(entity);
		rsp = kresp == null ? 0 : kresp.getId();
		return rsp;
	}
	
	public long crear(long idParent) {
		Key kp = KeyFactory.createKey(ENTIDAD, idParent);
		long rsp = 0;
		Entity entity = new Entity(ENTIDAD_HIJO, kp);
		entity.setProperty(PROPIEDAD_DESCRIP, "xxxHIJO");
		entity.setProperty(PROPIEDAD_NOMBRE, "XXXXHIJO");
		Key kresp = datastore.put(entity);
		rsp = kresp == null ? 0 : kresp.getId();
		return rsp;
	}
	
	public Entity get(long id){
		Key key = KeyFactory.createKey(ENTIDAD, id);
		Filter f1 = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key);
		Query  query = new Query();
		query.setFilter(f1);
		Entity e = datastore.prepare(query).asSingleEntity();
		try {
			Entity ex = datastore.get(key);
			System.out.println();
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public Entity getHijo(long idHijo){
		Key key = KeyFactory.createKey(ENTIDAD_HIJO, idHijo);
		
		Filter f1 = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key);
		Query  query = new Query();
		query.setFilter(f1);
		Entity e = datastore.prepare(query).asSingleEntity();
		try {
			Entity ex = datastore.get(key);
			System.out.println();
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

}
