package com.flashfeed.www;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

public class StatusFeed extends Activity{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusfeed);
        
        Gallery gallery1 = (Gallery) findViewById(R.id.gallery1);
        gallery1.setAdapter(new ImageAdapter(this));
        
        Gallery gallery2 = (Gallery) findViewById(R.id.gallery2);
        gallery2.setAdapter(new ImageAdapter(this));
        
        Gallery gallery3 = (Gallery) findViewById(R.id.gallery3);
        gallery3.setAdapter(new ImageAdapter(this));
        
        Gallery gallery4 = (Gallery) findViewById(R.id.gallery4);
        gallery4.setAdapter(new ImageAdapter(this));
        
        Gallery gallery5 = (Gallery) findViewById(R.id.gallery5);
        gallery5.setAdapter(new ImageAdapter(this));
        
        Gallery gallery6 = (Gallery) findViewById(R.id.gallery6);
        gallery6.setAdapter(new ImageAdapter(this));
        
        Gallery gallery7 = (Gallery) findViewById(R.id.gallery7);
        gallery7.setAdapter(new ImageAdapter(this));
        
        Gallery gallery8 = (Gallery) findViewById(R.id.gallery8);
        gallery8.setAdapter(new ImageAdapter(this));
        
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
