package icgtracker.liteon.com.iCGTracker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.service.BleService;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.Utils;

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
    private String mBLEAddress;
    List<BluetoothGattService> mGattServices = new ArrayList<>();
    private int mRssi;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleService = ((BleService.LocalBinder) iBinder).getService();
            if (!mBleService.init()) {
                finish();
            }
            mBleService.connect(mBLEAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mBleService = null;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        findViews();
        setListener();
        setOptionText();
        bindBleSevice();
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
                Toast.makeText(DeviceInfoActivity.this, "ACTION_GATT_DISCONNECTED", Toast.LENGTH_LONG)
                        .show();
//                if (sharedPreferences.getBoolean("AutoConnect", true)) {
//                    bleService.connect(bleAddress);
//                }
            }
            if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String uuid = null;
                mBleService.mBluetoothGatt.readRemoteRssi();
                mGattServices = mBleService.mBluetoothGatt.getServices();
                final ArrayList<HashMap<String, String>> serviceNames = new ArrayList<HashMap<String, String>>();
                for (BluetoothGattService ser : mGattServices) {
                    HashMap<String, String> currentServiceData = new HashMap<String, String>();
                    uuid = ser.getUuid().toString();
                    currentServiceData.put("Name", Utils.attributes
                            .containsKey(uuid) ? Utils.attributes.get(uuid)
                            : "Unknown Service");
                    serviceNames.add(currentServiceData);
                }
//                runOnUiThread(new Runnable() {
//                    @SuppressLint("NewApi")
//                    public void run() {
//                        servicesListAdapter.addServiceNames(serviceNames);
//                        servicesListAdapter.addService(gattServices);
//                        servicesListAdapter.notifyDataSetChanged();
//
//                    }
//                });

            }
            if (Def.ACTION_GATT_RSSI.equals(action)) {
                mRssi = intent.getExtras().getInt(Def.EXTRA_DATA_RSSI);
                Log.i(TAG, "Current rssi is " + mRssi);
            }
        }
    };
}
