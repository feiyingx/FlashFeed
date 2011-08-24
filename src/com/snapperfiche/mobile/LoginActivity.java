package com.snapperfiche.mobile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.snapperfiche.code.Enumerations.LoginStatus;
import com.snapperfiche.webservices.AccountService;

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */
	EditText txtUsername;
	EditText txtPassword;
	ProgressDialog dialog;
	Context mContext = this;
	Button mBtnFb;
	String mErrorMsg = "Please enter your:";
	LoginAsyncTask mTask;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        txtUsername = (EditText) findViewById(R.id.edittextusername);
        txtPassword = (EditText) findViewById(R.id.edittextpassword);
        
        View loginBtn = findViewById(R.id.buttonlogin);
        loginBtn.setOnClickListener(onClick_login);
        
        mBtnFb = (Button)findViewById(R.id.buttonfb);
        mBtnFb.setOnClickListener(onClick_fb);
        
        final LoginActivityDataHolder dataHolder = (LoginActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.task != null){
				mTask = dataHolder.task;
				mTask.attach(this);
				dialog = ProgressDialog.show(mContext, "", "Registering", true);
				initiateLoginState();
			}
		}
        
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
    
    @Override
    protected void onResume(){
    	super.onResume();
    	if(AccountService.IsAuthenticated()){
			Intent i = new Intent(this, StatusFeedActivity.class);
			startActivity(i);
		}
    }
    
  //activity events
	@Override
	public Object onRetainNonConfigurationInstance(){
		final LoginActivityDataHolder dataHolder = new LoginActivityDataHolder();
		if(mTask != null){
			mTask.detach();
			dataHolder.task = mTask;
		}
		return dataHolder;
	}
    
    public class LoginAsyncTask extends AsyncTask<String, Integer, LoginStatus>{
    	LoginActivity activity = null;
    	
		@Override
		protected LoginStatus doInBackground(String... params) {
			String email = params[0];
			String password = params[1];
			return AccountService.Login(email, password);
		}
    	
		@Override
		protected void onPostExecute(LoginStatus result) {
			activity.completeLogin(result);
		}
		
		void detach(){
			activity = null;
		}
		
		void attach(LoginActivity activity){
			this.activity = activity;
		}
    }
    
    static class LoginActivityDataHolder{
		LoginAsyncTask task;
	}
	
	private boolean validateForm(){
		boolean isValid = true;
		
		if(isEmpty(txtUsername)){
			isValid = false;
			mErrorMsg += "\n> Email";
		}else{
			if(!isValidEmailFormat(txtUsername.getText().toString())){
				isValid = false;
				mErrorMsg += "\n"+getString(R.string.error_email_format);
			}
		}
		
		if(isEmpty(txtPassword)){
			isValid = false;
			mErrorMsg += "\n"+getString(R.string.error_req_pass);
		}
		
		return isValid;
	}
	
	private void displayErrors(){
		Toast.makeText(mContext, mErrorMsg, Toast.LENGTH_LONG).show();
		//after displaying error, reset error msgs
		mErrorMsg = "Please enter your:";
	}
	
	private void completeLogin(LoginStatus status){
		if(status == LoginStatus.SUCCESS){
			dialog.cancel();
    		Intent i = new Intent(mContext,  StatusFeedActivity.class);
    		startActivity(i);
    		Toast.makeText(LoginActivity.this, "Welcome, your throne awaits you", Toast.LENGTH_LONG).show();
		}else{
			dialog.cancel();
			Toast.makeText(LoginActivity.this, "Ruh-roh, we couldn't find your account, please try again.", Toast.LENGTH_LONG).show();
		}
	}
	
	private void initiateLoginState(){
		dialog = ProgressDialog.show(LoginActivity.this, "", 
                "Loading.. Doo do do do, do do do do do do do, do do do do do do do, do do do do do do doo", true);
	}
	
	//helpers
	private boolean isEmpty(EditText txtBox){
		return txtBox.length() == 0 ;
	}
		
	private boolean isValidEmailFormat(String email){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	//events
	OnClickListener onClick_login = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(validateForm()){
				initiateLoginState();
				mTask = new LoginAsyncTask();
				mTask.attach(LoginActivity.this);
				mTask.execute(txtUsername.getText().toString(), txtPassword.getText().toString());
			}else{
				displayErrors();
			}
		}
	};
	
	OnClickListener onClick_fb = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getApplicationContext(), FacebookConnect.class);
			startActivity(i);
		}
		
	};
}