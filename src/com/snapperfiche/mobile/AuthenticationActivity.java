package com.snapperfiche.mobile;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.snapperfiche.webservices.AccountService;

public class AuthenticationActivity extends TabActivity {

	private TabHost tabHost;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);
        initTabs();
        
        //Intent i = new Intent(this, FavoritesActivity.class);
		//startActivity(i);
        /*
        AccountService.Login("bigfiche@fiche.com", "asdf");
		Intent i = new Intent(this, StatusFeedActivity.class);
		startActivity(i);
		*/
        /*
        Intent i = new Intent(this, FacebookConnect.class);
        startActivity(i);
        */
    }
	
	private void initTabs() {
		//TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		//tabHost.setup();
		tabHost = getTabHost();
		
		addTab(R.string.login_label_login, R.drawable.tab_info);
		addTab(R.string.login_label_signup, R.drawable.tab_info);
	}
	
	private void addTab(int labelId, int drawableId) {
		Intent i;
		if (labelId == R.string.login_label_signup) {
			i = new Intent(this, RegistrationActivity.class);
			
		}
		else {
			i = new Intent(this, LoginActivity.class);
		}
		
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);		
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		
        spec.setIndicator(tabIndicator);
        spec.setContent(i);
        tabHost.addTab(spec);
	}
}
