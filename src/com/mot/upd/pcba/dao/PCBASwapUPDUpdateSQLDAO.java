/**
 * 
 */
package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

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
	private Connection conn = null;
	private Connection con1=null;
	private PreparedStatement preparedStmt = null;
	private PreparedStatement prestmt = null;
	private PreparedStatement pstmt1 = null;
	private ResultSet rs=null;

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
			//preparedStmt.setString(1,pCBASerialNoUPdateQueryInput.getSerialNoIn());
			rs=preparedStmt.executeQuery();

			if(rs.next()){

				String MySql_updFactoryShipmentInfo ="insert into upd.upd_factory_shipment_info(serial_no,factory_code,gen_date,protocol,apc,trans_model,cust_model,mkt_model,item_code,warr_code," +
						"ship_date,ship_to_cust_id,ship_to_cust_addr,ship_to_cust_name,ship_to_cust_city,ship_to_cust_country,sold_to_cust_id,sold_to_cust_name,sold_date," +
						"cit,ta_no,carton_id,po_no,so_no,fo_sequence,msn,assign_date,gpp_id,product_type,location_type,packing_list,fab_date,imc_mfg_location,guid,pdb_id,created_datetime,created_by)"+
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),'pcba_pgm_success')";

				con1 = DBUtil.getConnection(ds);
				con1.setAutoCommit(false);
				pstmt1=con1.prepareStatement(MySql_updFactoryShipmentInfo);

				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoOut());
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
				pstmt1.setString(26, rs.getString("msn"));
				pstmt1.setDate(27, rs.getDate("assign_date"));
				pstmt1.setString(28, rs.getString("gpp_id"));
				pstmt1.setString(29, rs.getString("product_type"));
				pstmt1.setString(30, rs.getString("location_type"));
				pstmt1.setString(31, rs.getString("packing_list"));
				pstmt1.setDate(32, rs.getDate("fab_date"));
				pstmt1.setString(33, rs.getString("imc_mfg_location"));
				pstmt1.setString(34, rs.getString("guid"));
				pstmt1.setString(35, "pdb_id");
				boolean status1=pstmt1.execute();

				pstmt1=null;

				String MySql_updRepair="insert into upd.upd_repair (serial_no,cancel_code,swap_ref_no,swap_count,delete_flag,org_code,upd_time,dn,era,rma,receive_date,"+
						"delivery_date,return_date,rma_date,scrap_date,cust_id,warehouse_id,shop_id,status_id,action,remarks,csn,last_repair_date,repair_count,swap_date,created_datetime,created_by)"+
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),'pcba_pgm_success')";
				//con2 = DBUtil.getConnection(ds);
				pstmt1=con1.prepareStatement(MySql_updRepair);
				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				pstmt1.setString(2, rs.getString("cancel_code"));
				pstmt1.setString(3, rs.getString("swap_ref_no"));
				pstmt1.setString(4, rs.getString("swap_count"));
				pstmt1.setString(5, rs.getString("delete_flag"));
				pstmt1.setString(6, rs.getString("org_code"));
				pstmt1.setString(7, rs.getString("upd_time"));
				pstmt1.setString(8, rs.getString("dn"));
				pstmt1.setString(9, rs.getString("era"));
				pstmt1.setString(10, rs.getString("rma"));
				pstmt1.setDate(11, rs.getDate("receive_date"));
				pstmt1.setDate(12, rs.getDate("delivery_date"));
				pstmt1.setDate(13, rs.getDate("return_date"));
				pstmt1.setDate(14, rs.getDate("rma_date"));
				pstmt1.setDate(15,rs.getDate("scrap_date"));
				pstmt1.setString(16, rs.getString("cust_id"));
				pstmt1.setString(17, rs.getString("warehouse_id"));
				pstmt1.setString(18, rs.getString("shop_id"));
				pstmt1.setString(19, rs.getString("status_id"));
				pstmt1.setString(20, rs.getString("action"));
				pstmt1.setString(21, rs.getString("remarks"));
				pstmt1.setString(22, rs.getString("csn"));
				pstmt1.setDate(23, rs.getDate("last_repair_date"));
				pstmt1.setString(24, rs.getString("repair_count"));
				pstmt1.setDate(25, rs.getDate("swap_date"));
				boolean status2 = pstmt1.execute();

				pstmt1=null;

				String MySql_updWarrantyInfo="insert into upd.upd_warranty_info (serial_no,status_code,orig_warr_eff_date,ren_warr_code,warr_country_code,warr_region,orig_ship_date,reference_key,"+
						"pop_in_sysdate,pop_date,pop_identifier,created_datetime,created_by) values(?,?,?,?,?,?,?,?,?,?,?,now(),'pcba_pgm_success')";
				//con3 = DBUtil.getConnection(ds);
				pstmt1=con1.prepareStatement(MySql_updWarrantyInfo);

				Date curDate = new Date();				
				SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyy");		
				String DateToStr = format.format(curDate);

				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				pstmt1.setString(2, "ACT  "+DateToStr);
				pstmt1.setDate(3, rs.getDate("orig_warr_eff_date"));
				pstmt1.setString(4, rs.getString("ren_warr_code"));
				pstmt1.setString(5, rs.getString("warr_country_code"));
				pstmt1.setString(6, rs.getString("warr_region"));
				pstmt1.setString(7, rs.getString("orig_ship_date"));
				pstmt1.setString(8, rs.getString("reference_key"));
				pstmt1.setDate(9, rs.getDate("pop_in_sysdate"));
				pstmt1.setDate(10, rs.getDate("pop_date"));
				pstmt1.setString(11, rs.getString("pop_identifier"));
				boolean status3 = pstmt1.execute();

				pstmt1 =null;

				String MySql_updDeviceConfig="insert into upd.upd_device_config (serial_no,handset_type,flex_option,flex_sw,hw,icc_id,software_version,wimax,hsn,flash_uid,dual_serial_no,"+
						"dual_serial_no_type,fastt_id,base_processor_id,wlan,created_datetime,created_by) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),'pcba_pgm_success')";

				//con4 = DBUtil.getConnection(ds);
				pstmt1=con1.prepareStatement(MySql_updDeviceConfig);
				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				pstmt1.setString(2, rs.getString("handset_type"));
				pstmt1.setString(3, rs.getString("flex_option"));
				pstmt1.setString(4, rs.getString("flex_sw"));
				pstmt1.setString(5, rs.getString("hw"));
				pstmt1.setString(6, rs.getString("icc_id"));
				pstmt1.setString(7, rs.getString("software_version"));
				pstmt1.setString(8, rs.getString("wimax"));
				pstmt1.setString(9, rs.getString("hsn"));
				pstmt1.setString(10, rs.getString("flash_uid"));
				pstmt1.setString(11, rs.getString("dual_serial_no"));
				pstmt1.setString(12, rs.getString("dual_serial_no_type"));
				pstmt1.setString(13, rs.getString("fastt_id"));
				pstmt1.setString(14, rs.getString("base_processor_id"));
				pstmt1.setString(15, rs.getString("wlan"));
				boolean status4 = pstmt1.execute();

				pstmt1=null;

				String MySql_updDirectShipment="insert into upd.upd_direct_shipment (serial_no,so_line_no,ds_region_code,ds_so_no,ds_po_no,ds_cust_id,ds_bill_to_id,ds_ship_to_id,ds_cust_country_code,"+
						"ds_cust_name,bill_to_id,shipment_no,phone_no,wip_dj,sale_date,last_imei,created_datetime,created_by) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),'pcba_pgm_success')";
				//con5 = DBUtil.getConnection(ds);
				pstmt1=con1.prepareStatement(MySql_updDirectShipment);
				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				pstmt1.setString(2, rs.getString("so_line_no"));
				pstmt1.setString(3, rs.getString("ds_region_code"));
				pstmt1.setString(4, rs.getString("ds_so_no"));
				pstmt1.setString(5, rs.getString("ds_po_no"));
				pstmt1.setString(6, rs.getString("ds_cust_id"));
				pstmt1.setString(7, rs.getString("ds_bill_to_id"));
				pstmt1.setString(8, rs.getString("ds_ship_to_id"));
				pstmt1.setString(9, rs.getString("ds_cust_country_code"));
				pstmt1.setString(10, rs.getString("ds_cust_name"));
				pstmt1.setString(11, rs.getString("bill_to_id"));
				pstmt1.setString(12, rs.getString("shipment_no"));
				pstmt1.setString(13, rs.getString("phone_no"));
				pstmt1.setString(14, rs.getString("wip_dj"));
				pstmt1.setDate(15, rs.getDate("sale_date"));
				pstmt1.setString(16, rs.getString("last_imei"));
				boolean status5 = pstmt1.execute();

				pstmt1=null;

				String MySql_updLockCode="insert into upd.upd_lock_code (serial_no,meid_evdo_password,meid_a_key2_type,meid_a_key2,imc_lock_code,created_datetime,created_by) values(?,?,?,?,?,now(),'pcba_pgm_success')";
				//connection = DBUtil.getConnection(ds);
				pstmt1=con1.prepareStatement(MySql_updLockCode);
				pstmt1.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				pstmt1.setString(2, rs.getString("meid_evdo_password"));
				pstmt1.setString(3, rs.getString("meid_a_key2_type"));
				pstmt1.setString(4, rs.getString("meid_a_key2"));
				pstmt1.setString(5, rs.getString("imc_lock_code"));
				boolean status6 = pstmt1.execute();

				pstmt1=null;

				String Sql_updMeid =" insert into upd.upd_meid (serial_no,a_key_index,cas_no,created_datetime,created_by) values(?,?,?,now(),'pcba_pgm_success')";
				//conn = DBUtil.getConnection(ds);
				pstmt1=con1.prepareStatement(Sql_updMeid);
				pstmt1.setString(1,pCBASerialNoUPdateQueryInput.getSerialNoOut());
				pstmt1.setString(2,rs.getString("a_key_index"));
				pstmt1.setString(3,rs.getString("cas_no"));
				boolean status7 = pstmt1.execute();

				pstmt1=null;


				if(!status1 && !status2 && !status3 && !status4 && !status5 && !status6 && !status7){
					String statusCode ="SCR  "+DateToStr;
					//con6 = DBUtil.getConnection(ds);
					String statusUpdatingOldSerialNo="update upd.upd_warranty_info set status_code='"+statusCode+"' where serial_no='"+pCBASerialNoUPdateQueryInput.getSerialNoIn()+"'";
					pstmt1=con1.prepareStatement(statusUpdatingOldSerialNo);
					pstmt1.execute();
				}

				con1.commit();

				response.setResponseCode(ServiceMessageCodes.SUCCESS);
				response.setResponseMessage(ServiceMessageCodes.OPERATION_SUCCESS);

			}else{

				conn = DBUtil.getConnection(ds);				
				StringBuffer stb=new StringBuffer();
				stb.append("insert into upd.upd_shipment_notavail_sn(SERIAL_NO_IN,SERIAL_NO_OUT,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,STATUS) values(?,?,?,?,?,?,?)");
				prestmt=conn.prepareStatement(stb.toString());
				prestmt.setString(1, pCBASerialNoUPdateQueryInput.getSerialNoIn());
				prestmt.setString(2, pCBASerialNoUPdateQueryInput.getSerialNoOut());
				prestmt.setString(3, "PCBA_PGM");
				prestmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
				prestmt.setString(5, "PCBA_PGM");				
				prestmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
				prestmt.setString(7,"S");
				prestmt.execute();

				response.setResponseCode(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_WARRANTY_INFO_TABLE);
				response.setResponseMessage(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_WARRANTY_INFO_TABLE_MSG);

			}

		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DBUtil.closeConnection(con, preparedStmt, rs);
			DBUtil.connectionClosed(conn, prestmt);
			DBUtil.connectionClosed(con1, pstmt1);			
		}


		return response;
	}




}
