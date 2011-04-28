package com.snapperfiche.webservices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Utility;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.User;

public class PostService extends BaseService {
	public static BasicStatus NewPost(String caption, String imgUrl, double latitude, double longitude, String locality, String adminArea, String countryCode){
		User currentUser = AccountService.getUser();
		if(currentUser != null){
			//Uri imgUri = Uri.parse(imgUrl);
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
				reqEntity.addPart(Constants.requestParameter_UserId, new StringBody(userId));
		        reqEntity.addPart(Constants.requestParameter_Caption, new StringBody(caption));
		        reqEntity.addPart(Constants.requestParameter_PostType, new StringBody(Enumerations.PostType.DEFAULT.value()));
		        reqEntity.addPart(Constants.requestParameter_Latitude, new StringBody(sLatitude));
		        reqEntity.addPart(Constants.requestParameter_Longitude, new StringBody(sLongitude));
		        reqEntity.addPart(Constants.requestParameter_Locality, new StringBody(locality));
		        reqEntity.addPart(Constants.requestParameter_AdminArea, new StringBody(adminArea));
		        reqEntity.addPart(Constants.requestParameter_CountryCode, new StringBody(countryCode));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				HttpPost post = new HttpPost(Constants.NewDefaultPost);
				post.setEntity(reqEntity);
				HttpResponse response = GetHttpClient().execute(post);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream stream = entity.getContent();
					String jsonResultString = Utility.ConvertStreamToString(stream);
					entity.consumeContent();
					
					Gson gson = new Gson();
					int status = gson.fromJson(jsonResultString, int.class);
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
		List<Post> posts = new ArrayList();
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
					
					for(int i = 0; i < userPosts.length; i++){
						Post p = userPosts[i];
						int id = p.getId();
						Enumerations.PostType postType = p.getPostType();
						Date time = p.getDate();
						String caption = p.getCaption();
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
		return posts;
	}
}
