package com.snapperfiche.mobile.custom;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
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
	Post mRecentPost;
	GetLatestPostTask mTask;
	
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
		
		mtxtLatestActivity = (TextView)findViewById(R.id.textlatestactivity);
		mtxtLatestActivityDate = (TextView)findViewById(R.id.textactivity);
		
		mTask = new GetLatestPostTask();
		mTask.attach(this);
		mTask.execute();
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
	
	private void loadLatestPost(){
		
		//no activity yet
		if(mRecentPost == null){
			mtxtLatestActivity.setText("None yet, coming soon!");
		}else{
			Date date = mRecentPost.getDate();
			Format formatter = new SimpleDateFormat("hh:mm a");
			String activity = formatter.format(date) + " @" + mRecentPost.getLocality() + ", " + mRecentPost.getAdminArea();
			mtxtLatestActivity.setText(activity); 
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			mtxtLatestActivityDate.setText("Latest Activity: " + formatter.format(date));
		}
	}
	
	public class GetLatestPostTask extends AsyncTask<Void, Integer, Void>{
		UserInfoView activity = null;
		
		@Override
		protected Void doInBackground(Void... params) {
			activity.mRecentPost = PostService.GetLatestPost(false); 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			activity.loadLatestPost();
		}
		
		void attach(UserInfoView activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
}
