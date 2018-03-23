package icgtracker.liteon.com.iCGTracker.util;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.R;

/**
 * Created by trdcmacpro on 2018/3/23.
 */

public class FenceEntyAdapter extends RecyclerView.Adapter<FenceEntyAdapter.ViewHolder> {


    private List<FenceEntryItem> mDataList;

    public FenceEntyAdapter(List<FenceEntryItem> list) {
        mDataList = list;
    }

    @NonNull
    @Override
    public FenceEntyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_fence_entry_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.mEnterLeave = v.findViewById(R.id.fence_item_title);
        viewHolder.mEventIcon = v.findViewById(R.id.fence_icon);
        viewHolder.mEventTime = v.findViewById(R.id.fence_item_time);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FenceEntyAdapter.ViewHolder holder, int position) {

        FenceEntryItem item = mDataList.get(position);
        holder.mEnterLeave.setText(item.isEnter() ? R.string.fence_enter_fence : R.string.fence_leave_fence);
        holder.mEventTime.setText(item.getFenceEventTime());
        if (item.isEnter()) {
            holder.mEventIcon.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.color_fence_enter_bg));
        } else {
            holder.mEventIcon.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.color_fence_leave_bg));
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private View mRootView;
        private TextView mEventTime;
        private TextView mEnterLeave;
        private ImageView mEventIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;

        }
    }
}
