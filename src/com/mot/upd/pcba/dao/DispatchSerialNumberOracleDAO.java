package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.DispatchSerialRequestPOJO;
import com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO;
import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.utils.InitProperty;

/**
 * @author HRDJ36 Thammaiah M B
 */
public class DispatchSerialNumberOracleDAO implements DispatchSerialNumberDAO {
	PropertyResourceBundle bundle = InitProperty
			.getProperty("pcbasqlORA.properties");
	private static Logger logger = Logger
			.getLogger(DispatchSerialNumberOracleDAO.class);
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
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method dispatchSerialNumberIMEI");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method dispatchSerialNumberIMEI");

		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
					+ e.getMessage());
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			// String selectSerialNumber =
			// "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_IMEI WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and rownum=1";
			String selectSerialNumber = bundle.getString("IMEI.selectSerial");

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
				logger.debug("DispatchSerialNumberOracleDAO::dispatchSerialNumberIMEI:No Serial number available in DB for dispatch");
				logger.info("DispatchSerialNumberOracleDAO::dispatchSerialNumberIMEI:No Serial number available in DB for dispatch");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.out.println("error=" + e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println("error=" + e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method dispatchSerialNumberIMEI");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method dispatchSerialNumberIMEI");
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
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method updateDispatchStatusIMEI");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method updateDispatchStatusIMEI");
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
							+ e.getMessage());
			return dispatchSerialResponsePOJO;
		}
		try {
			// get database connection

			con = DBUtil.getConnection(ds);

			// update repo table

			con.setAutoCommit(false);
			// String updateDispatchStatusIMEI =
			// "UPDATE UPD_PCBA_PGM_IMEI SET DISPATCH_DATE=SYSDATE,DISPATCH_STATUS=?,RSD_ID=?,MASC_ID=?,CLIENT_REQUEST_DATETIME=SYSTIMESTAMP,LAST_MOD_BY=?,LAST_MOD_DATETIME=SYSTIMESTAMP WHERE SERIAL_NO=?";
			String updateDispatchStatusIMEI = bundle
					.getString("IMEI.updateDispatchStatus");
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

			// String inserSNReposIMEI =
			// "INSERT INTO UPD_SN_REPOS (SERIAL_NO,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,attribute_37) VALUES (?,?,SYSTIMESTAMP,?,SYSTIMESTAMP,'VOI-'||sysdate)";
			String inserSNReposIMEI = bundle.getString("IMEI.insertSNDeatail");
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
			// String selectDispatchStatus =
			// "SELECT DISPATCH_DATE FROM UPD_PCBA_PGM_IMEI WHERE SERIAL_NO=? ";
			String selectDispatchStatus = bundle
					.getString("IMEI.selectDispatchDate");

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
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SUCCESS);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.OPERATION_SUCCESS);
			dispatchSerialResponsePOJO.setRequestType(dispatchSerialRequestPOJO
					.getRequestType());
			// End setting remaining response parameter

			// Updating ULMA Address
			List<String> ulmaAddress = dispatchSerialResponsePOJO
					.getUlmaAddress();
			String ulmaAddressString = null;
			for (String address : ulmaAddress) {

				if (ulmaAddressString == null) {
					ulmaAddressString = "'" + address + "'";
				} else {
					ulmaAddressString = ulmaAddressString + "," + "'" + address
							+ "'";
				}

			}
			preparedStmt = null;
			String updateDispatchStatusForULMA = "UPDATE upd_ulma_detail  SET LAST_MOD_USER=?,LAST_MOD_DATETIME=SYSDATE,SERIAL_NO=?,ATTRIBUTE_STATE=? WHERE ATTRIBUTE_VALUE IN("
					+ ulmaAddressString + ")";
			preparedStmt = con.prepareStatement(updateDispatchStatusForULMA);
			preparedStmt.setString(1, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(2, dispatchSerialResponsePOJO
					.getNewSerialNo().trim());
			preparedStmt.setString(3, PCBADataDictionary.DISPATCHED);
			int rows = preparedStmt.executeUpdate();
			// update ULMA table

			con.commit();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
							+ e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
							+ e.getMessage());
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
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method updateDispatchStatusIMEI");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method updateDispatchStatusIMEI");
		return dispatchSerialResponsePOJO;
	}

	/*
	 * Validate if there are serial number available for disatch for IMEI
	 * 
	 * @param Request attribute
	 */

	public DispatchSerialResponsePOJO validateSerialNumberIMEI(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method validateSerialNumberIMEI");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method validateSerialNumberIMEI");
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
					+ e.getMessage());
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			// String selectSerialNumber =
			// "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_IMEI WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and rownum=1";
			String selectSerialNumber = bundle.getString("IMEI.validateSerial");

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
				logger.debug("DispatchSerialNumberOracleDAO::validateSerialNumberIMEI:No Serial Number  in DB");
				logger.info("DispatchSerialNumberOracleDAO::validateSerialNumberIMEI:No Serial Number  in DB");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} finally {
			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method validateSerialNumberIMEI");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method validateSerialNumberIMEI");
		return response;
	}

	/*
	 * Select the serial number to be dispatched for MEID
	 */
	public DispatchSerialResponsePOJO dispatchSerialNumberMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method dispatchSerialNumberMEID");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method dispatchSerialNumberMEID");
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
					+ e.getMessage());
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			// String selectSerialNumber =
			// "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_MEID WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and Protocol_name=? and rownum=1";
			String selectSerialNumber = bundle.getString("MEID.selectSerial");

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
				logger.debug("DispatchSerialNumberOracleDAO::dispatchSerialNumberMEID:No Serial number available for dispatch");
				logger.info("DispatchSerialNumberOracleDAO:dispatchSerialNumberMEID:No Serial number available for dispatch");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method dispatchSerialNumberMEID");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method dispatchSerialNumberMEID");
		return response;
	}

	/*
	 * DIspatch MEID by updating the status
	 */
	public DispatchSerialResponsePOJO updateDispatchStatusMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method updateDispatchStatusMEID");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method updateDispatchStatusMEID");
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
							+ e.getMessage());
			return dispatchSerialResponsePOJO;
		}
		try {
			// get database connection

			con = DBUtil.getConnection(ds);

			// update repo table

			con.setAutoCommit(false);

			// String updateDispatchStatusMEID =
			// "UPDATE UPD_PCBA_PGM_MEID SET DISPATCH_DATE=SYSDATE,DISPATCH_STATUS=?,RSD_ID=?,MASC_ID=?,CLIENT_REQUEST_DATETIME=SYSTIMESTAMP,LAST_MOD_BY=?,LAST_MOD_DATETIME=SYSTIMESTAMP WHERE SERIAL_NO=?";
			String updateDispatchStatusMEID = bundle
					.getString("MEID.selectSerial");

			preparedStmt = con.prepareStatement(updateDispatchStatusMEID);
			preparedStmt.setString(1, PCBADataDictionary.DISPATCHED);
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getRsdID());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getMascID());
			preparedStmt.setString(4, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(5, dispatchSerialResponsePOJO
					.getNewSerialNo().trim());
			preparedStmt.executeUpdate();

			// inerst SN Repos table
			preparedStmt = null;
			// String inserSNReposIMEI =
			// "INSERT INTO UPD_SN_REPOS (SERIAL_NO,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,attribute_37) VALUES (?,?,SYSTIMESTAMP,?,SYSTIMESTAMP,'VOI-'||sysdate)";
			String inserSNReposMEID = bundle.getString("MEID.insertSNDeatail");

			preparedStmt = con.prepareStatement(inserSNReposMEID);
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
			// String selectDispatchStatus =
			// "SELECT DISPATCH_DATE FROM UPD_PCBA_PGM_MEID WHERE SERIAL_NO=? ";
			String selectDispatchStatus = bundle
					.getString("MEID.selectDispatchDate");

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

			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SUCCESS);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.OPERATION_SUCCESS);
			dispatchSerialResponsePOJO.setRequestType(dispatchSerialRequestPOJO
					.getRequestType());
			// End setting remaining response parameter

			// Updating ULMA Address
			List<String> ulmaAddress = dispatchSerialResponsePOJO
					.getUlmaAddress();
			String ulmaAddressString = null;
			for (String address : ulmaAddress) {

				if (ulmaAddressString == null) {
					ulmaAddressString = "'" + address + "'";
				} else {
					ulmaAddressString = ulmaAddressString + "," + "'" + address
							+ "'";
				}

			}

			// update ULMA table
			preparedStmt = null;
			String updateDispatchStatusForULMA = "UPDATE upd_ulma_detail  SET LAST_MOD_USER=?,LAST_MOD_DATETIME=SYSDATE,SERIAL_NO=?,ATTRIBUTE_STATE=? WHERE ATTRIBUTE_VALUE IN("
					+ ulmaAddressString + ")";
			preparedStmt = con.prepareStatement(updateDispatchStatusForULMA);
			preparedStmt.setString(1, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(2, dispatchSerialResponsePOJO
					.getNewSerialNo().trim());
			preparedStmt.setString(3, PCBADataDictionary.DISPATCHED);
			int rows = preparedStmt.executeUpdate();
			// update ULMA table

			con.commit();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
							+ e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
							+ e.getMessage());
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
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method updateDispatchStatusMEID");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method updateDispatchStatusMEID");

		return dispatchSerialResponsePOJO;
	}

	/*
	 * Validate if MEID available for Dispatch
	 * 
	 * @see
	 * com.mot.upd.pcba.dao.DispatchSerialNumberDAO#validateSerialNumberMEID
	 * (com.mot.upd.pcba.pojo.DispatchSerialRequestPOJO)
	 */
	public DispatchSerialResponsePOJO validateSerialNumberMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method validateSerialNumberMEID");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method validateSerialNumberMEID");
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
					+ e.getMessage());
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			// String selectSerialNumber =
			// "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM UPD_PCBA_PGM_MEID WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and Protocol_name=? and rownum=1";
			String selectSerialNumber = bundle.getString("MEID.validateSerial");

			preparedStmt = con.prepareStatement(selectSerialNumber);

			preparedStmt.setString(1, dispatchSerialRequestPOJO.getGppdID());
			preparedStmt.setString(2, dispatchSerialRequestPOJO.getCustomer());
			preparedStmt.setString(3, dispatchSerialRequestPOJO.getBuildType());
			preparedStmt.setString(4, PCBADataDictionary.UNDISPATCHED);
			preparedStmt.setString(5, dispatchSerialRequestPOJO.getProtocol());

			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_AVAILABLE);
					response.setResponseMsg(ServiceMessageCodes.SERIAL_NO_AVAILABLE_FOR_DISPATCH_MSG);

				} while (rs.next());

			} else {
				logger.debug("DispatchSerialNumberOracleDAO::validateSerialNumberMEID:No Serial number in DB");
				logger.info("DispatchSerialNumberOracleDAO::validateSerialNumberMEID:No Serial number in DB");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method validateSerialNumberMEID");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method validateSerialNumberMEID");
		return response;

	}

	/*
	 * Dispatch ULMA Adress
	 * 
	 * @see
	 * com.mot.upd.pcba.dao.DispatchSerialNumberDAO#dispatchULMAAddress(com.
	 * mot.upd.pcba.pojo.DispatchSerialRequestPOJO,
	 * com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO)
	 */
	@Override
	public DispatchSerialResponsePOJO dispatchULMAAddress(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method dispatchULMAAddress");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method dispatchULMAAddress");
		List<String> ulmaAddress = new ArrayList<String>();
		// TODO Auto-generated method stub
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
							+ e.getMessage());
			return dispatchSerialResponsePOJO;
		}
		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			//String selectSerialNumber = "SELECT ATTRIBUTE_VALUE FROM upd_ulma_detail where ATTRIBUTE_STATE=? AND ROWNUM<=?";
			String selectSerialNumber  = bundle.getString("WS.dispatchULMA");

			preparedStmt = con.prepareStatement(selectSerialNumber);
			preparedStmt.setString(1, PCBADataDictionary.UNDISPATCHED);
			preparedStmt.setInt(2, dispatchSerialRequestPOJO.getNumberOfUlma());
			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {

					ulmaAddress.add(rs.getString("ATTRIBUTE_VALUE"));

				} while (rs.next());
				dispatchSerialResponsePOJO.setUlmaAddress(ulmaAddress);
			} else {
				logger.debug("DispatchSerialNumberOracleDAO::dispatchULMAAddress:No ULMA to dispatch");
				logger.info("DispatchSerialNumberOracleDAO::dispatchULMAAddress:No ULMA to dispatch");
				dispatchSerialResponsePOJO.reset();
				dispatchSerialResponsePOJO
						.setResponseCode(ServiceMessageCodes.NO_ULMA_AVAILABLE);
				dispatchSerialResponsePOJO
						.setResponseMsg(ServiceMessageCodes.NO_ULMA_AVAILABLE_MSG);

				return dispatchSerialResponsePOJO;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
							+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method dispatchULMAAddress");
		logger.info("DispatchSerialNumberOracleDAO:Leaving Method dispatchULMAAddress");

		return dispatchSerialResponsePOJO;

	}

	/*
	 * validate if ULMA available for dispatch
	 * 
	 * @see
	 * com.mot.upd.pcba.dao.DispatchSerialNumberDAO#validateULMAAddress(com.
	 * mot.upd.pcba.pojo.DispatchSerialRequestPOJO,
	 * com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO)
	 */
	@Override
	public DispatchSerialResponsePOJO validateULMAAddress(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		logger.debug("DispatchSerialNumberOracleDAO:Entered Method validateULMAAddress");
		logger.info("DispatchSerialNumberOracleDAO:Entered Method validateULMAAddress");
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
							+ e.getMessage());
			return response;
		}
		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			//String selectSerialNumber = "SELECT ATTRIBUTE_VALUE FROM upd_ulma_detail where ATTRIBUTE_STATE=? AND ROWNUM<=?";
			String selectSerialNumber  = bundle.getString("WS.validateULMA");

			preparedStmt = con.prepareStatement(selectSerialNumber);
			preparedStmt.setString(1, PCBADataDictionary.UNDISPATCHED);
			preparedStmt.setInt(2, dispatchSerialRequestPOJO.getNumberOfUlma());
			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {
					// Nothing

				} while (rs.next());

			} else {
				logger.debug("DispatchSerialNumberOracleDAO::validateULMAAddress:No ULMA Available in DB");
				logger.info("DispatchSerialNumberOracleDAO::validateULMAAddress:No ULMA Available in DB");
				dispatchSerialResponsePOJO.reset();
				dispatchSerialResponsePOJO
						.setResponseCode(ServiceMessageCodes.NO_ULMA_AVAILABLE);
				dispatchSerialResponsePOJO
						.setResponseMsg(ServiceMessageCodes.NO_ULMA_AVAILABLE_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
							+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberOracleDAO:Leaving Method validateULMAAddress");
		logger.info("DispatchSerialNumberOracleDAO:Leavingd Method validateULMAAddress");
		return dispatchSerialResponsePOJO;
	}

}
