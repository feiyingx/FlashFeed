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
	EditText etxtFirstName, etxtLastName, etxtEmail, etxtAlias, etxtPassword;
	Button btnRegister;
	boolean rfvFirstName = false;
	boolean rfvLastName = false;
	boolean rfvEmail = false;
	boolean rfvPassword = false;
	Context mContext;
	TextView txtReqFirstName, txtReqLastName, txtReqEmail, txtReqPassword, txtPassMin, txtEmailDup, txtEmailFormat;
	RegisterUserTask registrationTask;
	ProgressDialog dialog;
	
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
		etxtAlias = (EditText) findViewById(R.id.reg_etxt_username);
		etxtPassword = (EditText) findViewById(R.id.reg_etxt_password);
		btnRegister = (Button) findViewById(R.id.reg_btn_register);
		txtReqFirstName = (TextView) findViewById(R.id.reg_txtFnameReq);
		txtReqLastName = (TextView) findViewById(R.id.reg_txtLnameReq);
		txtReqEmail = (TextView) findViewById(R.id.reg_txtEmailReq);
		txtReqPassword = (TextView) findViewById(R.id.reg_txtPassReq);
		txtEmailDup = (TextView) findViewById(R.id.reg_txtEmailExists);
		txtPassMin = (TextView) findViewById(R.id.reg_txtPassShort);
		txtEmailFormat = (TextView) findViewById(R.id.reg_txtEmailFormat);
		btnRegister.setOnClickListener(submitFormOnClick);
		
		final RegistrationActivityDataHolder dataHolder = (RegistrationActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.task != null){
				registrationTask = dataHolder.task;
				initiateRegistrationState();
				registrationTask.attach(this);
			}
		}
	}
	
	//listener methods
	OnClickListener submitFormOnClick = new OnClickListener(){

		@Override
		public void onClick(View v) {
			boolean isValid = true;
			if(isEmpty(etxtFirstName)){
				isValid = false;
				txtReqFirstName.setVisibility(EditText.VISIBLE);
			}else{
				txtReqFirstName.setVisibility(EditText.GONE);
			}
			if(isEmpty(etxtLastName)){
				isValid = false;
				txtReqLastName.setVisibility(EditText.VISIBLE);
			}else
				txtReqLastName.setVisibility(EditText.GONE);
			
			if(isEmpty(etxtEmail)){
				isValid = false;
				txtReqEmail.setVisibility(EditText.VISIBLE);
			}else{
				txtReqEmail.setVisibility(EditText.GONE);
				if(!isValidEmailFormat(etxtEmail.getText().toString())){
					isValid = false;
					txtEmailFormat.setVisibility(EditText.VISIBLE);
				}else{
					txtEmailFormat.setVisibility(EditText.GONE);
				}
			}
			
			if(isEmpty(etxtPassword)){
				isValid = false;
				txtReqPassword.setVisibility(EditText.VISIBLE);
			}
			else
				txtReqPassword.setVisibility(EditText.GONE);
			
			if(isNotMinLength(etxtPassword, 4)){
				isValid = false;
				txtPassMin.setVisibility(EditText.VISIBLE);
			}else
				txtPassMin.setVisibility(EditText.GONE);
			
			if(isValid){
				RegistrationFormDataHolder form = new RegistrationFormDataHolder(etxtFirstName.getText().toString(), etxtLastName.getText().toString(), etxtEmail.getText().toString(), etxtAlias.getText().toString(), etxtPassword.getText().toString());
				registrationTask = new RegisterUserTask();
				registrationTask.execute(form);
				initiateRegistrationState();
				/*
				if(status == RegisterStatus.FAILED_EXISTS){
					Toast.makeText(mContext, "Email exists", Toast.LENGTH_SHORT);
				}else{
					Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT);
				}*/
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
