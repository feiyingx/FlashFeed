package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.snapperfiche.data.User.Friend;
import com.snapperfiche.mobile.custom.SeparatedListAdapter;
import com.snapperfiche.webservices.FriendService;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FriendActivity extends Activity {
	ListView mLvFriends;
	List<Friend> mFriends;
	Context mContext = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_layout);
		
		mLvFriends = (ListView)findViewById(R.id.friend_layout_lv_friends);
		
		//start async task to get a list of friends
		GetFriendsTask task = new GetFriendsTask();
		task.attach(this);
		task.execute();
	}
	
	//helpers
	private void loadData(){
		SeparatedListAdapter adapter = new SeparatedListAdapter(mContext);
		int count = mFriends.size();
		Friend prev = null;
		List<Friend> tempList = new ArrayList<Friend>();
		String sectionName = "";
		for(int i = 0; i < count; i++){
			Friend current = mFriends.get(i);
			
			if(i == 0){
				sectionName = current.getEmail().substring(0, 1);
				tempList.add(current);
			}else{
				if(prev != null){
					//check prevName
					String prevLetter = prev.getEmail().substring(0,1);
					String currentLetter = current.getEmail().substring(0,1); 
					if(!prevLetter.equalsIgnoreCase(currentLetter)){
						adapter.addSection(sectionName, new FriendListAdapter(mContext, tempList));
						sectionName = currentLetter;
						//reset tempList
						tempList = new ArrayList<Friend>();
					}
					tempList.add(current);
					
					if(i == count - 1){
						adapter.addSection(sectionName, new FriendListAdapter(mContext, tempList));
					}
				}
			}
			prev = current;
		}
		mLvFriends.setAdapter(adapter);
	}
	
	//classes
	private class GetFriendsTask extends AsyncTask<Void, Integer, Void>{
		FriendActivity activity = null;
		
		@Override
		protected Void doInBackground(Void... params) {
			activity.mFriends = FriendService.GetFriends(false);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			activity.loadData();
		}
		
		void attach(FriendActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	static class FriendItemViewHolder{
		TextView txtName, txtTime;
		Button btnComment;
	}
	
	public class FriendListAdapter extends BaseAdapter{
		List<Friend> mFriends;
		Context mContext;
		LayoutInflater mInflater;
		
		public FriendListAdapter(Context c, List<Friend> friends){
			mContext = c;
			mFriends = friends;
			mInflater = LayoutInflater.from(c);
		}
		
		@Override
		public int getCount() {
			if(mFriends != null)
				return mFriends.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mFriends == null)
				return null;
			return mFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			if(mFriends != null)
				return ((Friend)mFriends.get(position)).getId();
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Friend friend = (Friend)getItem(position);
			FriendItemViewHolder holder;
			if(friend != null){
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.friend_list_item, null);
					TextView txtName = (TextView)convertView.findViewById(R.id.friend_list_item_txt_name);
					TextView txtTime = (TextView)convertView.findViewById(R.id.friend_list_item_txt_time);
					Button btnRemove = (Button)convertView.findViewById(R.id.friend_list_item_btn_remove);
					holder = new FriendItemViewHolder();
					holder.txtName = txtName;
					holder.txtTime = txtTime;
					holder.btnComment = btnRemove;
					convertView.setTag(holder);
				}else{
					holder = (FriendItemViewHolder)convertView.getTag();
				}
				
				holder.txtName.setText(friend.getEmail());
				holder.txtTime.setText(friend.getFriendTime());
			}
				
			return convertView;
		}
		
	}
}
