package com.snapperfiche.mobile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class StatusDetailActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_detail);
		
		Bundle bundle = getIntent().getExtras();
		int position = bundle.getInt("position");
		
		//Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
		
		TextView positionText = (TextView) findViewById(R.id.position);
		positionText.setText("position: " + position);
	}
	
	public class CommentsAdapter extends BaseAdapter{
		private Context mContext;
		
		public CommentsAdapter(Context context){
			mContext = context;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
