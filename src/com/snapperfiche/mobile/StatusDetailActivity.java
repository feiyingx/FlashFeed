package com.snapperfiche.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
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
}
