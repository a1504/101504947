package com.roleandjoin.gcs.entidad;

import java.util.Date;

public class Token {
		
	String token;
	Date date;
	
	public Token() {
		super();
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
