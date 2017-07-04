package com.roleandjoin.gcs;

import java.util.Date;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.roleandjoin.utils.DateUtils;

public class PostStatisticGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "PostStatistic";
	public static final String PROPIEDAD_IP_ADDRESS = "ipAddress";
	public static final String PROPIEDAD_DATE_POST_VIEW = "datePostView";

	private final int pageSize = 10;

	public PostStatisticGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public void createPostStatistic(String ownerChannel, String idChannel,
			String idPost, String ipAddress) {
		if (addNewPostView(idChannel, idPost, ipAddress)) {
			PostGCS postGCS = new PostGCS();
			postGCS.addViewsCount(idPost, idChannel, ownerChannel);
		}
	}

	private boolean addNewPostView(String idChannel, String idPost, String ipAddress) {
		if (canRegisterStatistic(idChannel, idPost, ipAddress)) {
			Entity entity = getEntity(idChannel, idPost, ipAddress);
			if (entity == null) {
				Key keyChannelAncestor = KeyFactory.createKey(CanalGCS.ENTIDAD,
						idChannel);
				Key kPostAncestor = KeyFactory.createKey(keyChannelAncestor,
						PostGCS.ENTIDAD, idPost);
				Key key = KeyFactory.createKey(kPostAncestor, ENTIDAD, ipAddress);
				entity = new Entity(ENTIDAD, key);
			}
			entity.setProperty(PROPIEDAD_DATE_POST_VIEW, (new Date()).getTime());
			datastore.put(entity);
			return true;
		}
		return false;
	}

	private boolean canRegisterStatistic(String idCh, String idPost,
			String ipAddress) {
		long lngDateView = getDatePostView(idCh, idPost, ipAddress);
		if (lngDateView > 0) {
			long lngSeconds = DateUtils.getSecondsPassed(new Date(lngDateView));
			if (lngSeconds > 60) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	private long getDatePostView(String idChannel, String idPost, String ipAddress) {
		Entity entity = getEntity(idChannel, idPost, ipAddress);
		if (entity != null) {
			long lngDateView = (Long) entity
					.getProperty(PROPIEDAD_DATE_POST_VIEW);
			return lngDateView;
		}
		return 0L;
	}

	private Entity getEntity(String idChannel, String idPost, String ipAddress) {
		Key keyChannelAncestor = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key kPostAncestor = KeyFactory.createKey(keyChannelAncestor,
				PostGCS.ENTIDAD, idPost);
		Key key = KeyFactory.createKey(kPostAncestor, ENTIDAD, ipAddress);
		Query query = new Query(ENTIDAD).setAncestor(key);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entity;
	}

	

}
