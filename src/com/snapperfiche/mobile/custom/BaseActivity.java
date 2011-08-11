package com.snapperfiche.mobile.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.snapperfiche.mobile.AuthenticationActivity;
import com.snapperfiche.mobile.CameraActivity;
import com.snapperfiche.mobile.EditProfileActivity;
import com.snapperfiche.mobile.LoginActivity;
import com.snapperfiche.mobile.ProfileActivity;
import com.snapperfiche.mobile.QuestionActivity;
import com.snapperfiche.mobile.R;
import com.snapperfiche.mobile.StatusFeedActivity;
import com.snapperfiche.webservices.AccountService;

public class BaseActivity extends Activity {
	
	LinearLayout llBase;
	ProgressDialog mDialog;
	LogoutAsyncTask mLogoutTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.base_layout);
		
		llBase = (LinearLayout)findViewById(R.id.llBase);
		
		initializeTopNav();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(layoutResID, llBase);
	}
	
	//create options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.itm_edit_profile:
				Intent intent = new Intent(this, EditProfileActivity.class);
		        startActivity(intent);
				return true;
			case R.id.itm_settings:
				return true;
			case R.id.itm_logout:
				//logout
				mLogoutTask = new LogoutAsyncTask();
				mLogoutTask.attach(this);
				mDialog = ProgressDialog.show(BaseActivity.this, "", 
	                    "Bye bye.. Hope to see you soon!", true);
				mLogoutTask.execute();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//helpers
	private void initializeTopNav() {
		ImageButton btnHome = (ImageButton) findViewById(R.id.logo);
		btnHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BaseActivity.this, StatusFeedActivity.class);
				startActivity(i);
			}
		});
		
		ImageButton btnQuestion = (ImageButton) findViewById(R.id.question);
		btnQuestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BaseActivity.this, QuestionActivity.class);
				startActivity(i);
			}
		});
		
		ImageButton btnProfile = (ImageButton) findViewById(R.id.profile);
		btnProfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BaseActivity.this, ProfileActivity.class);
				startActivity(i);
			}
		});
		
		ImageButton btnCamera = (ImageButton) findViewById(R.id.camera);
		btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BaseActivity.this, CameraActivity.class);
				startActivity(i);
			}
		});
	}
	
	private void redirectToLogin(){
		if(mDialog != null) mDialog.cancel();
		Intent i = new Intent(getApplicationContext(), AuthenticationActivity.class);
		startActivity(i);
	}

	//classes
	public class LogoutAsyncTask extends AsyncTask<Void, Integer, Void>{
		BaseActivity activity = null;
		@Override
		protected Void doInBackground(Void... params) {
			AccountService.Logout();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			activity.redirectToLogin();
		}
		
		void attach(BaseActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
}
