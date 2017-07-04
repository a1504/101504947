package com.roleandjoin.gcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.Invitacion;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.utils.TokenIdentifierGenerator;

public class UserJoinGCS extends AbstractGCS {

	private final int pageSize = 10;
	public static final String ENTIDAD = "Join";
	public static final String PROPIEDAD_ID_CHANNEL = "idChannel";
	public static final String PROPIEDAD_DATE_JOIN = "dateJoin";
	public static final String PROPIEDAD_OWNER_CHANNEL = "ownerChannel";
	public static final String PROPIEDAD_DATE_LAST_POST = "dateLastPost";
	public static final String PROPIEDAD_ALLOW_POST = "allowPost";
	public static final String PROPIEDAD_IS_PUBLIC = "isPublic";

	public UserJoinGCS() {
		super();
	}

	public String join(Join join, boolean isPublic) {
		return createJoin(join,isPublic);
	}

	public String join(Join join) {
		return createJoin(join,getChannelAccess(join));
	}
	
	private boolean getChannelAccess(Join join){
		CanalGCS canalGCS = new CanalGCS();
		Canal channel = canalGCS.getCanalById(join.getOwnerChannel(), join.getIdChannel());
		return channel!=null?channel.isPublic():false;
	}
	
	private String createJoin(Join join, boolean channelIsPublic) {
		Join isjoin = getJoin(join.getIdChannel(), join.getUserName());
		if (isjoin != null)
			return null;

		CanalGCS canalGCS = new CanalGCS();
		boolean[] access = canalGCS.getAccess(join.getIdChannel(),
				join.getUserName(), join.getOwnerChannel());
		if (access[0]) {
			InvitacionGCS invitacionGCS = new InvitacionGCS();
			Invitacion invitacion = invitacionGCS.getInvitationByIdChannel(
					join.getIdChannel(), join.getUserName());
			String idJoin = crear(join, invitacion != null ? invitacion.isAllowPost()
					: false,channelIsPublic);
			if (idJoin != null && !idJoin.equals("")) {
				ChannelJoinGCS channelJoinGCS = new ChannelJoinGCS();
				channelJoinGCS.addJoin(join);
				if (invitacion != null) {
					long idInv = invitacion.getId();
					invitacionGCS.delete(idInv, join.getUserName());
				}
			}
			return idJoin;
		}
		return null;
	}
	
	private String crear(Join join, boolean allowPost, boolean channelIsPublic) {
		return createEntity(join, allowPost, channelIsPublic);
	}

	@Deprecated
	private String crear(Join join, boolean allowPost) {
		return createEntity(join, allowPost, false);
	}

