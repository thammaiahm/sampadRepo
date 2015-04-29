package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.handler.PCBASerialNumberModel;
import com.mot.upd.pcba.utils.DBUtil;





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
		logger.info("updConfig value : = " + updConfig);
		if(updConfig.equals(PCBADataDictionary.DBCONFIG)){
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
				String query = " select fsinfo.msn,repair.swap_ref_no,winfo.status_code from " +
						" upd.upd_repair repair, upd.upd_warranty_info winfo ,"+ 
						" upd.upd_factory_shipment_info fsinfo where fsinfo.serial_no = winfo.serial_no "+
						"and repair.serial_no != repair.swap_ref_no "+									
						" and winfo.serial_no = repair.serial_no " +
						 "and fsinfo.msn in (select substr(msn, 1, 10) from upd.upd_factory_shipment_info where serial_no ="+
						"'" + serialIn + "')";
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
			logger.info("PCBASerialNumberModel : " + pCBASerialNumberModel.toString());
			return pCBASerialNumberModel;
		}
}