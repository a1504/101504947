package com.roleandjoin.gcs.entidad;

public class File {
	
	long id;
	String path;
	String blobKey;
	long sizeFile;
	String typeFile;
	String name;
	String thumbBlobKey;
	
	public File(long id, String path, String blobKey, long sizeFile) {
		super();
		this.id = id;
		this.path = path;
		this.blobKey = blobKey;
		this.sizeFile = sizeFile;
	}

	public File(String path, String blobKey, long sizeFile) {
		super();
		this.path = path;
		this.blobKey = blobKey;
		this.sizeFile = sizeFile;
	}

	public File() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBlobKey() {
		return blobKey;
	}

	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}

	public long getSizeFile() {
		return sizeFile;
	}

	public void setSizeFile(long sizeFile) {
		this.sizeFile = sizeFile;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTypeFile() {
		return typeFile;
	}

	public void setTypeFile(String typeFile) {
		this.typeFile = typeFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbBlobKey() {
		return thumbBlobKey;
	}

	public void setThumbBlobKey(String thumbBlobKey) {
		this.thumbBlobKey = thumbBlobKey;
	}
	
}
