package icgtracker.liteon.com.iCGTracker.db;
import android.provider.BaseColumns;

public class ChildTable {

	public ChildTable() {}
	public static final String AUTHORITY = "icgtracker.liteon.com.iCGTracker";
	
	public static abstract class ChildEntry implements BaseColumns {
		public static final String TABLE_NAME = "child_entry";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_GIVEN_NAME = "name";
        public static final String COLUMN_NAME_NICK_NAME = "nick_name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_DOB = "dob";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_ROLL_NO = "roll_no";
        public static final String COLUMN_NAME_CLASS = "class";   
        public static final String COLUMN_NAME_STUDENT_ID = "student_id";
        public static final String COLUMN_NAME_IS_DELETED = "is_deleted";
	}
	
}
