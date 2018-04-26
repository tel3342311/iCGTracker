package icgtracker.liteon.com.iCGTracker.util;

import java.util.UUID;

public class Def {
	//URL
	public final static String URL_3rd_party = "icg.aricentcoe.com:8080";
	public final static String URL_internal = "www.icareguardian.com";
	//SharePreference
	public final static String SHARE_PREFERENCE = "com.liteon.icampusguardian.PREFERENCE_FILE_KEY";
	public final static String SP_USER_TERM_READ = "com.liteon.icampusguardian.SP_USER_TERM_READ";
	public final static String SP_CURRENT_STUDENT = "com.liteon.icampusguardian.SP_CURRENT_STUDENT";
	public static final String SP_LOGIN_TOKEN = "com.liteon.icampusguardian.SP_LOGIN_TOKEN";
	public static final String SP_TARGET_CARLOS = "com.liteon.icampusguardian.SP_TARGET_CARLOS";
	public static final String SP_TARGET_STEPS = "com.liteon.icampusguardian.SP_TARGET_STEPS";
	public static final String SP_TARGET_WALKING = "com.liteon.icampusguardian.SP_TARGET_WALKING";
	public static final String SP_TARGET_RUNNING = "com.liteon.icampusguardian.SP_TARGET_RUNNING";
	public static final String SP_TARGET_CYCLING = "com.liteon.icampusguardian.SP_TARGET_CYCLING";
	public static final String SP_TARGET_SLEEPING = "com.liteon.icampusguardian.SP_TARGET_SLEEPING";
	public static final String SP_ALARM_MAP = "com.liteon.icampusguardian.SP_ALARM_MAP";
	public static final String SP_TARGET_MAP = "com.liteon.icampusguardian.SP_TARGET_MAP";
	public static final String SP_IMPROVE_PLAN = "com.liteon.icampusguardian.SP_IMPROVE_PLAN";
	public static final String SP_TEACHER_PLAN = "com.liteon.icampusguardian.SP_TEACHER_PLAN";
	public static final String SP_PHOTO_MAP = "com.liteon.icampusguardian.SP_PHOTO_MAP";
	public static final String SP_PHOTO_MAP_WATCH = "com.liteon.icampusguardian.SP_PHOTO_MAP_WATCH";
	public static final String SP_GEO_ITEM_MAP = "com.liteon.icampusguardian.SP_GEO_ITEM_MAP";
	public static final String SP_BT_WATCH_ADDRESS = "com.liteon.icampusguardian.SP_BT_WATCH_ADDRESS";
    public static final String SP_URL = "com.liteon.icampusguardian.SP_URL";
    public static final String SP_ALARM_SYNCED = "com.liteon.icampusguardian.SP_ALARM_SYNCED";

	//RET CODE there are two kind of success code
	public static final String RET_SUCCESS_1 = "SUC01";
	public static final String RET_SUCCESS_2 = "WSUC01";
	public static final String RET_ERR_01 = "ERR01";
	public static final String RET_ERR_02 = "ERR02";
    public static final String RET_ERR_14 = "ERR14";
    public static final String RET_ERR_16 = "ERR16";
	public static final String RET_ERR_23 = "ERR23";

	//API 01 registration
	public static final String REQUEST_USER_REGISTRATION = "ParentUserRegistration";
	public static final String KEY_ACCOUNT_NAME = "account_name";
	//API 02 login
	public static final String REQUEST_USERLOGIN = "UserLogin";
	public static final String KEY_TYPE_USERLOGIN = "user.UserLogin";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_FORCELOGIN = "forcelogin";
	//API 04 Social Login
	public static final String REQUEST_FEDERATEDLOGIN = "FederatedLogin";
    public static final String KEY_SOCIAL_NAME = "name";
    public static final String KEY_SOCIAL_EMAIL = "email";
    public static final String KEY_SOCIAL_TOKEN = "token";
    public static final String KEY_SOCIAL_USERAGENT = "useragent";
    public static final String VALUE_SOCIAL_USERAGENT = "MOBILE";

    //API 06 Update Parent account detail.
	public static final String REQUEST_USER_UPDATE = "UserUpdate";
	public static final String KEY_PHONE_NUMBER = "mobile_number";
	//API 07 get student list
	public static final String REQUEST_GET_CHILDREN_LIST = "StudentList";
	//API 08 get student location 
	public static final String REQUEST_GET_CHILDREN_LOCATION = "StudentLocation";
	
	//API 11 Update FCM Token
	public static final String REQUEST_UPDATE_APP_TOKEN = "MobileAppTokenUpdate";
	public static final String KEY_APP_TOKEN = "appToken";
	public static final String KEY_APP_TYPE = "appType";
	public static final String KEY_APP_TYPE_ANDROID = "android";//fixed input

