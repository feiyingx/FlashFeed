package com.snapperfiche.mobile;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.code.Enumerations.PostType;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.QuestionPost;
import com.snapperfiche.mobile.ProfileTagsActivity.TagItemViewHolder;
import com.snapperfiche.mobile.ProfileTagsActivity.TagListAdapter;
import com.snapperfiche.mobile.custom.BaseActivity;
import com.snapperfiche.mobile.custom.SectionedAdapter;
import com.snapperfiche.webservices.PostService;
import com.snapperfiche.webservices.TagService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher;

public class ProfileFeedActivity extends BaseActivity 
{
	Context mContext = this;
	ListView mLvPosts;
	SectionedAdapter mAdapter;
	List<Post> mPosts;
	GetFeedTask mTask;
	ProgressDialog mDialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        
        mLvPosts = (ListView)findViewById(R.id.lv_posts);
        
        //load tasks
        if(mDialog == null){
        	mDialog = ProgressDialog.show(mContext, "", "Loading...", true);
        }
        mTask = new GetFeedTask();
        mTask.attach(this);
        mTask.execute();
    }
    
  //event listeners
	private OnItemClickListener postItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView parent, View v, int position, long id) {
    		ProfileFeedListViewHolder holder = (ProfileFeedListViewHolder) v.getTag();
    		Intent i = new Intent(ProfileFeedActivity.this, PostDetailActivity.class);				
			if(holder != null){
				if(holder.post != null){
					i.putExtra("post_id", holder.post.getId());
				}
    		}
			startActivity(i);
    	}
	};
	
	private OnClickListener postCommentClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int postId = (Integer)v.getTag();
			Intent i = new Intent(v.getContext(), StatusDetailActivity.class);
			i.putExtra("post_id", postId);
			startActivity(i);
		}
		
	};
    
    //helpers
    private void loadData(){
    	if(mAdapter == null){
    		mAdapter = new SectionedAdapter(){

				@Override
				protected View getHeaderView(String caption, int index,
						View convertView, ViewGroup parent) {
					TextView result = (TextView)convertView;
					if(convertView == null){
						result = (TextView)getLayoutInflater().inflate(R.layout.list_header, null);
					}
					result.setText(caption);
					return result;
				}
    		};
    	}
    	
    	int count = mPosts.size();
    	Post prev = null;
    	List<Post> tempList = new ArrayList<Post>();
    	String sectionName = "";
    	Format formatter = new SimpleDateFormat("MM/dd/yyyy");
    	for(int i = 0; i < count; i++){
    		//get current post
    		Post current = mPosts.get(i);
    		//if its the first item in the list
    		if(i == 0){
    			Date date = current.getDate();    			
    			sectionName = formatter.format(date);
    			tempList.add(current);
    		}else{
    			if(prev != null){
    				//compare to date of the prev item
    				String prevDate = formatter.format(prev.getDate());
    				String nowDate = formatter.format(current.getDate());
    				
    				if(!prevDate.equals(nowDate)){
    					mAdapter.addSection(sectionName, new ProfileFeedListAdapter(mContext, tempList));
    					//reset section name and list items
    					sectionName = nowDate;
    					tempList = new ArrayList<Post>();
    				}
    				tempList.add(current);
    				
    				//add section for last item
    				if(i == count - 1){
						mAdapter.addSection(sectionName, new ProfileFeedListAdapter(mContext, tempList));
					}
    			}
    		}
    		prev = current;
    	}
    	
    	this.mLvPosts.setAdapter(mAdapter);
    	this.mLvPosts.setOnItemClickListener(postItemClickListener);
    	if(this.mDialog != null)
    		this.mDialog.dismiss();
    }
    
    //classes
    public class GetFeedTask extends AsyncTask<Void, Integer, Void>{
    	ProfileFeedActivity activity = null;
		
		@Override
		protected Void doInBackground(Void... params) {
			activity.mPosts = PostService.GetLatestPosts();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			activity.loadData();
		}
		
		void attach(ProfileFeedActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
    
    class ProfileFeedListViewHolder{
    	TextView txtPostType, txtPostText, txtPostLocation;
    	Button btnComments;
    	WebImageView wimgPost;
    	Post post;
    }
    
    public class ProfileFeedListAdapter extends BaseAdapter{
    	List<Post> posts;
    	Context context;
    	LayoutInflater inflater;
    	
    	public ProfileFeedListAdapter(Context c, List<Post> posts){
    		this.posts = posts;
    		this.context = c;
    		this.inflater = LayoutInflater.from(c);
    	}
    	
		@Override
		public int getCount() {
			if(posts != null) return posts.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(posts != null) return posts.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(posts != null) return posts.get(position).getId();
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Post post = (Post)getItem(position);
			ProfileFeedListViewHolder holder;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.profile_post_row, null);
				TextView txtPostType = (TextView)convertView.findViewById(R.id.txtPostType);
				TextView txtPostText = (TextView)convertView.findViewById(R.id.txtPostText);
				TextView txtPostLocation = (TextView)convertView.findViewById(R.id.txtPostLocation);
				Button btnComments = (Button)convertView.findViewById(R.id.btnComments);
				WebImageView wimgPost = (WebImageView)convertView.findViewById(R.id.imgPost);
				holder = new ProfileFeedListViewHolder();
				holder.txtPostType = txtPostType;
				holder.txtPostText = txtPostText;
				holder.txtPostLocation = txtPostLocation;
				holder.btnComments = btnComments;
				holder.wimgPost = wimgPost;
				convertView.setTag(holder);
			}else{
				holder = (ProfileFeedListViewHolder)convertView.getTag();
			}
			
			String postTypeText = "You Posted";
			String numCommentsText = post.getNumComments() + " Comments";
			PostType type = post.getPostType();
			if(type == PostType.QUESTION){
				postTypeText = "You Asked";
				numCommentsText = post.getNumAnswers() + " Answers";
			}else if(type == PostType.ANSWER){
				postTypeText = "You Answered";
			}
			holder.txtPostType.setText(postTypeText);
			String caption = post.getCaption();
			if(caption.length() > 0){
				holder.txtPostText.setText("\"" + post.getCaption() + "\"");
			}
			
			String locationTimeText = Utility.GetPostDateLocationString(post.getDate(), post.getLocality(), post.getAdminArea());
			holder.txtPostLocation.setText(locationTimeText);
			
			holder.wimgPost.setImageUrl(post.getPhotoThumbUrl());
			holder.wimgPost.loadImage();
			
			holder.btnComments.setText(numCommentsText);
			holder.btnComments.setTag(post.getId()); //set the post id to the button
			holder.btnComments.setOnClickListener(postCommentClickListener);
			holder.post = post;
			return convertView;
		}
    	
    }
}
