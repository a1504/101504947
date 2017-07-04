package com.roleandjoin.gcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.Comentario;
import com.roleandjoin.utils.TokenIdentifierGenerator;
import com.roleandjoin.utils.ValidadorDeCadenas;

public class ComentarioGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "Cometario";
	public static final String PROPIEDAD_COMENTARIO = "coment";
	public static final String PROPIEDAD_REMITENTE = "remit";
	public static final String PROPIEDAD_DATE = "date";
	private final int pageSize=10;

	public ComentarioGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

//	@Deprecated
//	public long crear(long idPost, Comentario comentario) {
//		Key kParent = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
//		Entity entity = new Entity(ENTIDAD, kParent);
//		entity.setProperty(PROPIEDAD_COMENTARIO, comentario.getComentario());
//		entity.setProperty(PROPIEDAD_REMITENTE, comentario.getRemitente());
//		entity.setProperty(PROPIEDAD_DATE, new Date());
//		kParent = datastore.put(entity);
//		return kParent == null ? 0 : kParent.getId();
//	}
	
	public String crear(String idPost, Comentario comentario) {
		String idComment = generateIdChannel(idPost);
		Key kParent = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Entity entity = new Entity(ENTIDAD, idComment,kParent);
		entity.setProperty(PROPIEDAD_COMENTARIO, comentario.getComentario());
		entity.setProperty(PROPIEDAD_REMITENTE, comentario.getRemitente());
		entity.setProperty(PROPIEDAD_DATE, new Date());
		kParent = datastore.put(entity);
		return kParent == null ? "" : kParent.getName();
	}
	
	private String generateIdChannel(String idPost) {
		String idComment = TokenIdentifierGenerator.nextSessionId();
		while (getChannelExist(idPost, idComment)) {
			idComment = TokenIdentifierGenerator.nextSessionId();
		}
		return idComment;
	}

	public boolean getChannelExist(String idPost, String idComment) {
		Key kParent = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Key k = KeyFactory.createKey(kParent, ENTIDAD, idComment);
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

//	@Deprecated
//	public List<Comentario> getComentarios(long idPost, String userLogged, int numPag) {
//		numPag = ((numPag-1)*pageSize);
//		Key k = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
//		Query query = new Query(ENTIDAD).setAncestor(k).addSort(PROPIEDAD_DATE,
//				SortDirection.ASCENDING);
//	
//		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(pageSize).offset(numPag));
//		List<Comentario> lstComentarios = new ArrayList<Comentario>();
//		for (Entity entity : results) {
//			lstComentarios.add(entityTocomentario(entity,userLogged));
//		}
//		return lstComentarios;
//	}
	
	public List<Comentario> getComentarios(String idPost, String userLogged, int numPag) {
		numPag = ((numPag-1)*pageSize);
		Key k = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query query = new Query(ENTIDAD).setAncestor(k).addSort(PROPIEDAD_DATE,
				SortDirection.ASCENDING);
	
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Comentario> lstComentarios = new ArrayList<Comentario>();
		for (Entity entity : results) {
			lstComentarios.add(entityTocomentario(entity,userLogged));
		}
		return lstComentarios;
	}

//	@Deprecated
//	public void delete(long idPost, long idComment) {
//		Key kp = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
//		Key k = KeyFactory.createKey(kp, ENTIDAD, idComment);
//		datastore.delete(k);
//	}
	
	public void delete(String idPost, String idComment) {
		Key kp = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idComment);
		datastore.delete(k);
	}

//	@Deprecated
//	public Comentario get(long idPost, long idComment) {
//		Comentario comentario = null;
//		Key kp = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
//		Key k = KeyFactory.createKey(kp, ENTIDAD, idComment);
//		try {
//			Entity e = datastore.get(k);
//			comentario = entityTocomentario(e,"");
//			return comentario;
//		} catch (EntityNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public Comentario get(String idPost, String idComment) {
		Comentario comentario = null;
		Key kp = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idComment);
		try {
			Entity e = datastore.get(k);
			comentario = entityTocomentario(e,"");
			return comentario;
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	@Deprecated
//	public int getCommentsCount(long idPost){
//		Key kp = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
//		Query q = new Query(ENTIDAD).setAncestor(kp);
//		return datastore.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
//	}
	
	public int getCommentsCount(String idPost){
		Key kp = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query q = new Query(ENTIDAD).setAncestor(kp);
		return datastore.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
	}

	private Comentario entityTocomentario(Entity entity, String userLogged) {
		Comentario comentario = new Comentario();
		String str_comentario = (String) entity
				.getProperty(PROPIEDAD_COMENTARIO);
		comentario.setComentario(str_comentario);
		comentario.setRemitente((String) entity
				.getProperty(PROPIEDAD_REMITENTE));
		comentario.setId(entity.getKey().getName());
		comentario.setCanDelete(false);
		if (comentario.getRemitente().equals(userLogged))
			comentario.setCanDelete(true);
		return comentario;
	}

}
