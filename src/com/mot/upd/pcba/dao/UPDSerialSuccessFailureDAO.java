/**
 * 
 */
package com.mot.upd.pcba.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.PCBAProgramResponse;

/**
 * @author rviswa
 *
 */
public class UPDSerialSuccessFailureDAO {

	private static Logger logger = Logger.getLogger(UPDSerialSuccessFailureDAO.class);

	private DataSource ds;
	private Connection con = null;
	private PreparedStatement preparedStmt = null;

	PCBAProgramResponse response = new PCBAProgramResponse();

	public PCBAProgramResponse updateIMEIStatus(){

		try {

			ds = DBUtil.getDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in IMEI:"+e);
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);
			String snType="";// need to change
			StringBuffer sb=new StringBuffer();
			sb.append("update UPD_SN_REPOS set attribute_43 ='Y',attribute_29='"+snType+"',LAST_MOD_BY='pcba_pgm_IMEI_success',");
			sb.append("LAST_MOD_DATETIME=sysdate WHERE serial_no='"+snType+"'");
			preparedStmt = con.prepareStatement(sb.toString());
			logger.info("IMEI SQL Query:"+sb.toString());

		}catch(Exception e){
			logger.info("Update IMEI error:"+e);
		}
		finally{
			DBUtil.closeConnection(con, preparedStmt,null);
		}

		return response;
	}
	public PCBAProgramResponse updateMEIDStatus(){
		try {

			ds = DBUtil.getDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:"+e);
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}
		try{
			// get database connection
			con = DBUtil.getConnection(ds);
			String snType="";// need to change
			StringBuffer sb=new StringBuffer();
			sb.append("update UPD_SN_REPOS set attribute_43 ='Y',LAST_MOD_BY='pcba_pgm_MEID_success',");
			sb.append("LAST_MOD_DATETIME=sysdate WHERE serial_no='"+snType+"'");
			preparedStmt = con.prepareStatement(sb.toString());
			logger.info("MEID SQL Query:"+sb.toString());

		}catch(Exception e){
			logger.info("Update MEID error:"+e);
		}
		finally{
			DBUtil.closeConnection(con, preparedStmt,null);
		}

		return response;

	}

}
