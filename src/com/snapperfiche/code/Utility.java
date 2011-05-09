package com.snapperfiche.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
	
	public static Bitmap GetImageBitmapFromUrl(String url){
		InputStream contentStream = null;
		try{
			URL imgUrl = new URL(url);
			contentStream = (InputStream) imgUrl.getContent();
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return BitmapFactory.decodeStream(contentStream);
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
		
		return Constants.PostDetail + String.valueOf(id) + ".json";
	}
	
	public static String GetAddAnswerUrl(int questionId){
		String url = "";
		if(questionId <= 0)
			return url;
		
		return Constants.NewAnswer + String.valueOf(questionId) + ".json";
	}
}
