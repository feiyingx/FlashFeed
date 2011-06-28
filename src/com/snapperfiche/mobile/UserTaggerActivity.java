package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.User;
import com.snapperfiche.data.User.Friend;
import com.snapperfiche.mobile.PostDetailActivity.PostDetailActivityDataHolder;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.FriendService;
import com.snapperfiche.webservices.GroupService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

public class UserTaggerActivity extends Activity{
	ListView mLvFriends, mLvGroups, mLvSelected;
	ViewSwitcher mVsFriends, mVsGroups;
	CheckBox mCbCreateGroup;
	EditText mEtxtGroupName;
	Button mBtnFriendsTab, mBtnGroupsTab, mBtnDone;
	ViewFlipper mVfTabs;
	List<Friend> mFriends;
	List<Group> mGroups;
	Context mContext = this;
	GetFriendDataAsyncTask mGetFriendsTask;
	GetGroupDataAsyncTask mGetGroupsTask;
	AddGroupAsyncTask mAddGroupTask;
	List<String> mSelectedNames;
	List<Integer> mSelectedUsers, mSelectedGroups;
	boolean[] selectedStates_friends, selectedStates_groups;
	int mTabIndex = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_tagger_layout);
        
		mSelectedNames = new ArrayList<String>();
		mSelectedUsers = new ArrayList<Integer>();
		mSelectedGroups = new ArrayList<Integer>();
		
		mBtnFriendsTab = (Button)findViewById(R.id.btn_friends_tab);
		mBtnGroupsTab = (Button)findViewById(R.id.btn_groups_tab);
		
		mVfTabs = (ViewFlipper)findViewById(R.id.vf_content);
		
        mLvFriends = (ListView)findViewById(R.id.lv_friends);
        mLvGroups = (ListView)findViewById(R.id.lv_groups);
        mLvSelected = (ListView)findViewById(R.id.lv_selected);
        
        mVsFriends = (ViewSwitcher)findViewById(R.id.vs_friends_loading);
        mVsGroups = (ViewSwitcher)findViewById(R.id.vs_groups_loading);
        
        mCbCreateGroup = (CheckBox)findViewById(R.id.cb_create_group);
        mEtxtGroupName = (EditText)findViewById(R.id.etxt_group_name);
        mBtnDone = (Button)findViewById(R.id.btn_done);
        
        /* load from dataholder if there is data */
		final UserTaggerActivityDataHolder dataHolder = (UserTaggerActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.mFriends != null){
				mFriends = dataHolder.mFriends;
				loadResults(0);
			}
			
			if(dataHolder.mGroups != null){
				mGroups = dataHolder.mGroups;
				loadResults(1);
			}
			
			if(dataHolder.mSelectedUsers != null){
				mSelectedUsers = dataHolder.mSelectedUsers;
			}
			
			if(dataHolder.mSelectedGroups != null){
				mSelectedGroups = dataHolder.mSelectedGroups;
			}
			
			if(dataHolder.mSelectedNames != null){
				mSelectedNames = dataHolder.mSelectedNames;
				if(mSelectedNames.size() > 0)
					mLvSelected.setVisibility(ListView.VISIBLE);
			}
			
			if(dataHolder.selectedStates_friends != null){
				selectedStates_friends = dataHolder.selectedStates_friends;
			}
						
			if(dataHolder.selectedStates_groups != null){
				selectedStates_groups = dataHolder.selectedStates_groups;
			}
			
			mTabIndex = dataHolder.mTabIndex;
			
			if(dataHolder.mAddGroupTask != null){
				dataHolder.mAddGroupTask.attach(this);
				mAddGroupTask = dataHolder.mAddGroupTask;
				if(mAddGroupTask.getStatus() == Status.PENDING){
					mAddGroupTask.execute();
	        	}else if(mAddGroupTask.getStatus() == Status.FINISHED){
	        		onComplete_addGroup(BasicStatus.SUCCESS);
	        	}
			}
		}
        
        if(dataHolder != null && dataHolder.mGetFriendsTask != null){
        	dataHolder.mGetFriendsTask.attach(this);
        	mGetFriendsTask = dataHolder.mGetFriendsTask;
        	if(mGetFriendsTask.getStatus() == Status.PENDING){
        		mGetFriendsTask.execute();
        	}else if(mGetFriendsTask.getStatus() == Status.FINISHED){
        		loadResults(0);
        	}
        }else{
	        mGetFriendsTask = new GetFriendDataAsyncTask();
	        mGetFriendsTask.attach(this);
	        mGetFriendsTask.execute();
        }
        
        if(dataHolder != null && dataHolder.mGetGroupsTask != null){
        	dataHolder.mGetGroupsTask.attach(this);
        	mGetGroupsTask = dataHolder.mGetGroupsTask;
        	if(mGetGroupsTask.getStatus() == Status.PENDING){
        		mGetGroupsTask.execute();
        	}else if(mGetGroupsTask.getStatus() == Status.FINISHED){
        		loadResults(1);
        	}
        }else{
        	mGetGroupsTask = new GetGroupDataAsyncTask();
        	mGetGroupsTask.attach(this);
        	mGetGroupsTask.execute();
        }
        
        
        mLvSelected.setAdapter(new ArrayAdapter<String>(this, R.layout.friend_selected_row, mSelectedNames));
		mLvSelected.setDivider(null);
		mLvSelected.setDividerHeight(0);
        
		mVfTabs.setDisplayedChild(mTabIndex);
		
        //bind events to views
        mCbCreateGroup.setOnCheckedChangeListener(onCheckedChangeListener_cbCreateGroup);
        mBtnFriendsTab.setOnClickListener(onClick_openFriendsTab);
        mBtnGroupsTab.setOnClickListener(onClick_openGroupsTab);
        mEtxtGroupName.addTextChangedListener(tw_groupName);
        mBtnDone.setOnClickListener(onClick_done);
        mBtnDone.setEnabled(mSelectedNames.size() > 0);
        
        //display toast to inform users
        Toast.makeText(this, "Please select the people you want to send your question to", Toast.LENGTH_LONG).show();
	}
	
	//events
	@Override
	public Object onRetainNonConfigurationInstance(){
		if(mGetFriendsTask != null) mGetFriendsTask.detach();
		if(mGetGroupsTask != null) mGetGroupsTask.detach();
		if(mAddGroupTask != null) mAddGroupTask.detach();
		UserTaggerActivityDataHolder dataHolder = new UserTaggerActivityDataHolder();
		dataHolder.mFriends = mFriends;
		dataHolder.mGroups = mGroups;
		dataHolder.mGetFriendsTask = mGetFriendsTask;
		dataHolder.mSelectedNames = mSelectedNames;
		dataHolder.mSelectedUsers = mSelectedUsers;
		dataHolder.selectedStates_friends = selectedStates_friends;
		dataHolder.selectedStates_groups = selectedStates_groups;
		dataHolder.mTabIndex = mTabIndex;
		dataHolder.mAddGroupTask = mAddGroupTask;
		return dataHolder;
	}
	
	OnCheckedChangeListener onCheckedChangeListener_cbCreateGroup = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton checkBoxView, boolean isChecked) {
			boolean isUsersSelected = mSelectedNames.size() > 0;
			if(isChecked){
				mBtnDone.setEnabled(mEtxtGroupName.getText().length() > 0 && isUsersSelected);
				mEtxtGroupName.setVisibility(EditText.VISIBLE);
			}else{
				mBtnDone.setEnabled(true && isUsersSelected);
				mEtxtGroupName.setVisibility(EditText.GONE);
			}
		}
		
	};
		
	OnItemClickListener onItemClick_selectUser = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			UserTaggerAdapterViewHolder holder = (UserTaggerAdapterViewHolder)v.getTag();
			if(holder != null){
				//setting the new checked value to the opposite of what it was before
				boolean isChecked = !holder.cbSelect.isChecked();
				holder.cbSelect.setChecked(isChecked);
				selectedStates_friends[position] = isChecked;
				
				if(isChecked){
					mSelectedNames.add(holder.txtName.getText().toString());
					mSelectedUsers.add(holder.user.getId());
					((ArrayAdapter)mLvSelected.getAdapter()).notifyDataSetChanged();
					if(mSelectedNames.size() > 0){
						mLvSelected.setVisibility(ListView.VISIBLE);
						mBtnDone.setEnabled(true);
					}
					//((ArrayAdapter) mLvSelected.getAdapter()).add(holder.txtName.getText());
				}else{
					mSelectedNames.remove(holder.txtName.getText().toString());
					mSelectedUsers.remove(new Integer(holder.user.getId()));
					((ArrayAdapter)mLvSelected.getAdapter()).notifyDataSetChanged();
					if(mSelectedNames.size() == 0){
						mLvSelected.setVisibility(ListView.GONE);
						mBtnDone.setEnabled(false);
					}
					//((ArrayAdapter) mLvSelected.getAdapter()).remove(holder.txtName.getText());
				}
			}
		}
		
	};
	
	OnItemClickListener onItemClick_selectGroup = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			GroupTaggerAdapterViewHolder holder = (GroupTaggerAdapterViewHolder)v.getTag();
			if(holder != null){
				boolean isChecked = !holder.cbSelect.isChecked();
				holder.cbSelect.setChecked(isChecked);
				selectedStates_groups[position] = isChecked;
				
				if(isChecked){
					mSelectedNames.add("'"+holder.txtGroupName.getText().toString()+"'");
					mSelectedGroups.add(holder.group.getId());
					((ArrayAdapter)mLvSelected.getAdapter()).notifyDataSetChanged();
					if(mSelectedNames.size() > 0){
						mLvSelected.setVisibility(ListView.VISIBLE);
						mBtnDone.setEnabled(true);
					}
				}else{
					mSelectedNames.remove("'"+holder.txtGroupName.getText().toString()+"'");
					mSelectedGroups.remove(new Integer(holder.group.getId()));
					((ArrayAdapter)mLvSelected.getAdapter()).notifyDataSetChanged();
					if(mSelectedNames.size() == 0){
						mLvSelected.setVisibility(ListView.GONE);
						mBtnDone.setEnabled(false);
					}
				}
			}
			
		}
		
	};
	
	OnClickListener onClick_openFriendsTab = new OnClickListener(){
		@Override
		public void onClick(View v) {
			mVfTabs.setDisplayedChild(0);
			mTabIndex = 0;
		}
	};
	
	OnClickListener onClick_openGroupsTab = new OnClickListener(){
		@Override
		public void onClick(View v) {
			mVfTabs.setDisplayedChild(1);
			mTabIndex = 1;
		}
	};
	
	OnClickListener onClick_done = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int numUserIds = mSelectedUsers.size();
			int numGroupIds = mSelectedGroups.size();
			int[] userIds = new int[numUserIds];
			int[] groupIds = new int[numGroupIds];
			for(int i = 0; i < numUserIds; i++){
				userIds[i] = mSelectedUsers.get(i);
			}
			for(int i = 0; i < numGroupIds; i++){
				groupIds[i] = mSelectedGroups.get(i);
			}
			
			if(mCbCreateGroup.isChecked()){
				mAddGroupTask = new AddGroupAsyncTask();
				mAddGroupTask.attach(UserTaggerActivity.this);
				mAddGroupTask.execute(new NewGroupParam(userIds, mEtxtGroupName.getText().toString()));
			}
			
			Intent i = new Intent(mContext, CameraActivity.class);
			i.putExtra("user_ids", userIds);
			i.putExtra("group_ids", groupIds);
			startActivity(i);
		}
		
	};
	
	TextWatcher tw_groupName = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			boolean isUsersSelected = mSelectedNames.size() > 0;
			if(mCbCreateGroup.isChecked())
				mBtnDone.setEnabled(s.length() > 0 && isUsersSelected);
			else
				mBtnDone.setEnabled(true && isUsersSelected);
		}
		
	};
	
	//helpers
	private void loadResults(int loadType){
		switch(loadType){
			case 0:
				List<User> convertedUsers = new ArrayList<User>();
				int length = mFriends.size();
				for(int i = 0; i < length; i++){
					convertedUsers.add((User)mFriends.get(i));
				}
				mLvFriends.setAdapter(new UserTaggerAdapter(mContext, convertedUsers, 0));
				mLvFriends.setOnItemClickListener(onItemClick_selectUser);
				if(mVsFriends.getDisplayedChild() == 0)
					mVsFriends.showNext();
				break;
			case 1:
				mLvGroups.setAdapter(new GroupTaggerAdapter(mContext, mGroups));
				//if 'selectedStates' already exists, that means this is a data refresh
				//if its a data refresh, we got to make sure the selectedStates array is updated to the new size
				int newSize = mGroups.size();
				if(selectedStates_groups != null && selectedStates_groups.length != newSize){
					boolean[] temp_states = new boolean[newSize];
					for(int i = 0; i < newSize && i < selectedStates_groups.length; i++){
						temp_states[i] = selectedStates_groups[i];
					}
					selectedStates_groups = temp_states;
				}
				mLvGroups.setOnItemClickListener(onItemClick_selectGroup);
				if(mVsGroups.getDisplayedChild() == 0)
					mVsGroups.showNext();
				break;
			default:
				break;
		}
	}
	
	private void onComplete_addGroup(Enumerations.BasicStatus status){
		if(status == Enumerations.BasicStatus.SUCCESS){
        	mGetGroupsTask = new GetGroupDataAsyncTask();
        	mGetGroupsTask.attach(UserTaggerActivity.this);
        	mGetGroupsTask.execute();
			
			mEtxtGroupName.setText("");
			mCbCreateGroup.setChecked(false);
		}
	}
	
	//classes
	private class NewGroupParam{
		int[] userIds;
		String groupName;
		public NewGroupParam(int[] userIds, String groupName){
			this.userIds = userIds;
			this.groupName = groupName;
		}
	}
	
	private class UserTaggerActivityDataHolder{
		GetFriendDataAsyncTask mGetFriendsTask;
		GetGroupDataAsyncTask mGetGroupsTask;
		List<Integer> mSelectedUsers, mSelectedGroups;
		List<Friend> mFriends;
		List<Group> mGroups;
		List<String> mSelectedNames;
		boolean[] selectedStates_friends, selectedStates_groups;
		int mTabIndex;
		AddGroupAsyncTask mAddGroupTask;
	}
	
	public class GetFriendDataAsyncTask extends AsyncTask<Void, Integer, List<Friend>>{
		int mLoadType;
		UserTaggerActivity activity = null;
		public GetFriendDataAsyncTask(){
			mLoadType = 0;
		}
		
		@Override
		protected List<Friend> doInBackground(Void... params) {
			return FriendService.GetFriends(false);
		}
		
		@Override
		protected void onPostExecute(List<Friend> result){
			this.activity.mFriends = result;
			this.activity.loadResults(mLoadType);
		}
		
		void attach(UserTaggerActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	public class GetGroupDataAsyncTask extends AsyncTask<Void, Integer, List<Group>>{
		UserTaggerActivity activity = null;
		@Override
		protected List<Group> doInBackground(Void... params) {
			return GroupService.GetGroups(AccountService.getUser().getId(), Enumerations.GroupType.USER);
		}
		
		@Override
		protected void onPostExecute(List<Group> result){
			this.activity.mGroups = result;
			this.activity.loadResults(1);
		}
		
		void attach(UserTaggerActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	public class AddGroupAsyncTask extends AsyncTask<NewGroupParam, Integer, Enumerations.BasicStatus>{
		UserTaggerActivity activity = null;
		@Override
		protected Enumerations.BasicStatus doInBackground(NewGroupParam... params) {
			return GroupService.CreateGroup(params[0].groupName, Enumerations.GroupType.USER, params[0].userIds);
		}
		
		@Override
		protected void onPostExecute(Enumerations.BasicStatus result){
			this.activity.onComplete_addGroup(result);
		}
		
		void attach(UserTaggerActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	private class UserTaggerAdapterViewHolder{
		User user;
		WebImageView imgView;
		TextView txtName;
		CheckBox cbSelect;
	}
	
	private class GroupTaggerAdapterViewHolder{
		Group group;
		TextView txtGroupName;
		CheckBox cbSelect;
	}
	
	public class UserTaggerAdapter extends BaseAdapter {
		List<User> mUsers;
		Context mContext;
		LayoutInflater mInflater;
		int loadType;
		public UserTaggerAdapter(Context c, List<User> u, int loadType){
			mContext = c;
			mUsers = u;
			mInflater = LayoutInflater.from(c);
			this.loadType = loadType;
			if(u != null){
				switch(loadType){
					case 0:
						if(selectedStates_friends == null) selectedStates_friends = new boolean[u.size()];
						break;
					default:
						break;
				}
			}
		}
		
		@Override
		public int getCount() {
			if(mUsers != null) return mUsers.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mUsers != null && position < mUsers.size()) return mUsers.get(position); 
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(mUsers != null && position < mUsers.size()){
				return mUsers.get(position).getId();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UserTaggerAdapterViewHolder holder;
			//repull the user from our list every time, because the user that is
			//stored in the viewholder could have been recycled to another row's user, 
			//and it will be the wrong user
			User u = (User)getItem(position);
			if(u == null)
				return null;
			
			if(convertView == null){				
				convertView = mInflater.inflate(R.layout.user_tagger_row, parent, false);
				TextView txtName = (TextView)convertView.findViewById(R.id.txt_name);
				CheckBox cbSelect = (CheckBox)convertView.findViewById(R.id.cb_select);
				holder = new UserTaggerAdapterViewHolder();
				//holder.user = u;
				holder.txtName = txtName;
				holder.cbSelect = cbSelect;
			}else{
				holder = (UserTaggerAdapterViewHolder)convertView.getTag();
			}
			holder.user = u;
			holder.txtName.setText(u.getEmail());
			holder.cbSelect.setClickable(false);
			boolean isChecked = selectedStates_friends[position];
			holder.cbSelect.setChecked(isChecked);
			convertView.setTag(holder);
			return convertView;
		}
		
	}
	
	public class GroupTaggerAdapter extends BaseAdapter{
		Context mContext;
		LayoutInflater mInflater;
		List<Group> mGroups;
		public GroupTaggerAdapter(Context c, List<Group> g){
			mContext = c;
			mGroups = g;
			mInflater = LayoutInflater.from(c);
			if(g != null && selectedStates_groups == null){
				selectedStates_groups = new boolean[g.size()];
			}
		}
		
		@Override
		public int getCount() {
			if(mGroups != null) return mGroups.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mGroups != null) return mGroups.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(mGroups != null){
				Group tempGroup = mGroups.get(position);
				if(tempGroup != null) return tempGroup.getId();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Group group = (Group)getItem(position);
			if(group == null)
				return null;
			
			GroupTaggerAdapterViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.group_tagger_row, parent, false);
				TextView txtGroupName = (TextView)convertView.findViewById(R.id.txt_group_name);
				CheckBox cbSelect = (CheckBox)convertView.findViewById(R.id.cb_select);
				holder = new GroupTaggerAdapterViewHolder();
				holder.txtGroupName = txtGroupName;
				holder.cbSelect = cbSelect;
			}else{
				holder = (GroupTaggerAdapterViewHolder)convertView.getTag();
			}
			holder.group = group;
			holder.txtGroupName.setText(group.getName());
			holder.cbSelect.setChecked(selectedStates_groups[position]);
			convertView.setTag(holder);
			return convertView;
		}
		
	}
}
