package icgtracker.liteon.com.iCGTracker.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class RecordEventItem {

    String recordEventTime;
    LatLng latLng;
    Date date;
    boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getRecordEventTime() {
        return recordEventTime;
    }

    public void setRecordEventTime(String recordEventTime) {
        this.recordEventTime = recordEventTime;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
