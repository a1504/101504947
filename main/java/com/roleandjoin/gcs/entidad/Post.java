package com.roleandjoin.gcs.entidad;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.appengine.api.datastore.Text;
import com.roleandjoin.utils.ValidadorDeCadenas;

@XmlRootElement
public class Post {

	String idPost;
	String remitente;
	String msg;
	Date date;
	String comentario;
	long[] blobs;
	String[] typeBlobs;
	String[] nameBlobs;
	boolean canDelete;
	int commentCount;
	long datePost;
	int blobsCount=0;
	String with;
	
	public Post(String idPost, String remitente, String msg, Date date) {
		super();
		this.idPost = idPost;
		this.remitente = remitente;
		setMsgHtml(msg);
		this.setDate(date);
	}

	public Post(String idPost, String remitente, String msg, Date date,
			String comentario, long[] blobs,String[] typeBlobs ) {
		super();
		this.idPost = idPost;
		this.remitente = remitente;
		setMsgHtml(msg);
		this.setDate(date);
		this.comentario = comentario;
		this.blobs = blobs;
		this.typeBlobs = typeBlobs;
	}
	
	

	public Post() {
		super();
		this.comentario = "";
	}

	public String getRemitente() {
		return remitente;
	}

	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setMsgHtml(String msg) {
		this.msg = ValidadorDeCadenas.textToHtml(msg);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.datePost = date.getTime();
		this.date = date;
	}

	public String getIdPost() {
		return idPost;
	}

	public void setIdPost(String idPost) {
		this.idPost = idPost;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public long[] getBlobs() {
		return blobs;
	}

	public void setBlobs(long[] blobs) {
		this.blobs = blobs;
	}

	public String[] getTypeBlobs() {
		return typeBlobs;
	}

	public void setTypeBlobs(String[] typeBlobs) {
		this.typeBlobs = typeBlobs;
	}

	public String[] getNameBlobs() {
		return nameBlobs;
	}

	public void setNameBlobs(String[] nameBlobs) {
		this.nameBlobs = nameBlobs;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public long getDatePost() {
		return datePost;
	}

	public void setDatePost(long datePost) {
		this.datePost = datePost;
	}

	public int getBlobsCount() {
		return blobsCount;
	}

	public void setBlobsCount(int blobsCount) {
		this.blobsCount = blobsCount;
	}

	public String getWith() {
		return with;
	}

	public void setWith(String with) {
		this.with = with;
	}
}
