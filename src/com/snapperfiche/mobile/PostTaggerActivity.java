package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.snapperfiche.code.Constants;
import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.code.Utility;
import com.snapperfiche.data.Tag;
import com.snapperfiche.mobile.ProfileTagsActivity.GetTagsTask;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.GroupService;
import com.snapperfiche.webservices.TagService;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class PostTaggerActivity extends Activity{
	ListView mLvTags;
	Button mBtnAdd, mBtnCancel, mBtnDone;
	EditText mEtxtTagName;
	ViewSwitcher mVwswLoadingTags;
	List<Tag> mTags;
	HashMap mSelectedStates;
	Context mContext = this;
	GetTagsAsyncTask mGetTagsTask;
	List<Integer> mSelectedTagIds;
	AddTagAsyncTask mAddTagTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_tagger_layout);
		
		//find controls
		mLvTags = (ListView)findViewById(R.id.lv_tags);
		mBtnAdd = (Button)findViewById(R.id.btn_add);
		mEtxtTagName = (EditText)findViewById(R.id.etxt_tag_name);
		mVwswLoadingTags = (ViewSwitcher)findViewById(R.id.vwsw_loading_tags);
		mBtnCancel = (Button)findViewById(R.id.btn_cancel);
		mBtnDone = (Button)findViewById(R.id.btn_done);
		
		mSelectedTagIds = new ArrayList<Integer>();
		mSelectedStates = new HashMap();
		
		if(!Utility.IsNullOrEmpty(mEtxtTagName.getText().toString())){
			mBtnAdd.setEnabled(false);
		}
		
		//check to see if we already have the dataHolder
		final PostTaggerActivityDataHolder dataHolder = (PostTaggerActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.selectedTagIds != null){
				mSelectedTagIds = dataHolder.selectedTagIds;
			}
			
			if(dataHolder.selectedStates != null){
				mSelectedStates = dataHolder.selectedStates;
			}
			
			if(dataHolder.tags != null){
				mTags = dataHolder.tags;
				loadTags();
			}
		}
		
		if(dataHolder != null && dataHolder.getTagsTask != null){
			dataHolder.getTagsTask.attach(this);
			mGetTagsTask = dataHolder.getTagsTask;
			if(mGetTagsTask.getStatus() == Status.PENDING){
				mGetTagsTask.execute();
			}
		}else{
			mGetTagsTask = new GetTagsAsyncTask();
			mGetTagsTask.attach(this);
			mGetTagsTask.execute();
		}
		
		if(dataHolder != null && dataHolder.addTagTask != null){
			dataHolder.addTagTask.attach(this);
			mAddTagTask = dataHolder.addTagTask;
		}
		
		//bind to events
		mEtxtTagName.addTextChangedListener(tw_tagName);
		mBtnAdd.setOnClickListener(onClick_addTag);
	}
	
	//events
	@Override
	public Object onRetainNonConfigurationInstance(){
		PostTaggerActivityDataHolder dataHolder = new PostTaggerActivityDataHolder();
		if(mGetTagsTask != null) mGetTagsTask.detach();
		if(mAddTagTask != null) mAddTagTask.detach();
		dataHolder.getTagsTask = mGetTagsTask;
		dataHolder.selectedTagIds = mSelectedTagIds;
		dataHolder.tags = mTags;
		dataHolder.selectedStates = mSelectedStates; 
		dataHolder.addTagTask = mAddTagTask;
		
		return dataHolder;
	}
	
	OnItemClickListener onItemClick_rowClick = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			TagListSelectViewHolder holder = (TagListSelectViewHolder)v.getTag();
			if(holder != null){
				//set isChecked to the opposite of what the checkbox currently is
				boolean isChecked = !holder.cbSelect.isChecked();
				//update checkbox
				holder.cbSelect.setChecked(isChecked);
				//store the updated state in our array
				mSelectedStates.put(holder.tag.getId(), new Boolean(isChecked));
				
				if(isChecked){
					//add tag id to our list of selected tag ids
					mSelectedTagIds.add(holder.tag.getId());
				}else{
					//remove tag id from our list of selected tag ids
					mSelectedTagIds.remove(new Integer(holder.tag.getId()));
				}
			}
		}
		
	};
	
	OnClickListener onClick_addTag = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(mGetTagsTask != null && mGetTagsTask.getStatus() == Status.RUNNING){
				mGetTagsTask.cancel(true);
			}
			
			String tagName = mEtxtTagName.getText().toString();
			if(!Utility.IsNullOrEmpty(tagName)){
				mBtnAdd.setText("Adding...");
				mBtnAdd.setEnabled(false);
				mAddTagTask = new AddTagAsyncTask();
				mAddTagTask.attach(PostTaggerActivity.this);
				mAddTagTask.execute(tagName);
			}
		}
		
	};
	
	OnClickListener onClick_cancel = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent parentIntent = getIntent();
			if(parentIntent != null){
				//since we're canceling out of the tag activity, just return
				//to the parent with empty data
				parentIntent.putExtra("tag_ids", new int[0]);
				setResult(RESULT_OK, parentIntent);
				finish();
			}
		}
		
	};
	
	OnClickListener onClick_done = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent parentIntent = getIntent();
			if(parentIntent != null){
				//since we're canceling out of the tag activity, just return
				//to the parent with empty data
				int[] tagIdArray;
				if(mTags != null){
					int size = mTags.size();
					tagIdArray = new int[size];
					for(int i = 0; i < size; i++){
						tagIdArray[i] = ((Tag)mTags.get(i)).getId();
					}
				}else{
					tagIdArray = new int[0];
				}
				parentIntent.putExtra("tag_ids", tagIdArray);
				setResult(RESULT_OK, parentIntent);
				finish();
			}
		}
		
	};
	
	TextWatcher tw_tagName = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mBtnAdd.setEnabled(s.length() > 0);
		}
		
	};
	
	//helpers
	private void loadTags(){
		if(mTags != null && mLvTags != null){
			mLvTags.setAdapter(new TagListSelectAdapter(mContext, mTags));
			mLvTags.setOnItemClickListener(onItemClick_rowClick);
			//if we are on the first view, then show the second view which contains the loaded layout
			if(mVwswLoadingTags != null && mVwswLoadingTags.getDisplayedChild() == 0)
				mVwswLoadingTags.showNext();
		}
	}
	
	private void loadAddTagResult(BasicStatus status){
		if(status == BasicStatus.SUCCESS){
			if(mGetTagsTask != null){
				//if task is already running, then cancel it, and restart it since we have updated data
				if(mGetTagsTask.getStatus() == Status.RUNNING){
					mGetTagsTask.cancel(true);
				}
			}
			mGetTagsTask = new GetTagsAsyncTask();
			mGetTagsTask.attach(this);
			mGetTagsTask.execute();
		}
		mBtnAdd.setText("Add");
		mBtnAdd.setEnabled(true);
		mEtxtTagName.setText("");
	}
	
	//classes
	public class AddTagAsyncTask extends AsyncTask<String, Integer, BasicStatus>{
		PostTaggerActivity activity = null;
		@Override
		protected BasicStatus doInBackground(String... params){
			return TagService.AddTag(params[0]);
		}
		
		@Override
		protected void onPostExecute(BasicStatus result){
			activity.loadAddTagResult(result);
		}
		
		void attach(PostTaggerActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	private class GetTagsAsyncTask extends AsyncTask<Void, Integer, List<Tag>>{
		PostTaggerActivity activity = null;
		
		@Override
		protected List<Tag> doInBackground(Void... params) {
			return TagService.GetAllTags(false);
		}
		
		@Override
		protected void onPostExecute(List<Tag> result){
			this.activity.mTags = result;
			this.activity.loadTags();
		}
		
		void attach(PostTaggerActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	private class PostTaggerActivityDataHolder{
		List<Tag> tags;
		HashMap selectedStates;
		GetTagsAsyncTask getTagsTask;
		List<Integer> selectedTagIds;
		AddTagAsyncTask addTagTask;
	}
	
	private class TagListSelectViewHolder{
		TextView txtName;
		CheckBox cbSelect;
		Tag tag;
	}
	
	public class TagListSelectAdapter extends BaseAdapter{
		List<Tag> mTags;
		LayoutInflater mInflater;
		
		public TagListSelectAdapter(Context c, List<Tag> tags){
			mTags = tags;
			mInflater = LayoutInflater.from(c);
		}
		
		@Override
		public int getCount() {
			if(mTags != null) return mTags.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mTags != null) return mTags.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(mTags != null){
				return mTags.get(position).getId();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tag currentTag = (Tag)getItem(position);
			if(currentTag == null) return null;
			
			TagListSelectViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.select_tag_row, parent, false);
				TextView txtTagName = (TextView)convertView.findViewById(R.id.txt_name);
				CheckBox cbSelect = (CheckBox)convertView.findViewById(R.id.cb_select);
				holder = new TagListSelectViewHolder();
				holder.txtName = txtTagName;
				holder.cbSelect = cbSelect;
			}else{
				holder = (TagListSelectViewHolder)convertView.getTag();
			}
			
			holder.tag = currentTag;
			holder.txtName.setText(currentTag.getName());
			boolean isChecked = false; 
			//check if hashmap already contain this tag, if it does, use that value
			//if it doesn't, then we default to false, and then put that value in the hashmap
			if(mSelectedStates.containsKey(currentTag.getId())){
				isChecked = (Boolean)mSelectedStates.get(currentTag.getId());
			}else{
				mSelectedStates.put(currentTag.getId(), new Boolean(isChecked));
			}
			holder.cbSelect.setChecked(isChecked);
			convertView.setTag(holder);
			
			return convertView;
		}
		
	}
}
