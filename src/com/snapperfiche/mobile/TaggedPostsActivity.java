package com.snapperfiche.mobile;

import java.util.List;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.data.Post;
import com.snapperfiche.mobile.StatusFeed.StatusFeedGalleryViewHolder;
import com.snapperfiche.webservices.PostService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TaggedPostsActivity extends Activity{
	GridView mGvPosts;
	int mTagId;
	List<Post> mPosts;
	GetPostsByTagTask mGetPostsTask;
	Context mContext = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagged_posts_layout);
		
		Bundle bundle = getIntent().getExtras();
		mTagId = bundle.getInt("tag_id");
		
		mGvPosts = (GridView)findViewById(R.id.tagged_posts_gv_posts);
		TextView txtTagName = (TextView)findViewById(R.id.tagged_posts_txt_tag_name);
		
		mGetPostsTask = new GetPostsByTagTask();
		mGetPostsTask.attach(this);
		mGetPostsTask.execute(mTagId);
	}
	
	//helpers
	private void loadPosts(){
		mGvPosts.setAdapter(new SimplePostsGridAdapter(mPosts));
	}
	
	//classes
	public class SimplePostsGridAdapter extends BaseAdapter{
		List<Post> mPosts;
		
		public SimplePostsGridAdapter(List<Post> posts){
			mPosts = posts;
		}
		
		@Override
		public int getCount() {
			if(mPosts == null) return 0;
			return mPosts.size();
		}

		@Override
		public Object getItem(int position) {
			if(mPosts == null) return null;
			return mPosts.get(position);
		}

		@Override
		public long getItemId(int position) {
			if(mPosts == null) return 0;
			return mPosts.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Post post = (Post)getItem(position);
			if(post == null){
				return convertView;
			}
			StatusFeedGalleryViewHolder holder;
			String url = post.getPhotoThumbUrl();
			Drawable loader = mContext.getResources().getDrawable(R.drawable.loader);
			if(convertView != null){
				((WebImageView)convertView).setImageUrl(url);
				((WebImageView)convertView).loadImage();
				holder = (StatusFeedGalleryViewHolder)convertView.getTag();
			}else{
				WebImageView i = new WebImageView(mContext, url, loader, true);
				convertView = i;
				holder = new StatusFeedGalleryViewHolder();
			}
			 
			holder.post = post;
			convertView.setTag(holder);
			return convertView;
		}
		
	}
	
	public class GetPostsByTagTask extends AsyncTask<Integer, Integer, List<Post>>{
		TaggedPostsActivity activity = null;
		
		@Override
		protected List<Post> doInBackground(Integer... params) {
			return PostService.GetPostsByTagId(params[0], false);
		}
		
		@Override
		protected void onPostExecute(List<Post> result){
			activity.mPosts = result;
			activity.loadPosts();
		}
		
		void attach(TaggedPostsActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
}
