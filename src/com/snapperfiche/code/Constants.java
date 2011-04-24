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
	
	/* Follow urls */
	
	/* Url parameters, route keys, querystring keys */
	public static final String urlParameter_Email = "email";
	public static final String urlParameter_Password = "password";
}
