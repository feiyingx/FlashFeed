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
	public static final String PostDetail = BaseUrl + "posts/";
	public static final String NewAnswer = BaseUrl + "answer/";
	public static final String GetUserQuestions_Format = BaseUrl + "questions/:id.json";
	public static final String NewQuestion = BaseUrl + "questions.json";
	public static final String GetAnswersUrl_Format = BaseUrl + "post/:id/answers.json";
	public static final String GetQuestionsFromFriendsUrl = BaseUrl + "questions/friends.json";
	
	/* Comment urls */
	public static final String NewComment_Format = BaseUrl + "comments/:post_id.json";
	
	/* Follow urls */
	
	/* Friend urls */
	public static final String GetFriendsByUser_Format = BaseUrl + "friends/:user_id.json";
	
	/* Group urls */
	public static final String NewGroup = BaseUrl + "groups.json";
	public static final String GetGroupsByUser_Format = BaseUrl + "groups/byuser/:id/:group_type.json";
	
	/* json parameters, route keys, querystring keys, request key */
	public static final String urlParameter_Email = "email";
	public static final String urlParameter_Password = "password";
	
	public static final String jsonParameter_User = "user";
	public static final String jsonParameter_Status = "status";
	public static final String jsonParameter_Posts = "posts";
	public static final String jsonParameter_Post = "post";
	public static final String jsonParameter_Groups = "groups";
	public static final String jsonParameter_Friends = "friends";
	public static final String jsonParameter_Questions = "questions";
	public static final String jsonParameter_Answers = "answers";
	
	public static final String requestParameter_Photo = "photo";
	public static final String requestParameter_UserId = "user_id";
	public static final String requestParameter_Caption = "caption";
	public static final String requestParameter_PostType = "post_type";
	public static final String requestParameter_Longitude = "longitude";
	public static final String requestParameter_Latitude = "latitude";
	public static final String requestParameter_Locality = "locality"; //city
	public static final String requestParameter_AdminArea = "admin_area"; //state
	public static final String requestParameter_CountryCode = "country";
	public static final String requestParameter_Tags = "tags";
	public static final String requestParameter_Friends = "friends";
	public static final String requestParameter_QuestionId = "question_id";
	public static final String requestParameter_PostId = "post_id";
	public static final String requestParameter_Content = "content";
	//for groups
	public static final String requestParameter_Name = "name";
	public static final String requestParameter_GroupType = "group_type";
	public static final String requestParameter_GroupMembers = "group_members";
}
