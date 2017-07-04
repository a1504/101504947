package com.roleandjoin.gcs.entidad;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChannelCounts {
	
	int countCh=0;
	int countInv=0;
	int countJoin=0;
	
	public ChannelCounts() {
		// TODO Auto-generated constructor stub
	}
		
	public ChannelCounts(int countChannels, int countInvitations, int countJoins) {
		super();
		this.countCh = countChannels;
		this.countInv = countInvitations;
		this.countJoin = countJoins;
	}

	public int getCountCh() {
		return countCh;
	}

	public void setCountCh(int countCh) {
		this.countCh = countCh;
	}

	public int getCountInv() {
		return countInv;
	}

	public void setCountInv(int countInv) {
		this.countInv = countInv;
	}

	public int getCountJoin() {
		return countJoin;
	}

	public void setCountJoin(int countJoin) {
		this.countJoin = countJoin;
	}
	
}
