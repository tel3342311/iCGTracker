package icgtracker.liteon.com.iCGTracker.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.lang.ref.WeakReference;
import java.util.List;

import icgtracker.liteon.com.iCGTracker.R;

public class AppInfoPrivacyItemAdapter extends Adapter<AppInfoPrivacyItemAdapter.ViewHolder> {

	private WeakReference<ViewHolder.IAppInfoPrivacyViewHolderClicks> mClickListener;
	private List<AppInfoPrivacyItem> mDataset;
	private String mVersionInfo;

	public void setmVersionInfo(String mVersionInfo) {
		this.mVersionInfo = mVersionInfo;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        private AppInfoPrivacyItem mItem;
        public WeakReference<IAppInfoPrivacyViewHolderClicks> mClicks;
        public ViewHolder(View v, IAppInfoPrivacyViewHolderClicks click) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<IAppInfoPrivacyViewHolderClicks>(click);
        }
		@Override
		public void onClick(View v) {
			mClicks.get().onClick(mItem);
		}

		public static interface IAppInfoPrivacyViewHolderClicks {
	        public void onClick(AppInfoPrivacyItem item);
	    }
    }

    public AppInfoPrivacyItemAdapter(List<AppInfoPrivacyItem> appInfoDataset, ViewHolder.IAppInfoPrivacyViewHolderClicks clicks, String versionCode) {
        mDataset = appInfoDataset;
        mClickListener = new WeakReference<ViewHolder.IAppInfoPrivacyViewHolderClicks>(clicks);
        mVersionInfo = versionCode;
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		AppInfoPrivacyItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        if (item.getItemType() == AppInfoPrivacyItem.TYPE.APP_INFO) {
        	holder.mValueTextView.setText(mVersionInfo);
        }
        holder.mItem = mDataset.get(position);

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_app_info_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClickListener.get());
        vh.mTitleTextView = (TextView) v.findViewById(R.id.title_text);
        vh.mValueTextView = (TextView) v.findViewById(R.id.value_text);
        v.setOnClickListener(vh);
        return vh;
	}
}
