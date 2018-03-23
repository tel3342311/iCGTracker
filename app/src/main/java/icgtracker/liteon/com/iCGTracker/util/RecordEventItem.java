package icgtracker.liteon.com.iCGTracker.util;

/**
 * Created by trdcmacpro on 2018/3/23.
 */

public class FenceEntryItem {

    String fenceEventTime;
    boolean isEnter;

    public boolean isEnter() {
        return isEnter;
    }

    public void setEnter(boolean enter) {
        isEnter = enter;
    }

    public String getFenceEventTime() {
        return fenceEventTime;
    }

    public void setFenceEventTime(String fenceEventTime) {
        this.fenceEventTime = fenceEventTime;
    }
}
