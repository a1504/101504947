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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.roleandjoin.gcs.entidad.Canal;
import com.roleandjoin.gcs.entidad.File;
import com.roleandjoin.gcs.entidad.Post;
import com.roleandjoin.gcs.entidad.PostWith;
import com.roleandjoin.utils.TokenIdentifierGenerator;

public class PostGCS {
	DatastoreService datastore;
	public static final String ENTIDAD = "Post";
	public static final String PROPIEDAD_MENSAJE = "mensaje";
	public static final String PROPIEDAD_REMITENTE = "remitente";
	public static final String PROPIEDAD_DATE = "date";
	public static final String PROPIEDAD_HAS_WITH = "hasWith";
	public static final String PROPIEDAD_VIEWS_COUNT = "viewsCount";

	private final int pageSize = 10;

	public PostGCS() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public String crear(String idChannel, Post post, String ownerChannel) {
		String resp = "";
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			boolean hasWith = false;
			String idPost = generateIdPost(idChannel, ownerChannel);
			Text text = new Text(this.cutText(post.getMsg()));
			text = new Text(text.getValue().replaceAll("<", "&lt;"));
			text = new Text(text.getValue().replaceAll(">", "&gt;"));
			int indexOfIni = text.getValue().indexOf("&lt;t&gt;");
			int indexOfEnd = text.getValue().indexOf("&lt;/t&gt;");
			if (indexOfIni ==0 && indexOfEnd != -1 && indexOfEnd<=128) {
				text = new Text(text.getValue().replaceFirst("&lt;t&gt;",
						"<h3>"));
				text = new Text(text.getValue().replaceFirst("&lt;/t&gt;",
						"</h3>"));
			}
			Key kParent = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
			Entity entity = new Entity(ENTIDAD, idPost, kParent);
			entity.setProperty(PROPIEDAD_MENSAJE, text);
			entity.setProperty(PROPIEDAD_REMITENTE, post.getRemitente());
			entity.setProperty(PROPIEDAD_DATE, post.getDate());
			entity.setProperty(PROPIEDAD_VIEWS_COUNT, 0L);
			String with = post.getWith();
			if (with != null && !with.trim().isEmpty()) {
				hasWith = true;
			}
			entity.setProperty(PROPIEDAD_HAS_WITH, hasWith);
			kParent = datastore.put(entity);
			resp = kParent == null ? "" : kParent.getName();
			txn.commit();
			updateBlobEntity(post.getBlobs(), idPost);
			if (!resp.equals("")) {
				CanalGCS canalGCS = new CanalGCS();
				canalGCS.updateDateLastPost(idChannel, ownerChannel);
				ChannelJoinGCS channelJoinGCS = new ChannelJoinGCS();
				channelJoinGCS.updateDateLastPost(idChannel);
				if (hasWith) {
					savePostWith(idChannel, idPost, with + "," + ownerChannel
							+ "," + post.getRemitente());
				}
			}
		} catch (Exception e) {
			resp = "";
			Logger.getLogger(PostGCS.class.getName()).warning(
					"@@@@@Error to create new Post " + e.toString());
		} finally {
			return "";
		}
	}

	private String cutText(String txt) {
		String newMsg = txt;
		if (txt.length() > 2000) {
			newMsg = txt.substring(0, 2000);
		}
		return newMsg;
	}

	private String generateIdPost(String idChannel, String ownerChannel) {
		String idPost = TokenIdentifierGenerator.nextSessionId();
		while (getPostExist(idPost, idChannel, ownerChannel)) {
			idPost = TokenIdentifierGenerator.nextSessionId();
		}
		return idPost;
	}

	// @Deprecated
	// private void savePostWith(long idChannel, long idPost, String with) {
	// with = with.replaceAll("\\s+", ",");
	// String arrWiths[] = with.split(",");
	// PostWithGCS gcs = new PostWithGCS();
	// String wadd = "";
	// for (String w : arrWiths) {
	// // Deberia validar si el usuario tiene acceso a este canal
	// if (!wadd.contains(w + ",")) {
	// gcs.crear(idChannel, idPost, w);
	// wadd += w + ",";
	// }
	// }
	// }

	private void savePostWith(String idChannel, String idPost, String with) {
		with = with.replaceAll("\\s+", ",");
		String arrWiths[] = with.split(",");
		PostWithGCS gcs = new PostWithGCS();
		String wadd = "";
		for (String w : arrWiths) {
			// Deberia validar si el usuario tiene acceso a este canal
			if (!wadd.contains(w + ",")) {
				gcs.crear(idChannel, idPost, w);
				wadd += w + ",";
			}
		}
	}

	// @Deprecated
	// public Post getPostByIdPost(long idPost, long idCanal, String
	// ownerChannel) {
	// Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
	// Key k = KeyFactory.createKey(kp, ENTIDAD, idPost);
	// Entity e = getEntidadByKey(k);
	// Post p = null;
	// if (e != null) {
	// p = entityToPost(e);
	// CanalGCS canalGCS = new CanalGCS();
	// if (ownerChannel != null && !ownerChannel.trim().equals("")) {
	// p = setFileAccessOnPost(p,
	// canalGCS.isPublicChannel(ownerChannel, idCanal), true);
	// }
	// return p;
	// }
	// return null;
	// }

	public Post getPostByIdPost(String idPost, String idCanal,
			String ownerChannel) {
		Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idPost);
		Entity e = getEntidadByKey(k);
		Post p = null;
		if (e != null) {
			p = entityToPost(e);
			CanalGCS canalGCS = new CanalGCS();
			if (ownerChannel != null && !ownerChannel.trim().equals("")) {
				p = setFileAccessOnPost(p,
						canalGCS.isPublicChannel(ownerChannel, idCanal), true);
			}
			return p;
		}
		return null;
	}

	public void addViewsCount(String idPost, String idChannel,
			String ownerChannel) {
		long postViewsCount = 0;
		Entity e = getEntityPost(idPost, idChannel, ownerChannel);
		if (e != null) {
			try {
				postViewsCount = (Long) e.getProperty(PROPIEDAD_VIEWS_COUNT);
				postViewsCount += 1;
			} catch (Exception e2) {
				postViewsCount = 1;
				Logger.getLogger(PostGCS.class.getName())
						.warning(
								"@@@@@" + PostGCS.class.getName() + " "
										+ e2.toString());
			} finally {
				e.setProperty(PROPIEDAD_VIEWS_COUNT, postViewsCount);
				datastore.put(e);
			}
		}
	}

	private Entity getEntityPost(String idPost, String idChannel,
			String ownerChannel) {
		Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idPost);
		Entity e = getEntidadByKey(k);
		return e;
	}

	public boolean getPostExist(String idPost, String idChannel,
			String ownerChannel) {
		Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idPost);
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
			e.printStackTrace();
		} finally {
			return entity;
		}
	}

	// @Deprecated
	// private void updateBlobEntity(long[] blobs, long idPost) {
	// FileGCS gcs = new FileGCS();
	// File file;
	// for (long lng : blobs) {
	// file = gcs.getImagen(lng);
	// gcs.update(file, idPost);
	// }
	// }

	private void updateBlobEntity(long[] blobs, String idPost) {
		FileGCS gcs = new FileGCS();
		File file;
		for (long lng : blobs) {
			file = gcs.getImagen(lng);
			gcs.update(file, idPost);
		}
	}

	public List<Post> getPosts(String idCanal, String owner, String userLogged,
			int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Filter f1 = new FilterPredicate(PROPIEDAD_HAS_WITH,
				FilterOperator.EQUAL, false);
		Key kAncestor = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
		Query query = new Query(ENTIDAD).setAncestor(kAncestor).addSort(
				PROPIEDAD_DATE, SortDirection.DESCENDING);
		query.setFilter(f1);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Post> lstPosts = new ArrayList<Post>();
		FileGCS fileGCS = new FileGCS();
		for (Entity entity : results) {
			CanalGCS canalGCS = new CanalGCS();
			Canal c = canalGCS.getCanalById(owner, idCanal);
			Post post = fileGCS.getAccessFile(entity.getKey().getName(),
					c.isPublic(), true);
			lstPosts.add(entityToPost(entity, post.getBlobs(),
					post.getTypeBlobs(), post.getNameBlobs(), c.getOwnerUser(),
					userLogged));
		}
		return lstPosts;
	}

	public List<Post> getPostWith(String idChannel, String userLogged,
			String ownerChannel, int numPag) {
		PostWithGCS gcs = new PostWithGCS();
		List<PostWith> lst = gcs.get(idChannel, userLogged, numPag);
		List<Post> lstPost = new ArrayList<Post>();
		if (lst != null && !lst.isEmpty()) {
			Post post = null;
			for (PostWith postWith : lst) {
				CanalGCS canalGCS = new CanalGCS();
				Canal c = canalGCS.getCanalById(ownerChannel, idChannel);
				FileGCS fileGCS = new FileGCS();
				Post postBlob = fileGCS.getAccessFile(postWith.getIdPost(),
						c.isPublic(), true);
				post = this.getPostByIdPost(postWith.getIdPost(), idChannel,
						ownerChannel);
				if (post != null) {
					if (post.getRemitente().equals(userLogged)
							|| ownerChannel.equals(userLogged)) {
						post.setCanDelete(true);
					}
					post.setBlobs(postBlob.getBlobs());
					post.setTypeBlobs(postBlob.getTypeBlobs());
					post.setNameBlobs(postBlob.getNameBlobs());
					lstPost.add(post);
				}
			}
		}
		return lstPost;

	}

	// @Deprecated
	// public void delete(long idPost, long idCanal) {
	// FileGCS fileGCS = new FileGCS();
	// fileGCS.deleteAll(idPost);
	// Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
	// Key k = KeyFactory.createKey(kp, ENTIDAD, idPost);
	// datastore.delete(k);
	// }

	public void delete(String idPost, String idChannel) {
		FileGCS fileGCS = new FileGCS();
		fileGCS.deleteAll(idPost);
		Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key k = KeyFactory.createKey(kp, ENTIDAD, idPost);
		datastore.delete(k);
	}

	// @Deprecated
	// public void get(long idPost) {
	// Key k = KeyFactory.createKey(ENTIDAD, idPost);
	// Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
	// FilterOperator.EQUAL, k);
	// Query q = new Query(ENTIDAD).setFilter(keyFilter);
	// Entity e = datastore.prepare(q).asSingleEntity();
	// System.out.println();
	// }

	public void get(String idPost) {
		Key k = KeyFactory.createKey(ENTIDAD, idPost);
		Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
				FilterOperator.EQUAL, k);
		Query q = new Query(ENTIDAD).setFilter(keyFilter);
		Entity e = datastore.prepare(q).asSingleEntity();
		System.out.println();
	}

	public Post getLastPost(String idChannel, boolean channelIsPublic) {
		Key k = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Filter f1 = new FilterPredicate(PROPIEDAD_HAS_WITH,
				FilterOperator.EQUAL, false);
		Query query = new Query(ENTIDAD).setAncestor(k)
				.addSort(PROPIEDAD_DATE, SortDirection.DESCENDING)
				.setFilter(f1);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(1).offset(0));
		Post post = null;
		if (results != null && !results.isEmpty()) {
			post = new Post();
			Entity e = results.get(0);
			Text msg = (Text) e.getProperty(PROPIEDAD_MENSAJE);
			post.setMsgHtml(msg.getValue());
			post.setIdPost(e.getKey().getName());
			post.setRemitente((String) e.getProperty(PROPIEDAD_REMITENTE));
			setFileAccessOnPost(post, channelIsPublic, false);
		}
		return post;
	}

	public int getPostCount(String idCanal) {
		Filter f1 = new FilterPredicate(PROPIEDAD_HAS_WITH,
				FilterOperator.EQUAL, false);
		Key k = KeyFactory.createKey(CanalGCS.ENTIDAD, idCanal);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(f1);
		int count = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		return count;
	}

	public int getPostWithCount(String idChannel, String userLogged) {
		PostWithGCS postWithGCS = new PostWithGCS();
		return postWithGCS.countPostWith(idChannel, userLogged);
	}

	private Post setFileAccessOnPost(Post post, boolean channelIsPublic,
			boolean getAll) {
		FileGCS fileGCS = new FileGCS();
		Post pst = fileGCS.getAccessFile(post.getIdPost(), channelIsPublic,
				getAll);
		post.setBlobs(pst.getBlobs());
		post.setTypeBlobs(pst.getTypeBlobs());
		post.setNameBlobs(pst.getNameBlobs());
		post.setBlobsCount(pst.getBlobsCount());
		return post;
	}

	private Post entityToPost(Entity entity) {
		Post post = new Post();
		post.setMsgHtml(((Text) entity.getProperty(PROPIEDAD_MENSAJE))
				.getValue());
		post.setRemitente((String) entity.getProperty(PROPIEDAD_REMITENTE));
		post.setIdPost(entity.getKey().getName());
		post.setDate((Date) entity.getProperty(PROPIEDAD_DATE));
		return post;
	}

	private Post entityToPost(Entity entity, long[] blobs) {
		Post post = new Post();
		post.setMsgHtml(((Text) entity.getProperty(PROPIEDAD_MENSAJE))
				.getValue());
		post.setRemitente((String) entity.getProperty(PROPIEDAD_REMITENTE));
		post.setIdPost(entity.getKey().getName());
		post.setDate((Date) entity.getProperty(PROPIEDAD_DATE));
		post.setBlobs(blobs);
		return post;
	}

	private Post entityToPost(Entity entity, long[] blobs, String[] typeBlobs,
			String[] nameBlobs, String userOwnerChannel, String userLogged) {
		boolean canDelete = false;
		Post post = new Post();
		Text msg = (Text) entity.getProperty(PROPIEDAD_MENSAJE);
		post.setMsgHtml(msg.getValue());
		post.setRemitente((String) entity.getProperty(PROPIEDAD_REMITENTE));
		post.setIdPost(entity.getKey().getName());
		post.setDate((Date) entity.getProperty(PROPIEDAD_DATE));
		post.setBlobs(blobs);
		post.setTypeBlobs(typeBlobs);
		post.setNameBlobs(nameBlobs);
		if (userLogged != null && userLogged.equals(userOwnerChannel)
				|| post.getRemitente().equals(userLogged)) {
			canDelete = true;
		}
		post.setCanDelete(canDelete);
		ComentarioGCS comentarioGCS = new ComentarioGCS();
		post.setCommentCount(comentarioGCS.getCommentsCount(post.getIdPost()));
		try {
			Object hasWith = entity.getProperty(PROPIEDAD_HAS_WITH);
			Logger.getLogger("").warning(
					"@@@@@PostGCS.entityToPost.hasWith " + hasWith);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return post;
	}

	public List<Entity> getAll() {
		Query q = new Query(ENTIDAD).addSort(PROPIEDAD_DATE,
				SortDirection.DESCENDING);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		List<Post> lst = new ArrayList<Post>();
		Post p = null;
		for (Entity entity : results) {
			p = entityToPost(entity);
			lst.add(p);
		}
		return results;
	}

	public List<Post> getPaginado2(int numPag, int pageSize) {
		numPag = ((numPag - 1) * pageSize);
		Query query = new Query(ENTIDAD).addSort(PROPIEDAD_DATE,
				SortDirection.DESCENDING);
		int c = datastore.prepare(query).countEntities(
				FetchOptions.Builder.withDefaults());
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Post> lst = new ArrayList<Post>();
		Post p = null;
		for (Entity entity : results) {
			p = entityToPost(entity);
			lst.add(p);
		}
		return lst;
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

	private Filter subFilters(long[] idsPost, String idChannel) {
		Filter[] arrFilter = new Filter[idsPost.length];
		Key kp = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Key k = null;
		Filter filter = null;

		int count = 0;
		for (long l : idsPost) {
			k = KeyFactory.createKey(kp, ENTIDAD, l);
			filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
					FilterOperator.EQUAL, k);
			arrFilter[count] = filter;
			count++;
		}
		Filter f3 = CompositeFilterOperator.and(arrFilter);
		return f3;
	}

	public void getPostsTest(String idChannel, boolean has) {
		Filter f1 = new FilterPredicate(PROPIEDAD_HAS_WITH,
				FilterOperator.EQUAL, has);
		Key kAncestor = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Query query = new Query(ENTIDAD).setAncestor(kAncestor).addSort(
				PROPIEDAD_DATE, SortDirection.DESCENDING);
		query.setFilter(f1);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		System.out.println(results);
	}

	public void getPostsTestNull(String idChannel) {
		Filter f1 = new FilterPredicate(PROPIEDAD_HAS_WITH,
				FilterOperator.EQUAL, null);
		Key kAncestor = KeyFactory.createKey(CanalGCS.ENTIDAD, idChannel);
		Query query = new Query(ENTIDAD).setAncestor(kAncestor).addSort(
				PROPIEDAD_DATE, SortDirection.DESCENDING);
		query.setFilter(f1);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		System.out.println(results);
	}
}
