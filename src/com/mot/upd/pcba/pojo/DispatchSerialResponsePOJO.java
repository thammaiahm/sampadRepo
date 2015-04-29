package com.mot.upd.pcba.pojo;

import java.util.List;
import java.util.ArrayList;

/**
 * Author Thammaiah M B
 */

public class DispatchSerialResponsePOJO {
	
	private String newSerialNo;
	private String buildType;
	private String customer;
	private String dispatchedDate;
	private String mascID;
	private String gppdID;
	private String requestType;
	private int responseCode;
	private String responseMsg;
	private String rsdID;
	private List<String> ulmaAddress = new ArrayList<String>();
	

public String getNewSerialNo() {
		return newSerialNo;
	}

	public void setNewSerialNo(String newSerialNo) {
		this.newSerialNo = newSerialNo;
	}

	public String getBuildType() {
		return buildType;
	}

	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getDispatchedDate() {
		return dispatchedDate;
	}

	public void setDispatchedDate(String dispatchedDate) {
		this.dispatchedDate = dispatchedDate;
	}

	public String getMascID() {
		return mascID;
	}

	public void setMascID(String mascID) {
		this.mascID = mascID;
	}

	public String getGppdID() {
		return gppdID;
	}

	public void setGppdID(String gppdID) {
		this.gppdID = gppdID;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getRsdID() {
		return rsdID;
	}

	public void setRsdID(String rsdID) {
		this.rsdID = rsdID;
	}

	public void reset() {
		newSerialNo = null;
		buildType = null;
		customer = null;
		dispatchedDate = null;
		mascID = null;
		gppdID = null;
		requestType = null;
		responseCode = 0;
		responseMsg = null;
		rsdID = null;
		ulmaAddress=null;
	}

	public List<String> getUlmaAddress() {
		return ulmaAddress;
	}

	public void setUlmaAddress(List<String> ulmaAddress) {
		this.ulmaAddress = ulmaAddress;
	}

	

}
