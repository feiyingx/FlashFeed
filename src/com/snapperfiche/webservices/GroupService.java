package com.snapperfiche.webservices;

import java.io.File;
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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.User;

public class GroupService extends BaseService {
	public static BasicStatus CreateGroup(String groupName, GroupType groupType, int[] memberIds){
		User currentUser = AccountService.getUser();
		if(currentUser != null && groupType != GroupType.NONE && memberIds != null){
			String memberIdsJsonString = "";
			Gson gson = new Gson();
			
			memberIdsJsonString = gson.toJson(memberIds);
			String userId = String.valueOf(currentUser.getId());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(Constants.requestParameter_UserId, userId));
			nvps.add(new BasicNameValuePair(Constants.requestParameter_Name, groupName));
			nvps.add(new BasicNameValuePair(Constants.requestParameter_GroupType, groupType.value()));
			nvps.add(new BasicNameValuePair(Constants.requestParameter_GroupMembers, memberIdsJsonString));
			
			try {
				HttpPost post = new HttpPost(Constants.NewGroup);
				post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				HttpResponse response = GetHttpClient().execute(post);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					
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
		
		return BasicStatus.ERROR;
	}
	
	public static List<Group> GetGroups(int userId, GroupType groupType){
		List<Group> groups = new ArrayList<Group>();
		if(userId > 0 && groupType != GroupType.NONE){
			User currentUser = AccountService.getUser();
			if(currentUser != null){
				HttpGet getGroups = new HttpGet(Utility.GetGroupsByUserUrl(currentUser.getId(), groupType));
				try {
					HttpResponse response = GetHttpClient().execute(getGroups);
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
						if(status == BasicStatus.SUCCESS.value()){
							JsonElement groupsJson = resultJson.get(Constants.jsonParameter_Groups);
							Group[] userGroups = gson.fromJson(groupsJson, Group[].class);
							groups = new ArrayList<Group>(Arrays.asList(userGroups));
						}else if(status == BasicStatus.ERROR_NOT_AUTHENTICATED.value()){
							//TODO handle not authenticated error
						}else if(status == BasicStatus.ERROR.value()){
							//TODO handle error
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
		}
		
		return groups;
	}
}
