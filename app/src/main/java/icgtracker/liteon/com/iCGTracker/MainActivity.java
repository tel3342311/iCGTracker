package icgtracker.liteon.com.iCGTracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.fragment.FenceFragment;
import icgtracker.liteon.com.iCGTracker.fragment.RecordFragment;
import icgtracker.liteon.com.iCGTracker.fragment.SafeFragment;
import icgtracker.liteon.com.iCGTracker.util.AppDrawerItem;
import icgtracker.liteon.com.iCGTracker.util.AppDrawerItemAdapter;
import icgtracker.liteon.com.iCGTracker.util.BottomNavigationViewHelper;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.FenceEntyAdapter;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;
import icgtracker.liteon.com.iCGTracker.util.RecordEventItem;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharePreference;
    private Boolean isFirstLaunch = true;
    private BottomNavigationView mBottomView;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private AppCompatButton mLogoutButton;
    private Fragment mCurrentFragment;
    private static final int NAVIGATION_DRAWER = 1;
    private static final int NAVIGATION_BACK = 2;
    private TextView mTitleView;
    private View mDrawerHeader;
    private DBHelper mDbHelper;
    private List<JSONResponse.Student> mStudents;
    private int mCurrentStudentIdx;
    private TextView mChildName;
    private RecyclerView mDrawerList;
    private RecyclerView.Adapter mDrawerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AppDrawerItem> mDataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        findViews();
        setListener();
        setupToolbar();
        BottomNavigationViewHelper.disableShiftMode(mBottomView);
        mSharePreference = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mDbHelper = DBHelper.getInstance(this);
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        setupDrawerMenu();

    }

    private void findViews() {
        mToolbar = findViewById(R.id.toolbar);
        mBottomView = findViewById(R.id.bottom_navigation);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation);
        mLogoutButton = mNavigationView.findViewById(R.id.drawer_button_logout);
        mDrawerHeader = mNavigationView.getHeaderView(0);
        mDrawerList = mNavigationView.findViewById(R.id.drawer_list);
        mChildName = mDrawerHeader.findViewById(R.id.child_name);
        mTitleView = findViewById(R.id.toolbar_title);
    }

    private void setListener() {
        mBottomView.setOnNavigationItemSelectedListener( item -> {
            BottomNavigationViewHelper.updateBackground(mBottomView, item);

            switch(item.getItemId()) {
                case R.id.action_safety:
                    changeFragment(SafeFragment.newInstance("",""), getString(R.string.action_safe), NAVIGATION_DRAWER);
                    break;
                case R.id.action_region:
                    changeFragment(FenceFragment.newInstance("",""), getString(R.string.action_region), NAVIGATION_DRAWER);
                    break;
                case R.id.action_trackers:
                    changeFragment(RecordFragment.newInstance("",""), getString(R.string.action_trackers), NAVIGATION_DRAWER);
                    break;
            }
            return true;
        });

        mDrawerHeader.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, UserSettingActivity.class);
            startActivity(intent);
        });

        mNavigationView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_tracker:
                    break;
                case R.id.action_delete_tracker:
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        });
        mLogoutButton.setOnClickListener(v -> logoutAccount());
    }

    public void logoutAccount() {

        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.remove(Def.SP_LOGIN_TOKEN);
        editor.commit();
        mDrawerLayout.closeDrawers();
        showLoginPage();
    }

    private void setupDrawerMenu() {

        mDrawerList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mDrawerList.setLayoutManager(mLayoutManager);
        updateDrawerData();
        mDrawerAdapter = new AppDrawerItemAdapter(mDataset, itemSelect -> {

            switchAccount();

        }, "");
        mDrawerList.setAdapter(mDrawerAdapter);
    }

    private void switchAccount() {
        if (mStudents.size() == 0) {
            return;
        }
        if (mStudents.size() == 1) {
            mCurrentStudentIdx = 0;
        } else {

            if (mCurrentStudentIdx == mStudents.size() - 1) {
                mCurrentStudentIdx = 0;
            } else {
                mCurrentStudentIdx++;
            }
        }
        updateDrawerData();

        SharedPreferences.Editor editor = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putInt(Def.SP_CURRENT_STUDENT, mCurrentStudentIdx);
        editor.commit();
        mDrawerLayout.closeDrawers();
        showSplashPage();
    }

    private void updateDrawerData() {

        int i = 0;
        AppDrawerItem item;
        for (JSONResponse.Student student : mStudents) {
            item = new AppDrawerItem();
            item.setItemType(AppDrawerItem.TYPE.USER);
            item.setValue(student.getNickname());
            mDataset.add(i++, item);
        }
        item = new AppDrawerItem();
        item.setItemType(AppDrawerItem.TYPE.ADD_USER);
        mDataset.add(i++, item);

        item = new AppDrawerItem();
        item.setItemType(AppDrawerItem.TYPE.DELETE_USER);
        mDataset.add(i, item);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(Gravity.LEFT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLaunch) {
            showSplashPage();
            isFirstLaunch = false;
        }
        Boolean isUserterm = mSharePreference.getBoolean(Def.SP_USER_TERM_READ, false);
        String token = mSharePreference.getString(Def.SP_LOGIN_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            showLoginPage();
            return;
        }
        if (mBottomView.getSelectedItemId() != 0) {
            mBottomView.setSelectedItemId(mBottomView.getSelectedItemId());
        } else {
            mBottomView.setSelectedItemId(R.id.action_safety);
        }

        if (mStudents != null && mStudents.size() > 0) {
            String name = mStudents.get(mCurrentStudentIdx).getNickname();
            mChildName.setText(name);
        }
    }

    private void showLoginPage() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }

    private void showSplashPage() {
        Intent intent = new Intent();
        intent.setClass(this, SplashActivity.class);
        startActivity(intent);
    }

    private void changeFragment(Fragment frag, String title, int navigation) {
        mCurrentFragment = frag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag);
        fragmentTransaction.commit();

        if (mToolbar != null) {

            mToolbar.setTitle("");
            mTitleView.setText(title);
            if (navigation == NAVIGATION_BACK) {
                mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
                mToolbar.setNavigationOnClickListener(v -> onBackPressed());
            } else if (navigation == NAVIGATION_DRAWER) {
                mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
                mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(Gravity.LEFT));
            }
        }
    }
}
