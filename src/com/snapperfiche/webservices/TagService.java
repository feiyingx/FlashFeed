package com.snapperfiche.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Utility;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.data.Tag;
import com.snapperfiche.data.User;

public class TagService extends BaseService{
	public static List<Tag> GetAllTags(boolean forceCacheReload){
		List<Tag> tags = new ArrayList<Tag>();
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			HttpGet getTags = new HttpGet(Constants.GetTagsUrl);
			try {
				HttpResponse response = GetHttpClient().execute(getTags);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement tagsJson = resultJson.get(Constants.jsonParameter_Tags);
					
					Gson gson = new Gson();
					
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						Tag[] userTags = gson.fromJson(tagsJson, Tag[].class);
						tags = new ArrayList<Tag>(Arrays.asList(userTags));
					}else if(status == Enumerations.BasicStatus.NO_RESULTS.value()){
						
					}else if(status == Enumerations.BasicStatus.ERROR_NOT_AUTHENTICATED.value()){
						
					}else{ //TODO: error case
						
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tags;
	}
	
	public static BasicStatus AddTag(String name){
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			HttpPost newTag = new HttpPost(Constants.NewTagUrl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(Constants.requestParameter_Name, name));
			
			try {
				newTag.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				HttpResponse response = GetHttpClient().execute(newTag);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					
					Gson gson = new Gson();
					
					int status = gson.fromJson(statusJson, int.class);
					return BasicStatus.getStatus(status);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return BasicStatus.ERROR_NOT_AUTHENTICATED;
	}
}
