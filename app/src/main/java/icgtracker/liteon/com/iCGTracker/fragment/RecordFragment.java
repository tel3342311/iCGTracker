package icgtracker.liteon.com.iCGTracker.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.R;
import icgtracker.liteon.com.iCGTracker.util.FenceEntryItem;
import icgtracker.liteon.com.iCGTracker.util.FenceEntyAdapter;
import icgtracker.liteon.com.iCGTracker.util.RecordEventAdapter;
import icgtracker.liteon.com.iCGTracker.util.RecordEventItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    private View mRootView;
    private TextView mRecordTitle;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView mForward;
    private ImageView mBackward;
    private ImageView mNextDate;
    private ImageView mLastDate;
    private List<RecordEventItem> mDataset;
    private List<Marker> mMarkerList;
    private List<Date> mDateList;
    private static int MAX_DATE = 3;
    private int mCurrentDateIdx = MAX_DATE -1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_record, container, false);
        findViews();
        setListener();
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            initMapComponent();
        }
        mDataset = new ArrayList<>();
        mMarkerList = new ArrayList<>();
        initRecycleView();
        updateDateList();
        updateDateTitle(mDateList.get(mCurrentDateIdx));
        updateDateBtnState();
        return mRootView;
    }

    private void updateDateList() {
        mDateList = new ArrayList<>();
        Date date = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < MAX_DATE; i++) {
            c.setTime(date);
            c.add(Calendar.DATE, i - (MAX_DATE - 1));
            mDateList.add(0, c.getTime());
        }
        mCurrentDateIdx = MAX_DATE - 1;
    }
    private void updateDateTitle(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        mRecordTitle.setText(sdf.format(date));
    }

    private void initRecycleView() {

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        testData();
        mAdapter = new RecordEventAdapter(mDataset);
        ((RecordEventAdapter)mAdapter).setOnClickListener(v -> {
            int position = mRecyclerView.getChildLayoutPosition(v);
            for (RecordEventItem item : mDataset) {
                item.setSelect(false);
            }
            final RecordEventItem itemSelected = mDataset.get(position);
            itemSelected.setSelect(true);
            mAdapter.notifyDataSetChanged();
            BitmapDescriptor iconNormal = BitmapDescriptorFactory.fromResource(R.drawable.record_btn_time);
            BitmapDescriptor iconSelect = BitmapDescriptorFactory.fromResource(R.drawable.record_btnf_time);

            for (Marker marker : mMarkerList) {
                if (mMarkerList.indexOf(marker) == position){
                    marker.setIcon(iconSelect);
                } else{
                    marker.setIcon(iconNormal);
                }
            }
            if (mGoogleMap != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(itemSelected.getLatLng());
                mGoogleMap.animateCamera(cameraUpdate);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        updateRecordBtnState();
    }

    private void updateDateBtnState() {
        if (mCurrentDateIdx == MAX_DATE -1) {
            mNextDate.setVisibility(View.INVISIBLE);
        } else {
            mNextDate.setVisibility(View.VISIBLE);
        }

        if (mCurrentDateIdx == 0) {
            mLastDate.setVisibility(View.INVISIBLE);
        } else {
            mLastDate.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecordBtnState() {
        if (((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition() == 0) {
            mBackward.setVisibility(View.INVISIBLE);
        } else {
            mBackward.setVisibility(View.VISIBLE);
        }

        if (((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition() == mAdapter.getItemCount() - 1) {
            mForward.setVisibility(View.INVISIBLE);
        } else {
            mForward.setVisibility(View.VISIBLE);
        }
    }

    private void findViews() {
        mMapView = mRootView.findViewById(R.id.map_view);
        mLastDate = mRootView.findViewById(R.id.last_record);
        mNextDate = mRootView.findViewById(R.id.next_record);
        mForward = mRootView.findViewById(R.id.forward);
        mBackward = mRootView.findViewById(R.id.backward);
        mRecyclerView = mRootView.findViewById(R.id.fence_entry_list);
        mRecordTitle = mRootView.findViewById(R.id.record_title);
    }

    private void setListener() {
        mLastDate.setOnClickListener(v -> {
            mCurrentDateIdx = (mCurrentDateIdx - 1) < 0 ? 0 : mCurrentDateIdx - 1;
            updateDateTitle(mDateList.get(mCurrentDateIdx));
            updateDateBtnState();
        });
        mNextDate.setOnClickListener(v -> {
            mCurrentDateIdx = (mCurrentDateIdx + 1) >= MAX_DATE ? MAX_DATE - 1 : mCurrentDateIdx + 1;
            updateDateTitle(mDateList.get(mCurrentDateIdx));
            updateDateBtnState();
        });

        mForward.setOnClickListener(v -> {
            int position = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            position = ((position + 3) < mAdapter.getItemCount()) ? position + 3 : mAdapter.getItemCount() -1;
            mRecyclerView.smoothScrollToPosition(position);
        });

        mBackward.setOnClickListener(v -> {
            int position = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();
            position = ((position - 3) > 0 ) ? position - 3 : 0;
            mRecyclerView.smoothScrollToPosition(position);

        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateRecordBtnState();
            }
        });
    }

    private void initMapComponent() {
        mMapView.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.record_btn_time);
            for (RecordEventItem item : mDataset) {
                builder.include(item.getLatLng());


                MarkerOptions markerOptions = new MarkerOptions().position(item.getLatLng())
                        .title(item.getRecordEventTime())
                        .icon(icon);

                mMarkerList.add(mGoogleMap.addMarker(markerOptions));
            }
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.moveCamera(cameraUpdate);

        });
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

    private void testData() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (mDataset.size() == 0) {
            for (int i = 0; i < 16; i++) {
                calendar.setTime(currentDate);
                calendar.add(Calendar.HOUR, -i);
                RecordEventItem item = new RecordEventItem();
                item.setRecordEventTime(sdf.format(calendar.getTime()));
                item.setLatLng(getRandomLocation(new LatLng(25.077877, 121.571141), 2000));
                mDataset.add(item);
            }
        }
    }

    public LatLng getRandomLocation(LatLng point, int radius) {

        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for(int i = 0; i<10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }
}
