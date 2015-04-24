package com.mot.upd.pcba.restwebservice;


import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mot.upd.pcba.dao.R12SnSwapUpdateDAO;
import com.mot.upd.pcba.handler.PCBASerialNumberModel;
import com.mot.upd.pcba.pojo.R12SnSwapUpdateQueryInput;
import com.mot.upd.pcba.pojo.R12SnSwapUpdateQueryResult;


/**
 * @author Quinnox Dev Team
 *
 */
@Path("/")
public class R12SnSwapUpdateRestWebservice {
	
@GET
@Path("/{serialIn}")
@Produces(MediaType.APPLICATION_JSON)
public R12SnSwapUpdateQueryResult r12SnSwapUpdateService(@PathParam("serialIn") String serialIn){
		String serialOut = null;
		//serialIn = "353339060930372";
		System.out.println("serviceIn " +  serialIn);
		R12SnSwapUpdateQueryInput rs_R12UpdateQueryInput = new R12SnSwapUpdateQueryInput();
		R12SnSwapUpdateQueryResult rs_R12UpdateQueryResult = new R12SnSwapUpdateQueryResult();
		rs_R12UpdateQueryInput.setSerialNO(serialIn);
		if(isSerialLengthValid(serialIn)==true && !serialIn.equals(null)){
			R12SnSwapUpdateDAO r12SwapUpdateDAO = new R12SnSwapUpdateDAO();
			PCBASerialNumberModel pCBASerialNumberModel = r12SwapUpdateDAO.fetchR12SerialOutValue(rs_R12UpdateQueryInput.getSerialNO());
			System.out.println("serialOut " +serialOut);
		
		if(pCBASerialNumberModel.getNewSN() !=null && pCBASerialNumberModel.getSerialStatus().substring(0, 3).equals("ACT")){
			rs_R12UpdateQueryResult.setSerialIn(rs_R12UpdateQueryInput.getSerialNO());
			rs_R12UpdateQueryResult.setSerialOut(pCBASerialNumberModel.getNewSN());
			rs_R12UpdateQueryResult.setResponseCode("0000");
			rs_R12UpdateQueryResult.setResponseMsg("Success");
			
		}else{
			rs_R12UpdateQueryResult.setSerialIn(rs_R12UpdateQueryInput.getSerialNO());
			rs_R12UpdateQueryResult.setSerialOut(serialOut);
			rs_R12UpdateQueryResult.setResponseCode("5030");
			rs_R12UpdateQueryResult.setResponseMsg("SerialOut not available for given serial number");
			}
		}else{
			//rs_R12UpdateQueryResult.setSerialIn(rs_R12UpdateQueryInput.getSerialNO());
			rs_R12UpdateQueryResult.setResponseCode("5031");
			rs_R12UpdateQueryResult.setResponseMsg("SerialIn not Valid");
		}
		return rs_R12UpdateQueryResult;
	}
public static boolean isSerialLengthValid(String serial_no)
{
	
  int length = serial_no.length();
    	if (length == 15){
    		return true;
    	}
    	return false;
  }
}
