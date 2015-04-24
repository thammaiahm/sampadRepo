/**
 * 
 */
package com.mot.upd.pcba.pojo;

import java.io.Serializable;

/**
 * @author rviswa
 *
 */
public class PCBASerialNoUPdateResponse implements Serializable{
	
	private int    responseCode;
	private String responseMessage;
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	

}
