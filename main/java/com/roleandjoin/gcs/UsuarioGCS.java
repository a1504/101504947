package com.roleandjoin.gcs;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.roleandjoin.gcs.entidad.Usuario;
import com.roleandjoin.utils.TokenIdentifierGenerator;

public class UsuarioGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "Usuario";
	public static final String PROPIEDAD_NOMBRE = "nombre";
	public static final String PROPIEDAD_APELLIDO = "apellido";
	public static final String PROPIEDAD_PWD = "pwd";
	public static final String PROPIEDAD_EMAIL = "email";
	public static final String PROPIEDAD_EMAIL_VALIDATED = "emailValidated";
	public static final String PROPIEDAD_TOKEN_VALIDATED = "tokenValidated";
	public static final String PROPIEDAD_ACCOUNT_ACTIVATED = "accountActivated";
	public static final String PROPIEDAD_ACCOUNT_DELETE = "accountDelete";

	private static final int USERS_ALLOWED = 100;

	public UsuarioGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public boolean crear(Usuario usuario) {
		boolean wasCreate = false;
		if (userExists(usuario.getUserName()))
			return false;

		try {
			String tokenValidated = TokenIdentifierGenerator.nextSessionId();
			Entity entity = new Entity(ENTIDAD, usuario.getUserName());
			setEntityProperties(entity, usuario);
			entity.setProperty(PROPIEDAD_EMAIL_VALIDATED, false);
			entity.setProperty(PROPIEDAD_TOKEN_VALIDATED, tokenValidated);
			entity.setProperty(PROPIEDAD_ACCOUNT_ACTIVATED, false);
			entity.setProperty(PROPIEDAD_ACCOUNT_DELETE, false);
			Key k = datastore.put(entity);
			wasCreate = k == null ? false : true;
		} catch (Exception e) {
			Logger.getLogger("HelloApp").warning("@@@@@Error " + e);
		} finally {
			return wasCreate;
		}
	}

	public int createAndActivatedAccount(Usuario usuario) {
		boolean wasCreate = false;
		if (userExists(usuario.getUserName()))
			return 0;

		int usersCount = getCountUser();
		try {
			String tokenValidated = TokenIdentifierGenerator.nextSessionId();
			Entity entity = new Entity(ENTIDAD, usuario.getUserName());
			setEntityProperties(entity, usuario);
			entity.setProperty(PROPIEDAD_EMAIL_VALIDATED, false);
			entity.setProperty(PROPIEDAD_TOKEN_VALIDATED, tokenValidated);
			entity.setProperty(PROPIEDAD_ACCOUNT_ACTIVATED,
					activatedAccount(usersCount));
			entity.setProperty(PROPIEDAD_ACCOUNT_DELETE, false);
			Key k = datastore.put(entity);
			wasCreate = k == null ? false : true;
		} catch (Exception e) {
			Logger.getLogger("HelloApp").warning("@@@@@Error " + e);
		}
		return wasCreate ? getCountUser() : 0;
	}

	private void setEntityProperties(Entity entity, Usuario user) {
		entity.setProperty(PROPIEDAD_APELLIDO, user.getApellido());
		entity.setProperty(PROPIEDAD_NOMBRE, user.getNombre());
		entity.setProperty(PROPIEDAD_PWD, user.getPwd());
		entity.setProperty(PROPIEDAD_EMAIL, user.getEmail());
	}

	private boolean activatedAccount(int usersCount) {
		if (usersCount <= USERS_ALLOWED) {
			return true;
		}
		return false;
	}

	private boolean activatedAccount() {
		int count = getCountUser();
		if (count <= 100) {
			return true;
		}
		return false;
	}

	private int getCountUser() {
		Query q = new Query(ENTIDAD);
		int coutUsers = datastore.prepare(q).countEntities(
				FetchOptions.Builder.withDefaults());
		return coutUsers;
	}

	public Usuario getByUserName(String userName) {
		Key kParent = KeyFactory.createKey(ENTIDAD, userName);
		Entity entity = this.getEntidadByKey(kParent);
		Usuario usuario = null;
		if (entity != null) {
			usuario = entityToUsuario(entity);
		}
		return usuario;
	}

	public String getEmailFromUser(String userName) {
		Key kParent = KeyFactory.createKey(ENTIDAD, userName);
		Entity entity = this.getEntidadByKey(kParent);
		String email = "";
		if (entity != null) {
			email = getEmailFromEntity(entity);
		}
		return email;
	}

	public void test(String idChannel, long... idPost) {
		Key kp1 = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key k1 = KeyFactory.createKey(kp1, ENTIDAD, idPost[0]);
		Filter f1 = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
				FilterOperator.EQUAL, k1);

		Key kp2 = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key k2 = KeyFactory.createKey(kp2, ENTIDAD, idPost[1]);
		Filter f2 = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
				FilterOperator.EQUAL, k2);

		Filter f3 = CompositeFilterOperator.and(f1, f2);

		// Query q = new Query(ENTIDAD).setFilter(f3);
		Query q = new Query(ENTIDAD).setFilter(f3);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity e : pq.asIterable()) {
			Logger.getLogger("").warning("@@@@@test.e " + e);
		}
		Logger.getLogger("").warning("@@@@@test.e end");
		// List<Entity> e =
		// datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

	}

	public void updateEmailValidated(String tokenValidated) {
		Filter f1 = new FilterPredicate(PROPIEDAD_TOKEN_VALIDATED,
				FilterOperator.EQUAL, tokenValidated);
		Query q = new Query(ENTIDAD).setFilter(f1);
		PreparedQuery pq = datastore.prepare(q);
		Entity e = pq.asSingleEntity();
		if (e != null) {
			e.setProperty(PROPIEDAD_EMAIL_VALIDATED, true);
			datastore.put(e);
		}
	}

	public Usuario getUsuario(String userName, String pwd) {
		Usuario usuario = null;
		Entity entity = getEntityWithActivateAccount(userName);
		if (entity != null) {
			usuario = entityToUsuario(entity);
			if (!usuario.getPwd().equals(pwd)) {
				usuario = null;
			}
		}

		return usuario;
	}

	private Entity getEntityWithActivateAccount(String userName) {
		Filter f1 = new FilterPredicate(PROPIEDAD_ACCOUNT_ACTIVATED,
				FilterOperator.EQUAL, true);
		Key key = KeyFactory.createKey(ENTIDAD, userName);
		Query query = new Query(ENTIDAD).setAncestor(key).setFilter(f1);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entity;
	}

	public boolean userExists(String userName) {
		Key k = KeyFactory.createKey(ENTIDAD, userName);
		return getEntidadByKey(k) == null ? false : true;
	}

	public boolean accountIsActivate(String userName) {
		Entity e = getEntityWithActivateAccount(userName);
		if (e != null)
			return true;
		return false;
	}

	public void activateAccount(String userName) {
		Key kParent = KeyFactory.createKey(ENTIDAD, userName);
		Entity entity = this.getEntidadByKey(kParent);
		entity.setProperty(PROPIEDAD_ACCOUNT_ACTIVATED, true);
		datastore.put(entity);
	}

	public void updateUser(String userName, Usuario user) {
		Key kParent = KeyFactory.createKey(ENTIDAD, userName);
		Entity entity = this.getEntidadByKey(kParent);
		setEntityProperties(entity, user);
		datastore.put(entity);
	}

	public boolean deleteUser(String userName) {
		Key kParent = KeyFactory.createKey(ENTIDAD, userName);
		Entity entity = this.getEntidadByKey(kParent);
		if (entity != null) {
			entity.setProperty(PROPIEDAD_ACCOUNT_DELETE, true);
			entity.setProperty(PROPIEDAD_ACCOUNT_ACTIVATED, false);
			datastore.put(entity);
			return true;
		}
		return false;
	}

	private Usuario entityToUsuario(Entity entity) {
		Usuario usuario = new Usuario();
		usuario.setApellido((String) entity.getProperty(PROPIEDAD_APELLIDO));
		usuario.setNombre((String) entity.getProperty(PROPIEDAD_NOMBRE));
		usuario.setPwd((String) entity.getProperty(PROPIEDAD_PWD));
		usuario.setUserName(entity.getKey().getName());
		return usuario;
	}

	private String getEmailFromEntity(Entity entity) {
		String email = "";
		if (entity != null) {
			email = (String) entity.getProperty(PROPIEDAD_EMAIL);
		}
		return email;
	}

	private Entity getEntidadByKey(Key key) {
		Entity entity = null;
		try {
			entity = datastore.get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		} finally {
			return entity;
		}
	}

	public String getCanales() {

		Query query = new Query(ENTIDAD);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		String result = "";

		for (Entity entity : results) {

			result += "\n User: " + entity.getKey().getName();
			result += "\n Nombre: " + entity.getProperty(PROPIEDAD_NOMBRE);
			result += "\n Apellido: " + entity.getProperty(PROPIEDAD_APELLIDO);
			result += "\n pwd: " + entity.getProperty(PROPIEDAD_PWD);
			result += "\n -------------------------------------------";

		}

		return result;
	}

	public String getUserNameByEmail(String email) {
		Filter f1 = new FilterPredicate(PROPIEDAD_EMAIL, FilterOperator.EQUAL,
				email);
		Query query = new Query(ENTIDAD).setFilter(f1);
		Entity entity = datastore.prepare(query).asSingleEntity();
		if (entity == null)
			return null;

		return entity.getKey().getName();
	}

	public boolean emailExists(String email) {
		Filter f1 = new FilterPredicate(PROPIEDAD_EMAIL, FilterOperator.EQUAL,
				email);
		Query query = new Query(ENTIDAD).setFilter(f1);
		Entity entity = datastore.prepare(query).asSingleEntity();
		if (entity == null)
			return false;

		return true;
	}

}
