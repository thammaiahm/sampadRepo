package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.DispatchSerialRequestPOJO;
import com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO;
import com.mot.upd.pcba.utils.DBUtil;

public class DispatchSerialNumberMySQLDAO implements DispatchSerialNumberDAO  {

	private static Logger logger = Logger
			.getLogger(DispatchSerialNumberMySQLDAO.class);
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
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method dispatchSerialNumberIMEI");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method dispatchSerialNumberIMEI");

		try {

			ds = DBUtil.getMySqlDataSource();
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

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM upd.upd_pcba_pgm_imei WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? LIMIT 1";

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
				logger.debug("DispatchSerialNumberMySQLDAO::dispatchSerialNumberIMEI:No Serial number available in DB for dispatch");
				logger.info("DispatchSerialNumberMySQLDAO::dispatchSerialNumberIMEI:No Serial number available in DB for dispatch");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		
		logger.debug("DispatchSerialNumberMySQLDAO:Leaving Method dispatchSerialNumberIMEI");
		logger.info("DispatchSerialNumberMySQLDAO:Leaving Method dispatchSerialNumberIMEI");
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
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method updateDispatchStatusIMEI");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method updateDispatchStatusIMEI");
		try {

			ds = DBUtil.getMySqlDataSource();
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
			String updateDispatchStatusIMEI = "UPDATE upd.upd_pcba_pgm_imei SET DISPATCH_DATE=NOW(),DISPATCH_STATUS=?,RSD_ID=?,MASC_ID=?,CLIENT_REQUEST_DATETIME=NOW(),LAST_MOD_BY=?,LAST_MOD_DATETIME=NOW() WHERE SERIAL_NO=?";

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
			String inserSNReposIMEI = "INSERT INTO upd.upd_warranty_info (SERIAL_NO,CREATED_BY,CREATED_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,status_code) VALUES (?,?,NOW(),?,NOW(),concat('VOI','     ',now()))";
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
			String selectDispatchStatus = "SELECT DISPATCH_DATE FROM upd.upd_pcba_pgm_imei WHERE SERIAL_NO=? ";
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
			
			//Update ULMA
			
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
						String updateDispatchStatusForULMA = "update upd.upd_ulma_repos set LAST_MOD_BY=?,dispatched_datetime=now(),is_dispatched=?,LAST_MOD_DATETIME=NOW(),SERIAL_NO=? where ulma in("+ulmaAddressString+");";
						preparedStmt = con.prepareStatement(updateDispatchStatusForULMA);
						preparedStmt.setString(1, PCBADataDictionary.MODIFIED_BY);
						preparedStmt.setString(2, PCBADataDictionary.DISPATCHED);
						preparedStmt.setString(3, dispatchSerialResponsePOJO
								.getNewSerialNo().trim());
						
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

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberMySQLDAO:Leaving Method updateDispatchStatusIMEI");
		logger.info("DispatchSerialNumberMySQLDAO:Leaving Method updateDispatchStatusIMEI");
		return dispatchSerialResponsePOJO;
	}

	public DispatchSerialResponsePOJO validateSerialNumberIMEI(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method validateSerialNumberIMEI");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method validateSerialNumberIMEI");
		try {

			ds = DBUtil.getMySqlDataSource();
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

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM upd.upd_pcba_pgm_imei WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? LIMIT 1";

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
				logger.debug("DispatchSerialNumberMySQLDAO::validateSerialNumberIMEI:No Serial Number  in DB");
				logger.info("DispatchSerialNumberMySQLDAO::validateSerialNumberIMEI:No Serial Number  in DB");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberMySQLDAO:Leaving Method validateSerialNumberIMEI");
		logger.info("DispatchSerialNumberMySQLDAO:Leaving Method validateSerialNumberIMEI");
		return response;
	}

	/*
	 * MEID 
	 */
	public DispatchSerialResponsePOJO dispatchSerialNumberMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method dispatchSerialNumberMEID");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method dispatchSerialNumberMEID");
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG+e.getMessage());
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM upd.upd_pcba_pgm_meid WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and Protocol_name=? limit 1";
			

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
				logger.debug("DispatchSerialNumberMySQLDAO::dispatchSerialNumberMEID:No Serial number available for dispatch");
				logger.info("DispatchSerialNumberMySQLDAO:dispatchSerialNumberMEID:No Serial number available for dispatch");
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			System.out.println("error=" + e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG+e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberMySQLDAO:Leaving Method dispatchSerialNumberMEID");
		logger.info("DispatchSerialNumberMySQLDAO:Leaving Method dispatchSerialNumberMEID");
		return response;
	}
	
	


	public DispatchSerialResponsePOJO updateDispatchStatusMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method updateDispatchStatusMEID");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method updateDispatchStatusMEID");
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG+e.getMessage());
			return dispatchSerialResponsePOJO;
		}
		try {
			// get database connection

			con = DBUtil.getConnection(ds);

			// update repo table

			con.setAutoCommit(false);
			String updateDispatchStatusIMEI = "UPDATE upd.upd_pcba_pgm_meid SET DISPATCH_DATE=NOW(),DISPATCH_STATUS=?,RSD_ID=?,MASC_ID=?,CLIENT_REQUEST_DATETIME=NOW(),LAST_MOD_BY=?,LAST_MOD_DATETIME=NOW() WHERE SERIAL_NO=?";
			
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
			String inserSNReposIMEI = "INSERT INTO upd.upd_warranty_info (SERIAL_NO,CREATED_BY,CREATED_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,status_code) VALUES (?,?,NOW(),?,NOW(),concat('VOI','     ',now()))";
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
			String selectDispatchStatus = "SELECT DISPATCH_DATE FROM upd.upd_pcba_pgm_meid WHERE SERIAL_NO=? ";
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
			String updateDispatchStatusForULMA = "update upd.upd_ulma_repos set LAST_MOD_BY=?,dispatched_datetime=now(),is_dispatched=?,LAST_MOD_DATETIME=NOW(),SERIAL_NO=? where ulma in("+ulmaAddressString+");";
			preparedStmt = con.prepareStatement(updateDispatchStatusForULMA);
			preparedStmt.setString(1, PCBADataDictionary.MODIFIED_BY);
			preparedStmt.setString(2, PCBADataDictionary.DISPATCHED);
			preparedStmt.setString(3, dispatchSerialResponsePOJO
					.getNewSerialNo().trim());
			
			int rows = preparedStmt.executeUpdate();
	// update ULMA table

			con.commit();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			dispatchSerialResponsePOJO.reset();
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG+e.getMessage());
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
		logger.debug("DispatchSerialNumberMySQLDAO:Leaving Method updateDispatchStatusMEID");
		logger.info("DispatchSerialNumberMySQLDAO:Leaving Method updateDispatchStatusMEID");
		return dispatchSerialResponsePOJO;
	
	}
	
	public DispatchSerialResponsePOJO validateSerialNumberMEID(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method validateSerialNumberMEID");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method validateSerialNumberMEID");
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG+e.getMessage());
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			String selectSerialNumber = "SELECT SERIAL_NO,BUILD_TYPE,CUSTOMER,GPPD_ID FROM upd.upd_pcba_pgm_meid WHERE GPPD_ID=? and CUSTOMER=? and Build_Type=? and Dispatch_Date is null and Dispatch_Status=? and Protocol_name=? limit 1";

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
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);
				logger.debug("DispatchSerialNumberMySQLDAO::validateSerialNumberMEID:No Serial number in DB");
				logger.info("DispatchSerialNumberMySQLDAO::validateSerialNumberMEID:No Serial number in DB");

				return response;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG+e.getMessage());

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}
		logger.debug("DispatchSerialNumberMySQLDAO:Leaving Method validateSerialNumberMEID");
		logger.info("DispatchSerialNumberMySQLDAO:Leaving Method validateSerialNumberMEID");
		return response;

	}

	

	@Override
	public DispatchSerialResponsePOJO dispatchULMAAddress(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method dispatchULMAAddress");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method dispatchULMAAddress");
		List<String> ulmaAddress = new ArrayList<String>();
		// TODO Auto-generated method stub
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
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

			String selectSerialNumber = "SELECT ulma FROM upd.upd_ulma_repos WHERE  is_dispatched=? and dispatched_datetime is null limit ?";

			preparedStmt = con.prepareStatement(selectSerialNumber);
			preparedStmt.setString(1, PCBADataDictionary.UNDISPATCHED);
			preparedStmt.setInt(2, dispatchSerialRequestPOJO.getNumberOfUlma());
			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {

					ulmaAddress.add(rs.getString("ulma"));

				} while (rs.next());
				dispatchSerialResponsePOJO.setUlmaAddress(ulmaAddress);
			} else {
				logger.debug("DispatchSerialNumberMySQLDAO::dispatchULMAAddress:No ULMA to dispatch");
				logger.info("DispatchSerialNumberMySQLDAO::dispatchULMAAddress:No ULMA to dispatch");
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

		return dispatchSerialResponsePOJO;
	}

	@Override
	public DispatchSerialResponsePOJO validateULMAAddress(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO,
			DispatchSerialResponsePOJO dispatchSerialResponsePOJO) {
		logger.debug("DispatchSerialNumberMySQLDAO:Entered Method validateULMAAddress");
		logger.info("DispatchSerialNumberMySQLDAO:Entered Method validateULMAAddress");
		List<String> ulmaAddress = new ArrayList<String>();
		// TODO Auto-generated method stub
		try {

			ds = DBUtil.getMySqlDataSource();
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

			String selectSerialNumber = "SELECT ulma FROM upd.upd_ulma_repos WHERE  is_dispatched=? and dispatched_datetime is null limit ?";

			preparedStmt = con.prepareStatement(selectSerialNumber);
			preparedStmt.setString(1, PCBADataDictionary.UNDISPATCHED);
			preparedStmt.setInt(2, dispatchSerialRequestPOJO.getNumberOfUlma());
			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do {

				      //Nothing

				} while (rs.next());
				dispatchSerialResponsePOJO.setUlmaAddress(ulmaAddress);
			} else {
				logger.debug("DispatchSerialNumberMySQLDAO::validateULMAAddress:No ULMA Available in DB");
				logger.info("DispatchSerialNumberMySQLDAO::validateULMAAddress:No ULMA Available in DB");
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

		return dispatchSerialResponsePOJO;
	}

	


}
