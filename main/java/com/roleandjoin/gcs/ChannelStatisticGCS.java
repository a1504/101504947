package com.roleandjoin.gcs;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.roleandjoin.gcs.entidad.Post;
import com.roleandjoin.utils.DateUtils;

public class ChannelStatisticGCS {

	DatastoreService datastore;
	public static final String ENTIDAD = "ChannelStatistic";
	public static final String PROPIEDAD_IP_ADDRESS = "ipAddress";
	public static final String PROPIEDAD_DATE_CHANNEL_VIEW = "datePostView";

	private final int pageSize = 10;

	public ChannelStatisticGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public void createPostStatistic(String ownerChannel, String idCh,
			String ipAddress) {
		if (addNewPostView(ownerChannel, idCh, ipAddress)) {
			CanalGCS channelGCS = new CanalGCS();
			channelGCS.addViewsCount(idCh, ownerChannel);
		}
	}

	private boolean addNewPostView(String ownerChannel, String idCh,
			String ipAddress) {
		if (canRegisterStatistic(ownerChannel, idCh, ipAddress)) {
			Entity entity = getEntity(ownerChannel, idCh, ipAddress);
			if (entity == null) {
				Key keyUserAncestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD,
						ownerChannel);
				Key kChannelAncestor = KeyFactory.createKey(keyUserAncestor,
						PostGCS.ENTIDAD, idCh);
				Key key = KeyFactory.createKey(kChannelAncestor, ENTIDAD,
						ipAddress);
				entity = new Entity(ENTIDAD, key);
			}
			entity.setProperty(PROPIEDAD_DATE_CHANNEL_VIEW,
					(new Date()).getTime());
			datastore.put(entity);
			return true;
		}
		return false;
	}

	private boolean canRegisterStatistic(String ownerChannel, String idCh, String ipAddress) {
		long lngDateView = getDatePostView(ownerChannel, idCh, ipAddress);
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

	private long getDatePostView(String ownerChannel, String idCh, String ipAddress) {
		Entity entity = getEntity(ownerChannel, idCh, ipAddress);
		if (entity != null) {
			long lngDateView = (Long) entity
					.getProperty(PROPIEDAD_DATE_CHANNEL_VIEW);
			return lngDateView;
		}
		return 0L;
	}

	private Entity getEntity(String ownerChannel, String idCh, String ipAddress) {
		Key keyUserAncestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD,
				ownerChannel);
		Key kChannelAncestor = KeyFactory.createKey(keyUserAncestor,
				PostGCS.ENTIDAD, idCh);
		Key key = KeyFactory.createKey(kChannelAncestor, ENTIDAD, ipAddress);
		Query query = new Query(ENTIDAD).setAncestor(key);
		Entity entity = datastore.prepare(query).asSingleEntity();
		return entity;
	}

}
