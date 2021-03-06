package com.snapperfiche.mobile;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.snapperfiche.code.Enumerations.AccountType;
import com.snapperfiche.code.Enumerations.RegisterStatus;
import com.snapperfiche.webservices.AccountService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	EditText etxtFirstName, etxtLastName, etxtEmail, etxtPassword;
	Button btnRegister;
	boolean rfvFirstName = false;
	boolean rfvLastName = false;
	boolean rfvEmail = false;
	boolean rfvPassword = false;
	Context mContext = this;
	TextView txtReqFirstName, txtReqLastName, txtReqEmail, txtReqPassword, txtPassMin, txtEmailDup, txtEmailFormat;
	RegisterUserTask registrationTask;
	ProgressDialog dialog;
	String mErrorMsg = "Please enter your:";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		mContext = this;
		
		//find controls
		etxtFirstName = (EditText) findViewById(R.id.reg_etxt_fname);
		etxtLastName = (EditText) findViewById(R.id.reg_etxt_lname);
		etxtEmail = (EditText) findViewById(R.id.reg_etxt_email);
		etxtPassword = (EditText) findViewById(R.id.reg_etxt_password);
		btnRegister = (Button) findViewById(R.id.reg_btn_register);
		btnRegister.setOnClickListener(submitFormOnClick);
		
		final RegistrationActivityDataHolder dataHolder = (RegistrationActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.task != null){
				registrationTask = dataHolder.task;
				registrationTask.attach(this);
				initiateRegistrationState();
			}
		}
	}
	
	private boolean validateForm(){
		boolean isValid = true;
		if(isEmpty(etxtFirstName)){
			isValid = false;
			mErrorMsg += "\n" + getString(R.string.error_req_first_name);
		}
		
		if(isEmpty(etxtLastName)){
			isValid = false;
			mErrorMsg += "\n"+ getString(R.string.error_req_last_name);
		}
		
		if(isEmpty(etxtEmail)){
			isValid = false;
			mErrorMsg += "\n"+getString(R.string.error_req_email);
		}else{
			if(!isValidEmailFormat(etxtEmail.getText().toString())){
				isValid = false;
				mErrorMsg += "\n"+getString(R.string.error_email_format);
			}else{
				//check if email exists
				if(AccountService.IsUserExists(etxtEmail.getText().toString())){
					isValid = false;
					mErrorMsg += "\n"+getString(R.string.error_email_dup);
				}
			}
		}
		
		if(isEmpty(etxtPassword)){
			isValid = false;
			mErrorMsg += "\n"+getString(R.string.error_req_pass);
		}
		
		if(isNotMinLength(etxtPassword, 4)){
			isValid = false;
			mErrorMsg += "\n"+getString(R.string.error_pass_min_length);
		}
		
		return isValid;
	}
	
	private void displayErrors(){
		Toast.makeText(mContext, mErrorMsg, Toast.LENGTH_LONG).show();
		//after displaying error, reset error msgs
		mErrorMsg = "Please enter your:";
	}
	
	//listener methods
	OnClickListener submitFormOnClick = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			
			if(validateForm()){
				RegistrationFormDataHolder form = new RegistrationFormDataHolder(etxtFirstName.getText().toString(), etxtLastName.getText().toString(), etxtEmail.getText().toString(), "", etxtPassword.getText().toString());
				registrationTask = new RegisterUserTask();
				registrationTask.attach(RegistrationActivity.this);
				registrationTask.execute(form);
				initiateRegistrationState();
				/*
				if(status == RegisterStatus.FAILED_EXISTS){
					Toast.makeText(mContext, "Email exists", Toast.LENGTH_SHORT);
				}else{
					Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT);
				}*/
			}else{
				displayErrors();
			}
		}
		
	};
	
	//Async Task
	private class RegisterUserTask extends AsyncTask<RegistrationFormDataHolder, Integer, RegisterStatus>{
		RegistrationActivity activity = null;
		
		@Override
		protected RegisterStatus doInBackground(RegistrationFormDataHolder... args) {
			// TODO Auto-generated method stub
			RegistrationFormDataHolder form = args[0];
			return AccountService.Register(form.email, form.password, form.firstName, form.lastName, form.alias, AccountType.DEFAULT);
		}
		
		@Override
		protected void onPostExecute(RegisterStatus result) {
			activity.afterRegistration(result);
		}
		
		@Override
		protected void onCancelled(){
			//closeDialog();
			Log.d("RegistrationActivity", "OnCancelled, dismiss dialog");
		}
		
		void detach(){
			activity = null;
		}
		
		void attach(RegistrationActivity activity){
			this.activity = activity;
		}
	}
	
	static class RegistrationActivityDataHolder{
		RegisterUserTask task;
	}
	
	static class RegistrationFormDataHolder{
		String firstName, lastName, email, alias, password;
		public RegistrationFormDataHolder(String fname, String lname, String email, String alias, String pw){
			this.firstName = fname;
			this.lastName = lname;
			this.email = email;
			this.alias = alias;
			this.password = pw;
		}
	}
	
	//activity events
	@Override
	public Object onRetainNonConfigurationInstance(){
		final RegistrationActivityDataHolder data = new RegistrationActivityDataHolder();
		if(registrationTask != null){
			registrationTask.detach();
			data.task = registrationTask;
		}
		return data;
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	//ui helpers
	//update UI elements: disable register btn, display modal
	private void initiateRegistrationState(){
		dialog = ProgressDialog.show(mContext, "", "Registering", true);
	}
	
	//restore UI state: enable register btn, dismiss modal
	private void restoreDefaultState(){
		if(dialog != null)
			dialog.dismiss();
		
		Intent i = new Intent(mContext, StatusFeedActivity.class);
		startActivity(i);
	}
	
	private void afterRegistration(RegisterStatus status){
		restoreDefaultState();
	}
	
	//helpers
	private boolean isEmpty(EditText txtBox){
		return txtBox.length() == 0 ;
	}
	
	private boolean isNotMinLength(EditText txtBox, int length){
		return txtBox.length() < length;
	}
	
	private boolean isValidEmailFormat(String email){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
