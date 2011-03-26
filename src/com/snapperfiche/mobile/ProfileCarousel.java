package com.snapperfiche.mobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class ProfileCarousel extends Gallery {

	public ProfileCarousel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ProfileCarousel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
		 
	public ProfileCarousel(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onDown(MotionEvent e){
		dispatchTouchEvent(e);
		System.out.println("gallery onDown");
		return true;
	}
}
