package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.DispatchSerialRequestPOJO;
import com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO;

public class DispatchSerialNumberDAO {

	private DataSource ds;
	private Connection con = null;
	private PreparedStatement preparedStmt = null;
	private ResultSet rs = null;
	private DispatchSerialResponsePOJO response = new DispatchSerialResponsePOJO();

	

	public DispatchSerialResponsePOJO dispatchSerialNumber(
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
			preparedStmt.setString(4, "N");

			rs = preparedStmt.executeQuery();
			if (rs.next()) {
				do
				{
					response.setNewSerialNo(rs.getString("SERIAL_NO"));
					response.setBuildType(rs.getString("BUILD_TYPE"));
					response.setGppdID(rs.getString("GPPD_ID"));
					response.setCustomer(rs.getString("CUSTOMER"));
				}while(rs.next());

			} else {
				response.setResponseCode(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
				response.setResponseMsg(ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND_MSG);

				return response;
			}

		} catch (SQLException e) {
			System.out.println("error="+e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);

		} finally {

			DBUtil.closeConnections(con, preparedStmt, rs);

		}

		return response;
	}

}
