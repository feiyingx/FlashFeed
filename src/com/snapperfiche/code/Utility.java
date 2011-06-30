package com.snapperfiche.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.snapperfiche.code.Enumerations.GroupType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utility {
	public static String ConvertStreamToString(InputStream stream) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for(String line = null; (line = reader.readLine()) != null;){
			builder.append(line).append("\n");
		}
		return builder.toString();
	}
	
	public static Bitmap GetImageBitmapFromUrl(String url, String filename, Context context){
		File cacheDir = context.getCacheDir();
        if(new File(cacheDir, filename).exists()){
        	return BitmapFactory.decodeFile(new File(cacheDir, filename).getPath());
        }else{
        	InputStream contentStream = null;
    		try{
    			URL imgUrl = new URL(url);
    			contentStream = (InputStream) imgUrl.getContent();
    		}catch(MalformedURLException e){
    			e.printStackTrace();
    			return null;
    		}catch(IOException e){
    			e.printStackTrace();
    			return null;
    		}
    		/*
    		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
    		decodeOptions.inJustDecodeBounds = true;
    		*/
    		Bitmap img = BitmapFactory.decodeStream(contentStream);
        	FileOutputStream fos = null;
        	try {
				fos = new FileOutputStream(new File(cacheDir, filename));
				img.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	        	fos.flush();
	        	fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return img;
        }
	}
	
	public static String GetAbsoluteUrl(String url){
		if(url.startsWith("/")){
			url = (url.length() > 1) ? url.substring(1) : "";
		}
		return Constants.BaseUrl + url;
	}
	
	public static boolean IsNullOrEmpty(String input){
		return (input == null || input == "");
	}
	
	public static String GetPostDetailUrl(int id){
		String url = "";
		if(id <= 0)
			return url;
		
		return Constants.PostDetail_Format.replace(":id", String.valueOf(id));
	}
	
	public static String GetAddAnswerUrl(int questionId){
		String url = "";
		if(questionId <= 0)
			return url;
		
		return Constants.NewAnswer + String.valueOf(questionId) + ".json";
	}
	
	public static String GetGroupsByUserUrl(int userId, GroupType groupType){
		String url = "";
		if(userId < 0 || groupType == GroupType.NONE)
			return url;
		url = Constants.GetGroupsByUser_Format.replace(":id", String.valueOf(userId)).replace(":group_type", groupType.value());
		return url;
	}
	/*
	public static String GetFriendsByUserUrl(int userId){
		String url = "";
		if(userId < 0)
			return url;
		url = Constants.GetFriendsByUser_Format.replace(":user_id", String.valueOf(userId));
		return url;
	}*/
	
	public static String GetQuestionsByUserUrl(int userId){
		String url = "";
		if(userId < 0)
			return url;
		url = Constants.GetUserQuestions_Format.replace(":id", String.valueOf(userId));
		return url;
	}
	
	public static String GetAnswersByQuestionUrl(int questionId){
		String url = "";
		if(questionId < 0)
			return url;
		url = Constants.GetAnswersUrl_Format.replace(":id", String.valueOf(questionId));
		return url;
	}
	
	public static String GetNewCommentUrl(int postId){
		String url = "";
		if(postId < 0)
			return url;
		url = Constants.NewComment_Format.replace(":post_id", String.valueOf(postId));
		return url;
	}
	
	public static String GetCommentsByPostIdUrl(int postId){
		String url = "";
		if(postId < 0)
			return url;
		url = Constants.GetCommentsByPost_Format.replace(":post_id", String.valueOf(postId));
		return url;
	}
	
	public static String GetGlobalFeedByDayUrl(int daysAgo){
		String url = "";
		if(daysAgo < 0)
			return url;
		
		url = Constants.GetGloablFeed_Format.replace(":days_ago", String.valueOf(daysAgo));
		return url;
	}
	
	public static String GetLikePostUrl(int postId){
		String url = "";
		if(postId < 0)
			return url;
		
		url = Constants.LikePost_Format.replace(":post_id", String.valueOf(postId));
		return url;
	}
	
	public static String GetUnlikePostUrl(int postId){
		String url = "";
		if(postId < 0)
			return url;
		
		url = Constants.UnlikePost_Format.replace(":post_id", String.valueOf(postId));
		return url;
	}
	
	public static String GetSaveFavPostUrl(int postId){
		String url = "";
		if(postId < 0)
			return url;
		
		url = Constants.FavPost_Format.replace(":post_id", String.valueOf(postId));
		return url;
	}
	
	public static String GetRemoveFavPostUrl(int postId){
		String url = "";
		if(postId < 0)
			return url;
		
		url = Constants.UnfavPost_Format.replace(":post_id", String.valueOf(postId));
		return url;
	}
	
	public static String GetPostsByTagUrl(int tagId){
		String url = "";
		if(tagId < 0)
			return url;
		
		url = Constants.GetPostsByTag_Format.replace(":tag_id", String.valueOf(tagId));
		return url;
	}
	
	public static String GetReceivedNotificationsUrl(int userId){
		String url = "";
		if(userId < 0)
			return url;
		
		return Constants.GetReceivedNotifications_Format.replace(":user_id", String.valueOf(userId));
	}
}
