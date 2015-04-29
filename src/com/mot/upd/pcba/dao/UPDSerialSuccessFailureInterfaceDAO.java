/**
 * 
 */
package com.mot.upd.pcba.dao;

import com.mot.upd.pcba.pojo.PCBAProgramQueryInput;
import com.mot.upd.pcba.pojo.PCBAProgramResponse;

/**
 * @author rviswa
 *
 */
public interface UPDSerialSuccessFailureInterfaceDAO {

	PCBAProgramResponse updateIMEIStatusSuccess(
			PCBAProgramQueryInput pcbaProgramQueryInput);

	PCBAProgramResponse updateIMEIStatusFailure(
			PCBAProgramQueryInput pcbaProgramQueryInput);

	PCBAProgramResponse updateMEIDStatusSuccess(
			PCBAProgramQueryInput pcbaProgramQueryInput);

	PCBAProgramResponse updateMEIDStatusFailure(
			PCBAProgramQueryInput pcbaProgramQueryInput);

}
