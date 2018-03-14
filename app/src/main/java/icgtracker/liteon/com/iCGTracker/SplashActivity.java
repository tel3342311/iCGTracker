package icgtracker.liteon.com.iCGTracker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import icgtracker.liteon.com.iCGTracker.util.Utils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
    private Handler mHandlerTime;
    private int mProgressStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViews();
        setListener();
        mHandlerTime = new Handler();
    }

    private void findViews() {
    }

    private void setListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressStep = 0;
        mHandlerTime.postDelayed(UpdatePage, 150);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandlerTime.removeCallbacks(UpdatePage);
    }

    public Runnable UpdatePage = () -> {

            if (Utils.isNetworkConnectionAvailable()) {
                onBackPressed();
            } else {
                Utils.showErrorDialog(getString(R.string.login_no_network), v -> onBackPressed());
            }

    };
}
