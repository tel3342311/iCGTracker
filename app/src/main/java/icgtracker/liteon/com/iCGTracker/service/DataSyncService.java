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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.R;
import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.util.ChildLocationItem;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.FenceRangeItem;
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
                case Def.ACTION_CREATE_FENCE:
                    handleCreateFence(intent);
                    break;
                case Def.ACTION_DELETE_FENCE:
                    handleDeleteFence(intent);
                    break;
                case Def.ACTION_UPDATE_FENCE:
                    handleUpdateFence(intent);
                    break;
                case Def.ACTION_LIST_FENCE:
                    handleListFence(intent);
                    break;
            }
        }
    }

    private void handleCreateFence(Intent intent) {

        String uuid = intent.getStringExtra(Def.KEY_UUID);
        String fence_title = intent.getStringExtra(Def.KEY_ZONE_NAME);
        float fence_radius = intent.getFloatExtra(Def.KEY_ZONE_RADIUS, 0.1f);
        String fence_latlng = intent.getStringExtra(Def.KEY_ZONE_DETAIL);
        int fence_report_freq = intent.getIntExtra(Def.KEY_FREQ_MIN, 30);
        JSONResponse response = mApiClient.createFence(uuid, fence_title, fence_radius, fence_latlng, fence_report_freq);
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

    private void handleDeleteFence(Intent intent) {
    }

    private void handleUpdateFence(Intent intent) {
    }

    private void handleListFence(Intent intent) {
        String uuid = intent.getStringExtra(Def.KEY_UUID);
        JSONResponse response = mApiClient.listGeoFence(uuid);
        if (response == null) {
            sendErrorBrocast("ListFence : NO RESPONSE");
            return;
        }
        if (TextUtils.equals(response.getReturn().getResponseSummary().getStatusCode(),Def.RET_SUCCESS_1)) {
            if (response.getReturn().getResults() == null) {
                sendBroadcast(new Intent(Def.ACTION_LIST_FENCE));
                return;
            }
            JSONResponse.Geozone geozone[] = response.getReturn().getResults().getGeozones();

            if (geozone == null || geozone.length == 0) {
                sendBroadcast(new Intent(Def.ACTION_LIST_FENCE));
                return;
            }
            DBHelper dbHelper = DBHelper.getInstance(App.getContext());
            List<FenceRangeItem> listFence = new ArrayList<>();
            for (JSONResponse.Geozone fenceData : geozone) {
                int fence_id = fenceData.getGeozone_id();
                float fence_raius = fenceData.getZone_radius();
                String latlng_detail = fenceData.getZone_details();
                float lat = Float.parseFloat(latlng_detail.substring(0, latlng_detail.indexOf(",")));
                float lng = Float.parseFloat(latlng_detail.substring(latlng_detail.indexOf(",") + 1));
                int report_freq = fenceData.getFrequency_minutes();
                String fence_name = fenceData.getZone_name();

                FenceRangeItem item = new FenceRangeItem();
                item.setTitle(fence_name);
                item.setFence_id(fence_id);
                item.setLatitude(lat);
                item.setLongtitude(lng);
                item.setReport_freq(report_freq);
                item.setMeter(fence_raius * 1000);
                item.setUuid(uuid);
                listFence.add(item);
            }

            dbHelper.updateAllFenceItem(dbHelper.getWritableDatabase(), listFence);
            sendBroadcast(new Intent(Def.ACTION_LIST_FENCE));
        } else {
            sendErrorBrocast("ListFence : "+ response.getReturn().getResponseSummary().getErrorMessage());
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
        String student_id = intent.getStringExtra(Def.KEY_STUDENT_ID);
        String event_id = intent.getStringExtra(Def.KEY_EVENT_ID);
        JSONResponse response = mApiClient.getDeviceEventReport(student_id, event_id, "1");
        //Parse Event
        if (response != null) {
            JSONResponse.Results results = response.getReturn().getResults();
            if (results != null) {
                JSONResponse.Device[] devices = results.getDevices();
                if (devices != null && devices.length > 0) {
                    JSONResponse.DeviceEvent[] events = devices[0].getDevice_events();
                    String event_time = null;
                    for (JSONResponse.DeviceEvent event : events) {
                        String event_occured_date = event.getEvent_occured_date();
                        Date date = getDateByStringFormatted(event_occured_date, "yyyy-MM-dd HH:mm:ss.S");
                        event_time = getStringByDate(date, "HH:mm");
                    }
                    Intent event_intent = new Intent();
                    event_intent.setAction(Def.ACTION_GET_EVENT_REPORT);
                    event_intent.putExtra(Def.KEY_EVENT_ID, event_id);
                    event_intent.putExtra(Def.EXTRA_EVENT_VALUE, event_time);
                    sendBroadcast(event_intent);
                }
            }
        }
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


    private Date getDateByStringFormatted(String time, String pattern) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private String getStringByDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}