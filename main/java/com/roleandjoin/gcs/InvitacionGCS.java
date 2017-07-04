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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.Invitacion;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.utils.Email;
import com.roleandjoin.utils.ValidadorDeCadenas;

public class InvitacionGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "Invitacion";
	public static final String PROPIEDAD_ALLOW_POST = "allowPost";
	public static final String PROPIEDAD_ID_CANAL = "idCanal";
	public static final String PROPIEDAD_OWNER_CHANNEL = "ownerChannel";
	public static final String PROPIEDAD_DATE = "dateInvitation";
	private final int pageSize = 10;

	public InvitacionGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public long crear(String idChannel, String ownerChannel,
			String userNameInvitado, boolean canPost) {

		UserJoinGCS joinGCS = new UserJoinGCS();
		Join join = joinGCS.getJoin(idChannel, userNameInvitado);
		if (join == null) {

			ValidadorDeCadenas vdc = new ValidadorDeCadenas();
			if (vdc.validarEmail(userNameInvitado)) {
				UsuarioGCS usuarioGCS = new UsuarioGCS();
				String userName = usuarioGCS.getUserNameByEmail(userNameInvitado);
				userNameInvitado = userName!=null?userName:userNameInvitado;
			}

			Key kparent = KeyFactory.createKey(UsuarioGCS.ENTIDAD,
					userNameInvitado);
			Entity entity = new Entity(ENTIDAD, kparent);
			entity.setProperty(PROPIEDAD_ALLOW_POST, canPost);
			entity.setProperty(PROPIEDAD_ID_CANAL, idChannel);
			entity.setProperty(PROPIEDAD_OWNER_CHANNEL, ownerChannel);
			entity.setProperty(PROPIEDAD_DATE, (new Date()).getTime());
			Key k = datastore.put(entity);
			return k == null ? 0 : k.getId();
		} else {
			return 0L;
		}
	}

	public Invitacion getInvitationByIdChannel(String idChannel, String userLogged) {
		Key kparent = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CANAL,
				FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD, kparent).setFilter(f1);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entityToInvitacion(entity);
	}

