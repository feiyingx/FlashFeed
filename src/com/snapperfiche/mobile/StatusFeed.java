package com.snapperfiche.mobile;

import com.snapperfiche.mobile.custom.*;
import java.util.ArrayList;
import java.util.List;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.Post;
import com.snapperfiche.mobile.FriendTaggerActivity.ViewHolder;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.GroupService;
import com.snapperfiche.webservices.PostService;
import com.snapperfiche.webservices.SimpleCache;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StatusFeed extends BaseActivity{
	ProgressDialog dialog;
	Context myContext = this;
	List<Post> mPosts;
	List<Group> mGroups;
	Gallery g1, g2;
	LoadStatusFeed task;
	
	String mGroupCacheKey = "ck_StatusFeedActivity_mGroups";
	String mPostsCacheKey = "ck_StatusFeedActivity_mPosts";
		
	static class StatusFeedDataHolder{
		List<Post> postsData;
		List<Group> groupsData;
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    final StatusFeedDataHolder data = new StatusFeedDataHolder();
	    if(mPosts != null && mGroups != null){
		    data.postsData = mPosts;
		    data.groupsData = mGroups;
	    }else{
	    	return null;
	    }
	    
	    return data;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusfeed);
        
        /*final Object data = getLastNonConfigurationInstance();
        if(data != null){
        	//cast it into the data holder obj
        	StatusFeedDataHolder dataHolder = (StatusFeedDataHolder) data;
        	//set the existing data
        	mPosts = dataHolder.postsData;
        	mGroups = dataHolder.groupsData;
        	loadData();
        }else{
        	boolean reloadFeed = this.getIntent().getBooleanExtra("reloadFeed", false);
        	
        	if(!reloadFeed){
	        	//try to get from cache
	        	mPosts = (List<Post>)SimpleCache.get(mPostsCacheKey);
	        	mGroups = (List<Group>)SimpleCache.get(mGroupCacheKey);
	        	
	        	if(mPosts != null && mGroups != null){
	        		loadData();
	        	}else{
			        dialog = ProgressDialog.show(StatusFeed.this, "", 
			                "Loading... Fiching for your feed", true);
			        
			        g1 = (Gallery) findViewById(R.id.gallery1);
			        g2 = (Gallery) findViewById(R.id.gallery2);
			        task = new LoadStatusFeed();
			        task.execute();
	        	}
        	}else{
        		dialog = ProgressDialog.show(StatusFeed.this, "", 
		                "Loading... Fiching for your feed", true);
		        
		        g1 = (Gallery) findViewById(R.id.gallery1);
		        g2 = (Gallery) findViewById(R.id.gallery2);
		        task = new LoadStatusFeed();
		        task.execute();
        	}
        }*/
        //loadStatusFeed();
        /*
        AccountService.Login("bigfiche@fiche.com", "asdf");		
		mPosts = PostService.GetLatestPosts();
		
		mGroups = GroupService.GetGroups(AccountService.getUser().getId(), GroupType.USER_FEED);
		*/
        /*
		BindGroupsList();
		Gallery gallery1 = (Gallery) findViewById(R.id.gallery1);
        gallery1.setAdapter(new ImageAdapter(this));
        gallery1.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery2 = (Gallery) findViewById(R.id.gallery2);
        gallery2.setAdapter(new ImageAdapter(this));
        gallery2.setOnItemClickListener(statusImageItemClickListener);
        */
        /*
        Gallery gallery3 = (Gallery) findViewById(R.id.gallery3);
        gallery3.setAdapter(new ImageAdapter(this));
        gallery3.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery4 = (Gallery) findViewById(R.id.gallery4);
        gallery4.setAdapter(new ImageAdapter(this));
        gallery4.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery5 = (Gallery) findViewById(R.id.gallery5);
        gallery5.setAdapter(new ImageAdapter(this));
        gallery5.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery6 = (Gallery) findViewById(R.id.gallery6);
        gallery6.setAdapter(new ImageAdapter(this));
        gallery6.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery7 = (Gallery) findViewById(R.id.gallery7);
        gallery7.setAdapter(new ImageAdapter(this));
        gallery7.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery8 = (Gallery) findViewById(R.id.gallery8);
        gallery8.setAdapter(new ImageAdapter(this));
        gallery8.setOnItemClickListener(statusImageItemClickListener);
        */
        /*Button cameraBtn = (Button) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(v.getContext(), CameraView.class);
				Intent i = new Intent(v.getContext(), CameraActivity.class);
				startActivity(i);
			}
        	
        });*/
        
        //add new group feed button
        Button btnAddGroupFeed = (Button) findViewById(R.id.btn_status_feed_add_group);
        btnAddGroupFeed.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), AddUserGroupActivity.class);
				startActivity(i);
			}
        });
        
        //BindTopNav();
    }
    
    @Override
    public void onPause(){
    	closeDialog();
    	super.onPause();
    	Log.d("StatusFeed", "onPause called");
    	if(task != null){
    		task.cancel(false);
    		Log.d("StatusFeed", "Cancelled loadfeed task from pause");
    	}
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	Log.d("StatusFeed", "onStop called");
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.d("StatusFeed", "onDestroy called");
    }
    
    private OnItemClickListener statusImageItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView parent, View v, int position, long id) {
    		//Toast.makeText(StatusFeed.this, "" + position, Toast.LENGTH_SHORT).show();
    		//Toast.makeText(StatusFeed.this, "" + id, Toast.LENGTH_SHORT).show();
    		StatusFeedGalleryViewHolder holder = (StatusFeedGalleryViewHolder) v.getTag();
    		Intent i = new Intent(StatusFeed.this, StatusDetailActivity.class);				
			i.putExtra("position", position);
			if(holder != null){
				if(holder.post != null){
					i.putExtra("post_id", holder.post.getId());
				}
    		}
			startActivity(i);
    	}
	};
	private class LoadStatusFeed extends AsyncTask<Void, Integer, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			AccountService.Login("bigfiche@fiche.com", "asdf");		
			mPosts = PostService.GetLatestPosts();
			mGroups = GroupService.GetGroups(AccountService.getUser().getId(), GroupType.USER_FEED);
			
			//SimpleCache.put(mPostsCacheKey, mPosts);
			//SimpleCache.put(mGroupCacheKey, mGroups);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			loadData();
			closeDialog();
		}
		
		@Override
		protected void onCancelled(){
			//closeDialog();
			Log.d("StatusFeed", "OnCancelled, dismiss dialog");
		}
	}
	
	public void closeDialog(){
		if(dialog != null){
			dialog.dismiss();
		}
	}
	
	public void loadData(){
		BindGroupsList();
		Gallery gallery1 = (Gallery) findViewById(R.id.gallery1);
        gallery1.setAdapter(new ImageAdapter(this));
        gallery1.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery2 = (Gallery) findViewById(R.id.gallery2);
        gallery2.setAdapter(new ImageAdapter(this));
        gallery2.setOnItemClickListener(statusImageItemClickListener);
	}
	
	static class StatusFeedGalleryViewHolder{
		Post post;
	}
	
    public class ImageAdapter extends BaseAdapter {
    	int mGalleryItemBackground;
    	private Context mContext;
    	//private List<Post> mPosts;
    	
    	private Integer[] mImageIds = {
    			R.drawable.icon,
    			R.drawable.icon,
    			R.drawable.icon,
    			R.drawable.icon,
    			R.drawable.icon,
    			R.drawable.icon,
    			R.drawable.icon,
    			R.drawable.icon
    	};
    	
    	public ImageAdapter(Context c){
    		mContext = c;
    		//mPosts = PostService.GetLatestPosts();
    	}
    	
    	/*
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mImageIds.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	
		@Override
			// TODO Auto-generated method stub
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			
			Bitmap imgBitmap = Utility.GetImageBitmapFromUrl("http://192.168.1.4:3000/images/mine.jpg");
	        i.setImageBitmap(imgBitmap);
	        i.setLayoutParams(new Gallery.LayoutParams(120, 120));
	        /*
			i.setImageResource(mImageIds[position]);
			i.setLayoutParams(new Gallery.LayoutParams(150, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			
			//return i;
		//}
		*/
		
		
    	@Override
		public int getCount() {
			return mPosts.size();
		}

		@Override
		public Object getItem(int position) {
			if(position >= mPosts.size())
				return null;
			return mPosts.get(position);
		}

		@Override
		public long getItemId(int position) {
			if(position >= mPosts.size())
				return -1;
			return mPosts.get(position).getId();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			Post post = (Post)getItem(position);
			if(post == null){
				return convertView;
			}
			
			String url = post.getPhotoThumbUrl();
			Drawable loader = mContext.getResources().getDrawable(R.drawable.loader);
			WebImageView i = new WebImageView(mContext, url, loader, true);
			convertView = i;
			
			StatusFeedGalleryViewHolder holder = new StatusFeedGalleryViewHolder();
			holder.post = post;
			convertView.setTag(holder);
			return convertView;
		}
		/*
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			
			Post post = (Post)getItem(position);
			if(post == null){
				//TODO: log error if post wasn't found and hide this View
				return i;
			}
			
			Bitmap imgBitmap = Utility.GetImageBitmapFromUrl(post.getPhotoThumbUrl(), post.getPhotoFileName(), mContext);
			if(imgBitmap == null){
				//TODO: log error if imgBitmap wasn't found and hide this View
				return i;
			}
			
			int height = imgBitmap.getHeight();
			int width = imgBitmap.getWidth();
			//Toast.makeText(mContext, "height: " + String.valueOf(height) + "|width: " + String.valueOf(width), Toast.LENGTH_SHORT);
			
			i.setImageBitmap(imgBitmap);
			
			i.setLayoutParams(new Gallery.LayoutParams(100, 100));
			//i.setScaleType(ImageView.ScaleType.FIT_XY);
			
			return i;
		}*/
    	
    }
    
    /*public void BindTopNav(){
    	Button btnQuestion = (Button) findViewById(R.id.btn_top_nav_question);
    	btnQuestion.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(v.getContext(), CameraView.class);
				Intent i = new Intent(v.getContext(), QuestionActivity.class);
				startActivity(i);
			}
        });
    }*/
    
    public void BindGroupsGallery(){
    	//Gallery groupGallery = (Gallery) findViewById(R.id.gallery_status_feed_groups);
    	//groupGallery.setAdapter 
    }
    /*
    public void BindGroupsList(){
    	ListView lvGroups = (ListView) findViewById(R.id.lv_status_feed_groups);
    	lvGroups.setAdapter(new FeedGroupItemAdapter(myContext, mGroups));
    }*/
    
    /* Feed Group */
    static class FeedGroupItemViewHolder{
    	int groupId;
    }
    
    private final int TAGKEY_GROUP_BTN_GROUP_ID = 0;
    public void BindGroupsList(){
    	LinearLayout groupsList = (LinearLayout) findViewById(R.id.ll_status_feed_groups);
    	
    	int i;
    	List<Group> groups = new ArrayList<Group>();
    	groups.add(new Group(-1, "Global"));
    	groups.add(new Group(-2, "Friends"));
    	if(mGroups != null)
			groups.addAll(mGroups);
    	int count = groups.size();
    	for(i = 0; i < count; i++){
    		Group currentGroup = groups.get(i);
    		View childView = getLayoutInflater().inflate(R.layout.status_feed_groups_item, null);
	    	Button btn = (Button) childView.findViewById(R.id.btn_status_feed_group_item);
	    	btn.setText(currentGroup.getName());
	    	btn.setTag(currentGroup.getId());
	    	groupsList.addView(childView);
    	}
    }
    
    public class FeedGroupItemAdapter extends BaseAdapter{
    	private LayoutInflater mInflater;
    	private List<Group> galleryItems = new ArrayList<Group>();
    	public FeedGroupItemAdapter(Context c, List<Group> groups){
    		galleryItems.add(new Group(-1, "Global"));
    		galleryItems.add(new Group(-2, "Friends"));
    		if(groups != null)
    			galleryItems.addAll(groups);
    		
    		mInflater = LayoutInflater.from(c);
    	}
    	
		@Override
		public int getCount() {
			return galleryItems.size();
		}

		@Override
		public Object getItem(int position) {
			if(position <= galleryItems.size()){
				return galleryItems.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(position <= galleryItems.size()){
				return galleryItems.get(position).getId();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FeedGroupItemViewHolder holder;
			Group currentGroup = (Group) getItem(position);
			if(currentGroup == null)
				return null;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.status_feed_groups_item, parent, false);
				holder = new FeedGroupItemViewHolder();
				holder.groupId = currentGroup.getId();
				convertView.setTag(holder);
			}else{
				holder = (FeedGroupItemViewHolder) convertView.getTag();
			}
			
			Button btn = (Button) convertView.findViewById(R.id.btn_status_feed_group_item);
			btn.setText(currentGroup.getName());
			holder.groupId = currentGroup.getId();
			
			return convertView;
		}
    	
    }
}
