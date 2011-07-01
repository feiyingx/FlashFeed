package com.snapperfiche.mobile;

import java.text.SimpleDateFormat;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.data.Post;
import com.snapperfiche.webservices.LikeService;
import com.snapperfiche.webservices.PostService;
import com.snapperfiche.webservices.SimpleCache;
import com.snapperfiche.mobile.custom.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PostDetailActivity extends BaseActivity {
	//variables
	WebImageView mImgPost;
	TextView mTxtCaption, mTxtTime, mTxtLocation;
	Button mBtnComments, mBtnLikes, mBtnFav;
	Post mPost;
	LoadPostDetailTask mGetPostTask;
	int mPostId;
	boolean mIsLiked;
	int mTempNumLikes;
	boolean mIsFav;
	Context mContext = this;
	
	//events
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail);
		
		Bundle bundle = getIntent().getExtras();
		//TODO: can bundle be null?
		mPostId = bundle.getInt("post_id");
		
		//find controls
		mImgPost = (WebImageView)findViewById(R.id.post_detail_imgPost);
		mTxtCaption = (TextView)findViewById(R.id.post_detail_txtCaption);
		mTxtTime = (TextView)findViewById(R.id.post_detail_txtTime);
		mTxtLocation = (TextView)findViewById(R.id.post_detail_txtLocation);
		mBtnComments = (Button)findViewById(R.id.post_detail_btn_comments);
		mBtnLikes = (Button)findViewById(R.id.post_detail_btn_likes);
		mBtnFav = (Button)findViewById(R.id.post_detail_btn_fav);
		
		if(mImgPost == null || mTxtCaption == null || mTxtTime == null || mTxtLocation == null
				|| mBtnComments == null || mBtnFav == null || mBtnLikes == null){
			Log.e("Post Detail Activity", "onCreate: find controls null");
			return;
		}
		
		//set handlers
		mBtnLikes.setOnClickListener(onClick_Like);
		mBtnFav.setOnClickListener(onClick_Fav);
		mBtnComments.setOnClickListener(onClick_Comment);
		
		//get post data, check if we already have it
		final PostDetailActivityDataHolder dataHolder = (PostDetailActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.post != null){
				mPost = dataHolder.post;
				mGetPostTask = dataHolder.task;
				mGetPostTask.attach(this);
				loadResults(mPost);
			}else if(dataHolder.task != null){
				mGetPostTask = dataHolder.task;
				mGetPostTask.attach(this);
			}
		}else{
			//start load task to fetch the post data
			mGetPostTask = new LoadPostDetailTask();
			mGetPostTask.attach(this);
			mGetPostTask.execute(mPostId);
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance(){
		if(mGetPostTask != null) mGetPostTask.detach();
		PostDetailActivityDataHolder dataHolder = new PostDetailActivityDataHolder();
		dataHolder.post = mPost;
		dataHolder.task = mGetPostTask;
		return dataHolder;
	}
	
	//event handlers
	OnClickListener onClick_Like = new OnClickListener(){
		@Override
		public void onClick(View view) {
			if(mIsLiked){
				LikeService.Unlike(mPostId);
				SimpleCache.remove("GetPostById_" + String.valueOf(mPostId));
				mTempNumLikes--;
				mBtnLikes.setText(String.valueOf(mTempNumLikes) + " Likes");
			}else{
				LikeService.Like(mPostId);
				SimpleCache.remove("GetPostById_" + String.valueOf(mPostId));
				mTempNumLikes++;
				mBtnLikes.setText(String.valueOf(mTempNumLikes) + " Likes");
			}
			
			mIsLiked = !mIsLiked;
		}
	};
	
	OnClickListener onClick_Fav = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(mIsFav){
				PostService.UndoFavorite(mPostId);
				SimpleCache.remove("GetPostById_" + String.valueOf(mPostId));
				mBtnFav.setText("Favorite");
			}else{
				PostService.SetFavorite(mPostId);
				SimpleCache.remove("GetPostById_" + String.valueOf(mPostId));
				mBtnFav.setText("Favorited");
			}
			
			mIsFav = !mIsFav;
		}
	};
	
	OnClickListener onClick_Comment = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), StatusDetailActivity.class);
			i.putExtra("post_id", mPostId);
			startActivity(i);
		}
		
	};
	
	//helpers
	private void loadResults(Post p){
		if(p != null){
			String photoUrl = p.getPhotoUrl();
			mImgPost.setImageUrl(photoUrl);
			mImgPost.loadImage();
			
			mTxtCaption.setText(p.getCaption());
			SimpleDateFormat dtFormat = new SimpleDateFormat("MM.dd.yyyy | h:mm aa"); 
			mTxtTime.setText(dtFormat.format(p.getDate()));
			mTxtLocation.setText("@ " + p.getLocality() + ", " + p.getAdminArea());
			
			mBtnComments.setText(String.valueOf(p.getNumComments()) + " Comments");
			mBtnLikes.setText(String.valueOf(p.getNumLikes()) + " Likes");
			
			mIsLiked = p.isLike();
			mTempNumLikes = p.getNumLikes();
			mIsFav = p.isFav();
			if(p.isLike()){
				
			}
			
			if(p.isFav()){
				mBtnFav.setText("Favorited");
			}else{
				mBtnFav.setText("Favorite");
			}
		}
	}
	
	//classes
	static class PostDetailActivityDataHolder{
		Post post;
		LoadPostDetailTask task;
	}
	
	private class LoadPostDetailTask extends AsyncTask<Integer, Integer, Post>{
		PostDetailActivity activity = null;
		@Override
		protected Post doInBackground(Integer... params) {
			return PostService.GetPostById(params[0], false);
		}
		
		@Override
		protected void onPostExecute(Post result){
			activity.mPost = result;
			activity.loadResults(result);
		}
		
		void attach(PostDetailActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
}
