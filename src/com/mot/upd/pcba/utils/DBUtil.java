/**
 * 
 */
package com.mot.upd.pcba.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 * @author rviswa
 *
 */
public class DBUtil {

	private static Logger logger = Logger.getLogger(DBUtil.class);

	public static DataSource getDataSource() throws NamingException
	{
		logger.info("DBUtil Inside  DataSource method inside");
		DataSource ds = null;
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:jboss/updOracle");
		} catch (NamingException e) {
			throw e;
		}
		logger.info("DataSource method end");

		return ds;
	}
	public static Connection getConnection(DataSource ds) throws SQLException
	{
		logger.info("DBUtil Inside  Connection method inside");
		Connection con = null;
		con = ds.getConnection();
		return con;
	}
	public static void closeConnections(Connection con,Statement stmt,ResultSet rs){
		logger.info("DBUtil Inside  closeConnections method inside");

		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void closeConnection(Connection con,PreparedStatement preparedStmt,ResultSet rs){
		logger.info("DBUtil Inside  closeConnection method inside.");
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (preparedStmt != null) {
				preparedStmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