	//API 14 pair new device
	public static final String REQUEST_PAIR_NEW_DEVICE = "ParentUserDevicePair";
	//API 15 pair new device
	public static final String REQUEST_UNPAIR_DEVICE = "ParentUserDeviceUnPair";
	
	//API 19 get device event report
	public static final String REQUEST_GET_DEVICE_EVENT_REPORT = "DeviceEventReport";
	public static final String KEY_EMAIL = "email";
	//API 27 list fence
    public static final String REQUEST_LIST_FENCE = "GeozoneList";
    
	//API 28 create/update fence
	public static final String REQUEST_CREATE_FENCE = "GeozoneCreate";
    public static final String KEY_ZONE_DETAIL = "zone_details";
    public static final String KEY_ZONE_RADIUS = "zone_radius";
    public static final String KEY_ZONE_NAME = "zone_name";
    public static final String KEY_ZONE_ENTRY_ALERT = "zone_entry_alert";
    public static final String KEY_ZONE_EXIT_ALERT = "zone_exit_alert";
    public static final String KEY_ZONE_DESC = "zone_description";
    public static final String KEY_VALID_TILL = "valid_till";
    public static final String KEY_FREQ_MIN = "frequency_minutes";

    //API 29 delete fence
    public static final String REQUEST_DELETE_FENCE = "GeozoneDelete";

	//API 32 update child info
	public static final String REQUEST_UPDATE_CHILD_INFO = "StudentUpdate";
	public static final String KEY_STUDENT_ID = "student_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_CLASS = "101";
	public static final String KEY_ROLL_NO = "roll_no";
	public static final String KEY_HEIGHT  = "height";
	public static final String KEY_WEIGHT  = "weight";
	public static final String KEY_DOB = "dob";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_UUID = "uuid";
	public static final String KEY_PROFILE_NAME = "profile_name";
	public static final String KEY_MOBILE_NUMBER = "mobile_number";
	//API 33 reset password
	public static final String REQUEST_PASSWORD_REST = "PasswordResetRequest";
	public static final String KEY_USER_ROLE = "user_role";

	//API 36 get user detail
	public static final String REQUEST_USER_DETAIL = "UserDetails";
	//API 36 GrantTeacherAccessToSleepData
    public static final String REQUEST_GRANT_TEDETAIL = "GrantTeacherAccessToSleepData";
    //Web API StudentActivity
    public static final String REQUEST_STUDENT_ACTIVITY = "StudentActivity";
    public static final String KEY_MEASURE_TYPE = "measure_type";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_END_DATE = "end_date";

	//EVENT ID LIST
	public static final String EVENT_ID_ENTER_SCHOOL = "1";
	public static final String EVENT_ID_LEAVE_SCHOOL = "2";
	public static final String EVENT_ID_SOS_ALERT = "13";
	public static final String EVENT_ID_SOS_REMOVE = "14";
	public static final String EVENT_ID_GPS_LOCATION = "19";
	
	//EVENT DURATION
	public static final String EVENT_DURATION_ONE_DAY = "1";
	public static final String EVENT_DURATION_WEEK = "7";
	public static final String EVENT_DURATION_MONTH = "30";
	
	//Action
	public static final String ACTION_NOTIFY = "com.liteon.icampusguardian.ACTION_NOTIFY";
	public static final String EXTRA_NOTIFY_TYPE = "com.liteon.icampusguardian.EXTRA_NOTIFY_TYPE";
	public static final String EXTRA_SOS_LOCATION = "com.liteon.icampusguardian.EXTRA_SOS_LOCATION";

