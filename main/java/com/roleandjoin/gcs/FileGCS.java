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
import com.google.appengine.api.datastore.Query;
import com.roleandjoin.gcs.entidad.File;
import com.roleandjoin.gcs.entidad.Post;

public class FileGCS {
	DatastoreService datastore;
	public static final String ENTIDAD = "File";
	public static final String PROPIEDAD_BLOB_KEY = "blobKey";
	public static final String PROPIEDAD_SIZE_FILE = "sizeFile";
	public static final String PROPIEDAD_TYEPE_FILE = "typeFile";
	public static final String PROPIEDAD_FILE_NAME = "name";
	public static final String PROPIEDAD_THUMBNAIL_BLOB_KEY = "thumbnailBlobKey";

	public FileGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public long crear(String blobKey, int sizeFile, String typeFile,
			String fileName, String thumbnailBlobKey) {
		long resp = 0;
		try {
			thumbnailBlobKey = thumbnailBlobKey == null ? blobKey
					: thumbnailBlobKey;
			Entity entity = new Entity(ENTIDAD);
			entity.setProperty(PROPIEDAD_BLOB_KEY, blobKey);
			entity.setProperty(PROPIEDAD_SIZE_FILE, sizeFile);
			entity.setProperty(PROPIEDAD_TYEPE_FILE, typeFile);
			entity.setProperty(PROPIEDAD_FILE_NAME, fileName);
			entity.setProperty(PROPIEDAD_THUMBNAIL_BLOB_KEY, thumbnailBlobKey);
			Key kParent = datastore.put(entity);
			resp = kParent == null ? 0 : kParent.getId();
		} catch (Exception e) {
			resp = 0;
			Logger.getLogger("com.hola").warning("@@@@@error " + e.toString());
		} finally {
			return resp;
		}
	}

	public File getImagen(long idImage) {
		Key key = KeyFactory.createKey(ENTIDAD, idImage);
		Entity entity = getEntidadByKey(key);
		return entityToImagen(entity);
	}

	@Deprecated
	private Key[] getKeyByAncestor(long idPost) {
		Key kAncestor = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query query = new Query(kAncestor).setKeysOnly();
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (results == null || results.isEmpty())
			return null;
		Key[] ids = new Key[results.size()];
		int cont = 0;
		for (Entity entity : results) {
			ids[cont] = entity.getKey();
			cont++;
		}
		return ids;
	}

	private Key[] getKeyByAncestor(String idPost) {
		Key kAncestor = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query query = new Query(kAncestor).setKeysOnly();
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (results == null || results.isEmpty())
			return null;
		Key[] ids = new Key[results.size()];
		int cont = 0;
		for (Entity entity : results) {
			ids[cont] = entity.getKey();
			cont++;
		}
		return ids;
	}

	@Deprecated
	public long update(File imagen, long idPost) {
		long resp = 0;
		Key kParent = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Key key = KeyFactory.createKey(kParent, ENTIDAD, imagen.getId());
		Entity entity = new Entity(key);
		entity.setProperty(PROPIEDAD_BLOB_KEY, imagen.getBlobKey());
		entity.setProperty(PROPIEDAD_SIZE_FILE, imagen.getSizeFile());
		entity.setProperty(PROPIEDAD_TYEPE_FILE, imagen.getTypeFile());
		entity.setProperty(PROPIEDAD_FILE_NAME, imagen.getName());
		key = datastore.put(entity);
		resp = key == null ? 0 : key.getId();
		return resp;
	}

	public long update(File imagen, String idPost) {
		long resp = 0;
		Key kParent = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Key key = KeyFactory.createKey(kParent, ENTIDAD, imagen.getId());
		Entity entity = new Entity(key);
		entity.setProperty(PROPIEDAD_BLOB_KEY, imagen.getBlobKey());
		entity.setProperty(PROPIEDAD_SIZE_FILE, imagen.getSizeFile());
		entity.setProperty(PROPIEDAD_TYEPE_FILE, imagen.getTypeFile());
		entity.setProperty(PROPIEDAD_FILE_NAME, imagen.getName());
		key = datastore.put(entity);
		resp = key == null ? 0 : key.getId();
		return resp;
	}

	@Deprecated
	public void deleteAll(long idPost) {
		Key[] ks = getKeyByAncestor(idPost);
		if (ks == null)
			return;
		for (int i = 0; i < ks.length; i++) {
			delete(ks[i].getId());
		}
	}

	public void deleteAll(String idPost) {
		Key[] ks = getKeyByAncestor(idPost);
		if (ks == null)
			return;
		for (int i = 0; i < ks.length; i++) {
			delete(ks[i].getId());
		}
	}

	public void delete(long idKey) {
		Key k = KeyFactory.createKey(ENTIDAD, idKey);
		datastore.delete(k);
	}

	private File entityToImagen(Entity entity) {
		File file = null;
		if (entity != null) {
			file = new File();
			file.setBlobKey((String) entity.getProperty(PROPIEDAD_BLOB_KEY));
			file.setSizeFile((Long) entity.getProperty(PROPIEDAD_SIZE_FILE));
			file.setTypeFile((String) entity.getProperty(PROPIEDAD_TYEPE_FILE));
			file.setName((String) entity.getProperty(PROPIEDAD_FILE_NAME));
			file.setThumbBlobKey((String) entity
					.getProperty(PROPIEDAD_THUMBNAIL_BLOB_KEY));
			file.setId(entity.getKey().getId());
		}
		return file;
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

	public Post getAccessFile(String idPost, boolean isPublicChannel,
			boolean getAll) {
		Key k = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query query = new Query(ENTIDAD).setAncestor(k);
		List<Entity> results = null;
		if (getAll) {
			results = datastore.prepare(query).asList(
					FetchOptions.Builder.withDefaults());
		} else {
			results = datastore.prepare(query).asList(
					FetchOptions.Builder.withLimit(1).offset(0));
		}
		long[] blobs = new long[results.size()];
		String[] typeBlobs = new String[results.size()];
		String[] nameBlobs = new String[results.size()];
		FileAccessGCS accessGCS = new FileAccessGCS();
		for (int i = 0; i < blobs.length; i++) {
			if (isPublicChannel) {
				blobs[i] = results.get(i).getKey().getId();
			} else {
				blobs[i] = accessGCS.crear(results.get(i).getKey().getId());
			}
			typeBlobs[i] = (String) results.get(i).getProperty(
					PROPIEDAD_TYEPE_FILE);

			nameBlobs[i] = (String) results.get(i).getProperty(
					PROPIEDAD_FILE_NAME);
		}
		Post post = new Post();
		post.setBlobs(blobs);
		post.setTypeBlobs(typeBlobs);
		post.setNameBlobs(nameBlobs);
		post.setBlobsCount(this.countFilesInPost(idPost));
		return post;
	}

	@Deprecated
	public int countFilesInPost(long idPost) {
		Key k = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query query = new Query(ENTIDAD).setAncestor(k);
		int results = 0;
		results = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	public int countFilesInPost(String idPost) {
		Key k = KeyFactory.createKey(PostGCS.ENTIDAD, idPost);
		Query query = new Query(ENTIDAD).setAncestor(k);
		int results = 0;
		results = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return results;
	}
}
