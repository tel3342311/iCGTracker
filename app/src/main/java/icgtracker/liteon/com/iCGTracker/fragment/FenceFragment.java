package icgtracker.liteon.com.iCGTracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.R;
import icgtracker.liteon.com.iCGTracker.util.FenceEntryItem;
import icgtracker.liteon.com.iCGTracker.util.FenceEntyAdapter;

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
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_fence, container, false);
        findViews();
        setListener();
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            initMapComponent();
        }
        mDataset = new ArrayList<>();
        initRecycleView();
        return mRootView;
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
        });
    }

    private void findViews() {
        mFenceTitle = mRootView.findViewById(R.id.fence_title);
        mLastFence = mRootView.findViewById(R.id.last_fence);
        mNextFence = mRootView.findViewById(R.id.next_fence);
        mMapView = mRootView.findViewById(R.id.map_view);
        mRecyclerView = mRootView.findViewById(R.id.fence_entry_list);
        mForward = mRootView.findViewById(R.id.forward);
        mBackward = mRootView.findViewById(R.id.backward);
    }

    private void setListener() {
        mLastFence.setOnClickListener(v -> {

        });
        mNextFence.setOnClickListener(v -> {

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
}
