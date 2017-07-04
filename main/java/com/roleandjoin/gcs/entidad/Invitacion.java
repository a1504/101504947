package com.roleandjoin.gcs.entidad;

public class Invitacion {
	long id;
	String userInvitado;
	boolean allowPost;
	String ownerChannel;
	String idCanal;
	long dateInvitation;
	
	public Invitacion() {
		super();
	}
	public String getUserInvitado() {
		return userInvitado;
	}
	public void setUserInvitado(String userInvitado) {
		this.userInvitado = userInvitado;
	}
	public boolean isAllowPost() {
		return allowPost;
	}
	public void setAllowPost(boolean allowPost) {
		this.allowPost = allowPost;
	}
	public String getOwnerChannel() {
		return ownerChannel;
	}
	public void setOwnerChannel(String ownerChannel) {
		this.ownerChannel = ownerChannel;
	}
	public String getIdCanal() {
		return idCanal;
	}
	public void setIdCanal(String idCanal) {
		this.idCanal = idCanal;
	}
	public long getDateInvitation() {
		return dateInvitation;
	}
	public void setDateInvitation(long dateInvitation) {
		this.dateInvitation = dateInvitation;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
