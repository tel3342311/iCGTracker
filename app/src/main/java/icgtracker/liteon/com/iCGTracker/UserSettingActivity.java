package icgtracker.liteon.com.iCGTracker;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.util.AppInfoPrivacyItem;
import icgtracker.liteon.com.iCGTracker.util.AppInfoPrivacyItemAdapter;
import icgtracker.liteon.com.iCGTracker.util.AppInfoPrivacyItemAdapter.ViewHolder;

public class UserSettingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AppInfoPrivacyItem> myDataset = new ArrayList<>();
    private Toolbar mToolbar;
    public UserSettingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        findViews();
        //setupToolbar();
        initRecycleView();
    }

    private void findViews() {
        mRecyclerView = findViewById(R.id.setting_list);
        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mToolbar.setTitle("");
    }

    public void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupData();
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mAdapter = new AppInfoPrivacyItemAdapter(myDataset, mClicks, version);
            mRecyclerView.setAdapter(mAdapter);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private ViewHolder.IAppInfoPrivacyViewHolderClicks mClicks = new ViewHolder.IAppInfoPrivacyViewHolderClicks() {

        @Override
        public void onClick(AppInfoPrivacyItem item) {
            if (item.getItemType() == AppInfoPrivacyItem.TYPE.PARENT_INFO) {

            } else if (item.getItemType() == AppInfoPrivacyItem.TYPE.USER_TERM){

            }
        }
    };

    private void setupData(){
        if (myDataset.size() == 0) {
            for (AppInfoPrivacyItem.TYPE type : AppInfoPrivacyItem.TYPE.values()) {
                AppInfoPrivacyItem item = new AppInfoPrivacyItem();
                item.setItemType(type);
                myDataset.add(item);
            }
        }
    }
}
