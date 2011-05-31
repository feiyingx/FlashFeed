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
import com.snapperfiche.data.Comment;
import com.snapperfiche.data.Post;

public class CommentService extends BaseService {
	public static BasicStatus CreateComment(int userId, int postId, String comment){
		HttpPost post = new HttpPost(Utility.GetNewCommentUrl(postId));
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.requestParameter_UserId, String.valueOf(userId)));
		nvps.add(new BasicNameValuePair(Constants.requestParameter_PostId, String.valueOf(postId)));
		nvps.add(new BasicNameValuePair(Constants.requestParameter_Content, comment));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			HttpResponse addComment = GetHttpClient().execute(post);
			HttpEntity entity = addComment.getEntity();
			if(entity != null){				
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				JsonParser parser = new JsonParser();
				JsonElement resultElement = parser.parse(jsonResultString);
				JsonObject resultJson = resultElement.getAsJsonObject();
				JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
				
				Gson gson = new Gson();
				int status = gson.fromJson(statusJson, int.class);
				if(status == Enumerations.BasicStatus.SUCCESS.value()){
					return BasicStatus.SUCCESS;
				}else if(status == Enumerations.BasicStatus.ERROR_NOT_AUTHENTICATED.value()){
					//TODO: handle not authenticated error
					return BasicStatus.ERROR_NOT_AUTHENTICATED;
				}
				entity.consumeContent();
			}
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: handle error
		return BasicStatus.ERROR;
	}
	
	public static List<Comment> GetCommentsByPostId(int postId){
		List<Comment> comments = new ArrayList<Comment>();
		if(postId <= 0)
			return comments;
		
		HttpGet getComments = new HttpGet(Utility.GetCommentsByPostIdUrl(postId));
		try {
			HttpResponse response = GetHttpClient().execute(getComments);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				entity.consumeContent();
				
				JsonParser parser = new JsonParser();
				JsonElement resultElement = parser.parse(jsonResultString);
				JsonObject resultJson = resultElement.getAsJsonObject();
				//JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
				JsonElement commentsJson = resultJson.get(Constants.jsonParameter_Comments);
				
				Gson gson = new Gson();
				Comment[] postComments = gson.fromJson(commentsJson, Comment[].class);
				/*
				for(int i = 0; i < userPosts.length; i++){
					Post p = userPosts[i];
					int id = p.getId();
					Enumerations.PostType postType = p.getPostType();
					Date time = p.getDate();
					String caption = p.getCaption();
				}*/
				comments = new ArrayList<Comment>(Arrays.asList(postComments));
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return comments;
	}
}
