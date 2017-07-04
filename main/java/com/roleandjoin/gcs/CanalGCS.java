package com.roleandjoin.gcs;

import java.util.ArrayList;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.Invitacion;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.gcs.entidad.Post;
import com.roleandjoin.utils.TokenIdentifierGenerator;
import com.roleandjoin.utils.ValidadorDeCadenas;

public class CanalGCS {

	public static final int WHO_POST_SOLO_YO = 1;
	public static final int WHO_POST_INVITADOS = 2;
	public static final int WHO_POST_CUALQUIERA = 3;
	DatastoreService datastore;
	public static final String ENTIDAD = "Canal";
	public static final String PROPIEDAD_CHANNEL_NAME = "nombre";
	public static final String PROPIEDAD_CH_NAME_LOWER_CASE = "chNameLowerCase";
	public static final String PROPIEDAD_IS_PUBLIC = "isPublic";
	public static final String PROPIEDAD_DESCRIP = "descrip";
	public static final String PROPIEDAD_OWNER = "owner";
	public static final String PROPIEDAD_WHO_POST = "whoPost";
	public static final String PROPIEDAD_DATE_LAST_POST = "dateLastPost";
	public static final String PROPIEDAD_CLOSE = "close";
	public static final String PROPIEDAD_JOINERS_COUNT = "joinersCount";
	public static final String PROPIEDAD_JOINERS_MIN = "joinersMin";
	public static final String PROPIEDAD_VIEWS_COUNT = "viewsCount";

	private static final int JOINERS_MIN = 10;

	public CanalGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	private String generateIdChannel(String userNameOwner) {
		String idChannel = TokenIdentifierGenerator.nextSessionId();
		while (getChannelExist(idChannel, userNameOwner)) {
			idChannel = TokenIdentifierGenerator.nextSessionId();
		}
		return idChannel;
	}

	public boolean getChannelExist(String idChannel, String userNameOwner) {
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameOwner);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idChannel);
		Entity e = getEntidadByKey(k);
		if (e != null) {
			return true;
		}
		return false;
	}

	public List<Canal> getCanales(String userName, boolean isPublic,
			boolean isOwner, String userLogged) {
		userName = ValidadorDeCadenas.addArrobaNombreUsuario(userName);
		List<Canal> lstCanales = new ArrayList<Canal>();
		Filter f1 = new FilterPredicate(PROPIEDAD_IS_PUBLIC,
				FilterOperator.EQUAL, isPublic);

		Filter f2 = new FilterPredicate(PROPIEDAD_CLOSE, FilterOperator.EQUAL,
				false);

		Filter f3 = CompositeFilterOperator.and(f1, f2);

		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(f3);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (results != null && !results.isEmpty()) {
			Canal canal;
			UserJoinGCS joinGCS = new UserJoinGCS();
			Join join = null;
			for (Entity entity : results) {
				canal = this.entityToCanal(entity, userName, isOwner);
				join = joinGCS.getJoin(canal.getIdCanal(), userLogged);
				if (join != null)
					canal.setJoin(true);
				lstCanales.add(canal);
			}
		}
		return lstCanales;
	}

	public List<Canal> getCanales(String userName, int numPag) {
		numPag = ((numPag - 1) * pageSize);
		List<Canal> lstCanales = new ArrayList<Canal>();
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Query query = new Query(ENTIDAD).setAncestor(k);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		if (results != null && !results.isEmpty()) {
			Canal canal;
			for (Entity entity : results) {
				canal = this.entityToCanal(entity, userName);
				lstCanales.add(canal);
			}
		}
		return lstCanales;
	}

	public int getCountChannels(String userName) {
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Query query = new Query(ENTIDAD).setAncestor(k);
		int results = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	public String crear(String userName, Canal canal) {
		String idChannel = generateIdChannel(userName);
		canal.setNombre(ValidadorDeCadenas.addNumeralCanal(canal.getNombre()));
		String rsp = "";
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Entity entity = new Entity(ENTIDAD, idChannel, k);
		entity.setProperty(PROPIEDAD_DESCRIP, canal.getDescrip());
		entity.setProperty(PROPIEDAD_IS_PUBLIC, canal.isPublic());
		entity.setProperty(PROPIEDAD_CHANNEL_NAME, canal.getNombre());
		entity.setProperty(PROPIEDAD_CH_NAME_LOWER_CASE, canal.getNombre()
				.toLowerCase());
		entity.setProperty(PROPIEDAD_WHO_POST, new Integer(canal.getWhoPost()));
		entity.setProperty(PROPIEDAD_CLOSE, false);
		entity.setProperty(PROPIEDAD_JOINERS_COUNT, 0L);
		entity.setProperty(PROPIEDAD_JOINERS_MIN, false);
		entity.setProperty(PROPIEDAD_VIEWS_COUNT, 0L);
		Key kresp = datastore.put(entity);
		rsp = kresp == null ? "" : kresp.getName();
		return rsp;
	}

	public String getChannelName(String userName, String idChannel) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, idChannel);
		Entity entity = this.getEntidadByKey(k);
		Canal canal = null;
		if (entity != null) {
			return (String) entity.getProperty(PROPIEDAD_CHANNEL_NAME);
		}
		return null;
	}

	public Canal getCanalById(String userName, String idChannel) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, idChannel);

		Entity entity = this.getEntidadByKey(k);
		Canal canal = null;
		if (entity != null) {
			canal = entityToCanal(entity, userName);
		}
		return canal;
	}

