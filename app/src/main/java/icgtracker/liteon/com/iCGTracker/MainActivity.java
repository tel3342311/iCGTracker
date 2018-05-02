package icgtracker.liteon.com.iCGTracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.fragment.FenceFragment;
import icgtracker.liteon.com.iCGTracker.fragment.RecordFragment;
import icgtracker.liteon.com.iCGTracker.fragment.SafeFragment;
import icgtracker.liteon.com.iCGTracker.util.AppDrawerItem;
import icgtracker.liteon.com.iCGTracker.util.AppDrawerItemAdapter;
import icgtracker.liteon.com.iCGTracker.util.BottomNavigationViewHelper;
import icgtracker.liteon.com.iCGTracker.util.ConfirmDeleteDialog;
import icgtracker.liteon.com.iCGTracker.util.CustomDialog;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.FenceEntyAdapter;
import icgtracker.liteon.com.iCGTracker.util.GuardianApiClient;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;
import icgtracker.liteon.com.iCGTracker.util.RecordEventItem;
import icgtracker.liteon.com.iCGTracker.util.Utils;
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
    private ConfirmDeleteDialog mDeleteAccountConfirmDialog;
    private final static int REQUEST_ADDUSER = 1001;
    private final static int REQUEST_READY_PAIR = 1002;

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
        mDrawerAdapter = new AppDrawerItemAdapter(mDataset, (v,itemSelect) -> onDrawitemClick(v,itemSelect), "");
        mDrawerList.setAdapter(mDrawerAdapter);
    }

    private void onDrawitemClick(View v, AppDrawerItem item) {
        if (v.getId() == R.id.item_connect_icon) {
            Intent intent = new Intent();
            intent.putExtra(Def.EXTRA_STUDENT_NAME, item.getValue());
            intent.setClass(this, TrackerSettingActivity.class);
            startActivity(intent);
        } else {
            if (item.getItemType() == AppDrawerItem.TYPE.ADD_USER){

                Intent intent = new Intent();
                intent.setClass(this, ChildInfoUpdateActivity.class);
                startActivityForResult(intent, REQUEST_ADDUSER);

            } else if (item.getItemType() == AppDrawerItem.TYPE.DELETE_USER){
                deleteAccount();
            } else {
                if (TextUtils.equals(item.getValue(), mStudents.get(mCurrentStudentIdx).getNickname())){
                    mDrawerLayout.closeDrawers();
                } else {
                    switchAccount(item.getId());
                }
            }
        }
    }

    private void switchAccount(String studentId) {
        showSplashPage();

        for (int idx = 0; idx < mStudents.size(); idx++) {
            if (TextUtils.equals(mStudents.get(idx).getStudent_id(), studentId)){
                mCurrentStudentIdx = idx;
                break;
            }
        }
        updateDrawerData();

        SharedPreferences.Editor editor = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putInt(Def.SP_CURRENT_STUDENT, mCurrentStudentIdx);
        editor.commit();
        mDrawerLayout.closeDrawers();
        mDrawerAdapter.notifyDataSetChanged();
    }

    private void updateDrawerData() {

        mDataset.clear();
        int i = 0;
        AppDrawerItem item;
        for (int idx = 0; idx < mStudents.size(); idx++) {
            JSONResponse.Student student = mStudents.get(idx);
            item = new AppDrawerItem();
            item.setItemType(AppDrawerItem.TYPE.USER);
            item.setValue(student.getNickname());
            item.setId(student.getStudent_id());
            if (idx == mCurrentStudentIdx) {
                item.setSelect(true);
            }
            mDataset.add(i++, item);
        }
        item = new AppDrawerItem();
        item.setItemType(AppDrawerItem.TYPE.ADD_USER);
        mDataset.add(i++, item);

        item = new AppDrawerItem();
        item.setItemType(AppDrawerItem.TYPE.DELETE_USER);
        if (mStudents.size() > 0) {
                item.setValue(String.format(getString(R.string.delete_tracker), mStudents.get(mCurrentStudentIdx).getNickname()));
        }
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
            //mBottomView.setSelectedItemId(R.id.action_safety);
        }
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mCurrentStudentIdx = mSharePreference.getInt(Def.SP_CURRENT_STUDENT, 0);
        if (mStudents != null && mStudents.size() > 0) {
            String name = mStudents.get(mCurrentStudentIdx).getNickname();
            mChildName.setText(name);
        }
        updateDrawerData();
        mDrawerAdapter.notifyDataSetChanged();
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

    public void deleteAccount() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(String.format(getString(R.string.delete_tracking), mChildName.getText()))
                .append("\n")
                .append(getString(R.string.delete_warning));
        mDeleteAccountConfirmDialog = new ConfirmDeleteDialog();
        mDeleteAccountConfirmDialog.setOnConfirmEventListener(mOnDeleteAccountConfirm);
        mDeleteAccountConfirmDialog.setmOnCancelListener(mOnDeleteAccountCancel);
        mDeleteAccountConfirmDialog.setmTitleText(sBuilder.toString());
        mDeleteAccountConfirmDialog.setmBtnConfirmText(getString(R.string.delete_child_confirm));
        mDeleteAccountConfirmDialog.setmBtnCancelText(getString(R.string.delete_child_cancel));
        mDeleteAccountConfirmDialog.show(getSupportFragmentManager(), "dialog_fragment");
    }

    private View.OnClickListener mOnDeleteAccountConfirm = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mStudents.size() == 0) {
                return;
            }
            JSONResponse.Student student = mStudents.get(mCurrentStudentIdx);
            student.setIsDelete(1);
            mDbHelper.deleteChildByStudentID(mDbHelper.getWritableDatabase(), student.getStudent_id());
            mStudents.remove(student);
            SharedPreferences.Editor editor = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE).edit();
            editor.putInt(Def.SP_CURRENT_STUDENT, 0);
            editor.commit();
            new UnPairCloudTask().execute(student);
            mDeleteAccountConfirmDialog.dismiss();
            final CustomDialog dialog = new CustomDialog();
            dialog.setTitle(String.format(getString(R.string.child_deleted), mChildName.getText()));
            dialog.setBtnText(getString(android.R.string.ok));
            dialog.setBtnConfirm(v1 -> {
                dialog.dismiss();
                if (mStudents.size() == 0) {
                    mLogoutButton.callOnClick();
                } else {
                    switchAccount(mStudents.get(0).getStudent_id());
                }
            });
            dialog.show(getSupportFragmentManager(), "dialog_fragment");
        }
    };

    private View.OnClickListener mOnDeleteAccountCancel = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mDeleteAccountConfirmDialog.dismiss();
        }
    };

    class UnPairCloudTask extends AsyncTask<JSONResponse.Student, Void, Void> {

        @Override
        protected Void doInBackground(JSONResponse.Student... params) {
            JSONResponse.Student student = params[0];
            GuardianApiClient apiClient = GuardianApiClient.getInstance(MainActivity.this);
            JSONResponse response = apiClient.unpairDevice(student);
            String uuid = student.getUuid();
            if (response != null) {
                if (response.getReturn() != null) {
                    String statusCode = response.getReturn().getResponseSummary().getStatusCode();
                    if (TextUtils.equals(statusCode, Def.RET_SUCCESS_1)) {
                        student.setUuid("");
                        mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
                    } else if (TextUtils.equals(statusCode, Def.RET_ERR_16)) {
                        student.setUuid("");
                        mDbHelper.updateChildData(mDbHelper.getWritableDatabase(), student);
                    }
                    mDbHelper.deleteWearableData(mDbHelper.getWritableDatabase(), uuid);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDUSER) {
            if (RESULT_OK == resultCode){
                Intent intent = new Intent();
                intent.setClass(this, ChildPairingActivity.class);
                startActivityForResult(intent, REQUEST_READY_PAIR);
            }
        } else if (requestCode == REQUEST_READY_PAIR) {
            if (RESULT_OK == resultCode){
                Intent intent = new Intent();
                intent.setClass(this, BLEPairingListActivity.class);
                String name = mStudents.get(mCurrentStudentIdx).getNickname();
                intent.putExtra(Def.EXTRA_STUDENT_NAME, name);
                startActivityForResult(intent, REQUEST_READY_PAIR);
            }
        }
    }
}
