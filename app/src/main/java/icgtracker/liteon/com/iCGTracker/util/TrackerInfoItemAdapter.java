package icgtracker.liteon.com.iCGTracker.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.R;

public class TrackerInfoItemAdapter extends Adapter<TrackerInfoItemAdapter.ViewHolder> {

	private WeakReference<ViewHolder.ITrackerInfoViewHolderClicks> mClickListener;
	private List<TrackerInfoItem> mDataset;

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        private TrackerInfoItem mItem;
        public WeakReference<ITrackerInfoViewHolderClicks> mClicks;
        public ViewHolder(View v, ITrackerInfoViewHolderClicks click) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<>(click);
        }
		@Override
		public void onClick(View v) {
			mClicks.get().onClick(mItem);
		}

		public static interface ITrackerInfoViewHolderClicks {
	        public void onClick(TrackerInfoItem item);
	    }
    }

    public TrackerInfoItemAdapter(List<TrackerInfoItem> appInfoDataset, ViewHolder.ITrackerInfoViewHolderClicks clicks) {
        mDataset = appInfoDataset;
        mClickListener = new WeakReference<>(clicks);
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
        TrackerInfoItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        holder.mItem = mDataset.get(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_app_info_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClickListener.get());
        vh.mTitleTextView = v.findViewById(R.id.title_text);
        vh.mValueTextView = v.findViewById(R.id.value_text);
        v.setOnClickListener(vh);
        return vh;
	}
}
