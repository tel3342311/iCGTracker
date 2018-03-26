package icgtracker.liteon.com.iCGTracker.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import icgtracker.liteon.com.iCGTracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SafeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SafeFragment extends Fragment {
    private View mRootView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private LatLng mLatlng = new LatLng(25.077877, 121.571141);
    private FloatingActionButton mLocationButton;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SafeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SafeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SafeFragment newInstance(String param1, String param2) {
        SafeFragment fragment = new SafeFragment();
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
        mRootView = inflater.inflate(R.layout.fragment_safe, container, false);
        findViews();
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            initMapComponent();
        }
        setListener();
        return mRootView;
    }

    private void findViews() {

        mMapView = mRootView.findViewById(R.id.map_view);
        mLocationButton = mRootView.findViewById(R.id.map_location);
    }

    private void setListener() {
        mLocationButton.setOnClickListener( v -> {
            if (mGoogleMap != null) {
                CameraUpdate _cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng, 16.f);
                mGoogleMap.animateCamera(_cameraUpdate);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
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
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onStop();
        }
    }

    private void initMapComponent() {
        mMapView.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;

            MarkerOptions markerOptions = new MarkerOptions().position(mLatlng);
            if (mMarker != null) {
                mGoogleMap.clear();
            }
            mMarker = mGoogleMap.addMarker(markerOptions);
            CameraUpdate _cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng, 16.f);
            mGoogleMap.animateCamera(_cameraUpdate);
        });
    }
}
