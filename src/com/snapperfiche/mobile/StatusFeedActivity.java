package com.snapperfiche.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.Post;
import com.snapperfiche.mobile.RegistrationActivity.RegistrationActivityDataHolder;
import com.snapperfiche.mobile.StatusFeed.FeedGroupItemViewHolder;
import com.snapperfiche.mobile.StatusFeed.StatusFeedGalleryViewHolder;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.GroupService;
import com.snapperfiche.webservices.PostService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StatusFeedActivity extends Activity {
	LayoutInflater mInflater;
	Context mContext = this;
	List<FeedRowDataHolder> colFeedRowViewData;
	//List<GetGlobalFeedTask> colGetTasks;
	boolean mIsCacheRefresh = false;
	LinearLayout linlay_feed;
	TextView txtLoadFeed;
	GetGlobalFeedTask task;
	List<List<Post>> colPostsData;
	List<Group> mGroups;
	
	//override events
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_feed);
		
		//initialize vars
		colFeedRowViewData = new ArrayList<FeedRowDataHolder>();
		//colGetTasks = new ArrayList<GetGlobalFeedTask>();
		
		//find control
		linlay_feed = (LinearLayout) findViewById(R.id.linlay_feed);
		txtLoadFeed = (TextView) findViewById(R.id.status_feed_txt_fetching);
		
		mInflater = LayoutInflater.from(this);
        
        bindTopNav();
        
      //set to 7 right now, since each row is broken up into a day (instead of hour for now)
        //7 days a week, so 7 is a good number
        int numOfRows = 7; 
        Integer[] loadTaskArgs = new Integer[numOfRows];
		for(int i = 0; i < numOfRows; i++){
			View vwFeedRow = mInflater.inflate(R.layout.status_feed_row, null);
			//find controls
			TextView txtHeader = (TextView) vwFeedRow.findViewById(R.id.txt_feed_header);
			Gallery gallery = (Gallery) vwFeedRow.findViewById(R.id.gallery_feed);
			TextView txtEmptyFeed = (TextView) vwFeedRow.findViewById(R.id.txt_feed_empty);
			//TextView txtLoading = (TextView) vwFeedRow.findViewById(R.id.txt_feed_loading);
			
			Calendar calendar = Calendar.getInstance();
			if(i > 0){
				calendar.add(Calendar.DATE, -1*i);
			}
			Date dtFeed = calendar.getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			txtHeader.setText(dateFormat.format(dtFeed));			
			//add gallery to the layout
			linlay_feed.addView(vwFeedRow);
			colFeedRowViewData.add(new FeedRowDataHolder(txtHeader,txtEmptyFeed, gallery));
			
			//GetGlobalFeedTask task = new GetGlobalFeedTask();
			//colGetTasks.add(task);
			//task.execute(new Integer(i));
			loadTaskArgs[i] = i;
		}
        final StatusFeedActivityDataHolder dataHolder = (StatusFeedActivityDataHolder)getLastNonConfigurationInstance();
        if(dataHolder == null){
        	task = new GetGlobalFeedTask();
			task.attach(this);
			task.execute(loadTaskArgs);
        }else{
        	if(dataHolder.colPosts != null){
        		colPostsData = dataHolder.colPosts;
        		displayAllFeeds();
        	}else if(dataHolder.task != null){
        		task = dataHolder.task;
            	task.attach(this);
        	}else{
        		task = new GetGlobalFeedTask();
				task.attach(this);
				task.execute(loadTaskArgs);
        	}
        }
	}
	
	@Override
	public Object onRetainNonConfigurationInstance(){
		StatusFeedActivityDataHolder dataHolder = new StatusFeedActivityDataHolder();
		if(colPostsData != null){
			dataHolder.colPosts = colPostsData;
			if(task != null) task.detach(); 
			dataHolder.task = task;
		}else if(task != null){
			task.detach();
			dataHolder.task = task;
			dataHolder.colPosts = colPostsData;
		}
		return dataHolder;
	}
	
	//helpers	
	private void bindTopNav(){
    	Button btnQuestion = (Button) findViewById(R.id.btn_top_nav_question);
    	btnQuestion.setOnClickListener(onClick_Questions);
    	
    	Button btnProfile = (Button) findViewById(R.id.btn_top_nav_profile);
    	btnProfile.setOnClickListener(onClick_Profile);
    	
    	Button cameraBtn = (Button) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(onClick_Camera);
    }
	
	private void displayFeed(int rowIndex, List<Post> feedPosts){
		FeedRowDataHolder dataHolder = colFeedRowViewData.get(rowIndex);
		
		if(feedPosts != null && !feedPosts.isEmpty()){
			dataHolder.gallery.setAdapter(new FeedImageAdapter(mContext, feedPosts));
			dataHolder.gallery.setOnItemClickListener(statusImageItemClickListener);
			dataHolder.gallery.setVisibility(Gallery.VISIBLE);
		}else{
			//if there are no posts, then hide the gallery and display the emptyFeed textview
			dataHolder.txtEmptyFeed.setVisibility(TextView.VISIBLE);
			dataHolder.gallery.setVisibility(Gallery.GONE);
		}
		
	}
	
	private void displayAllFeeds(){
		BindGroupsList();
		int length = colPostsData.size();
		for(int i = 0; i < length; i++){
			displayFeed(i, colPostsData.get(i));
		}
		txtLoadFeed.setVisibility(TextView.GONE);
		linlay_feed.setVisibility(LinearLayout.VISIBLE);
	}
	
	private class GetGlobalFeedTask extends AsyncTask<Integer, Integer, Void>{
		StatusFeedActivity activity = null;
		int rowIndex;
		
		@Override
		protected Void doInBackground(Integer... args) {
			List<List<Post>> colPostList = new ArrayList<List<Post>>();
			int length = args.length;
			for(int i = 0; i < length; i++){
				colPostList.add(PostService.GetGlobalFeed(i, mIsCacheRefresh));
			}
			activity.colPostsData = colPostList;
			activity.mGroups = GroupService.GetGroups(AccountService.getUser().getId(), GroupType.USER_FEED);
			return null; 
		}
		
		@Override
		protected void onPostExecute(Void result){
			//activity.colPostsData = result;
			activity.displayAllFeeds();
		}
		
		void detach(){
			activity = null;
		}
		
		void attach(StatusFeedActivity activity){
			this.activity = activity;
		}
	}
	
	//event handlers
	private OnItemClickListener statusImageItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView parent, View v, int position, long id) {
    		//Toast.makeText(StatusFeed.this, "" + position, Toast.LENGTH_SHORT).show();
    		//Toast.makeText(StatusFeed.this, "" + id, Toast.LENGTH_SHORT).show();
    		StatusFeedGalleryViewHolder holder = (StatusFeedGalleryViewHolder) v.getTag();
    		Intent i = new Intent(StatusFeedActivity.this, PostDetailActivity.class);				
			if(holder != null){
				if(holder.post != null){
					i.putExtra("post_id", holder.post.getId());
				}
    		}
			startActivity(i);
    	}
	};
	
	OnClickListener onClick_Questions = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Intent i = new Intent(v.getContext(), CameraView.class);
			Intent i = new Intent(v.getContext(), QuestionActivity.class);
			startActivity(i);
		}
    };
    
    OnClickListener onClick_Profile = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Intent i = new Intent(v.getContext(), CameraView.class);
			Intent i = new Intent(mContext, ProfileActivity.class);
			startActivity(i);
		}
    };
    
    OnClickListener onClick_Camera = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Intent i = new Intent(v.getContext(), CameraView.class);
			Intent i = new Intent(v.getContext(), CameraActivity.class);
			startActivity(i);
		}
    };
	
	static class StatusFeedActivityDataHolder{
		GetGlobalFeedTask task;
		List<List<Post>> colPosts;
	}
	
	static class FeedRowDataHolder{
		TextView txtHeader, txtEmptyFeed;
		Gallery gallery;
		
		public FeedRowDataHolder(TextView txtHeader, TextView txtEmptyFeed, Gallery gallery){
			this.txtHeader = txtHeader;
			this.txtEmptyFeed =  txtEmptyFeed;
			this.gallery = gallery;
		}
	}
	
	public class FeedImageAdapter extends BaseAdapter {
    	int mGalleryItemBackground;
    	private Context mContext;
    	private List<Post> mPosts;
    	
    	public FeedImageAdapter(Context c, List<Post> posts){
    		mContext = c;
    		mPosts = posts;
    	}
		
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
    }
	
	static class FeedGroupItemViewHolder{
    	int groupId;
    }
    
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
