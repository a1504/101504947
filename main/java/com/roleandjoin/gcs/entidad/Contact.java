package com.roleandjoin.gcs.entidad;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Contact {
	
	long id;
	String userName;
	String contact;
	String date;
	
	public Contact() {
		super();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
}
