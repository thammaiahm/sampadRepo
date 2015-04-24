package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.mot.upd.pcba.handler.PCBASerialNumberModel;
import com.mot.upd.pcba.utils.DBUtil;





public class R12SnSwapUpdateDAO {
		
	//PropertyResourceBundle bundle = InitProperty.getProperty("pcbaSql.properties");
	public PCBASerialNumberModel fetchR12SerialOutValue(String serialIn){
		
		String serialOut = null;
		DataSource ds;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PCBASerialNumberModel pCBASerialNumberModel = new PCBASerialNumberModel();
		//String query = "select attribute_34, attribute_41, from UPD.UPD_SN_REPOS where SERIAL_NO = " +
			//	"'" + serialIn +"' and attribute_34 like('%SCR%')";
		//query need to modify
		String query=" select attribute_34, attribute_41, attribute_37 from upd_sn_repos where attribute_34 in (select substr(attribute_34, 0, 10) from upd_sn_repos where serial_no = "+
				"'" + serialIn + "')";
		try{
			//String queryForR12Swap = bundle.getString("r12SwapUpdateQuery");
			//System.out.println("query " + queryForR12Swap);
				Context ctx = new InitialContext();
				ds = (DataSource) ctx.lookup("java:jboss/jdbc/updJNDI");
				conn  = ds.getConnection();
				System.out.println("connection " + conn);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
			while(rs.next()){
			
				pCBASerialNumberModel.setSerialStatus(rs.getString(1));
				pCBASerialNumberModel.setNewSN(rs.getString(2));
				pCBASerialNumberModel.setSerialStatus(rs.getString(3));
				
			}

			
		}catch(Exception e){
			e.printStackTrace();
		
		}finally{
			DBUtil.closeConnections(conn, stmt, rs);
		}
			
		return pCBASerialNumberModel;

	}
}