package com.roleandjoin.gcs.entidad;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Canal {
	
	String idCanal;
	String nombre;
	String descrip;
	boolean isPublic;
	boolean owner;
	String invitados;
	int whoPost;
	boolean canPost;
	String ownerUser;
	int postCount;
	Post lastPost;
	boolean join=false;
	int postWithCount=0;
	int joiners=0;
	boolean close;
	boolean delete=false;
	
	public Canal() {
		// TODO Auto-generated constructor stub
	}
	
	public Canal(String idCanal, String nombre, String descrip, boolean isPublic,
			boolean owner) {
		super();
		this.idCanal = idCanal;
		this.nombre = nombre;
		this.descrip = descrip;
		this.isPublic = isPublic;
		this.owner = owner;
	}

	public Canal(String idCanal, String nombre, String descrip, boolean isPublic) {
		super();
		this.idCanal = idCanal;
		this.nombre = nombre;
		this.descrip = descrip;
		this.isPublic = isPublic;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescrip() {
		return descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}


	

	public String getIdCanal() {
		return idCanal;
	}

	public void setIdCanal(String idCanal) {
		this.idCanal = idCanal;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public int getWhoPost() {
		return whoPost;
	}

	public void setWhoPost(int whoPost) {
		this.whoPost = whoPost;
	}

	public String getInvitados() {
		return invitados;
	}

	public void setInvitados(String invitados) {
		this.invitados = invitados;
	}

	public boolean isCanPost() {
		return canPost;
	}

	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}

	public String getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}

	public int getPostCount() {
		return postCount;
	}

	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}

	public Post getLastPost() {
		return lastPost;
	}

	public void setLastPost(Post lastPost) {
		this.lastPost = lastPost;
	}

	public boolean isJoin() {
		return join;
	}

	public void setJoin(boolean join) {
		this.join = join;
	}

	public int getPostWithCount() {
		return postWithCount;
	}

	public void setPostWithCount(int postWithCount) {
		this.postWithCount = postWithCount;
	}

	public int getJoiners() {
		return joiners;
	}

	public void setJoiners(int joiners) {
		this.joiners = joiners;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	
	
}
