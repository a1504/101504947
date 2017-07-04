package com.roleandjoin.gcs;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.roleandjoin.gcs.entidad.Token;

public class TokenGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "Token";
	public static final String PROPIEDAD_TOKEN = "token";
	public static final String PROPIEDAD_DATE = "fechaHora";

	public TokenGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public long crear(String userName, Token token) {
		Key keyParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Entity entity = new Entity(ENTIDAD, token.getToken(), keyParent);
		entity.setProperty(PROPIEDAD_DATE, new Date());
		Key k = datastore.put(entity);
		return k == null ? 0 : k.getId();
	}

	public void delete(String userName, String token) {
		Key keyParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key key = KeyFactory.createKey(keyParent, ENTIDAD, token);
		datastore.delete(key);
	}

	public Token get(String userName, String token) {
		Key kparent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kparent, ENTIDAD, token);
		Token t = null;
		try {
			Entity entity = datastore.get(k);
			if(entity!=null && entity.getParent().getName().equals(userName)){
				t = entityToToken(entity);
			}
		} catch (EntityNotFoundException e) {
			Logger.getLogger("").warning("@@@@@@Error " + e);
		}finally{
			return t;
		}
		
	}
	
	private Token entityToToken(Entity entity){
		Token token = new Token();
		token.setToken(entity.getKey().getName());
		token.setDate((Date) entity.getProperty(PROPIEDAD_DATE));
		return token;
	}
	
	public String getTokens() {

		Query query = new Query(ENTIDAD);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		String result = "";

		for (Entity entity : results) {

			result += "\n TOKEN: " + entity.getKey().getName();
			result += "\n USER: " + entity.getParent().getName();
			result += "\n -------------------------------------------";

		}

		return result;
	}

}
