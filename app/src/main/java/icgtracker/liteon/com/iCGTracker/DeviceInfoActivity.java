package icgtracker.liteon.com.iCGTracker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.service.BleService;
import icgtracker.liteon.com.iCGTracker.util.CustomDialog;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;
import icgtracker.liteon.com.iCGTracker.util.Utils;
import icgtracker.liteon.com.iCGTracker.util.WearableInfo;

import static icgtracker.liteon.com.iCGTracker.util.Def.ACTION_GATT_SERVICES_DISCOVERED;

public class DeviceInfoActivity extends AppCompatActivity {
    private final static String TAG = DeviceInfoActivity.class.getSimpleName();
    private TextView mModel;
    private TextView mFirmware;
    private TextView mBatteryValue;
    private ImageView mBatteryIcon;
    private Button mDeviceBound;
    private Button mFirmwareUpdate;
    private View mReport30m;
    private View mReport60m;
    private View mReport90m;
    private View mReport120m;
    private View mBTSearch;
    private View mBTClose;
    private View mBTKeepInRange;
    private TextView mTitle;
    private ImageView mCancel;
    private BleService mBleService;
    List<BluetoothGattService> mGattServices = new ArrayList<>();
    private int mCurrnetStudentIdx;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private List<JSONResponse.Student> mStudents;
    private DBHelper mDbHelper;
    private int mRssi;
    private String mBleAddress;
    private WearableInfo mWearInfo;
    private final static int PERMISSION_REQUEST = 3;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleService = ((BleService.LocalBinder) iBinder).getService();
            if (!mBleService.init()) {
                finish();
            }
            mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
            mCurrnetStudentIdx = mSharedPreferences.getInt(Def.SP_CURRENT_STUDENT, 0);
            String uuid = mStudents.get(mCurrnetStudentIdx).getUuid();
            if (!TextUtils.isEmpty(uuid)) {
                WearableInfo info = mDbHelper.getWearableInfoByUuid(mDbHelper.getReadableDatabase(), uuid);
                if (info != null) {
                    mBleService.connect(info.getBtAddr());
                    mBleAddress = info.getBtAddr();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mBleService = null;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermisson();
        mSharedPreferences = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mDbHelper = DBHelper.getInstance(this);
        setContentView(R.layout.activity_device_info);
        findViews();
        setListener();
        setOptionText();
        bindBleSevice();
        registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    private void askPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    CustomDialog dialog = new CustomDialog();
                    dialog.setTitle("應用程式要求權限以繼續");
                    dialog.setIcon(0);
                    dialog.setBtnText("好");
                    dialog.show(getSupportFragmentManager(), "dialog_fragment");
                }

                break;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Def.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Def.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Def.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Def.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(Def.ACTION_GATT_RSSI);
        intentFilter.addAction(Def.ACTION_CHAR_READED);
        return intentFilter;
    }

    private void findViews() {
        mModel = findViewById(R.id.model_value);
        mFirmware = findViewById(R.id.fw_value);
        mBatteryValue = findViewById(R.id.battery_value);
        mBatteryIcon = findViewById(R.id.battery_icon);
        mDeviceBound = findViewById(R.id.device_bound);
        mFirmwareUpdate = findViewById(R.id.firmware_update);
        mReport30m = findViewById(R.id.report_30_min);
        mReport60m = findViewById(R.id.report_60_min);
        mReport90m = findViewById(R.id.report_90_min);
        mReport120m = findViewById(R.id.report_120_min);
        mBTSearch = findViewById(R.id.search_bt);
        mBTClose = findViewById(R.id.close_bt);
        mBTKeepInRange = findViewById(R.id.keep_bt);
        mTitle = findViewById(R.id.page_title);
        mCancel = findViewById(R.id.cancel);
    }

