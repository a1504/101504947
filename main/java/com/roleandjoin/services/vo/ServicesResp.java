package com.roleandjoin.services.vo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServicesResp {
	int id;
	String msg;
	
	
	public ServicesResp() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	
}
