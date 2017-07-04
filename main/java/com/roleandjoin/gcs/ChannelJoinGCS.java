package com.roleandjoin.gcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.Invitacion;
import com.roleandjoin.gcs.entidad.Join;

public class ChannelJoinGCS extends AbstractGCS {

	private final int pageSize = 10;
	public static final String ENTIDAD = "ChannelJoin";
	public static final String PROPIEDAD_USER_NAME = "userName";
	public static final String PROPIEDAD_DATE_JOIN = "dateJoin";
	public static final String PROPIEDAD_OWNER_CHANNEL = "ownerChannel";
	public static final String PROPIEDAD_DATE_LAST_POST = "dateLastPost";
	public static final String PROPIEDAD_CHANNEL_NAME = "channelName";

	public ChannelJoinGCS() {
		super();
	}

	public long addJoin(Join join) {
		CanalGCS canalGCS = new CanalGCS();
		String channelName = canalGCS.getChannelName(join.getOwnerChannel(), join.getIdChannel());
		long id = crear(join,channelName);
		if(id!=0){
			canalGCS.updateJoinersCount(join.getOwnerChannel(), join.getIdChannel(),true);
		}
		return id;
	}

	private long crear(Join join, String chName) {
		Key k = KeyFactory.createKey(CanalGCS.ENTIDAD, join.getIdChannel());
		Entity entity = new Entity(ENTIDAD, k);
		entity.setProperty(PROPIEDAD_USER_NAME, join.getUserName());
		entity.setProperty(PROPIEDAD_DATE_JOIN, (new Date()).getTime());
		entity.setProperty(PROPIEDAD_OWNER_CHANNEL, join.getOwnerChannel());
		entity.setProperty(PROPIEDAD_DATE_LAST_POST, 0);
		entity.setProperty(PROPIEDAD_CHANNEL_NAME, chName);
		Key kr = datastore.put(entity);
		return kr != null ? kr.getId() : 0;
	}

	public long updateDateLastPost(String idChannel) {
		long resp = 0;
		Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Query q = new Query(ENTIDAD, kp);
		List<Entity> lstEnties = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		if (lstEnties != null && !lstEnties.isEmpty()) {
			for (Entity entity : lstEnties) {
				entity.setProperty(PROPIEDAD_DATE_LAST_POST,
						(new Date()).getTime());
				kp = datastore.put(entity);
				resp = kp.getId();
			}
		}
		return resp;
	}

	// public List<Join> getJoins(String userName, int numPag) {
	// numPag = ((numPag - 1) * pageSize);
	// Filter keyFilter = new FilterPredicate(PROPIEDAD_USER_NAME,
	// FilterOperator.EQUAL, userName);
	// Query query = new Query(ENTIDAD).addSort(PROPIEDAD_DATE_JOIN,
	// SortDirection.DESCENDING).setFilter(keyFilter);
	// List<Entity> results = datastore.prepare(query).asList(
	// FetchOptions.Builder.withLimit(pageSize).offset(numPag));
	// List<Join> lstJoin = new ArrayList<Join>();
	// for (Entity entity : results) {
	// lstJoin.add(entityToPost(entity));
	// }
	// return lstJoin;
	// }

	public List<Join> getJoins(String userName, int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Filter keyFilter = new FilterPredicate(PROPIEDAD_USER_NAME,
				FilterOperator.EQUAL, userName);
		Query query = new Query(ENTIDAD).addSort(PROPIEDAD_DATE_LAST_POST,
				SortDirection.DESCENDING).setFilter(keyFilter);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Join> lstJoin = new ArrayList<Join>();
		for (Entity entity : results) {
			lstJoin.add(entityToPost(entity));
		}
		return lstJoin;
	}
	
//	public int getCountJoins(String userName) {
//		Filter keyFilter = new FilterPredicate(PROPIEDAD_USER_NAME,
//				FilterOperator.EQUAL, userName);
//		Query query = new Query(ENTIDAD).setFilter(keyFilter);
//		int results = datastore.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
//		return results;
//	}

	public int getCountJoins(String idCanal) {
		Key k = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
		Query query = new Query(ENTIDAD, k);
		int count = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return count;
	}
	
	public int getCountJoins2(long idCanal) {
		Key k = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
		Query query = new Query(ENTIDAD, k);
		int count = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return count;
	}

	public void delete(String userName, String idChannel) {
		Entity entity = getEntityJoin(userName, idChannel);
		if (entity != null) {
			String userOwnerChannel = (String) entity.getProperty(PROPIEDAD_OWNER_CHANNEL);
			datastore.delete(entity.getKey());
			CanalGCS canalGCS = new CanalGCS();
			canalGCS.updateJoinersCount(userOwnerChannel, idChannel,false);
		}
	}

	private Entity getEntityJoin(String userName, String idChannel) {
		Entity e = null;
		try {
			Key kancestor = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
			Filter f1 = new FilterPredicate(PROPIEDAD_USER_NAME,
					FilterOperator.EQUAL, userName);
			Query query = new Query(ENTIDAD, kancestor).setFilter(f1);
			e = datastore.prepare(query).asSingleEntity();
		} catch (Exception e2) {
			Logger.getLogger("").warning(e2.toString());
		} finally {
			return e;
		}

	}

	// public void unJoin(String userName, long idJoin){
	// Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
	// Key k = KeyFactory.createKey(kancestor, ENTIDAD, idJoin);
	// datastore.delete(k);
	// }

	private Join entityToPost(Entity entity) {
		Join join = new Join();
		join.setIdChannel(entity.getParent().getName());
		join.setOwnerChannel((String) entity
				.getProperty(PROPIEDAD_OWNER_CHANNEL));
		join.setChannelName((String) entity
				.getProperty(PROPIEDAD_CHANNEL_NAME));
		return join;
	}
}
