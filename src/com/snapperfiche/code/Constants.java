package com.snapperfiche.code;

public class Constants {
	// use this baseurl to test on emulator and localhost
	//public static final String BaseUrl = "http://10.0.2.2:3000/";
	// use this baseurl to test on device and localhost, this url depends on the computer that is running the localhost server, it makes use of the computer's internal ip within the network
	public static final String BaseUrl = "http://192.168.1.4:3000/";
	
	/* Account urls */
	public static final String LoginUrl = BaseUrl + "sessions.json";
	public static final String LogoutUrl = BaseUrl + "signout.json";
	public static final String IsAuthenticatedUrl = BaseUrl + "sessions/authenticated.json";
	
	/* Post urls */
	public static final String PostsIndexUrl = BaseUrl + "posts.json";
	public static final String NewDefaultPost = BaseUrl + "posts.json";
	public static final String UserLatestPosts = BaseUrl + "posts/latest.json";
	
	/* Follow urls */
	
	/* json parameters, route keys, querystring keys, request key */
	public static final String urlParameter_Email = "email";
	public static final String urlParameter_Password = "password";
	
	public static final String jsonParameter_User = "user";
	public static final String jsonParameter_Status = "status";
	public static final String jsonParameter_Posts = "posts";
	
	public static final String requestParameter_Photo = "photo";
	public static final String requestParameter_UserId = "user_id";
	public static final String requestParameter_Caption = "caption";
	public static final String requestParameter_PostType = "post_type";
	public static final String requestParameter_Longitude = "longitude";
	public static final String requestParameter_Latitude = "latitude";
	public static final String requestParameter_Locality = "locality"; //city
	public static final String requestParameter_AdminArea = "admin_area"; //state
	public static final String requestParameter_CountryCode = "country";
}
