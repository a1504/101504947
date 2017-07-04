package com.roleandjoin.gcs.entidad;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Usuario {

	String userName;
	String nombre;
	String apellido;
	String pwd;
	String email;
	String lastPwd;
	String newPwd;
	
		
	public Usuario(String userName, String nombre, String apellido, String pwd,
			String email) {
		super();
		this.userName = userName;
		this.nombre = nombre;
		this.apellido = apellido;
		this.pwd = pwd;
		this.email = email;
	}

	public Usuario() {
		super();
	}

	public Usuario(String userName, String nombre, String apellido) {
		super();
		this.userName = userName;
		this.nombre = nombre;
		this.apellido = apellido;
	}
	
	
	public Usuario(String userName, String nombre, String apellido, String pwd) {
		super();
		this.userName = userName;
		this.nombre = nombre;
		this.apellido = apellido;
		this.pwd = pwd;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastPwd() {
		return lastPwd;
	}

	public void setLastPwd(String lastPwd) {
		this.lastPwd = lastPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	
	
}
