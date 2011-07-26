package com.snapperfiche.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Enumerations.AccountType;
import com.snapperfiche.code.Enumerations.LoginStatus;
import com.snapperfiche.code.Enumerations.RegisterStatus;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.User;


public class AccountService extends BaseService {
	private static User currentUser;
	
	public static void setUser(User u){
		currentUser = u;
	}
	
	public static User getUser(){
		return currentUser;
	}
	
	public static LoginStatus Login(String username, String password){
		HttpPost post = new HttpPost(Constants.LoginUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.urlParameter_Email, username));
		nvps.add(new BasicNameValuePair(Constants.urlParameter_Password, password));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			HttpResponse loginResponse = GetHttpClient().execute(post);
			HttpEntity entity = loginResponse.getEntity();
			if(entity != null){				
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				JsonParser parser = new JsonParser();
				JsonElement resultElement = parser.parse(jsonResultString);
				JsonObject resultJson = resultElement.getAsJsonObject();
				JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
				JsonElement userJson = resultJson.get(Constants.jsonParameter_User);
				
				Gson gson = new Gson();
				int status = gson.fromJson(statusJson, int.class);
				if(status == Enumerations.LoginStatus.SUCCESS.value()){
					User currentUser = gson.fromJson(userJson, User.class);
					setUser(currentUser);
					List<Cookie> cookies = GetHttpClient().getCookieStore().getCookies();
					return LoginStatus.SUCCESS;
				}else if(status == Enumerations.LoginStatus.FAILED.value()){
					return LoginStatus.FAILED;
				}
				entity.consumeContent();
			}
			
			return LoginStatus.FAILED;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return LoginStatus.FAILED;
	}
	
	/* Will log out the current user by killing his/her cookies */
	public static void Logout(){
		HttpDelete logoutRequest = new HttpDelete(Constants.LogoutUrl);
		try {
			HttpResponse logoutResponse = GetHttpClient().execute(logoutRequest);
			StatusLine status = logoutResponse.getStatusLine();
			currentUser = null; //set user to null
			if(status.getStatusCode() != HttpStatus.SC_OK){
				//TODO handle a bad status code
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean IsAuthenticated(){
		HttpGet checkAuthentication = new HttpGet(Constants.IsAuthenticatedUrl);
		HttpResponse response;
		boolean isAuthenticated = false;
		try {
			response = GetHttpClient().execute(checkAuthentication);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				Gson gson = new Gson();
				isAuthenticated = gson.fromJson(jsonResultString, boolean.class);
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isAuthenticated;
	}
	
	public static RegisterStatus Register(String username, String password, String firstName, String lastName, String alias, AccountType accountType){
		HttpPost post = new HttpPost(Constants.RegisterUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user[email]", username));
		nvps.add(new BasicNameValuePair("user[password]", password));
		nvps.add(new BasicNameValuePair("user[password_confirmation]", password));
		nvps.add(new BasicNameValuePair("user[alias]", alias));
		nvps.add(new BasicNameValuePair("user[accounttype]", String.valueOf(accountType.value())));
		nvps.add(new BasicNameValuePair("user[first_name]", firstName));
		nvps.add(new BasicNameValuePair("user[last_name]", lastName));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			HttpResponse registerResponse = GetHttpClient().execute(post);
			HttpEntity entity = registerResponse.getEntity();
			if(entity != null){				
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				JsonParser parser = new JsonParser();
				JsonElement resultElement = parser.parse(jsonResultString);
				JsonObject resultJson = resultElement.getAsJsonObject();
				JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
				JsonElement userJson = resultJson.get(Constants.jsonParameter_User);
				
				Gson gson = new Gson();
				int status = gson.fromJson(statusJson, int.class);
				if(status == Enumerations.RegisterStatus.SUCCESS.value()){
					User currentUser = gson.fromJson(userJson, User.class);
					setUser(currentUser);
					List<Cookie> cookies = GetHttpClient().getCookieStore().getCookies();
					return RegisterStatus.SUCCESS;
				}else if(status == Enumerations.RegisterStatus.FAILED_EXISTS.value()){
					return RegisterStatus.FAILED_EXISTS;
				}
				
				entity.consumeContent();
			}
			
			return RegisterStatus.ERROR;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return RegisterStatus.ERROR;
	}
	
	public static boolean IsUserExists(String email){
		HttpPost post = new HttpPost(Constants.UserExistsUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", email));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			HttpResponse checkResponse = GetHttpClient().execute(post);
			HttpEntity entity = checkResponse.getEntity();
			if(entity != null){				
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				JsonParser parser = new JsonParser();
				JsonElement resultElement = parser.parse(jsonResultString);
				JsonObject resultJson = resultElement.getAsJsonObject();
				JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
				entity.consumeContent();
				
				Gson gson = new Gson();
				int status = gson.fromJson(statusJson, int.class);
				if(status == Enumerations.RegisterStatus.SUCCESS.value()){
					return true;
				}				
			}			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
