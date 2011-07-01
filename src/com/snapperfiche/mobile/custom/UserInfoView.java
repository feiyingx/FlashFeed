package com.snapperfiche.mobile.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snapperfiche.mobile.R;

public class UserInfoView extends LinearLayout {
	
	public UserInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.user_info, this);
		
		Button btnNotifications = (Button)findViewById(R.id.btnNotifications);
		btnNotifications.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), "Notifcations", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