	//Intent EXTRA
	public static final String EXTRA_DISABLE_USERTREM_BOTTOM = "com.liteon.icampusguardian.EXTRA_DISABLE_USERTREM_BOTTOM";
	public static final String EXTRA_CHOOSE_PHOTO_TYPE = "com.liteon.icampusguardian.EXTRA_GOTO_MAIN_EXTRA_CHOOSE_PHOTO_TYPE";
	public static final String EXTRA_GOTO_PAGE_ID = "com.liteon.icampusguardian.EXTRA_GOTO_PAGE_ID";
	public static final int EXTRA_PAGE_SETTING_ID = 1;
	public static final int EXTRA_PAGE_APPINFO_ID = 2;
	//Choose photo TYPE
	public static final String EXTRA_CHOOSE_CHILD_ICON = "child_icon";
	public static final String EXTRA_CHOOSE_WATCH_ICON = "watch_icon";
	public static final String EXTRA_STUDENT_NAME = "student_name";
	/**
	 * =================================================================
	 *
	 * Action for DataSync Service
	 *
	 * =================================================================
	 */
	public static final String ACTION_REGISTERATION_USER = "com.liteon.icampusguardian.ACTION_REGISTERATION_USER";
	public static final String ACTION_LOGIN_USER = "com.liteon.icampusguardian.ACTION_LOGIN_USER";
	public static final String ACTION_LOGOUT_USER = "com.liteon.icampusguardian.ACTION_LOGOUT_USER";
	public static final String ACTION_GET_STUDENT_LIST = "com.liteon.icampusguardian.ACTION_GET_STUDENT_LIST";
	public static final String ACTION_GET_LOCATION = "com.liteon.icampusguardian.ACTION_GET_LOCATION";
	public static final String ACTION_GET_EVENT_REPORT = "com.liteon.icampusguardian.ACTION_GET_EVENT_REPORT";
	public static final String ACTION_GET_PARENT_DETAIL = "com.liteon.icampusguardian.ACTION_GET_PARENT_DETAIL";
	public static final String ACTION_PAIR_NEW_DEVICE = "com.liteon.icampusguardian.ACTION_PAIR_NEW_DEVICE";
	public static final String ACTION_UNPAIR_DEVICE = "com.liteon.icampusguardian.ACTION_UNPAIR_NEW_DEVICE";
	public static final String ACTION_UPDATE_APP_TOKEN = "com.liteon.icampusguardian.ACTION_UPDATE_APP_TOKEN";
	public static final String ACTION_UPDATE_STUDENT_DETAIL = "com.liteon.icampusguardian.ACTION_UPDATE_STUDENT_DETAIL";
	public static final String ACTION_UPDATE_PARENT_DETAIL = "com.liteon.icampusguardian.ACTION_UPDATE_PARENT_DETAIL";
	public static final String ACTION_RESET_PASSWORD = "com.liteon.icampusguardian.ACTION_RESET_PASSWORD";
    public static final String ACTION_CREATE_FENCE = "com.liteon.icampusguardian.ACTION_CREATE_FENCE";
    public static final String ACTION_UPDATE_FENCE = "com.liteon.icampusguardian.ACTION_UPDATE_FENCE";
    public static final String ACTION_LIST_FENCE = "com.liteon.icampusguardian.ACTION_LIST_FENCE";
    public static final String ACTION_DELETE_FENCE = "com.liteon.icampusguardian.ACTION_DELETE_FENCE";

	//Action fro reponse of DataSync Service
	public static final String ACTION_ERROR_NOTIFY = "com.liteon.icampusguardian.ACTION_ERROR_NOTIFY";
	public static final String EXTRA_ERROR_MESSAGE = "com.liteon.icampusguardian.EXTRA_ERROR_MESSAGE";

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	// Key names received from the BluetoothAgent Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	public static final String BT_ERR_UNABLE_TO_CONNECT = "Unable to connect device";
	public static final String EXTRA_BT_ADDR = "com.liteon.icampusguardian.EXTRA_BT_ADDR";
	public static final int REQUEST_ENABLE_BT = 1;
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "channle_01";
	public static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "channl_sos";

	//BLE Service UUID
	public static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
	public static final UUID BATTERY_LEVEL_CHARACTER_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
	public static final UUID DEVICE_INFORMATION_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
	public static final UUID SOFTWARE_REVISION_STRING_UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");
	public static final UUID TRACKER_UUID = UUID.fromString("0e9bdeb8-6bba-4947-9a2a-88e2a1befbd3");
	public static final UUID NORDIC_UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID NORDIC_UART_SERVICE_RX_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID NORDIC_UART_SERVICE_TX_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
	public static final byte[] CMD_GET_UUID = "uuid_read".getBytes();
	/**
     * =================================================================
     *
     * Action for Ble Service
     *
     * =================================================================
     **/
    public final static String ACTION_GATT_CONNECTED = "icgtracker.liteon.com.iCGTracker.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "icgtracker.liteon.com.iCGTracker.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "icgtracker.liteon.com.iCGTracker.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "icgtracker.liteon.com.iCGTracker.ACTION_DATA_AVAILABLE";
    public final static String ACTION_CHAR_READED = "icgtracker.liteon.com.iCGTracker.ACTION_CHAR_READED";
    public final static String BATTERY_LEVEL_AVAILABLE = "icgtracker.liteon.com.iCGTracker.BATTERY_LEVEL_AVAILABLE";
    public final static String EXTRA_DATA = "icgtracker.liteon.com.iCGTracker.EXTRA_DATA";
    public final static String EXTRA_STRING_DATA = "icgtracker.liteon.com.iCGTracker.EXTRA_STRING_DATA";
    public final static String EXTRA_DATA_LENGTH = "icgtracker.liteon.com.iCGTracker.EXTRA_DATA_LENGTH";
    public final static String ACTION_GATT_RSSI = "icgtracker.liteon.com.iCGTracker.ACTION_GATT_RSSI";
    public final static String EXTRA_DATA_RSSI = "icgtracker.liteon.com.iCGTracker.ACTION_GATT_RSSI";
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";
	public static final String DISCONNECT_DEVICE = "find.device.disconnect";
	public static final String CANCEL_DEVICE_ALARM = "find.device.cancel.alarm";
	public static final String DEVICE_BATTERY = "device.battery.level";
}
