/**
 * 
 */
package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateQueryInput;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateResponse;
import com.mot.upd.pcba.utils.DBUtil;


/**
 * @author rviswa
 *
 */
public class PCBASwapUPDUpdateOracleDAO implements PCBASwapUPDUpdateInterfaceDAO{
	private static Logger logger = Logger.getLogger(PCBASwapUPDUpdateOracleDAO.class);

	
	private DataSource ds;
	private Connection con = null;
	private Connection connection = null;
	private Connection conn = null;
	private PreparedStatement preparedStmt = null;
	private PreparedStatement pstmt = null;
	private PreparedStatement prestmt = null;
	private ResultSet rs=null;
	

	PCBASerialNoUPdateResponse response = new PCBASerialNoUPdateResponse();
	public PCBASerialNoUPdateResponse serialNumberInfo(PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput){
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:"+e);
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}
		
		try {
			// get database connection
			con = DBUtil.getConnection(ds);
			
			StringBuffer sb=new StringBuffer();
			
			sb.append("select  SERIAL_NO, REQUEST_ID, REGION_ID, SYSTEM_ID, ATTRIBUTE_01, ATTRIBUTE_02, ATTRIBUTE_03, ATTRIBUTE_04, ATTRIBUTE_05,ATTRIBUTE_06,  ATTRIBUTE_07,  ATTRIBUTE_08,");  
			sb.append("ATTRIBUTE_09, ATTRIBUTE_10,   ATTRIBUTE_11,  ATTRIBUTE_12,  ATTRIBUTE_13,  ATTRIBUTE_14, ATTRIBUTE_15,  ATTRIBUTE_16,  ATTRIBUTE_17,  ATTRIBUTE_18,  ATTRIBUTE_19,");
			sb.append("ATTRIBUTE_20,  ATTRIBUTE_21,  ATTRIBUTE_22,  ATTRIBUTE_23,  ATTRIBUTE_24,  ATTRIBUTE_34, ATTRIBUTE_35,  ATTRIBUTE_37,  ATTRIBUTE_38,  ATTRIBUTE_39,  ATTRIBUTE_40,");
			sb.append("ATTRIBUTE_41,  ATTRIBUTE_42,  ATTRIBUTE_43,  ATTRIBUTE_44,  ATTRIBUTE_45,  ATTRIBUTE_46, ATTRIBUTE_47,  ATTRIBUTE_48,  ATTRIBUTE_49,  ATTRIBUTE_50,  ATTRIBUTE_51,");
			sb.append("ATTRIBUTE_52,  ATTRIBUTE_53,  ATTRIBUTE_54,  ATTRIBUTE_55,  ATTRIBUTE_56,  ATTRIBUTE_57, ATTRIBUTE_58,  ATTRIBUTE_59,  ATTRIBUTE_60,  ATTRIBUTE_61,  ATTRIBUTE_62,");
			sb.append("ATTRIBUTE_63,  ATTRIBUTE_64,  ATTRIBUTE_65,  ATTRIBUTE_66,  ATTRIBUTE_67,  ATTRIBUTE_68, ATTRIBUTE_69,  ATTRIBUTE_70,  ATTRIBUTE_71,  ATTRIBUTE_72,  ATTRIBUTE_73,");
			sb.append("ATTRIBUTE_74,  ATTRIBUTE_75,  ATTRIBUTE_76,  ATTRIBUTE_77,  ATTRIBUTE_78,  ATTRIBUTE_79, ATTRIBUTE_80,  ATTRIBUTE_81,  ATTRIBUTE_82,  ATTRIBUTE_84,  ATTRIBUTE_85,");
			sb.append("ATTRIBUTE_86,  ATTRIBUTE_87,  ATTRIBUTE_88,  ATTRIBUTE_89,  ATTRIBUTE_90,  ATTRIBUTE_91, ATTRIBUTE_92,  ATTRIBUTE_93,  ATTRIBUTE_94,  ATTRIBUTE_95,  ATTRIBUTE_96,");
			sb.append("ATTRIBUTE_97,  ATTRIBUTE_98,  ATTRIBUTE_99,  ATTRIBUTE_100, ATTRIBUTE_101, ATTRIBUTE_105,ATTRIBUTE_106, ATTRIBUTE_107, ATTRIBUTE_108, ATTRIBUTE_109, ATTRIBUTE_110,");
			sb.append("ATTRIBUTE_111, ATTRIBUTE_112, ATTRIBUTE_113, ATTRIBUTE_117, ATTRIBUTE_118, ATTRIBUTE_114,ATTRIBUTE_115, ATTRIBUTE_116, ATTRIBUTE_119, ATTRIBUTE_120, ATTRIBUTE_121,");
			sb.append("ATTRIBUTE_122, ATTRIBUTE_123 from UPD_SN_REPOS  where serial_no=?");
			
			preparedStmt = con.prepareStatement(sb.toString());
			preparedStmt.setString(1,pCBASerialNoUPdateQueryInput.getSerialNoIn());
			rs=preparedStmt.executeQuery();
			
			
			if(rs.next()){
				
				connection = DBUtil.getConnection(ds);
				
				StringBuffer SQLQuery=new StringBuffer();
								
				SQLQuery.append("insert into UPD_SN_REPOS SERIAL_NO, REQUEST_ID, REGION_ID, SYSTEM_ID, ATTRIBUTE_01, ATTRIBUTE_02, ATTRIBUTE_03, ATTRIBUTE_04, ATTRIBUTE_05,ATTRIBUTE_06,  ATTRIBUTE_07,  ATTRIBUTE_08,");  
				SQLQuery.append("ATTRIBUTE_09, ATTRIBUTE_10,   ATTRIBUTE_11,  ATTRIBUTE_12,  ATTRIBUTE_13,  ATTRIBUTE_14, ATTRIBUTE_15,  ATTRIBUTE_16,  ATTRIBUTE_17,  ATTRIBUTE_18,  ATTRIBUTE_19,");
				SQLQuery.append("ATTRIBUTE_20,  ATTRIBUTE_21,  ATTRIBUTE_22,  ATTRIBUTE_23,  ATTRIBUTE_24,  ATTRIBUTE_34, ATTRIBUTE_35,  ATTRIBUTE_37,  ATTRIBUTE_38,  ATTRIBUTE_39,  ATTRIBUTE_40,");
				SQLQuery.append("ATTRIBUTE_41,  ATTRIBUTE_42,  ATTRIBUTE_43,  ATTRIBUTE_44,  ATTRIBUTE_45,  ATTRIBUTE_46, ATTRIBUTE_47,  ATTRIBUTE_48,  ATTRIBUTE_49,  ATTRIBUTE_50,  ATTRIBUTE_51,");
				SQLQuery.append("ATTRIBUTE_52,  ATTRIBUTE_53,  ATTRIBUTE_54,  ATTRIBUTE_55,  ATTRIBUTE_56,  ATTRIBUTE_57, ATTRIBUTE_58,  ATTRIBUTE_59,  ATTRIBUTE_60,  ATTRIBUTE_61,  ATTRIBUTE_62,");
				SQLQuery.append("ATTRIBUTE_63,  ATTRIBUTE_64,  ATTRIBUTE_65,  ATTRIBUTE_66,  ATTRIBUTE_67,  ATTRIBUTE_68, ATTRIBUTE_69,  ATTRIBUTE_70,  ATTRIBUTE_71,  ATTRIBUTE_72,  ATTRIBUTE_73,");
				SQLQuery.append("ATTRIBUTE_74,  ATTRIBUTE_75,  ATTRIBUTE_76,  ATTRIBUTE_77,  ATTRIBUTE_78,  ATTRIBUTE_79, ATTRIBUTE_80,  ATTRIBUTE_81,  ATTRIBUTE_82,  ATTRIBUTE_84,  ATTRIBUTE_85,");
				SQLQuery.append("ATTRIBUTE_86,  ATTRIBUTE_87,  ATTRIBUTE_88,  ATTRIBUTE_89,  ATTRIBUTE_90,  ATTRIBUTE_91, ATTRIBUTE_92,  ATTRIBUTE_93,  ATTRIBUTE_94,  ATTRIBUTE_95,  ATTRIBUTE_96,");
				SQLQuery.append("ATTRIBUTE_97,  ATTRIBUTE_98,  ATTRIBUTE_99,  ATTRIBUTE_100, ATTRIBUTE_101, ATTRIBUTE_105,ATTRIBUTE_106, ATTRIBUTE_107, ATTRIBUTE_108, ATTRIBUTE_109, ATTRIBUTE_110,");
				SQLQuery.append("ATTRIBUTE_111, ATTRIBUTE_112, ATTRIBUTE_113, ATTRIBUTE_117, ATTRIBUTE_118, ATTRIBUTE_114,ATTRIBUTE_115, ATTRIBUTE_116, ATTRIBUTE_119, ATTRIBUTE_120, ATTRIBUTE_121,");
				SQLQuery.append("ATTRIBUTE_122, ATTRIBUTE_123 values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
				SQLQuery.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) where serial_no=?");
				pstmt = connection.prepareStatement(SQLQuery.toString());
				pstmt.setString(1, rs.getString("SERIAL_NO"));				
				pstmt.setString(2,rs.getString("REQUEST_ID"));
				pstmt.setString(3,rs.getString("REGION_ID"));
				pstmt.setString(4,rs.getString("SYSTEM_ID"));
				pstmt.setString(5,rs.getString("ATTRIBUTE_01"));
				pstmt.setDate(6,rs.getDate("ATTRIBUTE_02"));
				pstmt.setString(7,rs.getString("ATTRIBUTE_03"));
				pstmt.setString(8,rs.getString("ATTRIBUTE_04"));
				pstmt.setString(9,rs.getString("ATTRIBUTE_05"));
				pstmt.setString(10,rs.getString("ATTRIBUTE_06")); 
				pstmt.setString(11,rs.getString("ATTRIBUTE_07"));
				pstmt.setString(12,rs.getString("ATTRIBUTE_08"));
				pstmt.setString(13,rs.getString("ATTRIBUTE_09"));
				pstmt.setDate(14,rs.getDate("ATTRIBUTE_10")); 
				pstmt.setString(15,rs.getString("ATTRIBUTE_11")); 
				pstmt.setString(16,rs.getString("ATTRIBUTE_12"));
				pstmt.setString(17,rs.getString("ATTRIBUTE_13")); 
				pstmt.setString(18,rs.getString("ATTRIBUTE_14"));
				pstmt.setString(19,rs.getString("ATTRIBUTE_15")); 
				pstmt.setString(20,rs.getString("ATTRIBUTE_16"));
				pstmt.setString(21,rs.getString("ATTRIBUTE_17"));
				pstmt.setDate(22,rs.getDate("ATTRIBUTE_18"));
				pstmt.setString(23,rs.getString("ATTRIBUTE_19"));
				pstmt.setString(24,rs.getString("ATTRIBUTE_20"));
				pstmt.setString(25,rs.getString("ATTRIBUTE_21"));
				pstmt.setString(26,rs.getString("ATTRIBUTE_22"));
				pstmt.setString(27,rs.getString("ATTRIBUTE_23")); 
				pstmt.setString(28,rs.getString("ATTRIBUTE_24"));
				pstmt.setString(29,rs.getString("ATTRIBUTE_34"));
				pstmt.setString(30,rs.getString("ATTRIBUTE_35")); 
				pstmt.setString(31,rs.getString("ATTRIBUTE_37"));
				pstmt.setString(32,rs.getString("ATTRIBUTE_38")); 
				pstmt.setString(33,rs.getString("ATTRIBUTE_39")); 
				pstmt.setString(34,rs.getString("ATTRIBUTE_40"));
				pstmt.setString(35,rs.getString("ATTRIBUTE_41")); 
				pstmt.setString(36,rs.getString("ATTRIBUTE_42")); 
				pstmt.setString(37,rs.getString("ATTRIBUTE_43")); 
				pstmt.setString(38,rs.getString("ATTRIBUTE_44")); 
				pstmt.setString(39,rs.getString("ATTRIBUTE_45")); 
				pstmt.setString(40,rs.getString("ATTRIBUTE_46"));
				pstmt.setString(41,rs.getString("ATTRIBUTE_47")); 
				pstmt.setString(42,rs.getString("ATTRIBUTE_48")); 
				pstmt.setString(43,rs.getString("ATTRIBUTE_49")); 
				pstmt.setString(44,rs.getString("ATTRIBUTE_50"));
				pstmt.setString(45,rs.getString("ATTRIBUTE_51"));
				pstmt.setString(46,rs.getString("ATTRIBUTE_52"));
				pstmt.setString(47,rs.getString("ATTRIBUTE_53"));
				pstmt.setString(48,rs.getString("ATTRIBUTE_54")); 
				pstmt.setString(49,rs.getString("ATTRIBUTE_55")); 
				pstmt.setString(50,rs.getString("ATTRIBUTE_56")); 
				pstmt.setString(51,rs.getString("ATTRIBUTE_57"));
				pstmt.setString(52,rs.getString("ATTRIBUTE_58")); 
				pstmt.setString(53,rs.getString("ATTRIBUTE_59"));
				pstmt.setString(54,rs.getString("ATTRIBUTE_60")); 
				pstmt.setString(55,rs.getString("ATTRIBUTE_61")); 
				pstmt.setString(56,rs.getString("ATTRIBUTE_62"));
				pstmt.setString(57,rs.getString("ATTRIBUTE_63")); 
				pstmt.setString(58,rs.getString("ATTRIBUTE_64")); 
				pstmt.setString(59,rs.getString("ATTRIBUTE_65")); 
				pstmt.setString(60,rs.getString("ATTRIBUTE_66")); 
				pstmt.setString(61,rs.getString("ATTRIBUTE_67")); 
				pstmt.setString(62,rs.getString("ATTRIBUTE_68"));
				pstmt.setString(63,rs.getString("ATTRIBUTE_69")); 
				pstmt.setString(64,rs.getString("ATTRIBUTE_70")); 
				pstmt.setString(65,rs.getString("ATTRIBUTE_71")); 
				pstmt.setString(66,rs.getString("ATTRIBUTE_72")); 
				pstmt.setString(67,rs.getString("ATTRIBUTE_73"));
				pstmt.setString(68,rs.getString("ATTRIBUTE_74")); 
				pstmt.setString(69,rs.getString("ATTRIBUTE_75")); 
				pstmt.setString(70,rs.getString("ATTRIBUTE_76")); 
				pstmt.setString(71,rs.getString("ATTRIBUTE_77")); 
				pstmt.setString(72,rs.getString("ATTRIBUTE_78"));  
				pstmt.setString(73,rs.getString("ATTRIBUTE_79"));
				pstmt.setString(74,rs.getString("ATTRIBUTE_80")); 
				pstmt.setString(75,rs.getString("ATTRIBUTE_81")); 
				pstmt.setString(76,rs.getString("ATTRIBUTE_82")); 
				pstmt.setString(77,rs.getString("ATTRIBUTE_84")); 
				pstmt.setString(78,rs.getString("ATTRIBUTE_85"));
				pstmt.setString(79,rs.getString("ATTRIBUTE_86")); 
				pstmt.setString(80,rs.getString("ATTRIBUTE_87")); 
				pstmt.setString(81,rs.getString("ATTRIBUTE_88")); 
				pstmt.setString(82,rs.getString("ATTRIBUTE_89")); 
				pstmt.setString(83,rs.getString("ATTRIBUTE_90")); 
				pstmt.setString(84,rs.getString("ATTRIBUTE_91"));
				pstmt.setString(85,rs.getString("ATTRIBUTE_92")); 
				pstmt.setString(86,rs.getString("ATTRIBUTE_93")); 
				pstmt.setString(87,rs.getString("ATTRIBUTE_94")); 
				pstmt.setString(88,rs.getString("ATTRIBUTE_95")); 
				pstmt.setString(89,rs.getString("ATTRIBUTE_96"));
				pstmt.setString(90,rs.getString("ATTRIBUTE_97")); 
				pstmt.setString(91,rs.getString("ATTRIBUTE_98")); 
				pstmt.setString(92,rs.getString("ATTRIBUTE_99")); 
				pstmt.setString(93,rs.getString("ATTRIBUTE_100")); 
				pstmt.setString(94,rs.getString("ATTRIBUTE_101"));
				pstmt.setString(95,rs.getString("ATTRIBUTE_105"));
				pstmt.setString(96,rs.getString("ATTRIBUTE_106"));
				pstmt.setString(97,rs.getString("ATTRIBUTE_107"));
				pstmt.setString(98,rs.getString("ATTRIBUTE_108"));
				pstmt.setString(99,rs.getString("ATTRIBUTE_109"));
				pstmt.setString(100,rs.getString("ATTRIBUTE_110"));
				pstmt.setString(101,rs.getString("ATTRIBUTE_111"));
				pstmt.setString(102,rs.getString("ATTRIBUTE_112"));
				pstmt.setString(103,rs.getString("ATTRIBUTE_113"));
				pstmt.setString(104,rs.getString("ATTRIBUTE_117"));
				pstmt.setString(105,rs.getString("ATTRIBUTE_118"));
				pstmt.setString(106,rs.getString("ATTRIBUTE_114"));
				pstmt.setString(107,rs.getString("ATTRIBUTE_115"));
				pstmt.setString(108,rs.getString("ATTRIBUTE_116"));
				pstmt.setString(109,rs.getString("ATTRIBUTE_119"));
				pstmt.setString(110,rs.getString("ATTRIBUTE_120")); 
				pstmt.setString(111,rs.getString("ATTRIBUTE_121"));
				pstmt.setString(112,rs.getString("ATTRIBUTE_122"));
				pstmt.setString(113,rs.getString("ATTRIBUTE_123"));
				pstmt.setString(114, pCBASerialNoUPdateQueryInput.getSerialNoOut());				
				pstmt.execute();
				
				response.setResponseCode(ServiceMessageCodes.SUCCESS);
				response.setResponseMessage(ServiceMessageCodes.READING_OLD_SERIAL_NO_INTO_NEW_SERIAL_NO);
				
			}else{
				conn = DBUtil.getConnection(ds);				
				StringBuffer stb=new StringBuffer();
				stb.append("insert into shipment_notavail_sn(SERIAL_NO_IN,SERIAL_NO_OUT,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,STATUS) values(?,?,?,?,?,?,?)");
				prestmt=conn.prepareStatement(stb.toString());
				prestmt.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				prestmt.setString(2, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				prestmt.setString(3, "PCBA_PGM");
				prestmt.setDate(4, new Date(System.currentTimeMillis()));
				prestmt.setString(5, "PCBA_PGM");				
				prestmt.setDate(6, new Date(System.currentTimeMillis()));
				prestmt.setString(7,"S");
				prestmt.execute();
				response.setResponseCode(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_SHIPMENT_TABLE);
				response.setResponseMessage(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_SHIPMENT_TABLE_MSG);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DBUtil.closeConnection(con, preparedStmt, rs);
			DBUtil.connectionClosed(connection, pstmt);
			DBUtil.connectionClosed(conn, prestmt);
		}
		
		
		return response;
	}


}
