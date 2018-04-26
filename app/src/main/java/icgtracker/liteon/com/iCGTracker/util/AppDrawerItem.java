package icgtracker.liteon.com.iCGTracker.util;


import icgtracker.liteon.com.iCGTracker.App;
import icgtracker.liteon.com.iCGTracker.R;

public class AppDrawerItem {

	
	public static enum TYPE {
		USER("USER"),
        ADD_USER(App.getContext().getString(R.string.add_tracker)),
        DELETE_USER(App.getContext().getString(R.string.delete_tracker));
		
		private String name;
		private TYPE(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
    }
	
	TYPE itemType;
	String value;
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	boolean isSelect;

	public boolean getSelect() {
		return isSelect;
	}

	public void setSelect(Boolean select) {
		isSelect = select;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the itemType
	 */
	public TYPE getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(TYPE itemType) {
		this.itemType = itemType;
	}
	
	public String getTitle() {
		return itemType.getName();
	}
}
