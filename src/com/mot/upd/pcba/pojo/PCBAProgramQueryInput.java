/**
 * 
 */
package com.mot.upd.pcba.pojo;

import java.io.Serializable;

/**
 * @author rviswa
 *
 */
public class PCBAProgramQueryInput implements Serializable{
	
	private String serialNO;
	private String rsdID;
	private String mascID;
	private String status;
	private String msl;
	private String otksl;
	private String servicePassCode;
	private String snType;
	
	public String getSerialNO() {
		return serialNO;
	}
	public void setSerialNO(String serialNO) {
		this.serialNO = serialNO;
	}
	public String getRsdID() {
		return rsdID;
	}
	public void setRsdID(String rsdID) {
		this.rsdID = rsdID;
	}
	public String getMascID() {
		return mascID;
	}
	public void setMascID(String mascID) {
		this.mascID = mascID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsl() {
		return msl;
	}
	public void setMsl(String msl) {
		this.msl = msl;
	}
	public String getOtksl() {
		return otksl;
	}
	public void setOtksl(String otksl) {
		this.otksl = otksl;
	}
	public String getServicePassCode() {
		return servicePassCode;
	}
	public void setServicePassCode(String servicePassCode) {
		this.servicePassCode = servicePassCode;
	}
	public String getSnType() {
		return snType;
	}
	public void setSnType(String snType) {
		this.snType = snType;
	}

}
