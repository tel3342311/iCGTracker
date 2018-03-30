package icgtracker.liteon.com.iCGTracker.db;
import android.provider.BaseColumns;

public class FenceTable {

	public FenceTable() {}
	public static final String AUTHORITY = "icgtracker.liteon.com.iCGTracker";
	
	public static abstract class FenceEntry implements BaseColumns {
		public static final String TABLE_NAME = "fence_entry";
        public static final String COLUMN_NAME_UUID_ID = "uuid";
        public static final String COLUMN_NAME_FENCE_ID = "fence_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LATITUDE = "lat";
        public static final String COLUMN_NAME_LONGTITUDE = "lng";
        public static final String COLUMN_NAME_METER_RANGE = "meter";
        public static final String COLUMN_NAME_REPORT_FREQ = "report_freq";
        public static final String COLUMN_NAME_IS_DELETED = "is_deleted";
	}
	
}