//	public Invitacion get(long idCanal, String userNameInvitado) {
//		Key kparent = KeyFactory
//				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
//		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CANAL,
//				FilterOperator.EQUAL, idCanal);
//		Query query = new Query(ENTIDAD, kparent).setFilter(f1);
//		Entity entity = datastore.prepare(query).asSingleEntity();
//		return entityToInvitacion(entity);
//	}

	public Invitacion get(String idChannel, String userNameInvitado, boolean canPost) {
		Key kparent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CANAL,
				FilterOperator.EQUAL, idChannel);
		Filter f2 = new FilterPredicate(PROPIEDAD_ALLOW_POST,
				FilterOperator.EQUAL, canPost);
		Filter f3 = CompositeFilterOperator.and(f1, f2);
		Query query = new Query(ENTIDAD, kparent).setFilter(f3);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entityToInvitacion(entity);
	}

	public List<Invitacion> get(String userNameInvitado, int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Logger.getLogger(InvitacionGCS.class.getSimpleName()).warning(
				"@@@@@getInvitaciones " + userNameInvitado + " np: " + numPag);
		if (userNameInvitado == null)
			return null;

		Key kparent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
		Query query = new Query(ENTIDAD, kparent).addSort(PROPIEDAD_DATE,
				SortDirection.DESCENDING);
		Logger.getLogger(InvitacionGCS.class.getSimpleName()).warning(
				"@@@@@getInvitaciones 1");
		List<Entity> lstEntity = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		Logger.getLogger(InvitacionGCS.class.getSimpleName()).warning(
				"@@@@@getInvitaciones 2");
		List<Invitacion> lstInvitacions = null;
		if (lstEntity != null && !lstEntity.isEmpty()) {
			Logger.getLogger(InvitacionGCS.class.getSimpleName()).warning(
					"@@@@@getInvitaciones 3");
			lstInvitacions = new ArrayList<Invitacion>();
			for (int i = 0; i < lstEntity.size(); i++) {
				lstInvitacions.add(entityToInvitacion(lstEntity.get(i)));
			}
		} else {
			Logger.getLogger(InvitacionGCS.class.getSimpleName()).warning(
					"@@@@@getInvitaciones 4");
		}
		return lstInvitacions;
	}

	public int getCountInvitations(String userNameInvitado) {
		if (userNameInvitado == null)
			return 0;
		Key kparent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
		Query query = new Query(ENTIDAD, kparent);
		int result = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return result;
	}

	public List<Invitacion> get(String ownerChannel, String userNameInvitado) {

		if (ownerChannel == null || userNameInvitado == null)
			return null;

		Key kparent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
		Filter f1 = new FilterPredicate(PROPIEDAD_OWNER_CHANNEL,
				FilterOperator.EQUAL, ownerChannel);
		Query query = new Query(ENTIDAD, kparent).setFilter(f1);
		List<Entity> lstEntity = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		List<Invitacion> lstInvitacions = null;
		if (lstEntity != null && !lstEntity.isEmpty()) {
			lstInvitacions = new ArrayList<Invitacion>();
			for (int i = 0; i < lstEntity.size(); i++) {
				lstInvitacions.add(entityToInvitacion(lstEntity.get(i)));
			}
		}
		return lstInvitacions;
	}

	public Invitacion getInvitation(String idChannel, String userNameInvitado) {
		if (idChannel == null || userNameInvitado == null)
			return null;
		Key kparent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
		Filter f1 = new FilterPredicate(PROPIEDAD_ID_CANAL,
				FilterOperator.EQUAL, idChannel);
		Query query = new Query(ENTIDAD, kparent).setFilter(f1);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entityToInvitacion(entity);
	}

	
	public int getCountInvitations(String ownerChannel, String userNameInvitado) {

		if (ownerChannel == null || userNameInvitado == null)
			return 0;

		Key kparent = KeyFactory
				.createKey(UsuarioGCS.ENTIDAD, userNameInvitado);
		Filter f1 = new FilterPredicate(PROPIEDAD_OWNER_CHANNEL,
				FilterOperator.EQUAL, ownerChannel);
		Query query = new Query(ENTIDAD, kparent).setFilter(f1);
		int countEntity = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return countEntity;
	}

	public List<Invitacion> getInvitados(String ownerChannel) {
		Filter f1 = new FilterPredicate(PROPIEDAD_OWNER_CHANNEL,
				FilterOperator.EQUAL, ownerChannel);
		Query query = new Query(ENTIDAD).setFilter(f1);
		List<Entity> lstEntity = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		List<Invitacion> lstInvitacions = null;
		if (lstEntity != null && !lstEntity.isEmpty()) {
			lstInvitacions = new ArrayList<Invitacion>();
			for (int i = 0; i < lstEntity.size(); i++) {
				lstInvitacions.add(entityToInvitacion(lstEntity.get(i)));
			}
		}
		return lstInvitacions;
	}

	public List<Invitacion> getInvitados(String ownerChannel, String idCanal) {
		Filter f1 = new FilterPredicate(PROPIEDAD_OWNER_CHANNEL,
				FilterOperator.EQUAL, ownerChannel);
		Filter f2 = new FilterPredicate(PROPIEDAD_ID_CANAL,
				FilterOperator.EQUAL, idCanal);
		Filter f3 = CompositeFilterOperator.and(f1, f2);
		Query query = new Query(ENTIDAD).setFilter(f3);
		List<Entity> lstEntity = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		List<Invitacion> lstInvitacions = null;
		if (lstEntity != null && !lstEntity.isEmpty()) {
			lstInvitacions = new ArrayList<Invitacion>();
			for (int i = 0; i < lstEntity.size(); i++) {
				lstInvitacions.add(entityToInvitacion(lstEntity.get(i)));
			}
		}
		return lstInvitacions;
	}

	public List<Entity> getEntitys(String ownerChannel, String idChannel) {
		Filter f1 = new FilterPredicate(PROPIEDAD_OWNER_CHANNEL,
				FilterOperator.EQUAL, ownerChannel);
		Filter f2 = new FilterPredicate(PROPIEDAD_ID_CANAL,
				FilterOperator.EQUAL, idChannel);
		Filter f3 = CompositeFilterOperator.and(f1, f2);
		Query query = new Query(ENTIDAD).setFilter(f3);
		query.setKeysOnly();
		List<Entity> lstEntity = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		return lstEntity;
	}

	public String getInvitadosAsString(String ownerChannel, String idChannel) {
		List<Invitacion> invitados = getInvitados(ownerChannel, idChannel);
		String invs = "";
		if (invitados != null && !invitados.isEmpty()) {
			for (int i = 0; i < invitados.size(); i++) {
				invs += invitados.get(i).getUserInvitado() + ",";
			}
		}
		return invs;
	}

	public boolean canPost(String idChannel, String userNameInvitado) {
		return this.get(idChannel, userNameInvitado, true) == null ? false : true;
	}

	public boolean accessAllow(String idChannel, String userNameInvitado, String ownerChannel) {
		boolean response = get(ownerChannel, userNameInvitado) == null ? false
				: true;
		if (!response) {
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			String emailInvited = usuarioGCS.getEmailFromUser(userNameInvitado);
			response = get(ownerChannel, emailInvited) == null ? false : true;
		}
		return response;
	}
	
	public boolean accessAllow(String idChannel, String userNameInvitado) {
		boolean response = getInvitation(idChannel, userNameInvitado) == null ? false
				: true;
		if (!response) {
			UsuarioGCS usuarioGCS = new UsuarioGCS();
			String emailInvited = usuarioGCS.getEmailFromUser(userNameInvitado);
			response = getInvitation(idChannel, emailInvited) == null ? false : true;
		}
		return response;
	}
	
	public void saveInvitados(String idChannel, String ownerChannel,
			String invitados, boolean canPost) {
		if (invitados == null || invitados.trim().equals(""))
			return;
		invitados = invitados.trim();
		String[] arregloInvitados = invitados.split(",");
		for (int i = 0; i < arregloInvitados.length; i++) {
			InvitacionGCS gcs = new InvitacionGCS();
			gcs.crear(idChannel, ownerChannel, arregloInvitados[i], canPost);
		}
	}

	public void delete(long idInvitation, String userName) {
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userName);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idInvitation);
		datastore.delete(k);
	}

	public void delete(String ownerChannel, String idCanal) {
		List<Entity> lstEntitys = getEntitys(ownerChannel, idCanal);
		if (lstEntitys != null && !lstEntitys.isEmpty()) {
			for (Entity entity : lstEntitys) {
				datastore.delete(entity.getKey());
			}
		}
	}

	private Invitacion entityToInvitacion(Entity entity) {
		Invitacion invitacion = null;
		if (entity != null) {
			invitacion = new Invitacion();
			invitacion.setAllowPost((Boolean) entity
					.getProperty(PROPIEDAD_ALLOW_POST));
			invitacion.setUserInvitado((String) entity.getParent().getName());
			invitacion
					.setIdCanal((String) entity.getProperty(PROPIEDAD_ID_CANAL));
			invitacion.setOwnerChannel((String) entity
					.getProperty(PROPIEDAD_OWNER_CHANNEL));
			invitacion.setDateInvitation((Long) entity
					.getProperty(PROPIEDAD_DATE));
			invitacion.setId(entity.getKey().getId());
		}
		return invitacion;
	}
}
