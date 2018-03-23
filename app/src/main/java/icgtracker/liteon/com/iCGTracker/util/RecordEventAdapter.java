package icgtracker.liteon.com.iCGTracker.util;

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

public class RecordEventAdapter extends RecyclerView.Adapter<RecordEventAdapter.ViewHolder> {


    private List<RecordEventItem> mDataList;
    private View.OnClickListener mOnClickListener;
    public RecordEventAdapter(List<RecordEventItem> list) {
        mDataList = list;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecordEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_record_event_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.mEventTime = v.findViewById(R.id.record_item_time);
        viewHolder.mRecordIcon = v.findViewById(R.id.record_icon);
        if (mOnClickListener != null) {
            v.setOnClickListener(mOnClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordEventAdapter.ViewHolder holder, int position) {

        RecordEventItem item = mDataList.get(position);
        holder.mEventTime.setText(item.getRecordEventTime());
        if (item.isSelect()) {
            holder.mRecordIcon.setImageDrawable(ContextCompat.getDrawable(App.getContext(), R.drawable.record_btnf_time));
        } else {
            holder.mRecordIcon.setImageDrawable(ContextCompat.getDrawable(App.getContext(), R.drawable.record_btn_time));
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private View mRootView;
        private TextView mEventTime;
        private ImageView mRecordIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
        }
    }
}
