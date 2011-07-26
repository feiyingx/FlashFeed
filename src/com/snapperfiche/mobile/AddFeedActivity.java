package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.code.Utility;
import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.data.User;
import com.snapperfiche.data.User.Friend;
import com.snapperfiche.mobile.custom.BaseActivity;
import com.snapperfiche.webservices.FriendService;
import com.snapperfiche.webservices.GroupService;

public class AddFeedActivity extends BaseActivity {
	ViewSwitcher mVsLoading;
	ListView mLvFriends, mLvSelected;
	EditText mEtxtName;
	Button mBtnAdd;
	List<Friend> mFriends;
	boolean[] mSelectedStates;
	List<Integer> mSelectedFriendIds;
	List<String> mSelectedNames;
	AddFeedAsyncTask mAddTask;
	GetFriendsAsyncTask mGetFriendsTask;
	Context mContext = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_feed_layout);
		
		//find controls from layout
		mVsLoading = (ViewSwitcher)findViewById(R.id.vs_loading);
		mLvFriends = (ListView)findViewById(R.id.lv_friends);
		mLvSelected = (ListView)findViewById(R.id.lv_selected);
		mEtxtName = (EditText)findViewById(R.id.etxt_name);
		mBtnAdd = (Button)findViewById(R.id.btn_add);
		
		mSelectedFriendIds = new ArrayList<Integer>();
		mSelectedNames = new ArrayList<String>();
		
		mLvSelected.setAdapter(new ArrayAdapter<String>(this, R.layout.friend_selected_row, mSelectedNames));
		mLvSelected.setDivider(null);
		mLvSelected.setDividerHeight(0);
	}
	
	//helpers
	private void loadFriends(List<Friend> friends){
		mFriends = friends;
	}
	
	private void onComplete_addFeed(){
		Intent i = new Intent(mContext, StatusFeedActivity.class);
		startActivity(i);
	}
	
	//events
	OnItemClickListener onItemClick_selectFriend = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,long id) {
			SelectFriendAdapterViewHolder holder = (SelectFriendAdapterViewHolder)v.getTag();
			if(holder != null){
				//setting the new checked value to the opposite of what it was before
				boolean isChecked = !holder.cbSelect.isChecked();
				holder.cbSelect.setChecked(isChecked);
				mSelectedStates[position] = isChecked;
				
				if(isChecked){
					mSelectedNames.add(holder.txtName.getText().toString());
					mSelectedFriendIds.add(holder.friend.getId());
					((ArrayAdapter)mLvSelected.getAdapter()).notifyDataSetChanged();
					if(mSelectedNames.size() > 0){
						mLvSelected.setVisibility(ListView.VISIBLE);
						mBtnAdd.setEnabled(mEtxtName.getText().length() > 0);
					}
				}else{
					mSelectedNames.remove(holder.txtName.getText().toString());
					mSelectedFriendIds.remove(new Integer(holder.friend.getId()));
					((ArrayAdapter)mLvSelected.getAdapter()).notifyDataSetChanged();
					if(mSelectedNames.size() == 0){
						mLvSelected.setVisibility(ListView.GONE);
						mBtnAdd.setEnabled(false);
					}
				}
			}
		}
		
	};
	
	OnClickListener onClick_add = new OnClickListener(){

		@Override
		public void onClick(View view) {
			String groupName = mEtxtName.getText().toString();
			int size = mSelectedFriendIds.size();
			int[] userIds = new int[size];
			for(int i = 0; i < size; i++){
				userIds[i] = mSelectedFriendIds.get(i);
			}
			
			if(!Utility.IsNullOrEmpty(groupName) && size > 0){
				mAddTask = new AddFeedAsyncTask();
				mAddTask.execute(new NewGroupParam(userIds, groupName));
			}
		}
		
	};
	
	//classes
	private class NewGroupParam{
		int[] userIds;
		String groupName;
		public NewGroupParam(int[] userIds, String groupName){
			this.userIds = userIds;
			this.groupName = groupName;
		}
	}
	
	//async task class to add feed
	private class AddFeedAsyncTask extends AsyncTask<NewGroupParam, Integer, Void>{
		AddFeedActivity activity = null;
		@Override
		protected Void doInBackground(NewGroupParam... params) {
			GroupService.CreateGroup(params[0].groupName, GroupType.USER, params[0].userIds);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			
		}
		
		void attach(AddFeedActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	//async task class to retrieve friends
	private class GetFriendsAsyncTask extends AsyncTask<Void, Integer, List<Friend>>{
		AddFeedActivity activity = null;
		
		@Override
		protected List<Friend> doInBackground(Void... params) {
			return FriendService.GetFriends(false);
		}
		
		@Override
		protected void onPostExecute(List<Friend> result){
			
		}
		
		void attach(AddFeedActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	private class SelectFriendAdapterViewHolder{
		Friend friend;
		WebImageView imgView;
		TextView txtName;
		CheckBox cbSelect;
	}
	
	public class SelectFriendAdapter extends BaseAdapter{
		LayoutInflater mInflater;
		List<Friend> mFriends;
		
		public SelectFriendAdapter(Context c, List<Friend> friends){
			mInflater = LayoutInflater.from(c);
			mFriends = friends;
			if(friends != null && mSelectedStates == null){
				int size = friends.size();
				mSelectedStates = new boolean[size]; //values will default to false
			}
		}
		
		@Override
		public int getCount() {
			if(mFriends != null) return mFriends.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mFriends != null) return mFriends.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(mFriends != null) return mFriends.get(position).getId();
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Friend friend = (Friend)getItem(position);
			if(friend == null) return null;
			
			SelectFriendAdapterViewHolder holder;
			if(convertView == null){				
				convertView = mInflater.inflate(R.layout.user_tagger_row, parent, false);
				TextView txtName = (TextView)convertView.findViewById(R.id.txt_name);
				CheckBox cbSelect = (CheckBox)convertView.findViewById(R.id.cb_select);
				holder = new SelectFriendAdapterViewHolder();
				//holder.user = u;
				holder.txtName = txtName;
				holder.cbSelect = cbSelect;
			}else{
				holder = (SelectFriendAdapterViewHolder)convertView.getTag();
			}
			holder.friend = friend;
			holder.txtName.setText(friend.getEmail());
			holder.cbSelect.setClickable(false);
			boolean isChecked = mSelectedStates[position];
			holder.cbSelect.setChecked(isChecked);
			convertView.setTag(holder);
	
			return convertView;
		}
		
	}
}
