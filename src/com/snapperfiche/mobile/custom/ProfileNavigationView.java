package com.snapperfiche.mobile.custom;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snapperfiche.mobile.FavoritesActivity;
import com.snapperfiche.mobile.FriendActivity;
import com.snapperfiche.mobile.ProfileActivity;
import com.snapperfiche.mobile.QuestionActivity;
import com.snapperfiche.mobile.R;
import com.snapperfiche.mobile.TaggedPostsActivity;

public class ProfileNavigationView extends LinearLayout {
	
	Button btnPosts;
	Button btnQuestions;
	Button btnFriends;
	Button btnFavorites;
	Button btnTags;

	public ProfileNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.bottom_nav, this);
		
		initButtons();
		disableCurrentActivity(context);
	}
	
	private void initButtons() {
		btnPosts = (Button)findViewById(R.id.profile_layout_btn_posts);
		btnQuestions = (Button)findViewById(R.id.profile_layout_btn_questions);
		btnFriends = (Button)findViewById(R.id.profile_layout_btn_friend);
		btnFavorites = (Button)findViewById(R.id.profile_layout_btn_fav);
		btnTags = (Button)findViewById(R.id.profile_layout_btn_tags);
		
		btnPosts.setOnClickListener(postsClickListener);
		btnQuestions.setOnClickListener(questionsClickListener);
		btnFriends.setOnClickListener(friendsClickListener);
		btnFavorites.setOnClickListener(favoritesClickListener);
		btnTags.setOnClickListener(tagsClickListener);
	}
	
	private void disableCurrentActivity(Context context) {
		if ((context.getClass()).equals(ProfileActivity.class)) {
			btnPosts.setBackgroundResource(R.drawable.posts_selected);
			btnPosts.setEnabled(false);
		}
		else if ((context.getClass()).equals(QuestionActivity.class)) {
			btnQuestions.setBackgroundResource(R.drawable.questions_selected);
			btnQuestions.setEnabled(false);
		}
		else if ((context.getClass()).equals(FriendActivity.class)) {
			btnFriends.setBackgroundResource(R.drawable.friends_selected);
		}
		else if ((context.getClass()).equals(FavoritesActivity.class)) {
			btnFavorites.setBackgroundResource(R.drawable.favorites_selected);
			btnFavorites.setEnabled(false);
		}
		else if ((context.getClass()).equals(TaggedPostsActivity.class)) {
			btnTags.setBackgroundResource(R.drawable.tags_selected);
			btnTags.setEnabled(false);
		}
	}
	
	/****************************************************************/
	/** Listeners **/
	/****************************************************************/
	
	OnClickListener postsClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(getContext(), "Posts", Toast.LENGTH_SHORT).show();
		}
	};
	
	OnClickListener questionsClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//Toast.makeText(getContext(), "Questions", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getContext(), QuestionActivity.class);
			getContext().startActivity(i);
		}
	};
	
	OnClickListener friendsClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(getContext(), "Friends", Toast.LENGTH_SHORT).show();
		}
	};
	
	OnClickListener favoritesClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//Toast.makeText(getContext(), "Favorites", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getContext(), FavoritesActivity.class);
			getContext().startActivity(i);
		}
	};
	
	OnClickListener tagsClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(getContext(), "Tags", Toast.LENGTH_SHORT).show();
		}
	};
	/****************************************************************/
	
}
