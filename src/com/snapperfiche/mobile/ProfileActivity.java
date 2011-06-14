package com.snapperfiche.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProfileActivity extends Activity{
	Context mContext = this;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_layout);
		
		Button btnFriends = (Button)findViewById(R.id.profile_layout_btn_friend);
		btnFriends.setOnClickListener(onClick_Friends);
		
		Button btnTags = (Button)findViewById(R.id.profile_layout_btn_tags);
		btnTags.setOnClickListener(onClick_Tags);
	}

	//event handlers
	OnClickListener onClick_Friends = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Intent i = new Intent(mContext, FriendActivity.class);
			startActivity(i);
		}
	};
	
	OnClickListener onClick_Tags = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Intent i = new Intent(mContext, ProfileTagsActivity.class);
			startActivity(i);
		}
	};
}
