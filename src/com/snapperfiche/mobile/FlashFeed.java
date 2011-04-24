package com.snapperfiche.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class FlashFeed extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View loginBtn = findViewById(R.id.login_btn_login);
        loginBtn.setOnClickListener(this);
        
        View signinBtn = findViewById(R.id.login_btn_signup);
        signinBtn.setOnClickListener(this);
               
        //Intent i = new Intent(this, Profile.class);
		//startActivity(i);
        
        //Intent i = new Intent(this, TestHttpRequest.class);
        //startActivity(i);
		
    }
    
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.login_btn_login:
    		Intent i = new Intent(this, StatusFeed.class);
    		startActivity(i);
    		break;
    	case R.id.login_btn_signup:
    		i = new Intent(this, RegistrationActivity.class);
    		startActivity(i);
    		break;
    	}
    }
}