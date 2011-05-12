package com.snapperfiche.mobile;

import java.util.List;

import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Post;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.PostService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class StatusFeed extends Activity implements Runnable{
	ProgressDialog dialog;
	Context myContext = this;
	List<Post> mPosts;
	/** Called when the activity is first created. */
	@Override
	public void run() {
		AccountService.Login("bigfiche@fiche.com", "asdf");		
		mPosts = PostService.GetLatestPosts();
		
		Gallery gallery1 = (Gallery) findViewById(R.id.gallery1);
        gallery1.setAdapter(new ImageAdapter(this));
        gallery1.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery2 = (Gallery) findViewById(R.id.gallery2);
        gallery2.setAdapter(new ImageAdapter(this));
        gallery2.setOnItemClickListener(statusImageItemClickListener);
        
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
        
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			dialog.dismiss();
			//Intent i = new Intent(myContext,  StatusFeed.class);
			//startActivity(i);
			/*
			if(AccountService.IsAuthenticated()){
				dialog.cancel();
	    		Intent i = new Intent(myContext,  StatusFeed.class);
	    		startActivity(i);
	    		Toast.makeText(FlashFeed.this, "Welcome ^^", Toast.LENGTH_LONG).show();
			}else{
				dialog.cancel();
				Toast.makeText(FlashFeed.this, "Ruh-roh, we couldn't find your fiche <-< Please try again.", Toast.LENGTH_LONG).show();
			}*/
		}
	};
	
	private void loadStatusFeed(){
		dialog = ProgressDialog.show(StatusFeed.this, "", 
                "Loading... Fiching for your feed", true);
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusfeed);
        
        //loadStatusFeed();
        AccountService.Login("bigfiche@fiche.com", "asdf");		
		mPosts = PostService.GetLatestPosts();
		
		Gallery gallery1 = (Gallery) findViewById(R.id.gallery1);
        gallery1.setAdapter(new ImageAdapter(this));
        gallery1.setOnItemClickListener(statusImageItemClickListener);
        
        Gallery gallery2 = (Gallery) findViewById(R.id.gallery2);
        gallery2.setAdapter(new ImageAdapter(this));
        gallery2.setOnItemClickListener(statusImageItemClickListener);
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
    		Intent i = new Intent(StatusFeed.this, StatusDetailActivity.class);				
			i.putExtra("position", position);
			startActivity(i);
    	}
	};
    
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
		}
    	
    }
}
