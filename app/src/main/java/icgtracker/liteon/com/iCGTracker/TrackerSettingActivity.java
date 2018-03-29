package icgtracker.liteon.com.iCGTracker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.util.ChildLocationItem;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.TrackerInfoItem;
import icgtracker.liteon.com.iCGTracker.util.TrackerInfoItemAdapter;

public class TrackerSettingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TrackerInfoItem> myDataset = new ArrayList<>();
    private Toolbar mToolbar;
    private ImageView mBack;
    private String mStudentName;
    private TextView mTitle;
    public TrackerSettingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_setting);
        findViews();
        setListener();
        initRecycleView();
        mStudentName = getIntent().getStringExtra(Def.EXTRA_STUDENT_NAME);
        mTitle.setText(String.format(getString(R.string.tracker_setting), mStudentName));
    }

    private void findViews() {
        mRecyclerView = findViewById(R.id.setting_list);
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.toolbar_title);
        mBack = findViewById(R.id.cancel);
    }

    private void setListener() {
        mBack.setOnClickListener(v -> onBackPressed());
    }

    public void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupData();
        mAdapter = new TrackerInfoItemAdapter(myDataset, mClicks);
        mRecyclerView.setAdapter(mAdapter);

    }

    private TrackerInfoItemAdapter.ViewHolder.ITrackerInfoViewHolderClicks mClicks = item -> {
        if (item.getItemType() == TrackerInfoItem.TYPE.TRACKER_INFO) {

            Intent intent = new Intent();
            intent.setClass(this, ChildInfoUpdateActivity.class);
            intent.putExtra(Def.EXTRA_STUDENT_NAME, mStudentName);
            startActivity(intent);
        } else if (item.getItemType() == TrackerInfoItem.TYPE.DEVICE_INFO){

        }
    };

    private void setupData(){
        if (myDataset.size() == 0) {
            for (TrackerInfoItem.TYPE type : TrackerInfoItem.TYPE.values()) {
                TrackerInfoItem item = new TrackerInfoItem();
                item.setItemType(type);
                myDataset.add(item);
            }
        }
    }


}
