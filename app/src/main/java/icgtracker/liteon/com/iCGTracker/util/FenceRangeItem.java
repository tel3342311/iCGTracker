package icgtracker.liteon.com.iCGTracker.util;

/**
 * Created by trdcmacpro on 2018/3/26.
 */

public class FenceRangeItem {

    int fence_id;
    String Uuid;
    double latitude;
    double longtitude;
    String title;
    int report_freq;
    boolean isDelete;
    float meter;

    public int getFence_id() {
        return fence_id;
    }

    public void setFence_id(int fence_id) {
        this.fence_id = fence_id;
    }

    public int getReport_freq() {
        return report_freq;
    }

    public void setReport_freq(int report_freq) {
        this.report_freq = report_freq;
    }

    public float getMeter() {
        return meter;
    }

    public void setMeter(float meter) {
        this.meter = meter;
    }

    int _id;

    public String getUuid() {
        return Uuid;
    }

    public void setUuid(String uuid) {
        this.Uuid = uuid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
