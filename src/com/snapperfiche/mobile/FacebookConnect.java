package com.snapperfiche.mobile;

import com.easy.facebook.android.apicall.GraphApi;
import com.easy.facebook.android.data.User;
import com.easy.facebook.android.error.EasyFacebookError;
import com.easy.facebook.android.facebook.FBLoginManager;
import com.easy.facebook.android.facebook.Facebook;
import com.easy.facebook.android.facebook.LoginListener;
import com.snapperfiche.code.AppConfig;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Enumerations.AccountType;
import com.snapperfiche.webservices.AccountService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class FacebookConnect extends Activity implements LoginListener{

	private FBLoginManager fbManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            shareFacebook();
    }

    
    public void shareFacebook() {
    
    //change the permissions according to the function you want to use 
            String permissions[] = { "user_about_me", "email", "offline_access" };

            //change the parameters with those of your application
            fbManager = new FBLoginManager(this, R.layout.login,
                            AppConfig.FB_appId, permissions);

            if (fbManager.existsSavedFacebook()) {
                    fbManager.loadFacebook();
            } else {
                    fbManager.login();
            }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            fbManager.loginSuccess(data);
    }

    public void loginFail() {
            fbManager.displayToast("Login failed!");

    }

    public void logoutSuccess() {
            fbManager.displayToast("Logout success!");
    }

    public void loginSuccess(Facebook facebook) {
            
            //library use example
            GraphApi graphApi = new GraphApi(facebook);

            //this is a facebook user object, it's different from our site user object
            User user = new User();
            try {
                    user = graphApi.getMyAccountInfo();
                    if(user != null){
                    	//check if this user already exists in our system, if so, then log them in and set their cookie
                    	//else create an account for them on our site with their login
                    	String userEmail = user.getEmail();
                    	if(AccountService.IsUserExists(userEmail)){
                    		Enumerations.LoginStatus status = AccountService.Login(userEmail, AppConfig.FB_defaultPw);
                    		//this probably means the user email already exists as a normal account and not as a fb account
                    		//so have the user log in using their site account and pw
                    		if(status == Enumerations.LoginStatus.FAILED){
                    			
                    		}else{
                    			Intent i = new Intent(getApplicationContext(), StatusFeedActivity.class);
                    			startActivity(i);
                    		}
                    	}else{
                    		Enumerations.RegisterStatus status = AccountService.Register(userEmail, AppConfig.FB_defaultPw, user.getFirst_name(), user.getLast_name(), "", AccountType.FACEBOOK);
                    		if(status == Enumerations.RegisterStatus.SUCCESS){
                    			Intent i = new Intent(getApplicationContext(), StatusFeedActivity.class);
                    			startActivity(i);
                    		}else{
                    			
                    		}
                    	}
                    }
            } catch (EasyFacebookError e) {
                    e.toString();
            }
    }

}
