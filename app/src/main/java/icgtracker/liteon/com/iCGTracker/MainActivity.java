package icgtracker.liteon.com.iCGTracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;

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
    }

    private void setListener() {
        mBottomView.setOnNavigationItemSelectedListener( item -> {
            BottomNavigationViewHelper.updateBackground(mBottomView, item);
            return true;
        });
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


    }

    private void showSplashPage() {
        Intent intent = new Intent();
        intent.setClass(this, SplashActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
