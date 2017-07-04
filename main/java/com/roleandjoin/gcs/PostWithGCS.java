package com.roleandjoin.gcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.PostWith;

public class PostWithGCS {
	DatastoreService datastore;
	public static final String ENTIDAD = "WithPost";
	// public static final String PROPIEDAD_WITH = "with";
	public static final String PROPIEDAD_ID_POST = "idPost";
	public static final String PROPIEDAD_ID_CHANNEL = "idChannel";
	public static final String PROPIEDAD_DATE = "date";
	private final int pageSize = 10;

	public PostWithGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	@Deprecated
	public long crear(long idChannel, long idPost, String with) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, with);
		Entity entity = new Entity(ENTIDAD, kParent);
		entity.setProperty(PROPIEDAD_ID_CHANNEL, idChannel);
		entity.setProperty(PROPIEDAD_ID_POST, idPost);
		entity.setProperty(PROPIEDAD_DATE, (new Date()).getTime());
		Key k = datastore.put(entity);
		Logger.getLogger("").warning("@@@@@PostWith Save "+k.getId());
		return k.getId();
	}
	
	public long crear(String idChannel, String idPost, String with) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, with);
		Entity entity = new Entity(ENTIDAD, kParent);
		entity.setProperty(PROPIEDAD_ID_CHANNEL, idChannel);
		entity.setProperty(PROPIEDAD_ID_POST, idPost);
		entity.setProperty(PROPIEDAD_DATE, (new Date()).getTime());
		Key k = datastore.put(entity);
		Logger.getLogger("").warning("@@@@@PostWith Save "+k.getId());
		return k.getId();
	}

	public List<PostWith> get(String idChannel, String with,int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, with);
		Filter filter1 = new FilterPredicate(PROPIEDAD_ID_CHANNEL,
				FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(filter1).addSort(
				PROPIEDAD_DATE, SortDirection.DESCENDING);
		List<Entity> lstEntities = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<PostWith> lstPostWith = new ArrayList<PostWith>();
		if (lstEntities != null && !lstEntities.isEmpty()) {
			PostWith postWith = null;
			for (Entity entity : lstEntities) {
				postWith = entityToPostWith(entity);
				lstPostWith.add(postWith);
			}
		}
		return lstPostWith;
	}
	
	public int getPostWithCount(String idChannel, String with) {
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, with);
		Filter filter1 = new FilterPredicate(PROPIEDAD_ID_CHANNEL,
				FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(filter1);
		int count = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return count;
	}

	private PostWith entityToPostWith(Entity entity) {
		PostWith postWith = null;
		if (entity != null) {
			postWith = new PostWith();
			postWith.setIdCanal((String) entity.getProperty(PROPIEDAD_ID_CHANNEL));
			postWith.setIdPost((String) entity.getProperty(PROPIEDAD_ID_POST));
		}
		return postWith;
	}

	public int countPostWith(String idChannel, String with) {
		if(with==null)
			return 0;
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, with);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CHANNEL, FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(f1);
		int count = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return count;
	}

	
}
