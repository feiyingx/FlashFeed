package com.snapperfiche.data;

import java.util.Date;

import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Utility;

public class Post {
	private int id;
	private String post_type;
	private String username;
	private String photo_file_name;
	private String caption;
	private String created_at;
	private boolean is_private;
	private int num_comments;
	private int num_likes;
	private boolean is_like;
	private boolean is_favorite;
	private String locality;
	private String admin_area;
	private int num_answers;
	
	public Post() {}
	
	public int getId(){
		return id;
	}
	
	public Enumerations.PostType getPostType(){
		return Enumerations.PostType.getPostType(post_type);
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPhotoUrl(){
		return Utility.GetAbsoluteUrl(photo_file_name.replace(".jpg", "_original.jpg"));
	}
	
	public String getPhotoThumbUrl(){
		return Utility.GetAbsoluteUrl(photo_file_name.replace(".jpg", "_thumb.jpg"));
	}
	
	public String getPhotoFileName(){
		String[] parts = photo_file_name.split("/");
		int length = parts.length;
		if(length > 0)
			return parts[parts.length-1];
		return "";
	}
	
	public String getCaption(){
		return caption;
	}
	
	public Date getDate(){
		return Utility.GetDate(created_at);
	}
	
	public boolean getIsPrivate(){
		return is_private;
	}
	
	public int getNumComments(){
		return num_comments;
	}
	
	public int getNumLikes(){
		return num_likes;
	}
	
	public boolean isLike(){
		return is_like;
	}
	
	public boolean isFav(){
		return is_favorite;
	}
	
	public String getLocality(){
		return locality;
	}
	
	public String getAdminArea(){
		return admin_area;
	}
	
	public int getNumAnswers(){
		return num_answers;
	}
}
