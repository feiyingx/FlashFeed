package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.Post;
import com.snapperfiche.data.Tag;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.GroupService;
import com.snapperfiche.webservices.PostService;
import com.snapperfiche.webservices.SimpleCache;

import android.app.Activity;
import android.location.Address;
import android.os.Bundle;

public class TestActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Post p13 = PostService.GetPostById(13);
		
		AccountService.Login("bigfiche@fiche.com", "asdf");
		//p13 = PostService.GetPostById(13);
		//String caption = p13.getCaption();
		
		//test new post
		Address address = new Address(new Locale("en"));
		
		List<Tag> tags = new ArrayList<Tag>();
		Tag t1 = new Tag(3);
		Tag t2 = new Tag(4);
		tags.add(t1);
		tags.add(t2);
		
		int[] friends = new int[2];
		friends[0] = 1;
		friends[1] = 2;
		
		String tagsJsonString = "";
		//Gson gson = new Gson();
		//tagsJsonString = gson.toJson(tags);
		
		int[] tags2 = new int[2];
		tags2[0] = 3;
		tags2[1] = 4;
		
		//System.out.println(tagsJsonString);
		//PostService.Post("test cap", "postImg.jpg", address, friends, tags2);
		
		//PostService.AskQuestion("what are you doing?", "questionImage.jpg", address, friends, tags2);
		//PostService.AnswerQuestion(32, "i'm reading", "answerImage.jpg", address, friends, tags2);
		
		//int[] group_members = new int[2];
		//group_members[0] = 1;
		//group_members[1] = 2;
		//BasicStatus status = GroupService.CreateGroup("yum", GroupType.USER_FEED, group_members);
		//System.out.println(status.toString());
		List<Group> groups = GroupService.GetGroups(3, GroupType.USER_FEED);
		System.out.println(groups);
		String groupCacheKey = "coolGroupsCached";
		SimpleCache.put(groupCacheKey, groups);
		List<Group> groupsFromCache = (List<Group>)SimpleCache.get(groupCacheKey);
		System.out.println(groupsFromCache);
	}
}