	private String createEntity(Join join, boolean allowPost,
			boolean channelIsPublic) {
		String idJoin = generateIdChannel(join.getUserName());
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, join.getUserName());
		Entity entity = new Entity(ENTIDAD,idJoin ,k);
		entity.setProperty(PROPIEDAD_ID_CHANNEL, join.getIdChannel());
		entity.setProperty(PROPIEDAD_DATE_JOIN, (new Date()).getTime());
		entity.setProperty(PROPIEDAD_OWNER_CHANNEL, join.getOwnerChannel());
		entity.setProperty(PROPIEDAD_ALLOW_POST, allowPost);
		entity.setProperty(PROPIEDAD_IS_PUBLIC, channelIsPublic);
		Key kr = datastore.put(entity);
//		rsp = kr == null ? "" : kr.getName();
		return kr == null ? "" : kr.getName();
	}
	
	private String generateIdChannel(String userName) {
		String idJoin = TokenIdentifierGenerator.nextSessionId();
		while (getChannelExist(idJoin, userName)) {
			idJoin = TokenIdentifierGenerator.nextSessionId();
		}
		return idJoin;
	}

	private boolean getChannelExist(String idJoin, String userName) {
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idJoin);
		Entity e = getEntidadByKey(k);
		if (e != null) {
			return true;
		}
		return false;
	}
	
	private Entity getEntidadByKey(Key key) {
		Entity entity = null;
		try {
			entity = datastore.get(key);
		} catch (EntityNotFoundException e) {

		} finally {
			return entity;
		}
	}

	public List<Join> getJoins(String userName, int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Query query = new Query(ENTIDAD, kancestor).addSort(
				PROPIEDAD_DATE_JOIN, SortDirection.DESCENDING);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Join> lstJoin = new ArrayList<Join>();
		for (Entity entity : results) {
			lstJoin.add(entityToJoin(entity));
		}
		return lstJoin;
	}

	public int getCountJoins(String userName) {
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Query query = new Query(ENTIDAD, kancestor).addSort(
				PROPIEDAD_DATE_JOIN, SortDirection.DESCENDING);
		int results = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	public List<Join> getJoinsByOwnerChannel(String userLogged, String ownerChannel,
			int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Query query = new Query(ENTIDAD, kancestor).addSort(
				PROPIEDAD_DATE_JOIN, SortDirection.DESCENDING);
		Filter f1 = new FilterPredicate(PROPIEDAD_OWNER_CHANNEL,
				FilterOperator.EQUAL, ownerChannel);
		query.setFilter(f1);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Join> lstJoin = new ArrayList<Join>();
		for (Entity entity : results) {
			lstJoin.add(entityToJoin(entity));
		}
		return lstJoin;
	}
	

	public void delete(String userName, String idChannel) {
		ChannelJoinGCS channelJoinGCS = new ChannelJoinGCS();
		channelJoinGCS.delete(userName, idChannel);
		List<Entity> results = getJoins(userName, idChannel);
		for (Entity entity : results) {
			datastore.delete(entity.getKey());
		}
	}

	public long updateDateLastPost(String idChannel, String userOwner) {
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userOwner);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idChannel);
		Entity entity;
		try {
			entity = datastore.get(k);
			entity.setProperty(PROPIEDAD_DATE_LAST_POST, (new Date()).getTime());
			k = datastore.put(entity);
			return k.getId();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean accessAllow(String idChannel, String userLogged) {
		return getJoin(idChannel, userLogged) == null ? false : true;
	}

	public Join getJoin(String idChannel, String userLogged) {
		if (userLogged == null)
			return null;
		Key kparent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CHANNEL,
				FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD, kparent).setFilter(f1);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entityToJoin(entity);
	}

	private List<Entity> getJoins(String userName, String idChannel) {
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CHANNEL,
				FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD, kancestor).setFilter(f1).setKeysOnly();
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	public void unJoin(String userName, String idJoin) {
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kancestor, ENTIDAD, idJoin);
		datastore.delete(k);
	}

	public boolean getAllowPost(String idChannel, String userNameJoin,
			boolean canPost) {
		boolean allowPost = false;
		Key kparent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameJoin);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CHANNEL,
				FilterOperator.EQUAL, idChannel);
		Filter f2 = new FilterPredicate(PROPIEDAD_ALLOW_POST,
				FilterOperator.EQUAL, canPost);
		Filter f3 = CompositeFilterOperator.and(f1, f2);
		Query query = new Query(ENTIDAD, kparent).setFilter(f3);
		Entity entity = datastore.prepare(query).asSingleEntity();
		if (entity != null) {
			allowPost = (Boolean) entity.getProperty(PROPIEDAD_ALLOW_POST);
		}
		return allowPost;
	}

	private Join entityToJoin(Entity entity) {
		Join join = null;
		if (entity != null) {
			join = new Join();
			join.setIdChannel((String) entity.getProperty(PROPIEDAD_ID_CHANNEL));
			join.setOwnerChannel((String) entity
					.getProperty(PROPIEDAD_OWNER_CHANNEL));
			try {
				join.setChannelIsPublic((Boolean)entity.getProperty(PROPIEDAD_IS_PUBLIC));
			} catch (Exception e) {
				join.setChannelIsPublic(false);
			}
		}
		return join;
	}
}
