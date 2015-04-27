package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import com.mot.upd.pcba.handler.PCBASerialNumberModel;
import com.mot.upd.pcba.utils.DBUtil;

import org.apache.log4j.Logger;





public class R12SnSwapUpdateDAO {

	private static Logger logger = Logger.getLogger(R12SnSwapUpdateDAO.class);
	//PropertyResourceBundle bundle = InitProperty.getProperty("pcbaSql.properties");
	public PCBASerialNumberModel fetchR12SerialOutValue(String serialIn){

		//String serialOut = null;
		DataSource ds;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PCBASerialNumberModel pCBASerialNumberModel = new PCBASerialNumberModel();

		String updConfig = DBUtil.dbConfigCheck();
		if(updConfig.equals("YES")){
			//logger.info("inside oracle old db logic");
			try{

				String query=" select attribute_34, attribute_41, attribute_37 from upd_sn_repos where attribute_34 in (select substr(attribute_34, 0, 10) from upd_sn_repos where serial_no = "+
						"'" + serialIn + "')";
				logger.info("query : = " + query);

				ds = DBUtil.getOracleDataSource();
				conn  = ds.getConnection();
				logger.info("connection established for oracle: " + conn);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				while(rs.next()){

					pCBASerialNumberModel.setMsnStatus(rs.getString(1));
					pCBASerialNumberModel.setNewSN(rs.getString(2));
					pCBASerialNumberModel.setSerialStatus(rs.getString(3));

				}

			}catch(Exception e){
				e.printStackTrace();
			}finally{

				DBUtil.closeConnections(conn, stmt, rs);
			}
		}else{

			try{
				String query = " select u3.msn,u1.swap_ref_no,u2.status_code from " +
						" upd.upd_repair u1, upd.upd_warranty_info u2 ,"+ 
						" upd.upd_factory_shipment_info u3 where u3.serial_no = u2.serial_no "+
						" and u2.serial_no = u1.serial_no and u1.serial_no = " +
						"'" + serialIn + "'";
				logger.info("query : = " + query);
				ds = DBUtil.getMySqlDataSource();
				conn  = ds.getConnection();
				logger.info("connection established for mysql: " + conn);
				stmt = conn.createStatement(); 
				rs = stmt.executeQuery(query);
				while(rs.next()){

					pCBASerialNumberModel.setMsnStatus(rs.getString(1));
					pCBASerialNumberModel.setNewSN(rs.getString(2));
					pCBASerialNumberModel.setSerialStatus(rs.getString(3));

				}

			}catch(Exception e){

				e.printStackTrace();
			}finally{

				DBUtil.closeConnections(conn, stmt, rs);
			}
		}
		return pCBASerialNumberModel;

	}
}