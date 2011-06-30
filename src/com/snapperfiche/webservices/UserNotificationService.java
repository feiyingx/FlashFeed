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
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.User;
import com.snapperfiche.data.UserNotification;
import com.google.gson.Gson;

public class UserNotificationService extends BaseService{
	public static List<UserNotification> GetReceivedNotifications(boolean forceCacheRefresh){
		List<UserNotification> notifications = new ArrayList<UserNotification>();
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			String cacheKey = "GetReceivedNotifications_" + String.valueOf(currentUser.getId());
			List<UserNotification> cacheData = (List<UserNotification>)SimpleCache.get(cacheKey);
			if(!forceCacheRefresh && cacheData != null){
				return cacheData;
			}
			
			String url = Utility.GetReceivedNotificationsUrl(currentUser.getId());
			if(Utility.IsNullOrEmpty(url))
				return null;
			HttpGet getNotifications = new HttpGet(url);
			try {
				HttpResponse response = GetHttpClient().execute(getNotifications);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement notificationsJson = resultJson.get(Constants.jsonParameter_Notifications);
					
					Gson gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						UserNotification[] receivedNotifications = gson.fromJson(notificationsJson, UserNotification[].class);
						notifications = new ArrayList<UserNotification>(Arrays.asList(receivedNotifications));
						SimpleCache.put(cacheKey, notifications);
					}else if(status == Enumerations.BasicStatus.NO_RESULTS.value()){
						
					}else if(status == Enumerations.BasicStatus.ERROR_NOT_AUTHENTICATED.value()){
						
					}else{ //error case
						
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
		
		return notifications;
	}
	
	public static Enumerations.BasicStatus SendNotificationsToCommunity(Enumerations.NotificationType notificationType, Enumerations.AudienceType type, int[] userIds){
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			String audienceType = String.valueOf(type.value());
			String selectUserIdsJsonString = "";
			String fromUserId = String.valueOf(currentUser.getId());
			String sNotificationType = String.valueOf(notificationType.value());
			Gson gson = null;
			if(type == Enumerations.AudienceType.SELECT && userIds != null){
				gson = new Gson();
				selectUserIdsJsonString = gson.toJson(userIds);
			}
			
			HttpPost postNotifications = new HttpPost(Constants.NewNotificationsToCommunityUrl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(Constants.requestParameter_FromUserId, fromUserId));
			nvps.add(new BasicNameValuePair(Constants.requestParameter_AudienceType, audienceType));
			nvps.add(new BasicNameValuePair(Constants.requestParameter_UserIds, selectUserIdsJsonString));
			nvps.add(new BasicNameValuePair(Constants.requestParameter_NotificationType, sNotificationType));
			
			try {
				postNotifications.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			try {
				HttpResponse response = GetHttpClient().execute(postNotifications);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					
					if(gson == null)
						gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					return Enumerations.BasicStatus.getStatus(status);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Enumerations.BasicStatus.ERROR;
	}
	
	public static Enumerations.BasicStatus UpdateNotificationsViewed(int[] notificationIds){
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			String notificationIdsJsonString = "";
			Gson gson = null;
			if(notificationIds != null){
				gson = new Gson();
				notificationIdsJsonString = gson.toJson(notificationIds);
			}
			
			HttpPost updateNotifications = new HttpPost(Constants.UpdateNotificationsViewedUrl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(Constants.requestParameter_Ids, notificationIdsJsonString));
			
			try {
				updateNotifications.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			try {
				HttpResponse response = GetHttpClient().execute(updateNotifications);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					
					if(gson == null)
						gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					return Enumerations.BasicStatus.getStatus(status);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Enumerations.BasicStatus.ERROR_NOT_AUTHENTICATED;
	}
}
