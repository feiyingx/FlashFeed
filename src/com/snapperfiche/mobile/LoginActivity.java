package com.snapperfiche.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.snapperfiche.webservices.AccountService;

public class LoginActivity extends Activity implements OnClickListener, Runnable{
    /** Called when the activity is first created. */
	EditText txtUsername;
	EditText txtPassword;
	ProgressDialog dialog;
	Context myContext = this;
	Button mBtnFb;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        txtUsername = (EditText) findViewById(R.id.edittextusername);
        txtPassword = (EditText) findViewById(R.id.edittextpassword);
        
        View loginBtn = findViewById(R.id.buttonlogin);
        loginBtn.setOnClickListener(this);
        
        mBtnFb = (Button)findViewById(R.id.buttonfb);
        mBtnFb.setOnClickListener(onClick_fb);
        
        /*
        View signinBtn = findViewById(R.id.login_btn_signup);
        signinBtn.setOnClickListener(this);
         */
        /*
        Intent i = new Intent(this, StatusFeed.class);
        //Intent i = new Intent(this, CameraActivity.class);
			startActivity(i);
		*/
		
        /*
        AccountService.Login("bigfiche@fiche.com", "asdf");
		Intent i = new Intent(this, StatusFeedActivity.class);
		startActivity(i);
		*/
        /*
        Intent i = new Intent(this, FacebookConnect.class);
        startActivity(i);
        */
		/*
        AccountService.Login("d@d.com", "asdf");
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
        */
        

        /*View signinBtn = findViewById(R.id.butt);
        signinBtn.setOnClickListener(this);*/
    }
    
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.buttonlogin:
    		dialog = ProgressDialog.show(LoginActivity.this, "", 
                    "Loading... Here's a smile while you wait ^^", true);
    		
    		Thread thread = new Thread(this);
    		thread.start();
    		/*
    		Intent j = new Intent(myContext, StatusFeed.class);
    		startActivity(j);*/
    		break;
    	/*case R.id.login_btn_signup:
    		Intent i = new Intent(this, RegistrationActivity.class);
    		startActivity(i);
    		break;*/
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
			/*
			dialog.dismiss();
			Intent i = new Intent(myContext,  StatusFeed.class);
			startActivity(i);
			*/
			if(AccountService.IsAuthenticated()){
				dialog.cancel();
	    		Intent i = new Intent(myContext,  StatusFeedActivity.class);
	    		startActivity(i);
	    		Toast.makeText(LoginActivity.this, "Welcome ^^", Toast.LENGTH_LONG).show();
			}else{
				dialog.cancel();
				Toast.makeText(LoginActivity.this, "Ruh-roh, we couldn't find your fiche <-< Please try again.", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	//events
	OnClickListener onClick_fb = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getApplicationContext(), FacebookConnect.class);
			startActivity(i);
		}
		
	};
}