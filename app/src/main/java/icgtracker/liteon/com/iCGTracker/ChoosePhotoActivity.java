package icgtracker.liteon.com.iCGTracker;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import icgtracker.liteon.com.iCGTracker.db.DBHelper;
import icgtracker.liteon.com.iCGTracker.util.ConfirmDeleteDialog;
import icgtracker.liteon.com.iCGTracker.util.Def;
import icgtracker.liteon.com.iCGTracker.util.JSONResponse.Student;
import icgtracker.liteon.com.iCGTracker.util.PhotoItem;
import icgtracker.liteon.com.iCGTracker.util.PhotoItemAdapter;
import icgtracker.liteon.com.iCGTracker.util.PhotoItemAdapter.ViewHolder.IPhotoViewHolderClicks;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosePhotoActivity extends AppCompatActivity implements IPhotoViewHolderClicks {

	private final static String TAG = ChoosePhotoActivity.class.getName();
	private Toolbar mToolbar;
	private ImageView mUsedPhoto;
	private ImageView mUsedPhoto2;
	private ImageView mUsedPhoto3;
	private ImageView mCancel;
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;	
	private ArrayList<PhotoItem> mDataSet;
	private final static int CROP_PIC_REQUEST_CODE = 1;
    private final static int PERMISSION_REQUEST = 3;
	private List<Student> mStudents;
	private Student mStudent;
	private Map<String, PhotoItem> mPhotoMap;
	private Map<String, List<PhotoItem>> mPhotoMapWatchTheme;
	private DBHelper mDbHelper;
	private PhotoItem mCurrentItem;
	private PhotoItem[] mCurrentItemForWatch = new PhotoItem[3];
	private int mCurrentItemIdx = 0;
	private String mStudentName;
	private boolean isGranted;
	private boolean isFromWatchTheme;
	private final static int MAX_SAVE_ITEM = 3;
	private ConfirmDeleteDialog mPermissionDialog;
	File mCropping;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_photo);
		findViews();
		setListener();
		askPermisson();
		setupToolbar();
		mDbHelper = DBHelper.getInstance(this);
		//get child list
		mStudentName = getIntent().getStringExtra(Def.EXTRA_STUDENT_NAME);
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		mStudent = mDbHelper.queryChildByName(mDbHelper.getReadableDatabase(), mStudentName);
		Intent intent = getIntent();
		if (intent != null && TextUtils.equals(intent.getStringExtra(Def.EXTRA_CHOOSE_PHOTO_TYPE), Def.EXTRA_CHOOSE_WATCH_ICON)) {
			isFromWatchTheme = true;
		}
	}
	
	private void initRecycleView() {
		// set a GridLayoutManager with default vertical orientation and 3 number of columns
		mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mDataSet = getPhotoItem();
		mAdapter = new PhotoItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void findViews() {
		mToolbar = findViewById(R.id.toolbar);
		mRecyclerView = findViewById(R.id.photo_list);
		mUsedPhoto = findViewById(R.id.used_img);
		mUsedPhoto2 = findViewById(R.id.used_img2);
		mUsedPhoto3 = findViewById(R.id.used_img3);
		mCancel = findViewById(R.id.cancel);
	}
	
	private void setListener() {
		mUsedPhoto.setOnClickListener(mOnUsedPhotoClicked);
		mUsedPhoto2.setOnClickListener(mOnUsedPhotoClicked);
		mUsedPhoto3.setOnClickListener(mOnUsedPhotoClicked);
	}
	
	private View.OnClickListener mOnUsedPhotoClicked = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bitmap bitmap = null;
			if (!isFromWatchTheme) {
				if (mCurrentItem != null && !TextUtils.isEmpty(mCurrentItem.getFilePath())) {
                        float photoSize = convertDpToPixel(getResources().getDimension(R.dimen.choose_photo_item_size), ChoosePhotoActivity.this);
						bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentItem.getFilePath()),
                                (int) photoSize, (int) photoSize);

				}
			} else {
				int idx = 0;
				if (v.getId() == R.id.used_img2) {
					idx = 1;
				} else if (v.getId() == R.id.used_img3) {
					idx = 2;
				}
				PhotoItem item = mCurrentItemForWatch[idx];
				if (item != null && !TextUtils.isEmpty(item.getUri()) ) {
					try {
						bitmap = getThumbnail(Uri.parse(item.getUri()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (bitmap != null) {
				saveBitmapAsIcon(bitmap);
				exit();
			}
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		mToolbar.setTitle("");
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		if (!isFromWatchTheme) {
			restorePhotoItem();
		} else {
			restorePhotoItemForWatchTheme();
			mUsedPhoto2.setVisibility(View.VISIBLE);
			mUsedPhoto3.setVisibility(View.VISIBLE);
		}
	}

	private void restorePhotoItemForWatchTheme() {
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String watchThemeMap = sp.getString(Def.SP_PHOTO_MAP_WATCH, "");
		Type typeOfHashMap = new TypeToken<Map<String, List<PhotoItem>>>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mPhotoMapWatchTheme = gson.fromJson(watchThemeMap, typeOfHashMap);
		if (TextUtils.isEmpty(watchThemeMap)) {
			mPhotoMapWatchTheme = new HashMap<String, List<PhotoItem>>();
			for (Student student : mStudents) {
				String studentId = student.getStudent_id();
				mPhotoMapWatchTheme.put(studentId, new ArrayList<PhotoItem>());
			}
		}
		if (mPhotoMapWatchTheme.get(mStudent.getStudent_id()) == null) {
			mPhotoMapWatchTheme.put(mStudent.getStudent_id(), new ArrayList<PhotoItem>());

		}
		List listPhoto = mPhotoMapWatchTheme.get(mStudent.getStudent_id());
		if (listPhoto.size() == 0) {
			for (int i = 0; i < MAX_SAVE_ITEM; i++) {
				listPhoto.add(new PhotoItem());
			}
		}
		for (int i = 0; i < listPhoto.size();i++) {
			mCurrentItemForWatch[i] = (PhotoItem)listPhoto.get(i);
		}
		updateUsedItem();
	}
	
	private void restorePhotoItem() {
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String alarmMap = sp.getString(Def.SP_PHOTO_MAP, "");
		Type typeOfHashMap = new TypeToken<Map<String, PhotoItem>>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mPhotoMap = gson.fromJson(alarmMap, typeOfHashMap);
		if (TextUtils.isEmpty(alarmMap)) {
			mPhotoMap = new HashMap<String, PhotoItem>();
			for (Student student : mStudents) {
				String studentId = student.getStudent_id();
				mPhotoMap.put(studentId, new PhotoItem());
			}
		}
		if (mPhotoMap.get(mStudent.getStudent_id()) == null) {
			mPhotoMap.put(mStudent.getStudent_id(), new PhotoItem());
		}
		mCurrentItem = mPhotoMap.get(mStudent.getStudent_id());
		updateUsedItem();
	}
	
	private void savePhotoItem() {
		Gson gson = new Gson();
		SharedPreferences sp = getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		if (!isFromWatchTheme) {
			String input = gson.toJson(mPhotoMap);
			editor.putString(Def.SP_PHOTO_MAP, input);
		
		} else {
			String input = gson.toJson(mPhotoMapWatchTheme);
			editor.putString(Def.SP_PHOTO_MAP_WATCH, input);
		}
		editor.commit();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		savePhotoItem();
	}
	
	private void askPermisson() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    android.Manifest.permission.READ_EXTERNAL_STORAGE }, PERMISSION_REQUEST);
			} else {
				initRecycleView();
			}
		} else {
			initRecycleView();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
		case PERMISSION_REQUEST:
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "read storage permission granted");
				initRecycleView();
			} else {
				mPermissionDialog = new ConfirmDeleteDialog();
				mPermissionDialog.setOnConfirmEventListener(mOnPermissionConfirmClickListener);
				mPermissionDialog.setmOnCancelListener(mOnPermissionCancelClickListener);
				mPermissionDialog.setmTitleText(getString(R.string.pairing_watch_ask_permission));
				mPermissionDialog.setmBtnConfirmText(getString(android.R.string.ok));
				mPermissionDialog.setmBtnCancelText(getString(R.string.bind_cancel));
				mPermissionDialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			break;
		}
	}

    private View.OnClickListener mOnPermissionConfirmClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mPermissionDialog != null) {
                mPermissionDialog.dismiss();
            }
            askPermisson();
        }
    };

    private View.OnClickListener mOnPermissionCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mPermissionDialog != null) {
                mPermissionDialog.dismiss();
            }
            finish();
            Intent intent = new Intent();
            intent.setClass(ChoosePhotoActivity.this, MainActivity.class);
            intent.putExtra(Def.EXTRA_GOTO_PAGE_ID, Def.EXTRA_PAGE_SETTING_ID);
            startActivity(intent);
        }
    };

	public ArrayList<PhotoItem> getPhotoItem() {
		ArrayList<PhotoItem> list = new ArrayList<>();
        int int_position = 0;
        Uri uri,itemUri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_index_data_id;

        String absolutePathOfImage = null;
        uri = Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        column_index_data_id = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(Images.Media.BUCKET_DISPLAY_NAME);
        
        if (cursor != null && cursor.moveToFirst()) {
	        while (cursor.moveToNext()) {
	            absolutePathOfImage = cursor.getString(column_index_data);
	            int id = cursor.getInt(column_index_data_id);
	            Log.e("Column", absolutePathOfImage);
	            Log.e("Folder", cursor.getString(column_index_folder_name));
	            PhotoItem item = new PhotoItem();
	            item.setFilePath(absolutePathOfImage);
	            String uriItem = Uri.withAppendedPath(uri, "" + id).toString();
	            item.setUri(uriItem);
	            list.add(item);
	        }
        }
        return list;
    }

	@Override
	public void onPhotoClick(int position) {
		mCurrentItemIdx = position;
		if (!isFromWatchTheme) {
			mCurrentItem = mDataSet.get(position);
			doCrop(Uri.parse(mCurrentItem.getUri()));
		} else {
			PhotoItem item = mDataSet.get(position);
			doCrop(Uri.parse(item.getUri()));
		}
	}
	
	private void updateUsedItem() {
		float photoSize = convertDpToPixel(getResources().getDimension(R.dimen.choose_photo_item_size), this);
		if (!isFromWatchTheme) {
			if (!TextUtils.isEmpty(mCurrentItem.getFilePath())) {
				
				Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentItem.getFilePath()),
						(int) photoSize, (int) photoSize);
				mUsedPhoto.setImageBitmap(resized);
			}
		} else {
			ImageView [] list = {mUsedPhoto, mUsedPhoto2, mUsedPhoto3};
			for (int i = 0; i < mCurrentItemForWatch.length; i++) {
				if (!TextUtils.isEmpty(mCurrentItemForWatch[i].getFilePath())) {
					Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentItemForWatch[i].getFilePath()),
							(int) photoSize, (int) photoSize);
					list[i].setImageBitmap(resized);
				}
			}
		}
		
	}
	
	private void setupToolbar() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		mCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void doCrop(Uri picUri) {
	    try {

			File imagePath = new File(getFilesDir(), "images");
			if (!imagePath.exists()) {
				imagePath.mkdir();
			}
			mCropping = new File(imagePath, "_crop" + ".jpg");
			if (!mCropping.exists()) {
				mCropping.createNewFile();
			}

			IOUtils.copy(getContentResolver().openInputStream(picUri), new FileOutputStream(mCropping));
			Uri contentUri = FileProvider.getUriForFile(this, "icgtracker.liteon.com.iCGTracker.fileprovider", mCropping);

            CropImage.activity(contentUri)
					.setAspectRatio(1,1)
                    .start(this);

	    }
	    // respond to users whose devices do not support the crop action
	    catch (ActivityNotFoundException anfe) {
	        // display an error message
	        String errorMessage = "your device doesn't support the crop action!";
	        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
	        toast.show();
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		  return Uri.parse(path);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (mCropping != null && mCropping.exists()) {
                    mCropping.delete();
                }
                Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = getThumbnail(resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    File f = saveBitmapAsIcon(bitmap);
                    if (!isFromWatchTheme) {
                        mCurrentItem = mDataSet.get(mCurrentItemIdx);
                        mCurrentItem.filePath = f.getAbsolutePath();
                        mPhotoMap.put(mStudent.getStudent_id(), mCurrentItem);
                    } else {
                        PhotoItem item = mDataSet.get(mCurrentItemIdx);
                        File temp = new File(f.getAbsolutePath() + Long.toString(Calendar.getInstance().getTimeInMillis()));
                        try {
                            FileUtils.copyFile(f, temp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        item.filePath = temp.getAbsolutePath();
                        List listPhotoItem = mPhotoMapWatchTheme.get(mStudent.getStudent_id());
                        if (listPhotoItem.size() < 3) {
                            listPhotoItem.add(0, item);
                        } else {
                            listPhotoItem.remove(listPhotoItem.size() - 1);
                            listPhotoItem.add(0, item);
                        }
                    }
                    updateUsedItem();
                    exit();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
	    if (requestCode == CROP_PIC_REQUEST_CODE) {
	    	if (mCropping != null && mCropping.exists()) {
	    		mCropping.delete();
			}
	    	Bitmap bitmap = null;
	        if (data != null) {
	            Bundle extras = data.getExtras();
	            if (extras != null) {
	            	 bitmap = extras.getParcelable("data");
	            } else if (data.getData() != null) {
	            	Uri uri = data.getData();
	        		try {
	        			bitmap = getThumbnail(uri);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        } 
	        if (bitmap != null) {	        
	        	File f = saveBitmapAsIcon(bitmap);
	        	if (RESULT_OK == resultCode) {
	        		if (!isFromWatchTheme) {
	        			mCurrentItem = mDataSet.get(mCurrentItemIdx);
                        mCurrentItem.filePath = f.getAbsolutePath();
	        			mPhotoMap.put(mStudent.getStudent_id(), mCurrentItem);
	        		} else {
	        			PhotoItem item = mDataSet.get(mCurrentItemIdx);
                        File temp = new File(f.getAbsolutePath() + Long.toString(Calendar.getInstance().getTimeInMillis()));
                        try {
                            FileUtils.copyFile(f, temp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        item.filePath = temp.getAbsolutePath();
                        List listPhotoItem = mPhotoMapWatchTheme.get(mStudent.getStudent_id());
	        			if (listPhotoItem.size() < 3) {
	        				listPhotoItem.add(0,item);
	        			} else {
	        				listPhotoItem.remove(listPhotoItem.size() - 1);
	        				listPhotoItem.add(0,item);
	        			}
	        		}
	        		updateUsedItem();
	        	}
	        	exit();
			}
	    }

	}
	
	private void exit() {
		savePhotoItem();
		if (!isFromWatchTheme) {
			onBackPressed();
		} else {
			setResult(RESULT_OK);
			finish();
		}
	}
	
	private File saveBitmapAsIcon(Bitmap bitmap) {
		File cropFile = null;
		if (!isFromWatchTheme) {
			cropFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
					.getAbsolutePath() + "/" + mStudent.getStudent_id() + ".jpg");
		} else {
			cropFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
					.getAbsolutePath() + "/" + mStudent.getStudent_id() + "_watch.jpg");
		}
		try {
			FileOutputStream out = new FileOutputStream(cropFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cropFile;
	}
	public static float convertDpToPixel(float dp, Context context) {
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	    return px;
	}
	
	public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
		float photoSize = convertDpToPixel(getResources().getDimension(R.dimen.choose_photo_item_size), this);
		InputStream input = getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();

		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
			return null;
		}

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double ratio = (originalSize > photoSize) ? (originalSize / photoSize) : 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 1;//getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true; // optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;//
		input = this.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}
	
	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}
}
