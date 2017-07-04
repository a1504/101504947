package com.roleandjoin.gcs.entidad;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Join {
	
	String userName;
	String idChannel;
	String ownerChannel;
	String channelName;
	boolean channelIsPublic;
	
	
	public Join(String userName, String idChannel) {
		super();
		this.userName = userName;
		this.idChannel = idChannel;
	}
	public Join() {
		super();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIdChannel() {
		return idChannel;
	}
	public void setIdChannel(String idChannel) {
		this.idChannel = idChannel;
	}
	public String getOwnerChannel() {
		return ownerChannel;
	}
	public void setOwnerChannel(String ownerChannel) {
		this.ownerChannel = ownerChannel;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	public boolean isChannelIsPublic() {
		return channelIsPublic;
	}
	public void setChannelIsPublic(boolean channelIsPublic) {
		this.channelIsPublic = channelIsPublic;
	}
		
}
