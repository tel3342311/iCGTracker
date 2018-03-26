package icgtracker.liteon.com.iCGTracker.db;

import android.provider.BaseColumns;

public class WearableTable {

    public WearableTable() {
    }

    public static final String AUTHORITY = "icgtracker.liteon.com.iCGTracker";

    public static abstract class WearableEntry implements BaseColumns {
        public static final String TABLE_NAME = "wearable_entry";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_ADDR = "btAddr";
        public static final String COLUMN_NAME_STUDENT_ID = "student_id";
    }
}