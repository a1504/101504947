package com.roleandjoin.gcs;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.roleandjoin.gcs.entidad.FileAccess;

public class FileAccessGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "FileAccess";
	public static final String PROPIEDAD_FILE_KEY = "fileKey";
	public static final String PROPIEDAD_DATE_REQUEST = "dateRequest";

	public FileAccessGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public long crear(long imageKey) {
		Entity entity = new Entity(ENTIDAD);
		entity.setProperty(PROPIEDAD_FILE_KEY, imageKey);
		entity.setProperty(PROPIEDAD_DATE_REQUEST, new Date());
		Key k = datastore.put(entity);
		return k == null ? 0 : k.getId();
	}

	public FileAccess get(long idKey) {
		Key k = KeyFactory.createKey(ENTIDAD, idKey);
		FileAccess file = null;
		try {
			Entity e = datastore.get(k);
			file = entityToFileAccess(e);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public void delete(long idKey){
		Key k = KeyFactory.createKey(ENTIDAD, idKey);
		datastore.delete(k);
	}

	private FileAccess entityToFileAccess(Entity entity) {
		FileAccess file = null;
		if (entity != null) {
			file = new FileAccess();
			file.setDateRequest((Date) entity.getProperty(PROPIEDAD_DATE_REQUEST));
			file.setFileKey((Long) entity.getProperty(PROPIEDAD_FILE_KEY));
		}
		return file;
	}
}
