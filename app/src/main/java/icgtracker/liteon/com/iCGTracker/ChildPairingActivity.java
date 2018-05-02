package icgtracker.liteon.com.iCGTracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ChildPairingActivity extends AppCompatActivity {

	private AppCompatButton mStartPairing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_pairing);
		findViews();
		setListener();
	}

	private void findViews() {
		mStartPairing = findViewById(R.id.pairing_watch_now);
	}
	
	private void setListener() {
		mStartPairing.setOnClickListener(mOnClickListener);
	}
	
	private View.OnClickListener mOnClickListener = v -> {

        setResult(RESULT_OK);
        onBackPressed();

    };
}
