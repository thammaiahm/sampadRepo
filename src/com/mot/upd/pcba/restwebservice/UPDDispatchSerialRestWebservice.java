package com.mot.upd.pcba.restwebservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.dao.DispatchSerialNumberDAO;
import com.mot.upd.pcba.pojo.DispatchSerialRequestPOJO;
import com.mot.upd.pcba.pojo.DispatchSerialResponsePOJO;

/**
 * @author Quinnox Dev Team
 *
 */
@Path("/dispatchserialNumber")
public class UPDDispatchSerialRestWebservice {

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response doGetSerialNumber(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {

		DispatchSerialResponsePOJO dispatchSerialResponsePOJO = new DispatchSerialResponsePOJO();
		boolean isMissing = false;
		boolean isValidRequest = false;
		boolean isValidSerial = false;
		boolean isValidBuildType = false;
		// Check for Mandatory Fields in input
		isMissing = validateMandatoryInputParam(dispatchSerialRequestPOJO);
		if (isMissing) {

			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.INPUT_PARAM_MISSING);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.INPUT_PARAM_MISSING_MSG);
			return Response.status(200).entity(dispatchSerialResponsePOJO)
					.build();
		}

		// check if request type is valid
		isValidRequest = validateRequestType(dispatchSerialRequestPOJO);
		if (!isValidRequest) {
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.INVALID_REQUEST_TYPE);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.INVALID_REQUEST_TYPE_MSG);
			return Response.status(200).entity(dispatchSerialResponsePOJO)
					.build();
		}

		// check if sn type is valid
		isValidSerial = validateSNType(dispatchSerialRequestPOJO);
		if (!isValidSerial) {
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.INVALID_SN_TYPE);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.INVALID_SN_TYPE_MSG);
			return Response.status(200).entity(dispatchSerialResponsePOJO)
					.build();
		}

		// check if build type is valid
		isValidBuildType = validateBuildType(dispatchSerialRequestPOJO);
		if (!isValidBuildType) {
			dispatchSerialResponsePOJO
					.setResponseCode(ServiceMessageCodes.INVALID_BUILD_TYPE);
			dispatchSerialResponsePOJO
					.setResponseMsg(ServiceMessageCodes.INVALID_BUILD_TYPE_MSG);
			return Response.status(200).entity(dispatchSerialResponsePOJO)
					.build();
		}

		/*
		 * FOR ORACLE:if request type is IMEI check for protocol and query
		 */
		DispatchSerialNumberDAO dispatchSerialNumberDAO = new DispatchSerialNumberDAO();

		// If IMEI
		if (dispatchSerialRequestPOJO.getSnRequestType().trim()
				.equals(PCBADataDictionary.IMEI)) {

			if (dispatchSerialRequestPOJO.getRequestType().trim()
					.equals(PCBADataDictionary.REQUEST_DISPATCH)) {
				dispatchSerialResponsePOJO = dispatchSerialNumberDAO
						.dispatchSerialNumberIMEI(dispatchSerialRequestPOJO);
				if (dispatchSerialResponsePOJO.getResponseCode() == ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND
						|| dispatchSerialResponsePOJO.getResponseCode() == ServiceMessageCodes.SQL_EXCEPTION) {
					return Response.status(200)
							.entity(dispatchSerialResponsePOJO).build();
				}
				// If ULMA address not available return response
				/*
				 * dispatchSerialResponsePOJO = dispatchSerialNumberDAO
				 * .getULMAAddress(dispatchSerialRequestPOJO,
				 * dispatchSerialResponsePOJO);
				 */

				dispatchSerialResponsePOJO = dispatchSerialNumberDAO
						.updateDispatchStatusIMEI(dispatchSerialRequestPOJO,
								dispatchSerialResponsePOJO);
			}

			// Tested
			if (dispatchSerialRequestPOJO.getRequestType().trim()
					.equals(PCBADataDictionary.REQUEST_VALIDATE)) {
				dispatchSerialResponsePOJO = dispatchSerialNumberDAO
						.validateSerialNumberIMEI(dispatchSerialRequestPOJO);
			}
		}
		/*
		 * End Checking for IMEI
		 */

		/*
		 * FOR ORACLE:MEID
		 */
		if (dispatchSerialRequestPOJO.getSnRequestType().trim()
				.equals(PCBADataDictionary.MEID)) {

			// Check if protocol is present
			// Tested
			if (dispatchSerialRequestPOJO.getProtocol() == null
					|| dispatchSerialRequestPOJO.getProtocol() == "") {
				dispatchSerialResponsePOJO
						.setResponseCode(ServiceMessageCodes.NO_PROTOCOL_FOUND);
				dispatchSerialResponsePOJO
						.setResponseMsg(ServiceMessageCodes.NO_PROTOCOL_FOUND_MSG);
				return Response.status(200).entity(dispatchSerialResponsePOJO)
						.build();
			}

			if (dispatchSerialRequestPOJO.getRequestType().trim()
					.equals(PCBADataDictionary.REQUEST_DISPATCH)) {

				dispatchSerialResponsePOJO = dispatchSerialNumberDAO
						.dispatchSerialNumberMEID(dispatchSerialRequestPOJO);
				if (dispatchSerialResponsePOJO.getResponseCode() == ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND
						|| dispatchSerialResponsePOJO.getResponseCode() == ServiceMessageCodes.SQL_EXCEPTION) {
					return Response.status(200)
							.entity(dispatchSerialResponsePOJO).build();
				}
				dispatchSerialResponsePOJO = dispatchSerialNumberDAO
						.updateDispatchStatusMEID(dispatchSerialRequestPOJO,
								dispatchSerialResponsePOJO);

			}

			if (dispatchSerialRequestPOJO.getRequestType().trim()
					.equals(PCBADataDictionary.REQUEST_VALIDATE)) {

				dispatchSerialResponsePOJO = dispatchSerialNumberDAO
						.validateSerialNumberIMEI(dispatchSerialRequestPOJO);
			}
		}
		/*
		 * End Checking for MEID
		 */

		return Response.status(201).entity(dispatchSerialResponsePOJO).build();
	}

	private boolean validateBuildType(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		// TODO Auto-generated method stub
		if (dispatchSerialRequestPOJO.getBuildType().trim()
				.equals(PCBADataDictionary.BUILD_TYPE1)
				|| dispatchSerialRequestPOJO.getBuildType().trim()
						.equals(PCBADataDictionary.BUILD_TYPE2)) {
			return true;
		}

		return false;

	}

	private boolean validateSNType(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		// TODO Auto-generated method stub
		if (dispatchSerialRequestPOJO.getSnRequestType().trim()
				.equals(PCBADataDictionary.IMEI)
				|| dispatchSerialRequestPOJO.getSnRequestType().trim()
						.equals(PCBADataDictionary.MEID)) {
			return true;
		}

		return false;

	}

	private boolean validateRequestType(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		// TODO Auto-generated method stub
		if (dispatchSerialRequestPOJO.getRequestType().trim()
				.equals(PCBADataDictionary.REQUEST_VALIDATE)
				|| dispatchSerialRequestPOJO.getRequestType().trim()
						.equals(PCBADataDictionary.REQUEST_DISPATCH)) {
			return true;
		}

		return false;
	}

	private boolean validateMandatoryInputParam(
			DispatchSerialRequestPOJO dispatchSerialRequestPOJO) {
		// TODO Auto-generated method stub

		if (dispatchSerialRequestPOJO.getCustomer() == null
				|| dispatchSerialRequestPOJO.getRequestType() == null
				|| dispatchSerialRequestPOJO.getSnRequestType() == null
				|| dispatchSerialRequestPOJO.getNumberOfUlma() == 0
				|| dispatchSerialRequestPOJO.getGppdID() == null
				|| dispatchSerialRequestPOJO.getMascID() == null
				|| dispatchSerialRequestPOJO.getRsdID() == null
				|| dispatchSerialRequestPOJO.getBuildType() == null
				|| dispatchSerialRequestPOJO.getTrackID() == null) {
			return true;

		}
		
		if (dispatchSerialRequestPOJO.getCustomer() == ""
				|| dispatchSerialRequestPOJO.getRequestType() == ""
				|| dispatchSerialRequestPOJO.getSnRequestType() ==	""
				|| dispatchSerialRequestPOJO.getNumberOfUlma() == 0
				|| dispatchSerialRequestPOJO.getGppdID() == ""
				|| dispatchSerialRequestPOJO.getMascID() == ""
				|| dispatchSerialRequestPOJO.getRsdID() == ""
				|| dispatchSerialRequestPOJO.getBuildType() == ""
				|| dispatchSerialRequestPOJO.getTrackID() == "") {
			return true;

		}
		
		
		return false;
	}

}
