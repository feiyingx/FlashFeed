package com.snapperfiche.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.data.User;
import com.snapperfiche.mobile.custom.BaseActivity;
import com.snapperfiche.webservices.AccountService;

public class EditProfileActivity extends BaseActivity{
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	private String mFirstName, mLastName, mProfileImgUrl;
	
	Button mBtnDone, mBtnCancel;
	EditText mEtxtFirstName, mEtxtLastName, mEtxtPassword;
	ImageView mImgProfile;
	final String[] mImageSelectListItems = new String[]{"Use camera", "Use gallery"};
	Context mContext = this;
	AlertDialog mADiagSelect;
	Bitmap mNewProfilePhoto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile_layout);
		
		mBtnCancel = (Button)findViewById(R.id.btn_cancel);
		mBtnDone = (Button)findViewById(R.id.btn_done);
		mEtxtFirstName = (EditText)findViewById(R.id.etxt_fname);
		mEtxtLastName = (EditText)findViewById(R.id.etxt_lname);
		mEtxtPassword = (EditText)findViewById(R.id.etxt_pw);
		mImgProfile = (ImageView)findViewById(R.id.img_profile);
		
		//create image uploader/selecter dialog
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, mImageSelectListItems);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		
		dialogBuilder.setTitle("Select Image");
		dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//selected the 'camera' option
				if(which == 0){
					Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
					
					i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
					
					try{
						i.putExtra("return-data", true);
						startActivityForResult(i, PICK_FROM_CAMERA);
					}catch(ActivityNotFoundException e){
						e.printStackTrace();
					}
				}else{ //selected the 'gallery' option
					Intent i = new Intent();
					i.setType("image/*");
					i.setAction(Intent.ACTION_GET_CONTENT);
					
					startActivityForResult(Intent.createChooser(i, "Complete action using"), PICK_FROM_FILE);
				}
				
			}
		});
		//prepopulate user info
		User currentUser = AccountService.getUser();
		mFirstName = currentUser.getFirstName();
		mLastName = currentUser.getLastName();
		mProfileImgUrl = currentUser.getPhotoUrl();		
		mEtxtFirstName.setText(mFirstName);
		mEtxtLastName.setText(mLastName);
		if(mProfileImgUrl == ""){
			//set profile img to default img
		}else{
			//set to profile img
		}
		
		mADiagSelect = dialogBuilder.create();
		
		mImgProfile.setOnClickListener(onClick_imgProfile);
		
		//check rotation chg data holder
		final EditProfileActivityDataHolder holder = (EditProfileActivityDataHolder)getLastNonConfigurationInstance();
		if(holder != null){
			//if we have uploaded profile img, then set that as the profile img
			if(holder.mProfileImg != null){
				mNewProfilePhoto = holder.mProfileImg;
				mImgProfile.setImageBitmap(mNewProfilePhoto);
			}
			mImageCaptureUri = holder.mImgCaptureUri;
		}
		
		mBtnDone.setOnClickListener(onClick_btnDone);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode != RESULT_OK) return;
		
		switch(requestCode){
			case PICK_FROM_CAMERA:
				cropImage();
				break;
			case PICK_FROM_FILE:
				mImageCaptureUri = data.getData();
				cropImage();
				break;
			case CROP_FROM_CAMERA:
				Bundle extras = data.getExtras();
				if(extras != null){
					mNewProfilePhoto = extras.getParcelable("data");
					mImgProfile.setImageBitmap(mNewProfilePhoto);
					
					//delete file
					File f = new File(mImageCaptureUri.getPath());
				
					if(f.exists()) f.delete();
					FileOutputStream fos = null;
					
					try {				
						fos = new FileOutputStream(f);
						mNewProfilePhoto.compress(CompressFormat.JPEG, 75, fos);

						fos.close();
					} catch (Throwable ex) {
						Log.e("flashfeed.editProfile", ex.toString());
					}
				}
				
				break;
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance(){
		EditProfileActivityDataHolder holder = new EditProfileActivityDataHolder();
		holder.mImgCaptureUri = mImageCaptureUri;
		holder.mProfileImg = mNewProfilePhoto;
		
		return holder;
	}
	
	//events
	OnClickListener onClick_btnCancel = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener onClick_btnDone = new OnClickListener(){
		@Override
		public void onClick(View v) {
			String newFirstName = mEtxtFirstName.getText().toString();
			String newLastName = mEtxtLastName.getText().toString();
			String newPassword = mEtxtPassword.getText().toString();
			String newPhotoPath = "";
			if(mNewProfilePhoto != null){
				File myDir = new File("/sdcard/fichey_images");
				myDir.mkdirs();
				String username = "user" + AccountService.getUser().getId();

				SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmmss");
				String fname = username + "_profile_" + formatter.format(new Date()) + ".jpg";

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				String fullpath = file.getAbsolutePath();
				FileOutputStream fos = null;
				
				try {				
					fos = new FileOutputStream(file);
					mNewProfilePhoto.compress(CompressFormat.JPEG, 75, fos);

					fos.close();
				} catch (Throwable ex) {
					
				}
				
				newPhotoPath = file.getAbsolutePath();
			}
			
			if(newFirstName == mFirstName) newFirstName = "";
			if(newLastName == mLastName) newLastName = "";
			//don't need to check pw against old pw cuz if user didn't update pw, then the pw text field will be empty
	
			if(AccountService.EditProfile(newPassword, newFirstName, newLastName, newPhotoPath) == BasicStatus.SUCCESS){
				Toast.makeText(mContext, "Test Success", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(mContext, "Sorry for the inconvenience, but we ran into an error :( Please try updating again later.", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	OnClickListener onClick_imgProfile = new OnClickListener(){

		@Override
		public void onClick(View v) {
			mADiagSelect.show();
		}
		
	};
	
	//helpers
	private void cropImage(){
		
		Intent i = new Intent("com.android.camera.action.CROP");
		i.setType("image/*");
		
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(i, 0);
		int size = list.size();
		if(size == 0){
			
		}else{
			i.setData(mImageCaptureUri);
			i.putExtra("outputX", 150);
			i.putExtra("outputY", 150);
			i.putExtra("aspectX", 1);
			i.putExtra("aspectY", 1);
			i.putExtra("scale", true);
			i.putExtra("return-data", true);
			
			Intent intent = new Intent(i);
			ResolveInfo res = list.get(0);
			
			intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(intent, CROP_FROM_CAMERA);
		}
	}
	
	//classes
	private class EditProfileActivityDataHolder{
		Bitmap mProfileImg;
		Uri mImgCaptureUri;
	}
	
	private class ProfileDataHolder{
		String firstName;
		String lastName;
		String password;
		String profilePhotoUrl;
	}
	
}