//	public Canal getCanalById(String userName, String id) {
//		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
//		Key k = KeyFactory.createKey(kParent, ENTIDAD, id);
//
//		Entity entity = this.getEntidadByKey(k);
//		Canal canal = null;
//		if (entity != null) {
//			canal = entityToCanal(entity, userName);
//		}
//		return canal;
//	}

	public boolean isPublicChannel(String userName, String idChannel) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, idChannel);

		Entity entity = this.getEntidadByKey(k);
		Canal canal = null;
		if (entity != null) {
			return (Boolean) entity.getProperty(PROPIEDAD_IS_PUBLIC);
		}
		return false;
	}

	public Entity getEntityCanalById(String userName, String idChannel) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, idChannel);
		Entity entity = this.getEntidadByKey(k);
		return entity;
	}

	public Canal getCanalById(String idChannel) {
		Key k = KeyFactory.createKey(ENTIDAD, idChannel);
		Entity entity = this.getEntidadByKey(k);
		Canal canal = null;
		if (entity != null) {
			canal = entityToCanal(entity, "");
		}
		return canal;
	}

	public void delete(String userName, String idChannel) {
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idChannel);
		datastore.delete(k);
	}

	public void close(String userName, String idChannel) {
		Entity e = getEntityCanalById(userName, idChannel);
		if (e != null) {
			e.setProperty(PROPIEDAD_CLOSE, true);
			datastore.put(e);
		}
	}

	public boolean[] getAccess(String idChannel, String userNameAuth,
			String ownerChannel) {
		boolean[] resp = { false, false, false };
		Canal canal = getCanalById(ownerChannel, idChannel);

		if (canal == null)
			return resp;

		if (userNameAuth != null && userNameAuth.equals(ownerChannel)) {
			resp[0] = true;
			resp[1] = true;
			resp[2] = false;
			return resp;
		}

		boolean canPost = false;
		boolean access = false;
		boolean channelIsClose = canal.isClose();

		if (!canal.isPublic()) {
			InvitacionGCS gcs = new InvitacionGCS();
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			String emailInvited = usuarioGCS.getEmailFromUser(userNameAuth);
//			access = gcs.accessAllow(canal.getIdCanal(), userNameAuth, canal.getOwnerUser());
			access = gcs.accessAllow(idChannel, userNameAuth);
			if (!access) {
				UserJoinGCS gcsJoin = new UserJoinGCS();
				access = gcsJoin.accessAllow(canal.getIdCanal(), userNameAuth);
			}

			if (!access) {
				return resp;
			}
			// canPost = gcs.canPost(canal.getIdCanal(), userNameAuth);

			UserJoinGCS joinGCS = new UserJoinGCS();
			canPost = joinGCS.getAllowPost(idChannel, userNameAuth, true);

		} else if (canal.getWhoPost() == WHO_POST_CUALQUIERA) {
			access = true;
			canPost = true;
		} else if (canal.getWhoPost() == WHO_POST_INVITADOS) {
			access = true;
			// InvitacionGCS gcs = new InvitacionGCS();
			// canPost = gcs.canPost(canal.getIdCanal(), userNameAuth);
			UserJoinGCS joinGCS = new UserJoinGCS();
			canPost = joinGCS.getAllowPost(idChannel, userNameAuth, true);
		} else {
			access = true;
		}
		resp[0] = access;
		resp[1] = canPost;
		resp[2] = channelIsClose;
		return resp;
	}

	public long edit(String userName, Canal canal) {
		Key kParent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, canal.getIdCanal());
		Key kResp = null;
		Entity entity = this.getEntidadByKey(k);
		if (entity != null) {
			entity.setProperty(PROPIEDAD_WHO_POST, canal.getWhoPost());
			entity.setProperty(PROPIEDAD_DESCRIP, canal.getDescrip());
			kResp = datastore.put(entity);
			InvitacionGCS invitacionGCS = new InvitacionGCS();
			invitacionGCS.delete(userName, canal.getIdCanal());
			invitacionGCS.saveInvitados(canal.getIdCanal(), userName, canal
					.getInvitados(),
					canal.getWhoPost() == CanalGCS.WHO_POST_INVITADOS ? true
							: false);
		}
		return kResp == null ? 0 : 1;
	}

	public Canal getCanalByName(String userNameOwner, String nameChannel) {
		nameChannel = ValidadorDeCadenas.addNumeralCanal(nameChannel);
		Canal c = null;
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameOwner);
		Filter filter = new FilterPredicate(PROPIEDAD_CHANNEL_NAME,
				FilterOperator.EQUAL, nameChannel);
		Query q = new Query(ENTIDAD, kp).setFilter(filter);
		Entity e = datastore.prepare(q).asSingleEntity();
		if (e != null) {
			c = entityToCanal(e, userNameOwner);
		}
		return c;
	}

	public Canal getCanalByLowerCaseName(String userNameOwner,
			String nameChannel) {
		nameChannel = ValidadorDeCadenas.addNumeralCanal(nameChannel);
		Canal c = null;
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameOwner);
		Filter filter = new FilterPredicate(PROPIEDAD_CH_NAME_LOWER_CASE,
				FilterOperator.EQUAL, nameChannel.toLowerCase());
		Query q = new Query(ENTIDAD, kp).setFilter(filter);
		Entity e = datastore.prepare(q).asSingleEntity();
		if (e != null) {
			c = entityToCanal(e, userNameOwner);
		}
		return c;
	}

	public List<Canal> search(String userNameOwner, String userLogged,
			String nameChannel) {
		List<Entity> lstChannelsPublic = getChannelsByTypeAccess(userNameOwner,
				true);
		List<Entity> lstChannelsPrivate = getEntitiesInvitedChannels(
				userNameOwner, userLogged);
		lstChannelsPublic.addAll(lstChannelsPrivate);
		return search(lstChannelsPublic, nameChannel);
	}

	public List<Canal> getInvitedChannels(String userNameOwner,
			String userLogged) {
		List<Canal> lstCanales = new ArrayList<Canal>();
		InvitacionGCS invitacionGCS = new InvitacionGCS();
		List<Invitacion> lstInvs = invitacionGCS.get(userNameOwner, userLogged);
		if (lstInvs != null) {
			Canal ch;
			for (Invitacion invitacion : lstInvs) {
				ch = this.getCanalById(invitacion.getOwnerChannel(),
						invitacion.getIdCanal());
				if (ch != null) {
					ch.setCanPost(invitacion.isAllowPost());
					if (userLogged.equals(userNameOwner)) {
						ch.setOwner(true);
					} else
						ch.setOwner(false);
					lstCanales.add(ch);
				}
			}
		}
		return lstCanales;
	}

	public List<Entity> getEntitiesInvitedChannels(String userNameOwner,
			String userLogged) {
		List<Entity> lstCanales = new ArrayList<Entity>();
		InvitacionGCS invitacionGCS = new InvitacionGCS();
		List<Invitacion> lstInvs = invitacionGCS.get(userNameOwner, userLogged);
		if (lstInvs != null) {
			Entity entityCh;
			for (Invitacion invitacion : lstInvs) {
				entityCh = this.getEntityCanalById(
						invitacion.getOwnerChannel(), invitacion.getIdCanal());
				if (entityCh != null) {
					lstCanales.add(entityCh);
				}
			}
		}
		return lstCanales;
	}

	public List<Canal> getInvitations(String userLogged, int numPag) {
		Logger.getLogger(CanalGCS.class.getSimpleName()).warning(
				"@@@@@getInvitaciones " + userLogged + " np: " + numPag);
		InvitacionGCS invitacionGCS = new InvitacionGCS();
		List<Invitacion> lstInvs = invitacionGCS.get(userLogged, numPag);
		List<Canal> lstCanales = null;
		if (lstInvs != null) {
			lstCanales = new ArrayList<Canal>();
			Canal ch;
			for (Invitacion invitacion : lstInvs) {
				ch = this.getCanalById(invitacion.getOwnerChannel(),
						invitacion.getIdCanal());
				if (ch != null) {
					lstCanales.add(ch);
				}
			}
		}
		return lstCanales;
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

	int pageSize = 10;

	public List<Canal> getChannelsByLastPost(String userNameOwner, int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameOwner);
		Query query = new Query(ENTIDAD).setAncestor(k).addSort(
				PROPIEDAD_DATE_LAST_POST, SortDirection.DESCENDING);

		// Query query = new Query(ENTIDAD).setAncestor(k);

		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(10).offset(numPag));
		// List<Canal> lstCanal = null;
		// if (results != null && !results.isEmpty()) {
		// lstCanal = new ArrayList<Canal>();
		// Canal c = null;
		// PostGCS gcs = new PostGCS();
		// for (Entity e : results) {
		// c = entityToCanal(e, userNameOwner);
		// c.setLastPost(gcs.getLastPost(c.getIdCanal(), c.isPublic()));
		// lstCanal.add(c);
		// }
		// }
		return getChannelsWithLastPost(results, userNameOwner);
	}

	private List<Canal> getChannelsWithLastPost(List<Entity> channelEntities,
			String userNameOwner) {
		List<Canal> lstCanal = null;
		if (channelEntities != null && !channelEntities.isEmpty()) {
			lstCanal = new ArrayList<Canal>();
			Canal c = null;
			PostGCS gcs = new PostGCS();
			Post p = null;
			for (Entity e : channelEntities) {
				c = entityToCanal(e, userNameOwner);
				p = gcs.getLastPost(c.getIdCanal(), c.isPublic());
				if (p != null) {
					c.setLastPost(p);
					lstCanal.add(c);
				}
			}
		}
		return lstCanal;
	}

	public int getCountChannelsByLastPost(String userNameOwner) {
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameOwner);
		Query query = new Query(ENTIDAD).setAncestor(k).addSort(
				PROPIEDAD_DATE_LAST_POST, SortDirection.DESCENDING);
		int results = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	public List<Canal> getChannelsByJoins(String userLogged,
			String ownerChannel, int numPag) {
		UserJoinGCS joinGCS = new UserJoinGCS();
		List<Join> lstJoins = joinGCS.getJoinsByOwnerChannel(userLogged,
				ownerChannel, numPag);
		List<Canal> lstChs = null;
		Canal ch = null;

		if (lstJoins != null && !lstJoins.isEmpty()) {
			lstChs = new ArrayList<Canal>();
			for (Join join : lstJoins) {
				ch = this.getCanalById(join.getOwnerChannel(),
						join.getIdChannel());
				if (ch != null && !ch.isPublic()) {
					ch.setOwner(isOwnerChannel(userLogged, ownerChannel));
					ch.setJoin(true);
					lstChs.add(ch);
				}
			}
		}
		return lstChs;
	}

	private boolean isOwnerChannel(String userLogged, String ownerChannel) {

		return userLogged.equals(ownerChannel);

	}

	public void updateJoinersCount(String userOwnerChannel, String idChannel,
			boolean add) {
		Key kParent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userOwnerChannel);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, idChannel);
		Entity entity = this.getEntidadByKey(k);
		if (entity != null) {
			long joinersCount = (Long) entity
					.getProperty(PROPIEDAD_JOINERS_COUNT);
			if (add)
				joinersCount += 1;
			else if (joinersCount > 0)
				joinersCount -= 1;

			boolean updateJoinersMin = false;

			if (joinersCount >= JOINERS_MIN) {
				updateJoinersMin = true;
			}

			entity.setProperty(PROPIEDAD_JOINERS_MIN, updateJoinersMin);
			entity.setProperty(PROPIEDAD_JOINERS_COUNT, joinersCount);
			datastore.put(entity);
		}
	}

	private void updateJoinersMin() {

	}

	public List<Canal> getChannelsWithMoreJoiners(boolean publicChannels,
			int numPag) {
		numPag = ((numPag - 1) * pageSize);
		// Filter f1 = new FilterPredicate(PROPIEDAD_JOINERS_COUNT,
		// FilterOperator.GREATER_THAN, 10);

		Filter f1 = new FilterPredicate(PROPIEDAD_JOINERS_MIN,
				FilterOperator.EQUAL, true);

		Filter f2 = new FilterPredicate(PROPIEDAD_IS_PUBLIC,
				FilterOperator.EQUAL, publicChannels);
		Filter f3 = CompositeFilterOperator.and(f1, f2);

		Query q = new Query(ENTIDAD).addSort(PROPIEDAD_DATE_LAST_POST,
				SortDirection.DESCENDING).setFilter(f3);

		// Cada vez que el canal tenga un nuevo Join o algun unjoin determinar
		// si esta dentro de los mas vistos

		// Query q = new Query(ENTIDAD).setFilter(f3);

		List<Entity> lstEntities = datastore.prepare(q).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		return getChannelWithLastPost(lstEntities);
	}

	public void addViewsCount(String idChannel, String ownerChannel) {
		long channelViewsCount = 0;
		Entity e = getEntityCanalById(ownerChannel, idChannel);
		if (e != null) {
			try {
				channelViewsCount = (Long) e.getProperty(PROPIEDAD_VIEWS_COUNT);
				channelViewsCount += 1;
			} catch (Exception e2) {
				channelViewsCount = 1;
				Logger.getLogger(PostGCS.class.getName())
						.warning(
								"@@@@@" + PostGCS.class.getName() + " "
										+ e2.toString());
			} finally {
				e.setProperty(PROPIEDAD_VIEWS_COUNT, channelViewsCount);
				datastore.put(e);
			}
		}
	}

	private List<Canal> getChannelWithLastPost(List<Entity> lstEntities) {
		List<Canal> lstChannels = null;
		lstChannels = new ArrayList<Canal>();
		if (lstEntities != null && !lstEntities.isEmpty()) {
			Canal ch = null;
			Post post = null;
			PostGCS postGCS = new PostGCS();
			for (Entity entity : lstEntities) {
				ch = entityToCanal(entity, "");
				post = postGCS.getLastPost(ch.getIdCanal(), ch.isPublic());
				if (post != null) {
					ch.setLastPost(post);
					lstChannels.add(ch);
				}
			}
		}
		return lstChannels;
	}

	public int getCountChannelsWithMoreJoiners(boolean publicChannels,
			int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Filter f1 = new FilterPredicate(PROPIEDAD_JOINERS_COUNT,
				FilterOperator.GREATER_THAN, 10);
		Filter f2 = new FilterPredicate(PROPIEDAD_IS_PUBLIC,
				FilterOperator.EQUAL, publicChannels);
		Filter f3 = CompositeFilterOperator.and(f1, f2);
		Query q = new Query(ENTIDAD).setFilter(f3);
		int count = datastore.prepare(q).countEntities(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		return count;
	}

	private List<Entity> getChannelsByTypeAccess(String userNameOwner,
			boolean isPublic) {
		Filter f1 = new FilterPredicate(PROPIEDAD_IS_PUBLIC,
				FilterOperator.EQUAL, isPublic);
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userNameOwner);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(f1);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	private List<Canal> search(List<Entity> lstChannels, String nameChannel) {
		nameChannel = ValidadorDeCadenas.addNumeralCanal(nameChannel);
		String nameCh = "";
		List<Canal> listCh = null;
		if (lstChannels != null && !lstChannels.isEmpty()) {
			listCh = new ArrayList<Canal>();
			for (Entity entity : lstChannels) {
				nameCh = (String) entity.getProperty(PROPIEDAD_CHANNEL_NAME);
				if (nameCh.startsWith(nameChannel)) {
					listCh.add(entityToCanal(entity, "", false));
				}
			}
		}
		return listCh;
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

	private Canal entityToCanal(Entity entity, String ownerUser) {
		Canal canal = new Canal();
		canal.setDescrip((String) entity.getProperty(PROPIEDAD_DESCRIP));
		canal.setIdCanal(entity.getKey().getName());
		canal.setNombre((String) entity.getProperty(PROPIEDAD_CHANNEL_NAME));
		canal.setPublic((Boolean) entity.getProperty(PROPIEDAD_IS_PUBLIC));
		canal.setOwnerUser(entity.getParent().getName());
		canal.setJoiners(getJoinersCount(canal.getIdCanal()));
		Object objClose = entity.getProperty(PROPIEDAD_CLOSE);
		if (objClose != null) {
			canal.setClose((Boolean) objClose);
		}
		if (entity.getProperty(PROPIEDAD_WHO_POST) != null) {
			long whoPost = (Long) entity.getProperty(PROPIEDAD_WHO_POST);
			canal.setWhoPost((int) whoPost);
		}
		if (ownerUser.equals(entity.getParent().getName()))
			canal.setOwner(true);
		else
			canal.setOwner(false);

		return canal;
	}

	private int getJoinersCount(String idChannel) {
		ChannelJoinGCS gcs = new ChannelJoinGCS();
		return gcs.getCountJoins(idChannel);
	}

	private Canal entityToCanal(Entity entity, String ownerUser, boolean isOwner) {
		Canal canal = this.entityToCanal(entity, ownerUser);
		if (canal != null)
			canal.setOwner(isOwner);
		return canal;
	}

}
