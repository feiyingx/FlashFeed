package com.snapperfiche.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.User;
import com.snapperfiche.data.User.Friend;

public class FriendService extends BaseService {
	public static List<Friend> GetFriends(boolean forceCacheReload){
		List<Friend> friends = new ArrayList<Friend>();
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			HttpGet getFriends = new HttpGet(Constants.GetFriendsUrl);
			try {
				HttpResponse response = GetHttpClient().execute(getFriends);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement friendsJson = resultJson.get(Constants.jsonParameter_Friends);
					
					Gson gson = new Gson();
					
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						Friend[] userFriends = gson.fromJson(friendsJson, Friend[].class);
						friends = new ArrayList<Friend>(Arrays.asList(userFriends));
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
		return friends;
	}
	
	public static List<User> GetPendingFriends(int userId){
		return null;
	}
	
	public static List<User> GetRequestedFriends(int userId){
		return null;
	}
	
	public static void Accept(int friendId){

	}
	
	public static void Reject(int friendId){
		
	}
	
	public static void AddFriend(int friendId){
		
	}
	
	public static void RemoveFriend(int friendId){
		
	}
	
	public static boolean IsFriend(int friendId){
		return false;
	}
}
