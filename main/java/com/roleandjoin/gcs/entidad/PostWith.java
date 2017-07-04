package com.roleandjoin.gcs.entidad;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PostWith {
	
	long id;
	String idCanal;
	String idPost;
	String with;
	
	public PostWith() {
	
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdCanal() {
		return idCanal;
	}

	public void setIdCanal(String idCanal) {
		this.idCanal = idCanal;
	}

	public String getIdPost() {
		return idPost;
	}

	public void setIdPost(String idPost) {
		this.idPost = idPost;
	}

	public String getWith() {
		return with;
	}

	public void setWith(String with) {
		this.with = with;
	}
	
	
}
