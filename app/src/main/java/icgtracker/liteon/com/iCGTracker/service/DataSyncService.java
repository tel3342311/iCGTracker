package icgtracker.liteon.com.iCGTracker.service;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.R;
import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.util.ChildLocationItem;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.GuardianApiClient;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;


public class DataSyncService extends IntentService {
    private final static String TAG = DataSyncService.class.getName();
    private GuardianApiClient mApiClient;
    public DataSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mApiClient == null) {
            mApiClient = GuardianApiClient.getInstance(getApplicationContext());
        }
        SharedPreferences sp = App.getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
        mApiClient.setToken(token);
        if (intent != null) {
            switch (intent.getAction()) {
                case Def.ACTION_REGISTERATION_USER:
                    handleRegisterNewUser(intent);
                    break;
                case Def.ACTION_LOGIN_USER:
                    handleLoginUser(intent);
                    break;
                case Def.ACTION_LOGOUT_USER:
                    handleLogoutUser(intent);
                    break;
                case Def.ACTION_GET_EVENT_REPORT:
                    handleGetEventReport(intent);
                    break;
                case Def.ACTION_GET_LOCATION:
                    handleGetLocation(intent);
                    break;
                case Def.ACTION_GET_PARENT_DETAIL:
                    handleGetParentDetail(intent);
                    break;
                case Def.ACTION_GET_STUDENT_LIST:
                    handleGetStudentList(intent);
                    break;
                case Def.ACTION_PAIR_NEW_DEVICE:
                    handlePairNewDevice(intent);
                    break;
                case Def.ACTION_UNPAIR_DEVICE:
                    handleUnpairDevice(intent);
                    break;
                case Def.ACTION_RESET_PASSWORD:
                    handleResetPassword(intent);
                    break;
                case Def.ACTION_UPDATE_APP_TOKEN:
                    handleUpdateAppToken(intent);
                    break;
                case Def.ACTION_UPDATE_STUDENT_DETAIL:
                    handleUpdateStudentDetail(intent);
                    break;
                case Def.ACTION_UPDATE_PARENT_DETAIL:
                    handleUpdateParentDetail(intent);
                    break;
            }
        }
    }

    private void handleUpdateParentDetail(Intent intent) {

    }

    private void handleUpdateStudentDetail(Intent intent) {

    }

    private void handleUpdateAppToken(Intent intent) {

    }

    private void handleResetPassword(Intent intent) {

    }

    private void handleUnpairDevice(Intent intent) {

    }

    private void handlePairNewDevice(Intent intent) {

    }

    private void handleGetStudentList(Intent intent) {

    }

    private void handleGetParentDetail(Intent intent) {

    }

    private void handleGetLocation(Intent intent) {
        String uuid = intent.getStringExtra(Def.KEY_UUID);
        JSONResponse response = mApiClient.getStudentLocation(uuid);
        if (response == null) {
            sendErrorBrocast("GetLocation : NO RESPONSE");
            return;
        }
        if (TextUtils.equals(response.getReturn().getResponseSummary().getStatusCode(),Def.RET_SUCCESS_1)) {
            if (response.getReturn().getResults() == null) {
                sendBroadcast(new Intent(Def.ACTION_GET_LOCATION));
                return;
            }
            String lat = response.getReturn().getResults().getLatitude();
            String lnt = response.getReturn().getResults().getLongitude();
            if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lnt)) {
                sendBroadcast(new Intent(Def.ACTION_GET_LOCATION));
                return;
            }
            String lastPositionUpdateTime = response.getReturn().getResults().getEvent_occured_date();

            LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lnt));
            SimpleDateFormat sdFormat = new SimpleDateFormat();
            String format = "yyyy-MM-dd HH:mm:ss.S";
            sdFormat.applyPattern(format);
            Date date = Calendar.getInstance().getTime();
            if (!TextUtils.isEmpty(lastPositionUpdateTime)) {
                try {
                    date = sdFormat.parse(lastPositionUpdateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EE HH:mm");
            String updateTime = sdf.format(date);

            ChildLocationItem item = new ChildLocationItem();
            item.setUuid(uuid);
            item.setLatlng(latlng);
            item.setDate(updateTime);
            DBHelper dbHelper = DBHelper.getInstance(App.getContext());
            dbHelper.updateChildLocation(dbHelper.getWritableDatabase(), item);
            sendBroadcast(new Intent(Def.ACTION_GET_LOCATION));
        } else {
            sendErrorBrocast("GetLocation : "+ response.getReturn().getResponseSummary().getErrorMessage());
        }
    }

    private void handleGetEventReport(Intent intent) {

    }

    private void handleLogoutUser(Intent intent) {

    }

    private void handleRegisterNewUser(Intent intent) {

    }

    private void handleLoginUser(Intent intent) {

        String account = intent.getStringExtra(Def.KEY_ACCOUNT_NAME);
        String password = intent.getStringExtra(Def.KEY_PASSWORD);
        JSONResponse response = mApiClient.login(account, password);
        if (response == null) {
            sendErrorBrocast(getString(R.string.login_error_no_server_connection));
        }
    }

    public void sendBroadcast(Intent intent) {

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendErrorBrocast(String message) {
        Intent intent = new Intent(Def.ACTION_ERROR_NOTIFY);
        intent.putExtra(Def.EXTRA_ERROR_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}