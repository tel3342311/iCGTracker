package icgtracker.liteon.com.iCGTracker.util;

import android.support.v4.content.ContextCompat;
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

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.R;

public class AppDrawerItemAdapter extends Adapter<AppDrawerItemAdapter.ViewHolder> {

	private WeakReference<ViewHolder.IDrawerViewHolderClicks> mClickListener;
	private List<AppDrawerItem> mDataset;
	private String mTrackerName;

	public void setCurrentTrackerName(String trackerName) {
		this.mTrackerName = trackerName;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        // each data item is just a string in this case
        public View mRootView;
        public TextView mTitleTextView;
        public TextView mValueTextView;
        public ImageView mItemIcon;
        public ImageView mItemLinkIcon;
        private AppDrawerItem mItem;
        public WeakReference<IDrawerViewHolderClicks> mClicks;
        public ViewHolder(View v, IDrawerViewHolderClicks click) {
            super(v);
            mRootView = v;
            mClicks = new WeakReference<IDrawerViewHolderClicks>(click);
        }
		@Override
		public void onClick(View v) {
			mClicks.get().onClick(mItem);
		}

		public static interface IDrawerViewHolderClicks {
	        public void onClick(AppDrawerItem item);
	    }
    }

    public AppDrawerItemAdapter(List<AppDrawerItem> appInfoDataset, ViewHolder.IDrawerViewHolderClicks clicks, String trackerName) {
        mDataset = appInfoDataset;
        mClickListener = new WeakReference<ViewHolder.IDrawerViewHolderClicks>(clicks);
        mTrackerName = trackerName;
    }
    
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
        AppDrawerItem item = mDataset.get(position);
        holder.mTitleTextView.setText(item.getTitle());
        if (item.getItemType() == AppDrawerItem.TYPE.DELETE_USER) {
        	holder.mTitleTextView.setText(String.format(App.getContext().getString(R.string.delete_tracker),mTrackerName));
        	holder.mItemIcon.setBackground(ContextCompat.getDrawable(App.getContext(), R.drawable.menu_img_delete));
            holder.mRootView.setBackgroundColor(ContextCompat.getColor(App.getContext(),R.color.md_grey_300));
        } else if (item.getItemType() == AppDrawerItem.TYPE.ADD_USER) {
            holder.mItemIcon.setBackground(ContextCompat.getDrawable(App.getContext(), R.drawable.menu_img_add));
            holder.mRootView.setBackgroundColor(ContextCompat.getColor(App.getContext(),R.color.md_grey_300));
        }
        holder.mItem = mDataset.get(position);

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.component_menu_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mClickListener.get());
        vh.mTitleTextView = v.findViewById(R.id.title_text);
        vh.mItemIcon = v.findViewById(R.id.item_icon);
        vh.mItemLinkIcon = v.findViewById(R.id.item_connect_icon);
        v.setOnClickListener(vh);
        return vh;
	}
}
