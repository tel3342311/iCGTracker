package icgtracker.liteon.com.iCGTracker.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by trdcmacpro on 2018/3/28.
 */

public class ChildLocationItem {
    LatLng latlng;
    String date;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    String uuid;

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
