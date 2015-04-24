package com.mot.upd.pcba.handler;

public class PCBASerialNumberModel {
	
	private String msnStatus;
	private String newSN;
	private String serialStatus;
	public String getMsnStatus() {
		return msnStatus;
	}
	public void setMsnStatus(String msnStatus) {
		this.msnStatus = msnStatus;
	}
	public String getNewSN() {
		return newSN;
	}
	public void setNewSN(String newSN) {
		this.newSN = newSN;
	}
	public String getSerialStatus() {
		return serialStatus;
	}
	public void setSerialStatus(String serialStatus) {
		this.serialStatus = serialStatus;
	}
	
	@Override
	public String toString() {
		return "PCBASerialNumberModel [msnStatus=" + msnStatus + ", newSN="
				+ newSN + ", serialStatus=" + serialStatus + "]";
	}

}