    private void setListener() {

        mDeviceBound.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, BLEPairingListActivity.class);
            startActivity(intent);
        });
        mFirmwareUpdate.setOnClickListener(v -> {});
        mReport30m.setOnClickListener(v -> {});
        mReport60m.setOnClickListener(v -> {});
        mReport90m.setOnClickListener(v -> {});
        mReport120m.setOnClickListener(v -> {});
        mBTSearch.setOnClickListener(v -> {});
        mBTClose.setOnClickListener(v -> {});
        mBTKeepInRange.setOnClickListener(v -> {});
        mCancel.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mCurrnetStudentIdx = mSharedPreferences.getInt(Def.SP_CURRENT_STUDENT, 0);
        String uuid = mStudents.get(mCurrnetStudentIdx).getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            mWearInfo = mDbHelper.getWearableInfoByUuid(mDbHelper.getReadableDatabase(), uuid);
            if (mWearInfo == null) {
                mDeviceBound.setText(R.string.bind_watch);
            } else {
                mDeviceBound.setText(R.string.unbind_watch);
            }
        }
    }

    private void setOptionText() {
        ((TextView)mReport30m.findViewById(R.id.record_item_time)).setText("30");
        ((TextView)mReport60m.findViewById(R.id.record_item_time)).setText("60");
        ((TextView)mReport90m.findViewById(R.id.record_item_time)).setText("90");
        ((TextView)mReport120m.findViewById(R.id.record_item_time)).setText("120");
        mReport120m.findViewById(R.id.connect_line).setVisibility(View.GONE);
        ((TextView)mBTSearch.findViewById(R.id.record_item_time)).setText(getString(R.string.bt_search));
        ((TextView)mBTClose.findViewById(R.id.record_item_time)).setText(getString(R.string.bt_close));
        ((TextView)mBTKeepInRange.findViewById(R.id.record_item_time)).setText(getString(R.string.bt_keep));
        mBTKeepInRange.findViewById(R.id.connect_line).setVisibility(View.GONE);
    }

    private void bindBleSevice() {
        Intent serviceIntent = new Intent(this, BleService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleService.mBluetoothGatt != null) {
            mBleService.close(mBleService.mBluetoothGatt);
        }
        unbindService(mConnection);
        unregisterReceiver(mbtBroadcastReceiver);
    }

    BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint({ "NewApi", "DefaultLocale" })
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (Def.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(() -> Toast.makeText(DeviceInfoActivity.this, "ACTION_GATT_CONNECTED",
                        Toast.LENGTH_LONG).show());
            }
            if (Def.ACTION_GATT_DISCONNECTED.equals(action)) {
                runOnUiThread(() -> Toast.makeText(DeviceInfoActivity.this, "ACTION_GATT_DISCONNECTED", Toast.LENGTH_LONG).show());
//                if (sharedPreferences.getBoolean("AutoConnect", true)) {
//                    bleService.connect(bleAddress);
//                }
                mBleService.connect(mBleAddress);
            }
            if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String uuid = null;
                mBleService.mBluetoothGatt.readRemoteRssi();
                mGattServices = mBleService.mBluetoothGatt.getServices();
                for (BluetoothGattService service : mGattServices) {
                    Log.i(TAG, "[ACTION_GATT_SERVICES_DISCOVERED] UUID :" + service.getUuid());
                    Log.i(TAG, "[ACTION_GATT_SERVICES_DISCOVERED] InstanceId :" + service.getInstanceId());
                    if (Def.BATTERY_SERVICE_UUID.compareTo(service.getUuid()) == 0) {
                        List<BluetoothGattCharacteristic> characterList = service.getCharacteristics();
                        for (BluetoothGattCharacteristic ch : characterList) {
                            Log.i(TAG, "BluetoothGattCharacteristic UUID " + ch.getUuid());
                            Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                            Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                        }

                    } else if (Def.DEVICE_INFORMATION_UUID.compareTo(service.getUuid()) == 0) {
                        List<BluetoothGattCharacteristic> characterList = service.getCharacteristics();
                        for (BluetoothGattCharacteristic ch : characterList) {
                            if (ch.getUuid().compareTo(Def.SOFTWARE_REVISION_STRING_UUID) == 0) {
                                mBleService.mBluetoothGatt.readCharacteristic(ch);
                            }
                            Log.i(TAG, "BluetoothGattCharacteristic UUID " + ch.getUuid());
                            Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                            Log.i(TAG, "BluetoothGattCharacteristic Value " + ch.getStringValue(0));
                        }
                    }
                }
            }
            if (Def.ACTION_GATT_RSSI.equals(action)) {
                mRssi = intent.getExtras().getInt(Def.EXTRA_DATA_RSSI);
                Log.i(TAG, "Current rssi is " + mRssi);
            }
            if (Def.ACTION_CHAR_READED.equals(action)) {

            }
        }
    };
}
