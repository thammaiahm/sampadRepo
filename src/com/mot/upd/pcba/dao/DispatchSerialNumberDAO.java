package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.DispatchSerialRequestPOJO;
import com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO;
import com.mot.upd.pcba.utils.DBUtil;

public class DispatchSerialNumberDAO {
	private static Logger logger = Logger
			.getLogger(DispatchSerialNumberDAO.class);
	private DataSource ds;
	private Connection con = null;
	private PreparedStatement preparedStmt = null;
	private ResultSet rs = null;
	private DispatchSerialResponsePOJO response = new DispatchSerialResponsePOJO();

	/*
	 * Get the serial number which has to be dispatched when request is for IMEI
	 */
	public DispatchSerialResponsePOJO dispatchSerialNumberIMEI(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {

		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG);
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_IMEI WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and rownum=1";

			preparedStmt = con.prepareStatement(selectSerialNumber);

			preparedStmt.setString(1, dispatchSerialRequestPOJO.getGppdID());
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getCustomer());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getBuildType());
			preparedStmt.setString(4, PCBADataDictionary.UNDISPATCHED);

			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					response.setNewSerialNo(rs.getString("SERIAL_NO"));
					response.setBuildType(rs.getString("BUILD_TYPE"));
					response.setGppdID(rs.getString("GPPD_ID"));
					response.setCustomer(rs.getString("CUSTOMER"));
				} while (rs.next());

			} else {
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			System.out.println("error=" + e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}

		return response;
	}

	/*
	 * update Dispatch date,MascId RSDID and dispatch the serial by updating the
	 * status for IMEI
	 */
	public DispatchSerialResponsePOJO updateDispatchStatusIMEI(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		// TODO Auto-generated method stub
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG);
			return response;
		}
		try {
			// get database connection

			con = DBUtil.getConnection(ds);

			// update repo table

			con.setAutoCommit(false);
			String updateDispatchStatusIMEI = "UPDATE UPD_PCBA_PGM_IMEI SET DISPATCH_DATE=SYSDATE,DISPATCH_STATUS=?,RSD_ID=?,MASC_ID=?,CLIENT_REQUEST_DATETIME=SYSTIMESTAMP,LAST_MOD_BY=?,LAST_MOD_DATETIME=SYSTIMESTAMP WHERE SERIAL_NO=?";
			preparedStmt = con.prepareStatement(updateDispatchStatusIMEI);
			preparedStmt.setString(1, PCBADataDictionary.DISPATCHED);
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getRsdID());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getMascID());
			preparedStmt.setString(4, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(5, dispatchSerialResponsePOJO
					.getNewSerialNo().trim());
			preparedStmt.executeUpdate();

			// inerst SN Repos table
			preparedStmt = null;
			String inserSNReposIMEI = "INSERT INTO UPD_SN_REPOS (SERIAL_NO,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,attribute_37) VALUES (?,?,SYSTIMESTAMP,?,SYSTIMESTAMP,'VOI-'||sysdate)";
			preparedStmt = con.prepareStatement(inserSNReposIMEI);
			preparedStmt.setString(1,
					dispatchSerialResponsePOJO.getNewSerialNo());
			preparedStmt.setString(2, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(3, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.executeUpdate();

			/*
			 * Setting response parameter
			 */
			preparedStmt = null;
			dispatchSerialResponsePOJO.setMascID(dispatchSerialRequestPOJO
					.getMascID());
			dispatchSerialResponsePOJO.setRsdID(dispatchSerialRequestPOJO
					.getRsdID());
			String selectDispatchStatus = "SELECT DISPATCH_DATE FROM UPD_PCBA_PGM_IMEI WHERE SERIAL_NO=? ";
			preparedStmt = con.prepareStatement(selectDispatchStatus);
			preparedStmt.setString(1,
					dispatchSerialResponsePOJO.getNewSerialNo());
			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					dispatchSerialResponsePOJO.setDispatchedDate(rs
							.getString("DISPATCH_DATE"));

				} while (rs.next());

			}
			dispatchSerialResponsePOJO.setResponseCode(ServiceMessageCodes.SUCCESS);
			dispatchSerialResponsePOJO.setResponseMsg(ServiceMessageCodes.OPERATION_SUCCESS);
			dispatchSerialResponsePOJO.setRequestType(dispatchSerialRequestPOJO.getRequestType());
			//End setting remaining response parameter

			con.commit();
		} catch (SQLException e) {
			System.out.println("error=" + e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}

		return dispatchSerialResponsePOJO;
	}

	/*
	 * public DispatchSerialResponsePOJO getULMAAddress(
	 * DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
	 * DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
	 * 
	 * 
	 * }
	 */

	/*
	 * Validate if there are serial number available for disatch for IMEI
	 * 
	 * @param Request attribute
	 */

	public DispatchSerialResponsePOJO validateSerialNumberIMEI(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG);
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_IMEI WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and rownum=1";

			preparedStmt = con.prepareStatement(selectSerialNumber);

			preparedStmt.setString(1, dispatchSerialRequestPOJO.getGppdID());
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getCustomer());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getBuildType());
			preparedStmt.setString(4, PCBADataDictionary.UNDISPATCHED);

			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_AVAILABLE);
					response.setResponseMsg(ServiceMessageCodes.SERIAL_NO_AVAILABLE_FOR_DISPATCH_MSG);

				} while (rs.next());

			} else {
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			System.out.println("error=" + e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}

		return response;
	}

	/*
	 * Select the serial number to be dispatched for MEID
	 */
	public DispatchSerialResponsePOJO dispatchSerialNumberMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG);
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_MEID WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and Protocol_name=? and rownum=1";

			preparedStmt = con.prepareStatement(selectSerialNumber);

			preparedStmt.setString(1, dispatchSerialRequestPOJO.getGppdID());
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getCustomer());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getBuildType());
			preparedStmt.setString(4, PCBADataDictionary.UNDISPATCHED);
			preparedStmt.setString(5, dispatchSerialRequestPOJO.getProtocol());

			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					response.setNewSerialNo(rs.getString("SERIAL_NO"));
					response.setBuildType(rs.getString("BUILD_TYPE"));
					response.setGppdID(rs.getString("GPPD_ID"));
					response.setCustomer(rs.getString("CUSTOMER"));

				} while (rs.next());

			} else {
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			System.out.println("error=" + e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		return response;
	}

	/*
	 * DIspatch MEID by updating the status
	 */
	public DispatchSerialResponsePOJO updateDispatchStatusMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG);
			return response;
		}
		try {
			// get database connection

			con = DBUtil.getConnection(ds);

			// update repo table

			con.setAutoCommit(false);
			String updateDispatchStatusIMEI = "UPDATE UPD_PCBA_PGM_MEID SET DISPATCH_DATE=SYSDATE,DISPATCH_STATUS=?,RSD_ID=?,MASC_ID=?,CLIENT_REQUEST_DATETIME=SYSTIMESTAMP,LAST_MOD_BY=?,LAST_MOD_DATETIME=SYSTIMESTAMP WHERE SERIAL_NO=?";
			preparedStmt = con.prepareStatement(updateDispatchStatusIMEI);
			preparedStmt.setString(1, PCBADataDictionary.DISPATCHED);
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getRsdID());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getMascID());
			preparedStmt.setString(4, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(5, dispatchSerialResponsePOJO
					.getNewSerialNo().trim());
			preparedStmt.executeUpdate();

			// inerst SN Repos table
			preparedStmt = null;
			String inserSNReposIMEI = "INSERT INTO UPD_SN_REPOS (SERIAL_NO,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,attribute_37) VALUES (?,?,SYSTIMESTAMP,?,SYSTIMESTAMP,'VOI-'||sysdate)";
			preparedStmt = con.prepareStatement(inserSNReposIMEI);
			preparedStmt.setString(1,
					dispatchSerialResponsePOJO.getNewSerialNo());
			preparedStmt.setString(2, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(3, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.executeUpdate();

			/*
			 * Setting response parameter
			 */
			preparedStmt = null;
			dispatchSerialResponsePOJO.setMascID(dispatchSerialRequestPOJO
					.getMascID());
			dispatchSerialResponsePOJO.setRsdID(dispatchSerialRequestPOJO
					.getRsdID());
			String selectDispatchStatus = "SELECT DISPATCH_DATE FROM UPD_PCBA_PGM_MEID WHERE SERIAL_NO=? ";
			preparedStmt = con.prepareStatement(selectDispatchStatus);
			preparedStmt.setString(1,
					dispatchSerialResponsePOJO.getNewSerialNo());
			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					dispatchSerialResponsePOJO.setDispatchedDate(rs
							.getString("DISPATCH_DATE"));

				} while (rs.next());

			}
			
			dispatchSerialResponsePOJO.setResponseCode(ServiceMessageCodes.SUCCESS);
			dispatchSerialResponsePOJO.setResponseMsg(ServiceMessageCodes.OPERATION_SUCCESS);
			dispatchSerialResponsePOJO.setRequestType(dispatchSerialRequestPOJO.getRequestType());
			//End setting remaining response parameter

			con.commit();
		} catch (SQLException e) {
			System.out.println("error=" + e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}

		return dispatchSerialResponsePOJO;
	}

}
