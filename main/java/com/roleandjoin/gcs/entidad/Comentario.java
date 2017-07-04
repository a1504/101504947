package com.roleandjoin.gcs.entidad;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.roleandjoin.utils.ValidadorDeCadenas;

@XmlRootElement
public class Comentario {

	String id;
	String remitente;
	String comentario;
	Date date;
	boolean canDelete;
	
	public Comentario() {
		// TODO Auto-generated constructor stub
	}
	
	public Comentario(String remitente, String comentario) {
		super();
		this.remitente = remitente;
		this.comentario =  ValidadorDeCadenas.textToHtml(comentario);
	}

	public String getRemitente() {
		return remitente;
	}
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = ValidadorDeCadenas.textToHtml(comentario);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	
	
}
