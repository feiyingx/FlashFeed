package com.snapperfiche.webservices;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Utility;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.data.Post;

public class LikeService extends BaseService{
	public static BasicStatus Like(int postId){
		if(AccountService.getUser() != null){
			String likeUrl = Utility.GetLikePostUrl(postId);
			if(Utility.IsNullOrEmpty(likeUrl)) return BasicStatus.ERROR;
			
			HttpPost likeRequest = new HttpPost(likeUrl);
			try {
				HttpResponse response = GetHttpClient().execute(likeRequest);
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
	
	public static BasicStatus Unlike(int postId){
		if(AccountService.getUser() != null){
			String unlikeUrl = Utility.GetUnlikePostUrl(postId);
			if(Utility.IsNullOrEmpty(unlikeUrl)) return BasicStatus.ERROR;
			
			HttpDelete unlikeRequest = new HttpDelete(unlikeUrl);
			try {
				HttpResponse response = GetHttpClient().execute(unlikeRequest);
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
