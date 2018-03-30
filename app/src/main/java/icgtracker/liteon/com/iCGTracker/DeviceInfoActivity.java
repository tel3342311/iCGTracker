package icgtracker.liteon.com.iCGTracker;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceInfoActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        findViews();
        setListener();
        setOptionText();
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

        mDeviceBound.setOnClickListener(v -> {});
        mFirmwareUpdate.setOnClickListener(v -> {});
        mReport30m.setOnClickListener(v -> {});
        mReport60m.setOnClickListener(v -> {});
        mReport90m.setOnClickListener(v -> {});
        mReport120m.setOnClickListener(v -> {});
        mBTSearch.setOnClickListener(v -> {});
        mBTClose.setOnClickListener(v -> {});
        mBTKeepInRange.setOnClickListener(v -> {});
        mCancel.setOnClickListener( v-> onBackPressed());
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
}
