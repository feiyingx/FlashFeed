package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Tag;
import com.snapperfiche.mobile.StatusFeed.StatusFeedGalleryViewHolder;
import com.snapperfiche.mobile.custom.SectionedAdapter;
import com.snapperfiche.mobile.custom.SeparatedListAdapter;
import com.snapperfiche.webservices.SimpleCache;
import com.snapperfiche.webservices.TagService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ProfileTagsActivity extends Activity{
	Context mContext = this;
	ListView mLvTags;
	Button mBtnAdd;
	EditText mTxtName;
	List<Tag> mTags;
	//SeparatedListAdapter mAdapter;
	SectionedAdapter mAdapter2;
	GetTagsTask mTask;
	AddTagTask mAddTagTask;
	ProfileTagsActivity mActivity = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_tags_layout);
		
		mLvTags = (ListView)findViewById(R.id.profile_tags_lv_tags);
		mBtnAdd = (Button)findViewById(R.id.profile_tags_btn_add);
		mTxtName = (EditText)findViewById(R.id.profile_tags_txt_name);
		
		mBtnAdd.setOnClickListener(onClick_AddTag);
		
		mTask = new GetTagsTask();
		mTask.attach(this);
		mTask.execute();
	}
	
	//event handler
	OnClickListener onClick_AddTag = new OnClickListener(){
		@Override
		public void onClick(View v) {
			String tagName = mTxtName.getText().toString();
			if(!Utility.IsNullOrEmpty(tagName)){
				mBtnAdd.setText("Adding..");
				mBtnAdd.setEnabled(false);
				mAddTagTask = new AddTagTask();
				mAddTagTask.attach(mActivity);
				mAddTagTask.execute(tagName);
			}
		}
		
	};
	
	//event listeners
	private OnItemClickListener tagItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView parent, View v, int position, long id) {
    		TagItemViewHolder holder = (TagItemViewHolder) v.getTag();
    		Intent i = new Intent(ProfileTagsActivity.this, TaggedPostsActivity.class);				
			if(holder != null){
				if(holder.tag != null){
					i.putExtra("tag_id", holder.tag.getId());
				}
    		}
			startActivity(i);
    	}
	};
	
	//helpers
	private void loadData(){
		if(mAdapter2 == null){
			mAdapter2 = new SectionedAdapter(){
	
				@Override
				protected View getHeaderView(String caption, int index,
						View convertView, ViewGroup parent) {
					TextView result = (TextView)convertView;
					if(convertView == null){
						result = (TextView)getLayoutInflater().inflate(R.layout.list_header, null);
					}
					result.setText(caption);
					return result;
				}
				
			};
		}
		
		int count = mTags.size();
		Tag prev = null;
		List<Tag> tempList = new ArrayList<Tag>();
		String sectionName = "";
		for(int i = 0; i < count; i++){
			Tag current = mTags.get(i);
			
			if(i == 0){
				sectionName = current.getName().substring(0, 1);
				tempList.add(current);
			}else{
				if(prev != null){
					//check prevName
					String prevLetter = prev.getName().substring(0,1);
					String currentLetter = current.getName().substring(0,1); 
					if(!prevLetter.equalsIgnoreCase(currentLetter)){
						mAdapter2.addSection(sectionName, new TagListAdapter(mContext, tempList));
						sectionName = currentLetter;
						//reset tempList
						tempList = new ArrayList<Tag>();
					}
					tempList.add(current);
					
					if(i == count - 1){
						mAdapter2.addSection(sectionName, new TagListAdapter(mContext, tempList));
					}
				}
			}
			prev = current;
		}
		mLvTags.setAdapter(mAdapter2);
		mLvTags.setOnItemClickListener(tagItemClickListener);
	}
	
	private void loadAddTagResult(BasicStatus status){
		if(status == BasicStatus.SUCCESS){
			if(mTask != null){
				//if task is already running, then cancel it, and restart it since we have updated data
				if(mTask.getStatus() == Status.RUNNING){
					mTask.cancel(true);
				}
			}
			mTask = new GetTagsTask();
			mTask.attach(this);
			mTask.execute();
		}
		mBtnAdd.setText("Add");
		mBtnAdd.setEnabled(true);
		mTxtName.setText("");
	}
	
	//classes
	public class AddTagTask extends AsyncTask<String, Integer, BasicStatus>{
		ProfileTagsActivity activity = null;
		@Override
		protected BasicStatus doInBackground(String... params){
			BasicStatus status = TagService.AddTag(params[0]);
			return status;
		}
		
		@Override
		protected void onPostExecute(BasicStatus result){
			activity.loadAddTagResult(result);
		}
		
		void attach(ProfileTagsActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	public class GetTagsTask extends AsyncTask<Void, Integer, Void>{
		ProfileTagsActivity activity = null;
		
		@Override
		protected Void doInBackground(Void... params) {
			activity.mTags = TagService.GetAllTags(false);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			activity.loadData();
		}
		
		void attach(ProfileTagsActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	static class TagItemViewHolder{
		TextView txtName, txtNumPosts;
		Button btnEdit;
		Tag tag;
	}
	
	public class TagListAdapter extends BaseAdapter{
		List<Tag> mTags;
		Context mContext;
		LayoutInflater mInflater;
		
		public TagListAdapter(Context c, List<Tag> tags){
			mTags = tags;
			mContext = c;
			mInflater = LayoutInflater.from(c);
		}
		
		@Override
		public int getCount() {
			if(mTags != null){
				return mTags.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mTags != null){
				return mTags.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(mTags != null){
				return mTags.get(position).getId();
			}
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tag tag = (Tag) getItem(position);
			TagItemViewHolder holder;
			if(tag != null){
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.tag_list_item, null);
					TextView txtName = (TextView)convertView.findViewById(R.id.profile_tags_txt_name);
					TextView txtNumPosts = (TextView)convertView.findViewById(R.id.profile_tags_txt_num_posts);
					Button btnEdit = (Button)convertView.findViewById(R.id.profile_tags_btn_edit);
					holder = new TagItemViewHolder();
					holder.txtName = txtName;
					holder.txtNumPosts = txtNumPosts;
					holder.btnEdit = btnEdit;
					convertView.setTag(holder);
				}else{
					holder = (TagItemViewHolder)convertView.getTag();
				}
				
				holder.txtName.setText(tag.getName());
				holder.txtNumPosts.setText(String.valueOf(tag.getNumPosts()) + " posts");
				holder.tag = tag;
			}
				
			return convertView;
		}
	}
}
