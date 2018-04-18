package icgtracker.liteon.com.iCGTracker.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.EditFenceActivity;
import icgtracker.liteon.com.iCGTracker.R;
import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.service.DataSyncService;
import icgtracker.liteon.com.iCGTracker.util.ChildLocationItem;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.FenceEntryItem;
import icgtracker.liteon.com.iCGTracker.util.FenceEntyAdapter;
import icgtracker.liteon.com.iCGTracker.util.FenceRangeItem;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;
import icgtracker.liteon.com.iCGTracker.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FenceFragment extends Fragment {

    private View mRootView;
    private EditText mFenceTitle;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView mForward;
    private ImageView mBackward;
    private ImageView mNextFence;
    private ImageView mLastFence;
    private List<FenceEntryItem> mDataset;
    private Circle mFenceCircle;
    private Marker mMarker;
    private LatLng mLatlng = new LatLng(25.077877, 121.571141);
    private float mMeter;
    private List<JSONResponse.Student> mStudents;
    private int mCurrnetStudentIdx;
    private DBHelper mDbHelper;
    private List<FenceRangeItem> mFenceRangeList;
    private int mCurrentFenceIdx;
    private View mNoFenceView;
    private LocalBroadcastManager mLocalBroadcastManager;
    private SharedPreferences mSharePreference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FenceFragment newInstance(String param1, String param2) {
        FenceFragment fragment = new FenceFragment();
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
            mMapView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_fence, container, false);
        findViews();
        setListener();
        mSharePreference = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(App.getContext());
        mDbHelper = DBHelper.getInstance(getActivity());
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mFenceRangeList = mDbHelper.getFenceItemByUuid(mDbHelper.getReadableDatabase(), mStudents.get(mCurrnetStudentIdx).getUuid());
        if (mFenceRangeList != null && mFenceRangeList.size() > 0) {
            mCurrentFenceIdx = mFenceRangeList.size() - 1;
        }
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            initMapComponent();
        }
        mDataset = new ArrayList<>();
        initRecycleView();
        return mRootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (mFenceList.size() >= 3) {
            //show dialog
        //}
        switch (item.getItemId()) {
            case R.id.action_add_fence:
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditFenceActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecycleView() {

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        testData();
        mAdapter = new FenceEntyAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);
        updateFenceBtnState();
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
                FenceEntryItem item = new FenceEntryItem();
                item.setFenceEventTime(sdf.format(calendar.getTime()));
                item.setEnter((i % 2 == 0));
                mDataset.add(item);
            }
        }
    }

    private void initMapComponent() {
        mMapView.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;
            updateMap();
        });
    }

    private void updateMap() {
        if (mGoogleMap != null) {
            if (mFenceRangeList != null && mFenceRangeList.size() > 0) {
                FenceRangeItem fenceItem = mFenceRangeList.get(mCurrentFenceIdx);
                mLatlng = new LatLng(fenceItem.getLatitude(), fenceItem.getLongtitude());
                mMeter = fenceItem.getMeter();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.fence_img_location);
                MarkerOptions markerOptions = new MarkerOptions().position(mLatlng).icon(icon);

                if (mMarker != null) {
                    mGoogleMap.clear();
                }
                mMarker = mGoogleMap.addMarker(markerOptions);
                CameraUpdate _cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng, 16.f);

                if (mFenceCircle != null) {
                    mFenceCircle.remove();
                }
                mFenceCircle = mGoogleMap.addCircle(new CircleOptions()
                        .center(mLatlng)
                        .radius(mMeter)
                        .strokeColor(ContextCompat.getColor(App.getContext(), R.color.md_grey_700))
                        .strokeWidth(1.f)
                        .fillColor(ContextCompat.getColor(App.getContext(), R.color.color_fence_normal_circle_bg)));


                mGoogleMap.animateCamera(_cameraUpdate);

                mFenceTitle.setText(fenceItem.getTitle());
            }
        }
    }

    private void findViews() {
        mFenceTitle = mRootView.findViewById(R.id.fence_title);
        mLastFence = mRootView.findViewById(R.id.last_fence);
        mNextFence = mRootView.findViewById(R.id.next_fence);
        mMapView = mRootView.findViewById(R.id.map_view);
        mRecyclerView = mRootView.findViewById(R.id.fence_entry_list);
        mForward = mRootView.findViewById(R.id.forward);
        mBackward = mRootView.findViewById(R.id.backward);
        mNoFenceView = mRootView.findViewById(R.id.no_data);
    }

    private void setListener() {
        mLastFence.setOnClickListener(v -> {
            int idx = mCurrentFenceIdx - 1;
            mCurrentFenceIdx = (idx < 0) ? 0 : idx;
            updateMap();
            updateFenceRangeItemBtnState();
        });
        mNextFence.setOnClickListener(v -> {
            int idx = mCurrentFenceIdx + 1;
            mCurrentFenceIdx = (idx > mFenceRangeList.size() - 1) ? mFenceRangeList.size() - 1 : idx;
            updateMap();
            updateFenceRangeItemBtnState();
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
                updateFenceBtnState();
            }
        });
    }

    private void updateFenceBtnState() {
        if (((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() == 0) {
            mBackward.setVisibility(View.INVISIBLE);
        } else {
            mBackward.setVisibility(View.VISIBLE);
        }

        if (((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition() == mAdapter.getItemCount() -1){
            mForward.setVisibility(View.INVISIBLE);
        } else {
            mForward.setVisibility(View.VISIBLE);
        }
    }

    private void updateFenceRangeItemBtnState() {
        if (mCurrentFenceIdx == 0) {
            mLastFence.setVisibility(View.INVISIBLE);
        } else {
            mLastFence.setVisibility(View.VISIBLE);
        }

        if (mCurrentFenceIdx == mFenceRangeList.size() - 1) {
            mNextFence.setVisibility(View.INVISIBLE);
        } else {
            mNextFence.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
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
        IntentFilter filter = new IntentFilter();
        filter.addAction(Def.ACTION_ERROR_NOTIFY);
        filter.addAction(Def.ACTION_LIST_FENCE);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);

        mCurrnetStudentIdx = mSharePreference.getInt(Def.SP_CURRENT_STUDENT, 0);

        mFenceRangeList = mDbHelper.getFenceItemByUuid(mDbHelper.getReadableDatabase(), mStudents.get(mCurrnetStudentIdx).getUuid());
        if (mFenceRangeList != null && mFenceRangeList.size() > 0) {
            mCurrentFenceIdx = mFenceRangeList.size() - 1;
            mNoFenceView.setVisibility(View.GONE);
        } else {
            mNoFenceView.setVisibility(View.VISIBLE);
        }
        if (mStudents.size() > 0 && mCurrnetStudentIdx < mStudents.size()) {
            Intent startIntent = new Intent(App.getContext(), DataSyncService.class);
            startIntent.setAction(Def.ACTION_LIST_FENCE);
            startIntent.putExtra(Def.KEY_UUID, mStudents.get(mCurrnetStudentIdx).getUuid());
            getActivity().startService(startIntent);
        }
        updateMap();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onStop();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(Def.ACTION_ERROR_NOTIFY, intent.getAction())) {
                String error = intent.getStringExtra(Def.EXTRA_ERROR_MESSAGE);
                Utils.showErrorDialog(error);
            } else if (TextUtils.equals(Def.ACTION_LIST_FENCE, intent.getAction())) {
                mFenceRangeList = mDbHelper.getFenceItemByUuid(mDbHelper.getReadableDatabase(), mStudents.get(mCurrnetStudentIdx).getUuid());
                if (mFenceRangeList != null && mFenceRangeList.size() > 0) {
                    mCurrentFenceIdx = mFenceRangeList.size() - 1;
                    mNoFenceView.setVisibility(View.GONE);
                } else {
                    mNoFenceView.setVisibility(View.VISIBLE);
                }
                updateMap();
            }
        }
    };
}
