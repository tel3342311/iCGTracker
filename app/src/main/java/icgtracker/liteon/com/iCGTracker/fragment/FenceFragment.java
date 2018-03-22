package icgtracker.liteon.com.iCGTracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import icgtracker.liteon.com.iCGTracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FenceFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private RecyclerView mRecyclerView;
    private ImageView mForward;
    private ImageView mBackward;
    private ImageView mNextFence;
    private ImageView mLastFence;
    
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fence, container, false);
    }

}
