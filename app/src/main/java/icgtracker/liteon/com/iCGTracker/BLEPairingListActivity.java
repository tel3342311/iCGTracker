package icgtracker.liteon.com.iCGTracker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.service.BleService;
import icgtracker.liteon.com.iCGTracker.util.BLEItem;
import icgtracker.liteon.com.iCGTracker.util.BLEItemAdapter;
import icgtracker.liteon.com.iCGTracker.util.ConfirmDeleteDialog;
import icgtracker.liteon.com.iCGTracker.util.CustomDialog;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.GuardianApiClient;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;
import icgtracker.liteon.com.iCGTracker.util.Utils;
import icgtracker.liteon.com.iCGTracker.util.WearableInfo;

import static icgtracker.liteon.com.iCGTracker.util.Def.EXTRA_STRING_DATA;

public class BLEPairingListActivity extends AppCompatActivity implements BLEItemAdapter.ViewHolder.IBLEItemClickListener {

    private final static String TAG = BLEPairingListActivity.class.getName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<BLEItem> mDataSet;
    //ble
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private static int VERSION_CODES = 21;
    private final static int PERMISSION_REQUEST = 3;
    private DBHelper mDbHelper;
    private List<JSONResponse.Student> mStudents;
    private int mCurrnetStudentIdx;
    private BluetoothGattCharacteristic mRxCharacteristic;
    List<BluetoothGattService> mGattServices = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private BleService mBleService;
    private ConfirmDeleteDialog mBLEFailConfirmDialog;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleService = ((BleService.LocalBinder) iBinder).getService();
            if (!mBleService.init()) {
                finish();
            }
            //mBleService.connect(mBLEAddress);
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
        setContentView(R.layout.activity_ble_pairing_list);
        findViews();
        setListener();
        initRecycleView();
        initBleComponent();
        mDbHelper = DBHelper.getInstance(this);
        //get child list
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        bindBleSevice();
        registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    private void bindBleSevice() {
        Intent serviceIntent = new Intent(this, BleService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    private void initBleComponent() {
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupData();
        mAdapter = new BLEItemAdapter(mDataSet, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupData(){
        mDataSet = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (mGatt == null) {
                if (Build.VERSION.SDK_INT >= VERSION_CODES) {
                    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    settings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build();
                    filters = new ArrayList<>();
                }
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                List<BluetoothDevice> deviceList = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
                for (BluetoothDevice device : deviceList) {
                    UpdateList(device);
                }
                scanLeDevice(true);
            } else {
                //BluetoothDevice btDevice = mGatt.getConnectedDevices();

            }

        }
        mCurrnetStudentIdx = mSharedPreferences.getInt(Def.SP_CURRENT_STUDENT, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(() -> {
                if (Build.VERSION.SDK_INT < VERSION_CODES) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                } else {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < VERSION_CODES) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < VERSION_CODES) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }

    private void UpdateList(BluetoothDevice device) {
        BluetoothDevice btDevice = device;
        boolean isDuplicated = false;
        for (BLEItem item : mDataSet) {
            BluetoothDevice bluetoothDevice = item.getmBluetoothDevice();
            if (bluetoothDevice == null) {
                continue;
            } else if (TextUtils.equals(btDevice.getAddress(), bluetoothDevice.getAddress()) ) {
                isDuplicated = true;
                break;
            }
        }
        if (isDuplicated) {
            return;
        }
        BLEItem item = new BLEItem();
        item.setName(btDevice.getName());
        item.setId(btDevice.getAddress());
        //item.setId(btDevice.getUuids());
        int bond_state = btDevice.getBondState();
        if (bond_state == btDevice.BOND_NONE) {
            item.setValue("Not Connected");
        } else {
            item.setValue("Connected");
        }
        item.setmBluetoothDevice(btDevice);
        //connectToDevice(btDevice);
        mDataSet.add(item);
        mAdapter.notifyDataSetChanged();
    }

    private void UpdateList(BluetoothDevice device, int profile) {
        BluetoothDevice btDevice = device;
        for (BLEItem item : mDataSet) {
            BluetoothDevice bluetoothDevice = item.getmBluetoothDevice();
            if (bluetoothDevice == null) {
                continue;
            } else if (TextUtils.equals(btDevice.getAddress(), bluetoothDevice.getAddress()) ) {
                if (profile == BluetoothProfile.STATE_CONNECTED) {
                    item.setValue("Connected");
                } else if (profile == BluetoothProfile.STATE_DISCONNECTED) {
                    item.setValue("Not Connected");
                }
                item.setmBluetoothDevice(btDevice);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, " callbackType : " + String.valueOf(callbackType));
            Log.i(TAG, "Scan result " +result.toString());

            BluetoothDevice btDevice = result.getDevice();
            UpdateList(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i(TAG, "ScanResult - Results " + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG,"Scan Failed "+ " Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            (device, rssi, scanRecord) -> runOnUiThread(() -> {
                Log.i(TAG,"onLeScan " + device.toString());
                connectToDevice(device);
                UpdateList(device);
            });

    public void connectToDevice(BluetoothDevice device) {
        if (device != null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }



    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG,"onConnectionStateChange " + "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i(TAG,"gattCallback "+ "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e(TAG,"gattCallback "+ "STATE_DISCONNECTED");
                    runOnUiThread( () -> {
                        UpdateList(gatt.getDevice(), newState);
                    });
                    break;
                default:
                    Log.e(TAG,"gattCallback "+ "STATE_OTHER :" + newState);
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i(TAG,"onServicesDiscovered " + services.toString());
            for (BluetoothGattService service : services) {
                Log.i(TAG, "[onServicesDiscoveredservice] UUID :" + service.getUuid());
                Log.i(TAG, "[onServicesDiscoveredservice] InstanceId :" + service.getInstanceId());
                if (Def.BATTERY_SERVICE_UUID.compareTo(service.getUuid()) == 0) {
                    List<BluetoothGattCharacteristic> characterList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic ch : characterList) {
                        //gatt.readCharacteristic(ch);
                        Log.i(TAG, "BluetoothGattCharacteristic UUID " + ch.getUuid());
                        Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                        Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                    }

                } else if (Def.DEVICE_INFORMATION_UUID.compareTo(service.getUuid()) == 0) {
                    List<BluetoothGattCharacteristic> characterList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic ch : characterList) {
                        //if (ch.getUuid().compareTo(Def.SOFTWARE_REVISION_STRING_UUID) == 0) {
                        //    gatt.readCharacteristic(ch);
                        //}
                        Log.i(TAG, "BluetoothGattCharacteristic UUID " + ch.getUuid());
                        Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                        Log.i(TAG, "BluetoothGattCharacteristic Value " + ch.getStringValue(0));
                    }
                } else if (Def.NORDIC_UART_SERVICE_UUID.compareTo(service.getUuid()) == 0) {
                    runOnUiThread( () -> {
                        UpdateList(gatt.getDevice(), BluetoothProfile.STATE_CONNECTED);
                    });
                    List<BluetoothGattCharacteristic> characterList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic ch : characterList) {
                        if (ch.getUuid().compareTo(Def.NORDIC_UART_SERVICE_RX_UUID) == 0) {
                            //write cmd to get UUID
                            ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                            ch.setValue("uuid_read");
                            mRxCharacteristic = ch;
                        } else if (ch.getUuid().compareTo(Def.NORDIC_UART_SERVICE_TX_UUID) == 0) {
                            UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
                            BluetoothGattDescriptor descriptor = ch.getDescriptor(uuid);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            try {
                                Thread.sleep(1000);
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                            }
                            gatt.setCharacteristicNotification(ch, true);
                            if (mRxCharacteristic != null) {

                                try {
                                    Thread.sleep(8000);
                                }catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                                gatt.writeCharacteristic(mRxCharacteristic);
                            }
                        }
                        Log.i(TAG, "BluetoothGattCharacteristic UUID " + ch.getUuid());
                        Log.i(TAG, "BluetoothGattCharacteristic Value " + Arrays.toString(ch.getValue()));
                        Log.i(TAG, "BluetoothGattCharacteristic Value " + ch.getStringValue(0));
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG,"onCharacteristicRead " + characteristic.toString());
                Log.i(TAG,"onCharacteristicRead " + "getUUID : " + characteristic.getUuid());
                Log.i(TAG,"onCharacteristicRead " + "getDescriptor : " + characteristic.getDescriptor(characteristic.getUuid()));
                Log.i(TAG,"onCharacteristicRead " + "getInstanceId : " + characteristic.getInstanceId());
                Log.i(TAG,"onCharacteristicRead " + "getValue : " + Arrays.toString(characteristic.getValue()));
                Log.i(TAG,"onCharacteristicRead " + "getStringValue : " + characteristic.getStringValue(0));
                //gatt.disconnect();

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG,"onCharacteristicChanged " + characteristic.toString());
            Log.d(TAG,"onCharacteristicChanged " + "getUUID : " + characteristic.getUuid());
            Log.d(TAG,"onCharacteristicChanged " + "getDescriptor : " + characteristic.getDescriptor(characteristic.getUuid()));
            Log.d(TAG,"onCharacteristicChanged " + "getInstanceId : " + characteristic.getInstanceId());
            Log.d(TAG,"onCharacteristicChanged " + "getValue : " + Arrays.toString(characteristic.getValue()));
            Log.d(TAG,"onCharacteristicChanged " + "getStringValue : " + characteristic.getStringValue(0));
            if (characteristic.getUuid().compareTo(Def.NORDIC_UART_SERVICE_TX_UUID) == 0) {
                String plain = characteristic.getStringValue(0);
                final String uuid = String.format("%s-%s-%s-%s", plain.substring(0,7), plain.substring(7,11), plain.substring(11,15), plain.substring(15));
                Log.d(TAG,"onCharacteristicChanged " + "get Tracker uuid : " + uuid);
                mStudents.get(mCurrnetStudentIdx).setUuid(uuid);
                mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), mStudents.get(mCurrnetStudentIdx));
                new Thread( () -> {
                    GuardianApiClient apiClient = GuardianApiClient.getInstance(BLEPairingListActivity.this);
                    apiClient.pairNewDevice(mStudents.get(mCurrnetStudentIdx));
                });
            }
        }
    };

    private void findViews() {
        mRecyclerView = findViewById(R.id.profile_view);

    }

    private void setListener() {

    }

    @Override
    public void onBleItemClick(BLEItem item) {
        BluetoothDevice device = item.getmBluetoothDevice();
        if (mBleService != null && mBleService.init()) {

            mBleService.disconnect();
            mBleService.connect(device.getAddress());
        }
        //connectToDevice(device);
        //mStudents.get(mCurrnetStudentIdx).setUuid(item.getId());
        //mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), mStudents.get(mCurrnetStudentIdx));

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Def.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Def.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Def.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Def.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint({ "NewApi", "DefaultLocale" })
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Def.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(() -> Toast.makeText(BLEPairingListActivity.this, "ACTION_GATT_CONNECTED", Toast.LENGTH_LONG).show());
                mBleService.mBluetoothGatt.discoverServices();
            } else if (Def.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(BLEPairingListActivity.this, "ACTION_GATT_DISCONNECTED", Toast.LENGTH_LONG)
                        .show();

            } else if (Def.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String uuid = null;
                //mBleService.mBluetoothGatt.readRemoteRssi();
                mGattServices = mBleService.mBluetoothGatt.getServices();
                for (BluetoothGattService gattService : mGattServices) {
                    if (Def.NORDIC_UART_SERVICE_UUID.compareTo(gattService.getUuid()) == 0){
                        runOnUiThread(() -> Toast.makeText(BLEPairingListActivity.this, "Found NORDIC_UART_SERVICE_UUID", Toast.LENGTH_LONG).show());
                        UpdateList(mBleService.mBluetoothGatt.getDevice(), BluetoothProfile.STATE_CONNECTED);
                        getUUIDFromService(gattService);
                    }
                }

            } else if (Def.ACTION_DATA_AVAILABLE.equals(action)) {
                String uuid = intent.getStringExtra(EXTRA_STRING_DATA);
                runOnUiThread(() -> Toast.makeText(BLEPairingListActivity.this, "UUID " + uuid , Toast.LENGTH_LONG).show());
                if (!TextUtils.isEmpty(uuid)) {
                    new UpdateUUIDToCloud().execute(uuid, mBleService.mBluetoothGatt.getDevice().getAddress());
                }
            }
        }
    };

    private void getUUIDFromService(BluetoothGattService service) {
        BluetoothGattCharacteristic rxCharacteristic = null;
        BluetoothGattCharacteristic txCharacteristic = null;
        List<BluetoothGattCharacteristic> characterList = service.getCharacteristics();
        for (BluetoothGattCharacteristic ch : characterList) {
            if (ch.getUuid().compareTo(Def.NORDIC_UART_SERVICE_RX_UUID) == 0) {
                //write cmd to get UUID
                ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                ch.setValue("uuid_read");
                rxCharacteristic = ch;
            } else if (ch.getUuid().compareTo(Def.NORDIC_UART_SERVICE_TX_UUID) == 0) {
                txCharacteristic = ch;

            }
            Log.i(TAG, "getUUIDFromService UUID " + ch.getUuid());
            Log.i(TAG, "getUUIDFromService Value " + Arrays.toString(ch.getValue()));
            Log.i(TAG, "getUUIDFromService Value " + ch.getStringValue(0));
        }

        if (txCharacteristic != null) {
            UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
            BluetoothGattDescriptor descriptor = txCharacteristic.getDescriptor(uuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBleService.mBluetoothGatt.writeDescriptor(descriptor);

            mBleService.mBluetoothGatt.setCharacteristicNotification(txCharacteristic, true);
            if (rxCharacteristic != null) {

                try {
                    Thread.sleep(3000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
                mBleService.mBluetoothGatt.writeCharacteristic(rxCharacteristic);
            }
        }
    }

    class UpdateUUIDToCloud extends AsyncTask<String, Void, Boolean> {

        private String mErrorMessage;
        @Override
        protected Boolean doInBackground(String... strings) {
            String uuid = strings[0];
            String address = strings[1];
            JSONResponse.Student student = mStudents.get(mCurrnetStudentIdx);
            //Update Wearable info
            WearableInfo info = new WearableInfo();
            info.setUuid(uuid);
            info.setBtAddr(address);
            info.setStudentID(mStudents.get(mCurrnetStudentIdx).getStudent_id());
            mDbHelper.replaceWearableData(mDbHelper.getWritableDatabase(), info);

            GuardianApiClient mApiClient = GuardianApiClient.getInstance(BLEPairingListActivity.this);
            student.setUuid(uuid);
            JSONResponse response = mApiClient.pairNewDevice(student);
            if (response != null) {
                String statusCode = response.getReturn().getResponseSummary().getStatusCode();
                if (!TextUtils.equals(statusCode, Def.RET_SUCCESS_1) && !TextUtils.equals(statusCode, Def.RET_ERR_14) ) {
                    return false;
                } else {
                    if (response.getReturn().getResults() != null) {

                        String studentID = Integer.toString(response.getReturn().getResults().getStudent_id());
                        String studentName = response.getReturn().getResults().getStudent_name();
                        String nickName = response.getReturn().getResults().getNickname();
                        String roll_no = Integer.toString(response.getReturn().getResults().getRoll_no());
                        String registration_no = response.getReturn().getResults().getRegistration_no();
                        String dob = response.getReturn().getResults().getDob();
                        String gender = response.getReturn().getResults().getGender();
                        String weight = response.getReturn().getResults().getWeight();
                        String height = response.getReturn().getResults().getHeight();
                        String emergency_contact = response.getReturn().getResults().getEmergency_contact();
                        String allergies = response.getReturn().getResults().getAllergies();

                        JSONResponse.Student item = new JSONResponse.Student();
                        item.setUuid(uuid);
                        item.setStudent_id(Integer.parseInt(studentID));
                        item.setNickname(nickName);
                        item.setRoll_no(Integer.parseInt(roll_no));
                        item.setName(studentName);
                        item.setRegistration_no(registration_no);
                        item.setDob(dob);
                        item.setGender(gender);
                        item.setWeight(weight);
                        item.setHeight(height);
                        item.setEmergency_contact(emergency_contact);
                        item.setAllergies(allergies);
                        //For New child
                        if (!TextUtils.equals(studentID, student.getStudent_id())) {

                            item.setNickname(student.getNickname());
                            item.setDob(student.getDob());
                            item.setGender(student.getGender());
                            item.setHeight(student.getHeight());
                            item.setWeight(student.getWeight());
                            mDbHelper.deleteChildByStudentID(mDbHelper.getWritableDatabase(), student.getStudent_id());
                            mDbHelper.insertChild(mDbHelper.getWritableDatabase(), item);
                        }
                        mApiClient.updateChildData(item);
                    }
                    mDbHelper.updateChildByStudentId(mDbHelper.getWritableDatabase(), student);
                    return true;
                }

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool.booleanValue() == false) {
                CustomDialog dialog = new CustomDialog();
                String title = String.format(getString(R.string.pairing_watch_pin_error));
                dialog.setTitle(title);
                dialog.setIcon(0);
                dialog.setBtnText(getString(android.R.string.ok));
                dialog.setBtnConfirm(v -> dialog.dismiss());
                dialog.show(getSupportFragmentManager(), "dialog_fragment");
            } else {
                CustomDialog dialog = new CustomDialog();
                String title = String.format(getString(R.string.pairing_watch_success), mStudents.get(mCurrnetStudentIdx).getNickname());
                dialog.setTitle(title);
                dialog.setIcon(0);
                dialog.setBtnText(getString(android.R.string.ok));
                dialog.setBtnConfirm(v -> onBackPressed());
                dialog.show(getSupportFragmentManager(), "dialog_fragment");
            }
        }
    }
}
