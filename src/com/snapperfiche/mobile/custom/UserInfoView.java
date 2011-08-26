package com.snapperfiche.mobile.custom;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.User;
import com.snapperfiche.mobile.R;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.PostService;

public class UserInfoView extends LinearLayout {
	WebImageView mImgProfile;
	TextView mtxtName, mtxtLatestActivity, mtxtLatestActivityDate;
	
	public UserInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.user_info, this);
		
		User user = AccountService.getUser();
		mImgProfile = (WebImageView)findViewById(R.id.imageprofilepicture);
		mImgProfile.setImageUrl(user.getPhotoUrl());
		mImgProfile.loadImage();
		
		mtxtName = (TextView)findViewById(R.id.textusername);
		mtxtName.setText(user.getFirstName() + " " + user.getLastName());
		
		Post recentPost = PostService.GetLatestPost(false); 
		mtxtLatestActivity = (TextView)findViewById(R.id.textlatestactivity);
		mtxtLatestActivityDate = (TextView)findViewById(R.id.textactivity);
		//no activity yet
		if(recentPost == null){
			mtxtLatestActivity.setText("None yet, coming soon!");
		}else{
			Date date = recentPost.getDate();
			Format formatter = new SimpleDateFormat("hh:mma");
			String activity = formatter.format(date) + " @ " + recentPost.getLocality() + ", " + recentPost.getAdminArea();
			mtxtLatestActivity.setText(activity); 
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			mtxtLatestActivityDate.setText("Latest Activity: " + formatter.format(date));
		}
		/*
		Button btnNotifications = (Button)findViewById(R.id.btnNotifications);
		btnNotifications.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), "Notifcations", Toast.LENGTH_SHORT).show();
			}
		});
		*/
	}
}
