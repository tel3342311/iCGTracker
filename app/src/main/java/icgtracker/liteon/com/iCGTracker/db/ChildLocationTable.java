package icgtracker.liteon.com.iCGTracker.db;
import android.provider.BaseColumns;

public class ChildLocationTable {

	public ChildLocationTable() {}
	public static final String AUTHORITY = "icgtracker.liteon.com.iCGTracker";
	
	public static abstract class ChildLocationEntry implements BaseColumns {
		public static final String TABLE_NAME = "child_location_entry";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
		public static final String COLUMN_NAME_UPDATE_TIME = "update_time";
	}
	
}
