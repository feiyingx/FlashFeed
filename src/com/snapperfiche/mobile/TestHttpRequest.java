package com.snapperfiche.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestHttpRequest extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_http_request);
		
		TextView responseView = (TextView) findViewById(R.id.response);
		
		String postUrl = "http://api.geonames.org/citiesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo";
		
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(postUrl);
		
		try {
			HttpResponse response = client.execute(httpPost);
			InputStream responseStream = response.getEntity().getContent();
			String jsonResponse = inputStreamToString(responseStream);
			
			//responseView.setText(inputStreamToString(responseStream));
			
			JSONObject json = new JSONObject(jsonResponse);
			
			JSONArray nameArray = json.names();
			JSONArray valArray = json.toJSONArray(nameArray);
			String result = "";
			/*for (int i=0; i < valArray.length(); i++) {
				result += (String.format("%s: %s\n", nameArray.getString(i), valArray.getString(i)));
			}*/
			
			
			//result = (String.format("%s: %s\n", nameArray.getString(0), valArray.getJSONArray(0)));
			
			//JSONArray geonames = json.getJSONArray("geonames");
			//JSONObject geonames = valArray.getJSONObject(0);
			
			//JSONArray geoNames = geonames.names();
			//JSONArray geoValues = geonames.toJSONArray(geoNames);
			
			JSONArray geonames = valArray.getJSONArray(0);
			
			//result = String.format("%s: %s\n", geoNames.getString(0), geoValues.getString(0));
			result = String.format("%d: %s\n", 0, geonames.getString(0));
			//String fcodeName = geonames.getString("fcodeName");
			responseView.setText(result);
			
		} catch (ClientProtocolException e) {
			
		} catch (IOException e) {
			
		} catch (JSONException e) {
			responseView.setText("Error");
		}
	}
	
	private String inputStreamToString(InputStream is) {
		try {
			String line = "";
			StringBuilder total = new StringBuilder();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
			is.close();
			return total.toString();	
		} catch (Exception ex) {
			return "Error";
		}
	}

}
