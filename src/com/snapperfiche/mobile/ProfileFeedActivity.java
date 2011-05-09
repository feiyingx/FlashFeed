package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Post;
import com.snapperfiche.webservices.PostService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProfileFeedActivity extends Activity 
{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusfeed);
        //TODO: need to filter posts by time
        List<Post> posts = PostService.GetLatestPosts();
        
        Gallery gallery1 = (Gallery) findViewById(R.id.gallery1);
        gallery1.setAdapter(new ImageAdapter(this, posts));
        gallery1.setOnItemClickListener(statusImageItemClickListener);
        
        Button cameraBtn = (Button) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(v.getContext(), CameraView.class);
				Intent i = new Intent(v.getContext(), CameraTest.class);
				startActivity(i);
			}
        	
        });
    }
    
    private OnItemClickListener statusImageItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView parent, View v, int position, long id) {
    		//Toast.makeText(StatusFeed.this, "" + position, Toast.LENGTH_SHORT).show();
    		//Toast.makeText(StatusFeed.this, "" + id, Toast.LENGTH_SHORT).show();
    		Intent i = new Intent(ProfileFeedActivity.this, StatusDetailActivity.class);				
			i.putExtra("position", position);
			startActivity(i);
    	}
	};
    
    public class ImageAdapter extends BaseAdapter {
    	private Context mContext;
    	private List<Post> mPosts = new ArrayList<Post>();
    	
    	public ImageAdapter(Context c, List<Post> posts){
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			
			Post post = (Post)getItem(position);
			if(post == null){
				//TODO: log error if post wasn't found and hide this View
				return i;
			}
			
			Bitmap imgBitmap = Utility.GetImageBitmapFromUrl(post.getPhotoUrl());
			if(imgBitmap == null){
				//TODO: log error if imgBitmap wasn't found and hide this View
				return i;
			}
			
			int height = imgBitmap.getHeight();
			int width = imgBitmap.getWidth();
			Toast.makeText(mContext, "height: " + String.valueOf(height) + "|width: " + String.valueOf(width), Toast.LENGTH_SHORT);
			
			i.setImageBitmap(imgBitmap);
			
			i.setLayoutParams(new Gallery.LayoutParams(150, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			
			return i;
		}
    	
    }
}
