package com.flashfeed.www;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.LinearLayout;

public class CarouselImageAdapter extends BaseAdapter{

	int mGalleryItemBackground;
	private Context mContext;
	private Integer[] mResourceIds;
	
	public CarouselImageAdapter(Context c, Integer[] resourceIds){
		mContext = c;
		mResourceIds = resourceIds;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResourceIds.length;
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
		LinearLayout layout = new LinearLayout(mContext);
		layout.addView(new Button(mContext), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		CheckBox cb = new CheckBox(mContext);
		cb.setButtonDrawable(mResourceIds[position]);
		cb.setPadding(20, 0, 20, 0);
		cb.setWidth(128);
		cb.setClickable(false);
		layout.addView(cb);
		return layout;
	}
}

