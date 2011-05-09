package com.snapperfiche.webservices;

import java.util.List;

import com.snapperfiche.data.User;

public class FriendService extends BaseService {
	public static List<User> GetFriends(int userId){
		return null;
	}
	
	public static List<User> GetPendingFriends(int userId){
		return null;
	}
	
	public static List<User> GetRequestedFriends(int userId){
		return null;
	}
	
	public static void Accept(int friendId){

	}
	
	public static void Reject(int friendId){
		
	}
	
	public static void AddFriend(int friendId){
		
	}
	
	public static void RemoveFriend(int friendId){
		
	}
	
	public static boolean IsFriend(int friendId){
		return false;
	}
}
