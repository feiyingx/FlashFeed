package com.snapperfiche.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class StatusFeed extends Activity{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusfeed);
        
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
        
        Button cameraBtn = (Button) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(v.getContext(), CameraView.class);
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
    	}
    	
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
			
			i.setImageResource(mImageIds[position]);
			i.setLayoutParams(new Gallery.LayoutParams(150, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			
			return i;
		}
    	
    }
}
