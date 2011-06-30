package com.snapperfiche.mobile;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.data.Post;
import com.snapperfiche.mobile.custom.BaseActivity;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.PostService;

public class FavoritesActivity extends BaseActivity {
	
	List<Post> mFavoritePosts;
	GridView gvFavorite;
	LoadFavorites task;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites_layout);
		
		
		
		dialog = ProgressDialog.show(FavoritesActivity.this, "", 
                "Loading... Fiching for your favorites", true);
		task = new LoadFavorites();
		task.execute();
		
	}
	
	private class LoadFavorites extends AsyncTask<Void, Integer, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			AccountService.Login("bigfiche@fiche.com", "asdf");		
			mFavoritePosts = PostService.GetLatestPosts();
			
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
			Log.d("FavoritesActivity", "OnCancelled, dismiss dialog");
		}
	}
	
	public void loadData(){
		gvFavorite = (GridView) findViewById(R.id.gvFavorites);
		gvFavorite.setAdapter(new ImageAdapter(this));
		
		gvFavorite.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				
			}
		});
	}
	
	public void closeDialog(){
		if(dialog != null){
			dialog.dismiss();
		}
	}
	
	static class FavoritesGridViewHolder{
		Post post;
		//WebImageView imgFavorite;
		TextView txtTest;
		TextView txtUsername;
	}
	
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
		
		public ImageAdapter(Context c) {
			mContext = c;
			mInflater = LayoutInflater.from(c);
		}
		
		public int getCount() {
			//return mFavoritePosts.size();
			return 25;
		}
		
		public Object getItem(int position) {
			if (position < 0 || position >= mFavoritePosts.size())
				return null;
			return mFavoritePosts.get(position);
		}
		
		public long getItemId(int position) {
			if (position < 0 || position >= mFavoritePosts.size())
				return -1;
			return mFavoritePosts.get(position).getId();
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			/*Post post = (Post)getItem(position);
			if(post == null){
				return convertView;
			}
			
			String url = post.getPhotoThumbUrl();
			Drawable loader = mContext.getResources().getDrawable(R.drawable.loader);
			WebImageView i = new WebImageView(mContext, url, loader, true);
			
			i.setLayoutParams(new GridView.LayoutParams(100, 100));
			convertView = i;
			
			FavoritesGridViewHolder holder = new FavoritesGridViewHolder();
			holder.post = post;
			convertView.setTag(holder);
			return convertView;*/
			FavoritesGridViewHolder viewHolder;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.favorites_list_item, null);
				viewHolder = new FavoritesGridViewHolder();
				//viewHolder.imgFavorite = (WebImageView) convertView.findViewById(R.id.favoriteItem_image);
				viewHolder.txtTest = (TextView) convertView.findViewById(R.id.favoriteItem_test);
				viewHolder.txtUsername = (TextView) convertView.findViewById(R.id.favoriteItem_username);
				
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (FavoritesGridViewHolder) convertView.getTag();
			}
			
			Post post = (Post) getItem(position);
			if (post != null) {
				String url = post.getPhotoThumbUrl();
				Drawable loader = mContext.getResources().getDrawable(R.drawable.loader);
				
				viewHolder.post = post;
				//viewHolder.imgFavorite.setImageUrl(url);
				//viewHolder.imgFavorite.loadImage();
				//viewHolder.imgFavorite.setLayoutParams(new GridView.LayoutParams(100, 100));
				viewHolder.txtUsername.setText("user");
			}
			return convertView;
		}
	}
	
}
