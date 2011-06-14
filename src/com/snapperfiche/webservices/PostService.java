package com.snapperfiche.webservices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.location.Address;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Utility;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.code.Enumerations.PostType;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.QuestionPost;
import com.snapperfiche.data.Tag;
import com.snapperfiche.data.User;

public class PostService extends BaseService {
	private static BasicStatus NewPost(Enumerations.PostType postType, String caption, String imgUrl, Address address, int[] friends_ids, int[] tags_ids, boolean isPrivate){
		User currentUser = AccountService.getUser();
		if(currentUser != null && address != null){
			String tagsJsonString = "";
			Gson gson = new Gson();
			
			if(tags_ids != null){
				tagsJsonString = gson.toJson(tags_ids);
			}
			
			String friendsJsonString = "";
			if(friends_ids != null){
				friendsJsonString = gson.toJson(friends_ids);
			}
			
			//Uri imgUri = Uri.parse(imgUrl);
			//get address info from address object
			double latitude = 10; //address.getLatitude();
			double longitude = 11; //address.getLongitude();
			String locality = "Cerritos"; //address.getLocality();
			String adminArea = "CA"; //address.getAdminArea();
			String countryCode = "US"; address.getCountryCode();
			
			
			File pic = new File(imgUrl);
	        pic = pic.getAbsoluteFile();
	        boolean exists = pic.exists();
	        
	        FileBody bin = new FileBody(pic, "image/jpeg");
	        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	        
	        reqEntity.addPart(Constants.requestParameter_Photo, bin);
	        
	        try {
				String userId = String.valueOf(currentUser.getId());
				String sLatitude = String.valueOf(latitude);
				String sLongitude = String.valueOf(longitude);
				String privacy = isPrivate ? "1" : "0";
				reqEntity.addPart(Constants.requestParameter_UserId, new StringBody(userId));
		        reqEntity.addPart(Constants.requestParameter_Caption, new StringBody(caption));
		        reqEntity.addPart(Constants.requestParameter_PostType, new StringBody(postType.value()));
		        reqEntity.addPart(Constants.requestParameter_Latitude, new StringBody(sLatitude));
		        reqEntity.addPart(Constants.requestParameter_Longitude, new StringBody(sLongitude));
		        reqEntity.addPart(Constants.requestParameter_Locality, new StringBody(locality));
		        reqEntity.addPart(Constants.requestParameter_AdminArea, new StringBody(adminArea));
		        reqEntity.addPart(Constants.requestParameter_CountryCode, new StringBody(countryCode));
		        reqEntity.addPart(Constants.requestParameter_IsPrivate, new StringBody(privacy));
		        if(!Utility.IsNullOrEmpty(tagsJsonString)){
		        	reqEntity.addPart(Constants.requestParameter_Tags, new StringBody(tagsJsonString));
		        }
		        
		        if(!Utility.IsNullOrEmpty(friendsJsonString)){
		        	reqEntity.addPart(Constants.requestParameter_Friends, new StringBody(friendsJsonString));
		        }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				String url = Constants.NewDefaultPost;
				if(postType == PostType.QUESTION)
					url = Constants.NewQuestion;
				HttpPost post = new HttpPost(url);
				post.setEntity(reqEntity);
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
	
	public static BasicStatus Post(String caption, String imgUrl, Address address, int[] friends_ids, int[] tags_ids, boolean isPrivate){
		return NewPost(PostType.DEFAULT, caption, imgUrl, address, friends_ids, tags_ids, isPrivate);
	}
	
	public static BasicStatus AskQuestion(String question, String imgUrl, Address address, int[] friends_ids, boolean isPrivate){
		return NewPost(PostType.QUESTION, question, imgUrl, address, friends_ids, null, isPrivate);
	}
	
	public static BasicStatus AnswerQuestion(int questionId, String answer, String answerImgUrl, Address address, int[] friends_ids, int[] tags_ids, boolean isPrivate){
		
		User currentUser = AccountService.getUser();
		if(currentUser != null && address != null && questionId > 0){
			String tagsJsonString = "";
			Gson gson = new Gson();
			
			if(tags_ids != null){
				tagsJsonString = gson.toJson(tags_ids);
			}
			
			String friendsJsonString = "";
			if(friends_ids != null){
				friendsJsonString = gson.toJson(friends_ids);
			}
			
			//Uri imgUri = Uri.parse(imgUrl);
			//get address info from address object
			double latitude = 10; //address.getLatitude();
			double longitude = 11; //address.getLongitude();
			String locality = "Cerritos"; //address.getLocality();
			String adminArea = "CA"; //address.getAdminArea();
			String countryCode = "US"; //address.getCountryCode();
			
			
			File pic = new File(answerImgUrl);
	        pic = pic.getAbsoluteFile();
	        boolean exists = pic.exists();
	        
	        FileBody bin = new FileBody(pic, "image/jpeg");
	        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	        
	        reqEntity.addPart(Constants.requestParameter_Photo, bin);
	        
	        try {
				String userId = String.valueOf(currentUser.getId());
				String sLatitude = String.valueOf(latitude);
				String sLongitude = String.valueOf(longitude);
				String privacy = String.valueOf(isPrivate);
				reqEntity.addPart(Constants.requestParameter_UserId, new StringBody(userId));
		        reqEntity.addPart(Constants.requestParameter_Caption, new StringBody(answer));
		        reqEntity.addPart(Constants.requestParameter_PostType, new StringBody(Enumerations.PostType.ANSWER.value()));
		        reqEntity.addPart(Constants.requestParameter_Latitude, new StringBody(sLatitude));
		        reqEntity.addPart(Constants.requestParameter_Longitude, new StringBody(sLongitude));
		        reqEntity.addPart(Constants.requestParameter_Locality, new StringBody(locality));
		        reqEntity.addPart(Constants.requestParameter_AdminArea, new StringBody(adminArea));
		        reqEntity.addPart(Constants.requestParameter_CountryCode, new StringBody(countryCode));
		        reqEntity.addPart(Constants.requestParameter_IsPrivate, new StringBody(privacy));
		        if(!Utility.IsNullOrEmpty(tagsJsonString)){
		        	reqEntity.addPart(Constants.requestParameter_Tags, new StringBody(tagsJsonString));
		        }
		        
		        if(!Utility.IsNullOrEmpty(friendsJsonString)){
		        	reqEntity.addPart(Constants.requestParameter_Friends, new StringBody(friendsJsonString));
		        }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				String url = Utility.GetAddAnswerUrl(questionId);
				if(Utility.IsNullOrEmpty(url)){
					return BasicStatus.ERROR;
				}
				HttpPost post = new HttpPost(url);
				post.setEntity(reqEntity);
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
	
	public static List<Post> GetLatestPosts(){
		List<Post> posts = new ArrayList<Post>();
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			HttpGet getPosts = new HttpGet(Constants.UserLatestPosts);
			try {
				HttpResponse response = GetHttpClient().execute(getPosts);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					//JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement postsJson = resultJson.get(Constants.jsonParameter_Posts);
					
					Gson gson = new Gson();
					Post[] userPosts = gson.fromJson(postsJson, Post[].class);
					/*
					for(int i = 0; i < userPosts.length; i++){
						Post p = userPosts[i];
						int id = p.getId();
						Enumerations.PostType postType = p.getPostType();
						Date time = p.getDate();
						String caption = p.getCaption();
					}*/
					posts = new ArrayList<Post>(Arrays.asList(userPosts));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return posts;
	}
	
	public static Post GetPostById(int id, boolean forceCacheRefresh){
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			String cacheKey = "GetPostById_" + String.valueOf(id);
			Post cacheData = (Post)SimpleCache.get(cacheKey);
			if(!forceCacheRefresh && cacheData != null){
				return cacheData;
			}
			
			String postDetailUrl = Utility.GetPostDetailUrl(id);
			if(Utility.IsNullOrEmpty(postDetailUrl))
				return null;
			HttpGet getPost = new HttpGet(postDetailUrl);
			try {
				HttpResponse response = GetHttpClient().execute(getPost);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement postJson = resultJson.get(Constants.jsonParameter_Post);
					
					Gson gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						Post userPost = gson.fromJson(postJson, Post.class);
						SimpleCache.put(cacheKey, userPost);
						return userPost;
					}else if(status == Enumerations.BasicStatus.NO_RESULTS.value()){
						
					}else if(status == Enumerations.BasicStatus.ERROR_NOT_AUTHENTICATED.value()){
						
					}else{ //error case
						
					}
					/*
					for(int i = 0; i < userPosts.length; i++){
						Post p = userPosts[i];
						int id = p.getId();
						Enumerations.PostType postType = p.getPostType();
						Date time = p.getDate();
						String caption = p.getCaption();
					}*/
					//posts = new ArrayList<Post>(Arrays.asList(userPosts));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	//TODO: implement getQuestionsByUser
	public static List<QuestionPost> GetQuestionsByUser(int userId, int pageSize, int pageNum){
		List<QuestionPost> questions = new ArrayList<QuestionPost>();
		User currentUser = AccountService.getUser();
		if(currentUser == null || userId < 0)
			return questions;
		
		HttpGet getQuestions = new HttpGet(Utility.GetQuestionsByUserUrl(currentUser.getId()));
		try {
			HttpResponse response = GetHttpClient().execute(getQuestions);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				InputStream stream = entity.getContent();
				String jsonResultString = Utility.ConvertStreamToString(stream);
				entity.consumeContent();
				
				JsonParser parser = new JsonParser();
				JsonElement resultElement = parser.parse(jsonResultString);
				JsonObject resultJson = resultElement.getAsJsonObject();
				JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
				JsonElement questionsJson = resultJson.get(Constants.jsonParameter_Questions);
				
				Gson gson = new Gson();
				QuestionPost[] questionPosts = gson.fromJson(questionsJson, QuestionPost[].class);

				questions = new ArrayList<QuestionPost>(Arrays.asList(questionPosts));
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return questions;
	}
	
	public static List<Post> GetAnswersForQuestion(int questionId){
		List<Post> answers = new ArrayList<Post>();
		User currentUser = AccountService.getUser();
		if(currentUser != null && questionId > 0){
			HttpGet getAnswers = new HttpGet(Utility.GetAnswersByQuestionUrl(questionId));
			try {
				HttpResponse response = GetHttpClient().execute(getAnswers);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement answersJson = resultJson.get(Constants.jsonParameter_Answers);
					
					Gson gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						Post[] answerPosts = gson.fromJson(answersJson, Post[].class);
						answers = new ArrayList<Post>(Arrays.asList(answerPosts));
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
		return answers;
	}
	
	public static List<Post> GetQuestionsFromFriends(int userId){
		List<Post> questionsFromFriends = new ArrayList<Post>();
		User currentUser = AccountService.getUser();
		if(currentUser != null && userId > 0 && userId == currentUser.getId()){
			HttpGet getQuestions = new HttpGet(Constants.GetQuestionsFromFriendsUrl);
			try {
				HttpResponse response = GetHttpClient().execute(getQuestions);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement questionsJson = resultJson.get(Constants.jsonParameter_Questions);
					
					Gson gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						Post[] answerPosts = gson.fromJson(questionsJson, Post[].class);
						questionsFromFriends = new ArrayList<Post>(Arrays.asList(answerPosts));
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
		return questionsFromFriends;
	}
	
	public static String getCacheKey_GetGlobalFeed(int daysAgo){
		SimpleDateFormat dtFormat = new SimpleDateFormat("MM_dd_yyyy");
		return "GetGloablFeed_" + dtFormat.format(new Date()) + "_" + String.valueOf(daysAgo);
	}
	
	public static List<Post> GetGlobalFeed(int daysAgo, boolean forceCacheRefresh){
		List<Post> posts = new ArrayList<Post>();
		if(AccountService.getUser() != null){
			SimpleDateFormat dtFormat = new SimpleDateFormat("MM_dd_yyyy");
			String cacheKey = getCacheKey_GetGlobalFeed(daysAgo);
			Object cacheData = SimpleCache.get(cacheKey);
			if(!forceCacheRefresh && cacheData != null){
				posts = (List<Post>)cacheData;
			}else{
				String url = Utility.GetGlobalFeedByDayUrl(daysAgo);
				if(!Utility.IsNullOrEmpty(url)){
					HttpGet getGlobalFeed = new HttpGet(url);
					try {
						HttpResponse response = GetHttpClient().execute(getGlobalFeed);
						HttpEntity entity = response.getEntity();
						if(entity != null){
							InputStream stream = entity.getContent();
							String jsonResultString = Utility.ConvertStreamToString(stream);
							entity.consumeContent();
							
							JsonParser parser = new JsonParser();
							JsonElement resultElement = parser.parse(jsonResultString);
							JsonObject resultJson = resultElement.getAsJsonObject();
							JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
							JsonElement postsJson = resultJson.get(Constants.jsonParameter_Posts);
							
							Gson gson = new Gson();
							int status = gson.fromJson(statusJson, int.class);
							
							if(status == Enumerations.BasicStatus.SUCCESS.value()){
								Post[] feed = gson.fromJson(postsJson, Post[].class);
								posts = new ArrayList<Post>(Arrays.asList(feed));
								SimpleCache.put(cacheKey, posts);
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
			}
		}
		return posts;
	}
	
	public static BasicStatus SetFavorite(int postId){
		if(AccountService.getUser() != null){
			String favUrl = Utility.GetSaveFavPostUrl(postId);
			if(Utility.IsNullOrEmpty(favUrl)) return BasicStatus.ERROR;
			
			HttpPost favRequest = new HttpPost(favUrl);
			try {
				HttpResponse response = GetHttpClient().execute(favRequest);
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
	
	public static BasicStatus UndoFavorite(int postId){
		if(AccountService.getUser() != null){
			String unfavUrl = Utility.GetRemoveFavPostUrl(postId);
			if(Utility.IsNullOrEmpty(unfavUrl)) return BasicStatus.ERROR;
			
			HttpDelete unfavRequest = new HttpDelete(unfavUrl);
			try {
				HttpResponse response = GetHttpClient().execute(unfavRequest);
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
	
	public static List<Post> GetPostsByTagId(int tagId, boolean forceCacheRefresh){
		User currentUser = AccountService.getUser();
		List<Post> posts = new ArrayList<Post>();
		if(currentUser != null){
			String cacheKey = "GetPostByTagId_" + String.valueOf(tagId);
			List<Post> cacheData = (List<Post>)SimpleCache.get(cacheKey);
			if(!forceCacheRefresh && cacheData != null){
				return cacheData;
			}
			
			String url = Utility.GetPostsByTagUrl(tagId);
			if(Utility.IsNullOrEmpty(url))
				return null;
			HttpGet getPosts = new HttpGet(url);
			try {
				HttpResponse response = GetHttpClient().execute(getPosts);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					JsonParser parser = new JsonParser();
					JsonElement resultElement = parser.parse(jsonResultString);
					JsonObject resultJson = resultElement.getAsJsonObject();
					JsonElement statusJson = resultJson.get(Constants.jsonParameter_Status);
					JsonElement postsJson = resultJson.get(Constants.jsonParameter_Posts);
					
					Gson gson = new Gson();
					int status = gson.fromJson(statusJson, int.class);
					
					if(status == Enumerations.BasicStatus.SUCCESS.value()){
						Post[] userPosts = gson.fromJson(postsJson, Post[].class);
						posts = new ArrayList<Post>(Arrays.asList(userPosts));
						SimpleCache.put(cacheKey, posts);
					}else if(status == Enumerations.BasicStatus.NO_RESULTS.value()){
						
					}else if(status == Enumerations.BasicStatus.ERROR_NOT_AUTHENTICATED.value()){
						
					}else{ //error case
						
					}
					/*
					for(int i = 0; i < userPosts.length; i++){
						Post p = userPosts[i];
						int id = p.getId();
						Enumerations.PostType postType = p.getPostType();
						Date time = p.getDate();
						String caption = p.getCaption();
					}*/
					//posts = new ArrayList<Post>(Arrays.asList(userPosts));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return posts;
	}
 }
