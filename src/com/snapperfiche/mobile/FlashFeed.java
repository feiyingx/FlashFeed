package com.snapperfiche.mobile;

import com.snapperfiche.webservices.AccountService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;

public class FlashFeed extends Activity implements OnClickListener, Runnable{
    /** Called when the activity is first created. */
	EditText txtUsername;
	EditText txtPassword;
	ProgressDialog dialog;
	Context myContext = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtUsername = (EditText) findViewById(R.id.login_txtBox_username);
        txtPassword = (EditText) findViewById(R.id.login_txtBox_password);
        
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
    		dialog = ProgressDialog.show(FlashFeed.this, "", 
                    "Loading... Here's a smile while you wait ^^", true);
    		
    		Thread thread = new Thread(this);
    		thread.start();
    		break;
    	case R.id.login_btn_signup:
    		Intent i = new Intent(this, RegistrationActivity.class);
    		startActivity(i);
    		break;
    	}
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String username = txtUsername.getText().toString();
		String password = txtPassword.getText().toString();
		AccountService.Login(username, password);
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			dialog.dismiss();
			if(AccountService.IsAuthenticated()){
				dialog.cancel();
	    		Intent i = new Intent(myContext, StatusFeed.class);
	    		startActivity(i);
	    		Toast.makeText(FlashFeed.this, "Welcome ^^", Toast.LENGTH_LONG).show();
			}else{
				dialog.cancel();
				Toast.makeText(FlashFeed.this, "Ruh-roh, we couldn't find your fiche <-< Please try again.", Toast.LENGTH_LONG).show();
			}
		}
	};
}