/**
 * 
 */
package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class PCBASwapUPDUpdateSQLDAO implements PCBASwapUPDUpdateInterfaceDAO{
	private static Logger logger = Logger.getLogger(PCBASwapUPDUpdateSQLDAO.class);

	private DataSource ds;
	private Connection con = null;
	private Connection connection = null;
	private Connection conn = null;
	private PreparedStatement preparedStmt = null;
	private PreparedStatement pstmt = null;
	private PreparedStatement prestmt = null;
	private ResultSet rs=null;
	private Connection con1=null;
	private Connection con2=null;
	private Connection con3=null;
	private Connection con4=null;
	private Connection con5=null;
	private PreparedStatement pstmt1 = null;
	private PreparedStatement pstmt2 = null;
	private PreparedStatement pstmt3 = null;
	private PreparedStatement pstmt4 = null;
	private PreparedStatement pstmt5 = null;
	

	PCBASerialNoUPdateResponse response = new PCBASerialNoUPdateResponse();
	public PCBASerialNoUPdateResponse serialNumberInfo(PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput){
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:"+e);
			response.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}
		
		try {
			// get database connection
			con = DBUtil.getConnection(ds);
			
			String mySQLQuery="select fsi.factory_code,fsi.gen_date,fsi.protocol,fsi.apc,fsi.trans_model,fsi.cust_model,fsi.mkt_model,fsi.item_code,fsi.warr_code,fsi.ship_date,"			
			+"fsi.ship_to_cust_id,fsi.ship_to_cust_addr,fsi.ship_to_cust_name,fsi.ship_to_cust_city,fsi.ship_to_cust_country,fsi.sold_to_cust_id,fsi.sold_to_cust_name,"	
			+"fsi.sold_date,fsi.cit,fsi.ta_no,fsi.carton_id,fsi.po_no,fsi.so_no,fsi.fo_sequence,fsi.msn,r.csn,wi.status_code,wi.orig_warr_eff_date,wi.ren_warr_code,"
			+"r.cancel_code,r.swap_ref_no,r.swap_count,r.delete_flag,r.org_code,r.upd_time,r.dn,r.era,r.rma,r.receive_date,r.delivery_date,r.return_date,r.rma_date,"
			+"r.scrap_date,r.cust_id,r.warehouse_id,r.shop_id,r.status_id,r.action,r.remarks,dc.handset_type,dc.flex_option,dc.flex_sw,dc.hw,dc.icc_id,ds.so_line_no,"			
			+"ds.ds_region_code,ds.ds_so_no,ds.ds_po_no,ds.ds_cust_id,ds.ds_bill_to_id,ds.ds_ship_to_id,ds.ds_cust_country_code,ds.ds_cust_name,ds.bill_to_id,ds.shipment_no,"			
			+"ds.phone_no,ds.wip_dj,ds.sale_date,ds.last_imei,r.swap_date,dc.wlan,lc.meid_evdo_password,lc.meid_a_key2_type,lc.meid_a_key2,dc.fastt_id,dc.base_processor_id,"
			+"fsi.location_type,fsi.packing_list,fsi.fab_date,dc.software_version,wi.warr_country_code,wi.warr_region,wi.orig_ship_date,wi.reference_key,dc.wimax,dc.hsn,"
			+"m.a_key_index,m.cas_no,lc.imc_lock_code,dc.flash_uid,fsi.imc_mfg_location,fsi.guid,fsi.pdb_id,dc.dual_serial_no,dc.dual_serial_no_type,wi.pop_in_sysdate,"
			+"wi.pop_date,wi.pop_identifier,r.last_repair_date,r.repair_count,fsi.assign_date,fsi.gpp_id,fsi.product_type"
			+" from upd.upd_factory_shipment_info fsi,upd.upd_repair r,upd.upd_warranty_info wi,upd.upd_device_config dc,upd.upd_direct_shipment ds,upd.upd_lock_code lc,upd.upd_meid m LIMIT 1";
			
			preparedStmt = con.prepareStatement(mySQLQuery);
			preparedStmt.setString(1,pCBASerialNoUPdateQueryInput.getSerialNoIn());
			rs=preparedStmt.executeQuery();
			
			if(rs.next()){
				
				
				
				
				String MySql_updFactoryShipmentInfo ="insert into upd_factory_shipment_info (serial_no,factory_code,gen_date,protocol,apc,trans_model,cust_model,mkt_model,item_code,warr_code," +
						"ship_date,ship_to_cust_id,ship_to_cust_addr,ship_to_cust_name,ship_to_cust_city,ship_to_cust_country,sold_to_cust_id,sold_to_cust_name,sold_date," +
						"cit,ta_no,carton_id,po_no,so_no,fo_sequence,msn,assign_date,gpp_id,product_type,location_type,packing_list,fab_date,imc_mfg_location,guid,pdb_id"+
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt1=con1.prepareStatement("MySql_updRepair");
				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				pstmt1.setString(2, rs.getString("factory_code"));
				pstmt1.setDate(3, rs.getDate("gen_date"));
				pstmt1.setString(4, rs.getString("protocol"));
				pstmt1.setString(5, rs.getString("apc"));
				pstmt1.setString(6, rs.getString("trans_model"));
				pstmt1.setString(7, rs.getString("cust_model"));
				pstmt1.setString(8, rs.getString("mkt_model"));
				pstmt1.setString(9, rs.getString("item_code"));
				pstmt1.setString(10, rs.getString("warr_code"));
				pstmt1.setDate(11, rs.getDate("ship_date"));
				pstmt1.setString(12, rs.getString("ship_to_cust_id"));
				pstmt1.setString(13, rs.getString("ship_to_cust_addr"));
				pstmt1.setString(14, rs.getString("ship_to_cust_name"));
				pstmt1.setString(15, rs.getString("ship_to_cust_city"));
				pstmt1.setString(16, rs.getString("ship_to_cust_country"));
				pstmt1.setString(17, rs.getString("sold_to_cust_id"));
				pstmt1.setString(18, rs.getString("sold_to_cust_name"));
				pstmt1.setDate(19, rs.getDate("sold_date"));
				pstmt1.setString(20, rs.getString("cit"));
				pstmt1.setString(21, rs.getString("ta_no"));
				pstmt1.setString(22, rs.getString("carton_id"));
				pstmt1.setString(23, rs.getString("po_no"));
				pstmt1.setString(24, rs.getString("so_no"));
				pstmt1.setString(25, rs.getString("fo_sequence"));
				pstmt1.setDate(26, rs.getDate("assign_date"));
				pstmt1.setString(27, rs.getString("gpp_id"));
				pstmt1.setString(28, rs.getString("product_type"));
				pstmt1.setString(29, rs.getString("product_type"));
				pstmt1.setString(30, rs.getString("location_type"));
				pstmt1.setString(31, rs.getString("packing_list"));
				pstmt1.setDate(32, rs.getDate("fab_date"));
				pstmt1.setString(33, rs.getString("imc_mfg_location"));
				pstmt1.setString(34, rs.getString("guid"));
				pstmt1.setString(35, "pdb_id");
				pstmt1.execute();
				String MySql_updRepair="insert into upd_repair (serial_no,cancel_code,swap_ref_no,swap_count,delete_flag,org_code,upd_time,dn,era,rma,receive_date,"+
						"delivery_date,return_date,rma_date,scrap_date,cust_id,warehouse_id,shop_id,status_id,action,remarks,csn,last_repair_date,repair_count,swap_date"+
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt2=con2.prepareStatement("MySql_updRepair");
				pstmt2.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				pstmt2.setString(2, rs.getString("cancel_code"));
				pstmt2.setString(3, rs.getString("swap_ref_no"));
				pstmt2.setString(4, rs.getString("swap_count"));
				pstmt2.setString(5, rs.getString("delete_flag"));
				pstmt2.setString(6, rs.getString("org_code"));
				pstmt2.setString(7, rs.getString("upd_time"));
				pstmt2.setString(8, rs.getString("dn"));
				pstmt2.setString(9, rs.getString("era"));
				pstmt2.setString(10, rs.getString("rma"));
				pstmt2.setDate(11, rs.getDate("receive_date"));
				pstmt2.setDate(12, rs.getDate("delivery_date"));
				pstmt2.setDate(13, rs.getDate("return_date"));
				pstmt2.setDate(14, rs.getDate("rma_date"));
				pstmt2.setDate(15,rs.getDate("scrap_date"));
				pstmt2.setString(16, rs.getString("cust_id"));
				pstmt2.setString(17, rs.getString("warehouse_id"));
				pstmt2.setString(18, rs.getString("shop_id"));
				pstmt2.setString(19, rs.getString("status_id"));
				pstmt2.setString(20, rs.getString("action"));
				pstmt2.setString(21, rs.getString("remarks"));
				pstmt2.setString(22, rs.getString("csn"));
				pstmt2.setDate(23, rs.getDate("last_repair_date"));
				pstmt2.setString(24, rs.getString("repair_count"));
				pstmt2.setDate(25, rs.getDate("swap_date"));
				pstmt2.execute();
				
				String MySql_updWarrantyInfo="insert into upd_warranty_info (serial_no,status_code,orig_warr_eff_date,ren_warr_code,warr_country_code,warr_region,orig_ship_date,reference_key,"+
						"pop_in_sysdate,pop_date,pop_identifier values(?,?,?,?,?,?,?,?,?,?,?)";
				pstmt3=con3.prepareStatement(MySql_updWarrantyInfo);
				pstmt3.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				pstmt3.setString(2, rs.getString("status_code"));
				pstmt3.setDate(3, rs.getDate("orig_warr_eff_date"));
				pstmt3.setDate(4, rs.getDate("ren_warr_code"));
				pstmt3.setString(5, rs.getString("warr_country_code"));
				pstmt3.setString(6, rs.getString("warr_region"));
				pstmt3.setDate(7, rs.getDate("orig_ship_date"));
				pstmt3.setString(8, rs.getString("reference_key"));
				pstmt3.setDate(9, rs.getDate("pop_in_sysdate"));
				pstmt3.setDate(10, rs.getDate("pop_date"));
				pstmt3.setString(11, rs.getString("pop_identifier"));
				pstmt3.execute();
				
				String MySql_updDeviceConfig="insert into upd_device_config (serial_no,handset_type,flex_option,flex_sw,hw,icc_id,software_version,wimax,hsn,flash_uid,dual_serial_no,"+
				"dual_serial_no_type,fastt_id,base_processor_id,wlan values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt4=con4.prepareStatement(MySql_updDeviceConfig);
				pstmt4.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				pstmt4.setString(2, rs.getString("handset_type"));
				pstmt4.setString(3, rs.getString("flex_option"));
				pstmt4.setString(4, rs.getString("flex_sw"));
				pstmt4.setString(5, rs.getString("hw"));
				pstmt4.setString(6, rs.getString("icc_id"));
				pstmt4.setString(7, rs.getString("software_version"));
				pstmt4.setString(8, rs.getString("wimax"));
				pstmt4.setString(9, rs.getString("hsn"));
				pstmt4.setString(10, rs.getString("flash_uid"));
				pstmt4.setString(11, rs.getString("dual_serial_no"));
				pstmt4.setString(12, rs.getString("dual_serial_no_type"));
				pstmt4.setString(13, rs.getString("fastt_id"));
				pstmt4.setString(14, rs.getString("base_processor_id"));
				pstmt4.setString(15, rs.getString("wlan"));
				pstmt4.execute();
				
				
				String MySql_updDirectShipment="insert into upd_direct_shipment (serial_no,so_line_no,ds_region_code,ds_so_no,ds_po_no,ds_cust_id,ds_bill_to_id,ds_ship_to_id,ds_cust_country_code,"+
				"ds_cust_name,bill_to_id,shipment_no,phone_no,wip_dj,sale_date,last_imei values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt5=con5.prepareStatement("MySql_updDirectShipment");
				pstmt5.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				pstmt5.setString(2, rs.getString("so_line_no"));
				pstmt5.setString(3, rs.getString("ds_region_code"));
				pstmt5.setString(4, rs.getString("ds_so_no"));
				pstmt5.setString(5, rs.getString("ds_po_no"));
				pstmt5.setString(6, rs.getString("ds_cust_id"));
				pstmt5.setString(7, rs.getString("ds_bill_to_id"));
				pstmt5.setString(8, rs.getString("ds_ship_to_id"));
				pstmt5.setString(9, rs.getString("ds_cust_country_code"));
				pstmt5.setString(10, rs.getString("ds_cust_name"));
				pstmt5.setString(11, rs.getString("bill_to_id"));
				pstmt5.setString(12, rs.getString("shipment_no"));
				pstmt5.setString(13, rs.getString("phone_no"));
				pstmt5.setString(14, rs.getString("wip_dj"));
				pstmt5.setDate(15, rs.getDate("sale_date"));
				pstmt5.setString(6, rs.getString("last_imei"));
				pstmt5.execute();
								
				String MySql_updLockCode="insert into upd_lock_code (serial_no,meid_evdo_password,meid_a_key2_type,meid_a_key2,imc_lock_code) values(?,?,?,?,?)";
				prestmt=connection.prepareStatement(MySql_updLockCode);
				prestmt.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				prestmt.setString(2, rs.getString("meid_evdo_password"));
				prestmt.setString(3, rs.getString("meid_a_key2_type"));
				prestmt.setString(4, rs.getString("meid_a_key2"));
				prestmt.setString(5, rs.getString("imc_lock_code"));
				prestmt.execute();
				
				
				String Sql_updMeid =" insert into upd_meid (serial_no,a_key_index,cas_no) values(?,?,?)";
				pstmt=conn.prepareStatement(Sql_updMeid);
				pstmt.setString(1,pCBASerialNoUPdateQueryInput.getSerialNoIn());
				pstmt.setString(2,rs.getString("a_key_index"));
				pstmt.setString(3,rs.getString("cas_no"));
				pstmt.execute();
				response.setResponseCode(ServiceMessageCodes.SUCCESS);
				response.setResponseMessage(ServiceMessageCodes.IMEI_SUCCES_MSG);
				
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
				
				response.setResponseCode(ServiceMessageCodes.SUCCESS);
				response.setResponseMessage(ServiceMessageCodes.IMEI_SUCCES_MSG);

				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DBUtil.closeConnection(con, preparedStmt, rs);
			DBUtil.connectionClosed(connection, pstmt);
			DBUtil.connectionClosed(conn, prestmt);
			DBUtil.connectionClosed(con1, pstmt1);
			DBUtil.connectionClosed(con2, pstmt2);
			DBUtil.connectionClosed(con3, pstmt3);
			DBUtil.connectionClosed(con4, pstmt4);
			DBUtil.connectionClosed(con5, pstmt5);
		}
		
		
		return response;
	}


				

}
