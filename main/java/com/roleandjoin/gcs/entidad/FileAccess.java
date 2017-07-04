package com.roleandjoin.gcs.entidad;

import java.util.Date;

public class FileAccess {
	
	long fileKey;
	Date dateRequest;
	
	public FileAccess() {
		super();
	}
	
	public FileAccess(long fileKey, Date dateRequest) {
		super();
		this.fileKey = fileKey;
		this.dateRequest = dateRequest;
	}

	public long getFileKey() {
		return fileKey;
	}

	public void setFileKey(long fileKey) {
		this.fileKey = fileKey;
	}

	public Date getDateRequest() {
		return dateRequest;
	}

	public void setDateRequest(Date dateRequest) {
		this.dateRequest = dateRequest;
	}
}
