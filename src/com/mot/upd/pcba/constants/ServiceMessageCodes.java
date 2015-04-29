package com.mot.upd.pcba.constants;

public class ServiceMessageCodes {

	public ServiceMessageCodes() {

	}

	// Thammaiah added
	// Common codes(Application Exception)
	public static final int NO_DATASOURCE_FOUND = 6000;
	public static final int SQL_EXCEPTION = 6001;

	// Common Messages
	public static final String NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG = "Dispatch Serial WS: Can't get data source!";
	public static final String SQL_EXCEPTION_MSG = "Some Internal Exception";
	// Thammaiah added
	// Codes for dispatch serial no

	public static final int SUCCESS = 0000;
	public static final int NO_NEW_SERIAL_NO_AVAILABLE = 5001;
	public static final int INVALID_REQUEST_TYPE = 5002;
	public static final int INVALID_BUILD_TYPE = 5003;
	public static final int SERIAL_NO_NOT_VALID = 5004;
	public static final int INPUT_PARAM_MISSING = 5005;
	public static final int NO_ULMA_AVAILABLE = 5006;
	public static final int NEW_SERIAL_NO_AVAILABLE = 5007;
	public static final int NEW_ULMA_AVAILABLE = 5008;
	public static final int INVALID_SN_TYPE = 5015;
	public static final int NO_PROTOCOL_FOUND = 5016;

	// Viswanath added

	public static final int MEID_SUCCESS = 5009;
	public static final int MEID_FAILURE = 5010;
	public static final int INVALID_STATUS = 5011;
	public static final int NO_LOCK_CODE_FOUND = 5012;
	public static final int OLD_SERIAL_NO_NOT_FOUND = 5013;
	public static final int NEW_SERIAL_NO_NOT_FOUND = 5014;

	
	// Thammaiah added
	// Messages for dispatch serial no WS
	public static final String OPERATION_SUCCESS = "Operation Success";
	public static final String NO_SERIAL_NO_AVAILABLE_FOR_DISPATCH_MSG = "No New Serial number available for Dispatch";
	public static final String INVALID_REQUEST_TYPE_MSG = "Request type Value must be V or D";
	public static final String INVALID_BUILD_TYPE_MSG = "Build type Value must be PROD";
	public static final String SERIAL_NO_NOT_VALID_MSG = "Serial number not valid";
	public static final String INPUT_PARAM_MISSING_MSG = "Following Fields are mandatory- Serial Number,Build type,Request-type,RSD id,MASC ID.Please re-enter and try again.";
	public static final String NO_ULMA_AVAILABLE_MSG = "No ULMA Available For Dispatch";
	public static final String NEW_ULMA_AVAILABLE_MSG = "New ULMA Available For Dispatch";
	public static final String SERIAL_NO_AVAILABLE_FOR_DISPATCH_MSG = "New Serial number available for Dispatch";
	public static final String INVALID_SN_TYPE_MSG = "Serial number type should be either MEID or IMEI";
	public static final String NO_PROTOCOL_FOUND_MSG = "Protocol Name is mandatory for MEID";

	// Viswanath added

	public static final String MEID_SUCCES_MSG = "MEID Success";
	public static final String MEID_FAILURE_MSG = "MEID Failure";
	public static final String INVALID_STATUS_MSG = "Invalid Status";
	public static final String NO_LOCK_CODE_DETAILS_FOUND_MSG = "No Lock Code Details Found";
	public static final String OLD_SERIAL_NO_NOT_FOUND_MSG = "Old Serial Not Found";
	public static final String NEW_SERIAL_NO_NOT_FOUND_MSG = "New Serial Not Found";
	public static final String NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG ="No DataSource found for SerialNO.";

}
