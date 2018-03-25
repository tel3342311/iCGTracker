package icgtracker.liteon.com.iCGTracker;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditFenceActivity extends AppCompatActivity {


    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Toolbar mToolbar;
    private ImageView mCancel;
    private ImageView mConfirm;
    private EditText mFenceName;
    private View mFenceSizeArea;
    private View mBtn100meter;
    private View mBtn200meter;
    private View mBtn500meter;
    private View mBtn1000meter;
    private Marker mMarker;
    private Circle mFenceCircle;
    private LatLng mLatlng;
    private final static int METER_100  = 100;
    private final static int METER_200  = 200;
    private final static int METER_500  = 500;
    private final static int METER_1000  = 1000;
    private int mCurrentFenceRange = METER_100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fence);
        findViews();
        setListener();

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            initMapComponent();
        }
    }

    private void initMapComponent() {
        mMapView.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(25.077877, 121.571141), 16);
            mGoogleMap.animateCamera(cameraUpdate);

            mGoogleMap.setOnMapClickListener(latLng -> {

                mLatlng = latLng;
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                if (mMarker != null) {
                    mGoogleMap.clear();
                }
                mMarker = mGoogleMap.addMarker(markerOptions);
                CameraUpdate _cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.f);
                mGoogleMap.animateCamera(_cameraUpdate);
                onFenceRangeChange(mCurrentFenceRange);
            });
        });
    }

    private void findViews() {
        mMapView = findViewById(R.id.map_view);
        mToolbar = findViewById(R.id.toolbar);
        mCancel = findViewById(R.id.cancel);
        mConfirm = findViewById(R.id.confirm);
        mFenceName = findViewById(R.id.fence_title);
        mBtn100meter = findViewById(R.id.fence_100_meter);
        mBtn200meter = findViewById(R.id.fence_200_meter);
        mBtn500meter = findViewById(R.id.fence_500_meter);
        mBtn1000meter = findViewById(R.id.fence_1000_meter);
        //setup Range text
        ((TextView)(mBtn100meter.findViewById(R.id.record_item_time))).setText("0.1");
        ((TextView)(mBtn200meter.findViewById(R.id.record_item_time))).setText("0.2");
        ((TextView)(mBtn500meter.findViewById(R.id.record_item_time))).setText("0.5");
        ((TextView)(mBtn1000meter.findViewById(R.id.record_item_time))).setText("1.0");
        updateSelectedRange(mCurrentFenceRange);
        mBtn1000meter.findViewById(R.id.connect_line).setVisibility(View.INVISIBLE);
    }

    private void setListener() {
        mCancel.setOnClickListener( v->onBackPressed());
        mConfirm.setOnClickListener(v->onBackPressed());
        mBtn100meter.setOnClickListener(v->onFenceRangeChange(100));
        mBtn200meter.setOnClickListener(v->onFenceRangeChange(200));
        mBtn500meter.setOnClickListener(v->onFenceRangeChange(500));
        mBtn1000meter.setOnClickListener(v->onFenceRangeChange(1000));
    }

    private void updateSelectedRange(int meter) {

        mBtn100meter.findViewById(R.id.record_icon).setSelected(false);
        mBtn200meter.findViewById(R.id.record_icon).setSelected(false);
        mBtn500meter.findViewById(R.id.record_icon).setSelected(false);
        mBtn1000meter.findViewById(R.id.record_icon).setSelected(false);

        switch(meter) {
            case METER_100:
                mBtn100meter.findViewById(R.id.record_icon).setSelected(true);
                break;
            case METER_200:
                mBtn200meter.findViewById(R.id.record_icon).setSelected(true);
                break;
            case METER_500:
                mBtn500meter.findViewById(R.id.record_icon).setSelected(true);
                break;
            case METER_1000:
                mBtn1000meter.findViewById(R.id.record_icon).setSelected(true);
                break;
        }
        mCurrentFenceRange = meter;
    }

    private void onFenceRangeChange(int meter) {
        if (mGoogleMap != null && mLatlng != null) {
            if (mFenceCircle != null) {
                mFenceCircle.remove();
            }
            mFenceCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(mLatlng)
                    .radius(meter)
                    .strokeColor(ContextCompat.getColor(App.getContext(), R.color.md_grey_700))
                    .strokeWidth(1.f)
                    .fillColor(ContextCompat.getColor(App.getContext(), R.color.color_fence_circle_bg)));

            updateSelectedRange(meter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onStop();
        }
    }
}
