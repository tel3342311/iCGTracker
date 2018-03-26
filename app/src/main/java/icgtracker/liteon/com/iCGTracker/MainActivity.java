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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import icgtracker.liteon.com.iCGTracker.fragment.FenceFragment;
import icgtracker.liteon.com.iCGTracker.fragment.RecordFragment;
import icgtracker.liteon.com.iCGTracker.fragment.SafeFragment;
import icgtracker.liteon.com.iCGTracker.util.BottomNavigationViewHelper;
import icgtracker.liteon.com.iCGTracker.util.Def;
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
    }

    private void findViews() {
        mToolbar = findViewById(R.id.toolbar);
        mBottomView = findViewById(R.id.bottom_navigation);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation);
        mLogoutButton = mNavigationView.findViewById(R.id.drawer_button_logout);
        mDrawerHeader = mNavigationView.getHeaderView(0);
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

    private void logoutAccount() {

        mDrawerLayout.closeDrawers();
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

        if (mBottomView.getSelectedItemId() != 0) {
            mBottomView.setSelectedItemId(mBottomView.getSelectedItemId());
        } else {
            mBottomView.setSelectedItemId(R.id.action_safety);
        }

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
