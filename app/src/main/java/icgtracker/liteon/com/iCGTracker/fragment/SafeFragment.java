package icgtracker.liteon.com.iCGTracker.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.MainActivity;
import icgtracker.liteon.com.iCGTracker.R;
import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.service.DataSyncService;
import icgtracker.liteon.com.iCGTracker.util.ChildLocationItem;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.GuardianApiClient;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse;
import icgtracker.liteon.com.iCGTracker.util.Utils;

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
    private LatLng mLatlng;
    private FloatingActionButton mLocationButton;
    private List<JSONResponse.Student> mStudents;
    private int mCurrnetStudentIdx;
    private String mLastPositionUpdateTime;
    private TextView mUpdateTime;
    private DBHelper mDbHelper;
    private SharedPreferences mSharePreference;
    private LocalBroadcastManager mLocalBroadcastManager;

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
        mDbHelper = DBHelper.getInstance(getActivity());
        mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(App.getContext());
        mSharePreference = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mCurrnetStudentIdx = mSharePreference.getInt(Def.SP_CURRENT_STUDENT, 0);
        return mRootView;
    }

    private void findViews() {

        mMapView = mRootView.findViewById(R.id.map_view);
        mLocationButton = mRootView.findViewById(R.id.map_location);
        mUpdateTime = mRootView.findViewById(R.id.update_time);
    }

    private void setListener() {
        mLocationButton.setOnClickListener( v -> {
            if (mGoogleMap != null) {
                if (mLatlng != null) {
                    CameraUpdate _cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng, 16.f);
                    mGoogleMap.animateCamera(_cameraUpdate);
                }
                Intent startIntent = new Intent(App.getContext(), DataSyncService.class);
                startIntent.setAction(Def.ACTION_GET_LOCATION);
                startIntent.putExtra(Def.KEY_UUID, mStudents.get(mCurrnetStudentIdx).getUuid());
                getActivity().startService(startIntent);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
        mCurrnetStudentIdx = mSharePreference.getInt(Def.SP_CURRENT_STUDENT, 0);

        Intent startIntent = new Intent(App.getContext(), DataSyncService.class);
        startIntent.setAction(Def.ACTION_GET_LOCATION);
        startIntent.putExtra(Def.KEY_UUID, mStudents.get(mCurrnetStudentIdx).getUuid());
        getActivity().startService(startIntent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Def.ACTION_ERROR_NOTIFY);
        filter.addAction(Def.ACTION_GET_LOCATION);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
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
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onStop();
        }
    }

    private void initMapComponent() {
        mMapView.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;
            CameraUpdate _cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(25.077877, 121.571141), 16.f);
            mGoogleMap.moveCamera(_cameraUpdate);
            //new getCurrentLocation().execute();
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(Def.ACTION_ERROR_NOTIFY, intent.getAction())) {
                String error = intent.getStringExtra(Def.EXTRA_ERROR_MESSAGE);
                Utils.showErrorDialog(error);
            } else if (TextUtils.equals(Def.ACTION_GET_LOCATION, intent.getAction())) {
                ChildLocationItem item = mDbHelper.getChildLocationByID(mDbHelper.getReadableDatabase(), mStudents.get(mCurrnetStudentIdx).getUuid());
                if (item != null) {
                    mLatlng = item.getLatlng();
                    mUpdateTime.setText(item.getDate());
                } else {
                    mLatlng = null;
                    mUpdateTime.setText(R.string.no_gps_data);
                }
                updateMap();
            }
        }
    };

    private void updateMap() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            CameraUpdate cameraUpdate;
            if (mLatlng != null) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.fence_img_location);
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mLatlng).title("最後位置").icon(icon));
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng, 16);
            } else {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(25.077877, 121.571141), 16);
            }
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    class getCurrentLocation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (mStudents == null || mStudents.size() == 0) {
                return "";
            }
            SharedPreferences sp = App.getContext().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
            GuardianApiClient apiClient = GuardianApiClient.getInstance(App.getContext());
            apiClient.setToken(token);
            JSONResponse response = apiClient.getStudentLocation(mStudents.get(mCurrnetStudentIdx));
            if (response == null) {
                return null;
            }
            if (TextUtils.equals(Def.RET_SUCCESS_1, response.getReturn().getResponseSummary().getStatusCode())) {
                if (response.getReturn().getResults() == null) {
                    return "";
                }
                String lat = response.getReturn().getResults().getLatitude();
                String lnt = response.getReturn().getResults().getLongitude();
                if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lnt)) {
                    return "";
                }
                mLastPositionUpdateTime = response.getReturn().getResults().getEvent_occured_date();

                mLatlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lnt));
            } else if (TextUtils.equals(Def.RET_ERR_02, response.getReturn().getResponseSummary().getStatusCode())) {
                return Def.RET_ERR_02;
            }
            return "";
        }

        protected void onPostExecute(String result) {
            mMapView.setVisibility(View.VISIBLE);
            if (TextUtils.equals(Def.RET_ERR_02, result)) {
                ((MainActivity)getActivity()).logoutAccount();
                return;
            }
            if (mGoogleMap != null) {
                mGoogleMap.addMarker(new MarkerOptions().position(mLatlng).title("最後位置"));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng, 16);
                mGoogleMap.moveCamera(cameraUpdate);
                SimpleDateFormat sdFormat = new SimpleDateFormat();
                String format = "yyyy-MM-dd HH:mm:ss.S";
                sdFormat.applyPattern(format);
                Date date = Calendar.getInstance().getTime();
                if (!TextUtils.isEmpty(mLastPositionUpdateTime)) {
                    try {
                        date = sdFormat.parse(mLastPositionUpdateTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EE HH:mm");
                String updateTime = sdf.format(date);
                mUpdateTime.setText(updateTime);
            }
        };
    }
}
