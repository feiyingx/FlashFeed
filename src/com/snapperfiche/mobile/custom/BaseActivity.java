package com.snapperfiche.mobile.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.snapperfiche.mobile.CameraActivity;
import com.snapperfiche.mobile.ProfileActivity;
import com.snapperfiche.mobile.QuestionActivity;
import com.snapperfiche.mobile.R;

public class BaseActivity extends Activity {
	
	LinearLayout llBase;
	
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
	
	private void initializeTopNav() {
		/*ImageButton btnQuestion = (ImageButton) findViewById(R.id.question);
		btnQuestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BaseActivity.this, QuestionActivity.class);
				startActivity(i);
			}
		});*/
		
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

}
