package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.User;
import com.snapperfiche.data.User.Friend;
import com.snapperfiche.mobile.FriendTaggerActivity.ViewHolder;
import com.snapperfiche.mobile.StatusFeed.FeedGroupItemViewHolder;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.FriendService;
import com.snapperfiche.webservices.GroupService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddUserGroupActivity extends Activity {
	private List<Friend> mUsers;
	private Context mContext = this;
	EditText txtGroupName;
	private List<Friend> mSelectedUsers;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_group_layout);
        
        mSelectedUsers = new ArrayList<Friend>();
        
        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.add_existing_group_tab);
        spec.setIndicator("My Circles");
        tabs.addTab(spec);
        
        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.add_new_group_tab);
        spec.setIndicator("New Circle");
        tabs.addTab(spec);
        
        txtGroupName = (EditText) findViewById(R.id.txt_add_group_name);
        
        Button btnAddGroupConfirm = (Button) findViewById(R.id.btn_add_group_confirm);
        btnAddGroupConfirm.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v) {
        		int count = mSelectedUsers.size();
        		int[] userIds = new int[count];
        		for(int i = 0; i < count; i++){
        			userIds[i] = mSelectedUsers.get(i).getId();
        		}
				GroupService.CreateGroup(txtGroupName.getText().toString(), GroupType.USER_FEED, userIds);
				Intent i = new Intent(mContext, StatusFeed.class);
	    		startActivity(i);
			}
        });
        
        mUsers = FriendService.GetFriends(false);
        ListView lvFriends = (ListView) findViewById(R.id.lv_new_group_friends);
        lvFriends.setAdapter(new FriendItemAdapter(this, mUsers));
		
		lvFriends.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				FriendItemViewHolder holder = (FriendItemViewHolder) view.getTag();
				Boolean currentState = (Boolean) rowState.get(position);
				rowState.set(position, !currentState);
				if((Boolean)rowState.get(position) == false){
					view.setBackgroundColor(Color.BLACK);
					mSelectedUsers.remove(mUsers.get(position));
					//((ArrayAdapter) mlvSelectedFriends.getAdapter()).remove(holder.text2.getText());
				}
				else{
					view.setBackgroundColor(Color.MAGENTA);
					mSelectedUsers.add(mUsers.get(position));
					//((ArrayAdapter) mlvSelectedFriends.getAdapter()).add(holder.text2.getText());
				}
			}});
		
        ListView lvExistingGroups = (ListView) findViewById(R.id.lv_existing_group);
	}
	
	private static List<Boolean> rowState = new ArrayList<Boolean>();
	static class FriendItemViewHolder{
		User userData;
		TextView text;
        TextView text2;
        int backgroundColor;
	}
	
	public class FriendItemAdapter extends BaseAdapter{
    	private LayoutInflater mInflater;
    	private List<Friend> users = new ArrayList<Friend>();
    	public FriendItemAdapter(Context c, List<Friend> u){
    		this.users = u;
    		mInflater = LayoutInflater.from(c);
    		for(int i = 0; i < users.size(); i++){
				rowState.add(false);
			}
    	}
    	
		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			if(position <= users.size()){
				return users.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(position <= users.size()){
				return users.get(position).getId();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FriendItemViewHolder holder;
			User u = (User) users.get(position);
			if(u == null)
				return null;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.friend_tagger_row, null);
				holder = new FriendItemViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.friend_tagger_row_img_text);
				holder.text2 = (TextView) convertView.findViewById(R.id.friend_tagger_row_name_text);
				//holder.backgroundColor = Color.BLACK;
				holder.userData = u;
				convertView.setTag(holder);
			}else{
				holder = (FriendItemViewHolder) convertView.getTag();
			}
			
			holder.text.setText(String.valueOf(u.getId()));
			holder.text2.setText(u.getEmail());
			
			if((Boolean) rowState.get(position) == false)
				convertView.setBackgroundColor(Color.BLACK);
			else
				convertView.setBackgroundColor(Color.MAGENTA);
			//convertView.setBackgroundColor(holder.backgroundColor);
			return convertView;
		}
    	
    }
}
