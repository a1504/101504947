package com.roleandjoin.gcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.roleandjoin.gcs.entidad.Contact;
import com.roleandjoin.gcs.entidad.Join;
import com.roleandjoin.utils.ValidadorDeCadenas;

public class ContactGCS extends AbstractGCS {

	public static final String ENTIDAD = "Contact";
	public static final String PROPIEDAD_CONTACT = "contact";
	public static final String PROPIEDAD_DATE = "date";
	private final int pageSize = 10;

	public ContactGCS() {
		super();
	}

	public long create(String userLogged, Contact contact) {
		contact.setContact(ValidadorDeCadenas.addArrobaNombreUsuario(contact
				.getContact()));
		
		Contact contact2 = searchContact(userLogged, contact.getContact());
		
		if(contact2!=null)
			return 0;
		
		Key kp = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Entity entity = new Entity(ENTIDAD, kp);
		entity.setProperty(PROPIEDAD_CONTACT, contact.getContact());
		entity.setProperty(PROPIEDAD_DATE, (new Date()).getTime());
		Key kr = datastore.put(entity);
		return kr != null ? kr.getId() : 0;
	}

	public List<Contact> getContacts(String userLogged, int numPag) {
		numPag = ((numPag - 1) * pageSize);
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Query query = new Query(ENTIDAD, kancestor).addSort(PROPIEDAD_DATE,
				SortDirection.DESCENDING);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(pageSize).offset(numPag));
		List<Contact> lstContact = new ArrayList<Contact>();
		for (Entity entity : results) {
			lstContact.add(entityToContact(entity));
		}
		return lstContact;
	}

	public List<Entity> getAllContacts(String userLogged) {
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Query query = new Query(ENTIDAD, kancestor);
		List<Entity> results = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		return results;
	}

	public void delete(String userLogged, long idContact) {
		Key kancestor = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Key k = KeyFactory.createKey(kancestor, ENTIDAD, idContact);
		datastore.delete(k);
	}

	public Contact searchContact(String userLogged, String strSearch) {
		strSearch = ValidadorDeCadenas.addArrobaNombreUsuario(strSearch);
		Filter f1 = new FilterPredicate(PROPIEDAD_CONTACT,
				FilterOperator.EQUAL, strSearch);
		Key k = KeyFactory.createKey(UsuarioGCS.ENTIDAD, userLogged);
		Query query = new Query(ENTIDAD).setAncestor(k).setFilter(f1);
		Entity results = datastore.prepare(query).asSingleEntity();
		Contact contact = entityToContact(results);
		if(contact!=null){
			contact.setDate(null);
			contact.setId(0);
		}
		return contact;
	}

	public List<Contact> searchContacts(String userLogged, String strSearch) {
		strSearch = ValidadorDeCadenas.addArrobaNombreUsuario(strSearch);
		List<Entity> lstEntities = getAllContacts(userLogged);
		List<Contact> lstContact = new ArrayList<Contact>();
		String contact = "";
		for (Entity entity : lstEntities) {
			contact = (String) entity.getProperty(PROPIEDAD_CONTACT);
			if (contact.startsWith(strSearch)) {
				lstContact.add(entityToContact(entity));
			}
		}
		return lstContact;
	}

	private Contact entityToContact(Entity entity) {
		Contact contact = null;
		if (entity != null) {
			contact = new Contact();
			contact.setContact((String) entity.getProperty(PROPIEDAD_CONTACT));
			contact.setId(entity.getKey().getId());
		}
		return contact;
	}

}
