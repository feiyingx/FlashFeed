package com.snapperfiche.mobile;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.snapperfiche.code.Enumerations;
import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.code.Enumerations.GroupType;
import com.snapperfiche.data.Comment;
import com.snapperfiche.data.Group;
import com.snapperfiche.data.User;
import com.snapperfiche.mobile.StatusFeed.FeedGroupItemViewHolder;
import com.snapperfiche.mobile.StatusFeed.StatusFeedDataHolder;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.CommentService;
import com.snapperfiche.webservices.GroupService;
import com.snapperfiche.webservices.PostService;
import com.snapperfiche.webservices.SimpleCache;
import com.snapperfiche.mobile.custom.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StatusDetailActivity extends BaseActivity {
	ListView mLvComments;
	List<Comment> mComments;
	Context mContext;
	EditText txtCommentBox;
	int mPostId;
	User currentUser;
	CommentsAdapter mCommentsAdapter;
	Button mBtnSubmitComment;
	boolean mRefreshComments = false;
	String mPostCommentsCacheKey;
	AddCommentTask addCommentTask;
	boolean mResetCommentTextBox = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_detail);
		
		mContext = this;
		currentUser = AccountService.getUser();
		
		Bundle bundle = getIntent().getExtras();
		int position = bundle.getInt("position");
		
		//Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
		
		TextView positionText = (TextView) findViewById(R.id.position);
		positionText.setText("position: " + position);
		
		txtCommentBox = (EditText) findViewById(R.id.txtComment);
		Log.d("StatusDetail onCreate", "txtCommentBox:"+ txtCommentBox.getText().toString());
		
		mLvComments = (ListView) findViewById(R.id.lv_post_comments);
		
		mPostId = bundle.getInt("post_id");
		mPostCommentsCacheKey = "ck_StatusDetailActivity_Post_".concat(String.valueOf(mPostId)).concat("Comments");
		
		mBtnSubmitComment = (Button) findViewById(R.id.btnAddComment);
        mBtnSubmitComment.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v) {
        		mBtnSubmitComment.setText("Can't wait to see what you commented..");
        		mBtnSubmitComment.setEnabled(false);
        		addCommentTask = new AddCommentTask();
        		addCommentTask.execute();
			}
        });
        
		final Object data = getLastNonConfigurationInstance();
        if(data != null){
        	//cast it into the data holder obj
        	StatusFeedDetailDataHolder dataHolder = (StatusFeedDetailDataHolder) data;
        	if(dataHolder.addTask != null){
        		Log.d("Status Detail onCreate", "Got addTask data from configchange event");
        		if(dataHolder.addTask.getStatus() == Status.FINISHED || dataHolder.addTask.getStatus() == Status.RUNNING){
        			try {
        				mBtnSubmitComment.setText("Can't wait to see what you commented..");
                		mBtnSubmitComment.setEnabled(false);
						BasicStatus addCommentStatus = dataHolder.addTask.get();
						updateComment(addCommentStatus);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}else{
	        	//set the existing data
	        	if(dataHolder.comments != null){
		        	Log.d("Status Detail onCreate", "Got comments data from configchange event");
	        		mComments = dataHolder.comments;
		        	loadComments();
	        	}else{
	        		Log.d("Status Detail onCreate", "Didn't get comments data from configchange event");
	        	}
        	}
        }else{
        	if(!mRefreshComments){
        		mComments = (List<Comment>)SimpleCache.get(mPostCommentsCacheKey);
        		if(mComments != null){
        			loadComments();
        		}else{
        			LoadCommentsTask task = new LoadCommentsTask();
        			task.execute(mPostId);
        		}
        	}else{
				LoadCommentsTask task = new LoadCommentsTask();
				task.execute(mPostId);
        	}
        }
        
        Log.d("StatusDetail onCreate", "3 txtCommentBox:"+ txtCommentBox.getText().toString());
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.d("StatusDetail onCreate", "3 txtCommentBox:"+ txtCommentBox.getText().toString());
		if(mResetCommentTextBox){
			txtCommentBox.setText("");
			mResetCommentTextBox = false;
		}
	}
	
	static class StatusFeedDetailDataHolder{
		List<Comment> comments;
		AddCommentTask addTask;
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    final StatusFeedDetailDataHolder data = new StatusFeedDetailDataHolder();
	    data.comments = mComments;
	    data.addTask = addCommentTask;
	    return data;
	}
	
	private void updateComment(BasicStatus status){
		if(status == Enumerations.BasicStatus.SUCCESS){
			mComments = CommentService.GetCommentsByPostId(mPostId);
			SimpleCache.put(mPostCommentsCacheKey, mComments);
			//update comments
			if(mCommentsAdapter != null){
				mCommentsAdapter.notifyDataSetChanged();
			}else{
				loadComments();
			}
			txtCommentBox.setText("");
			Log.d("StatusDetail onCreate", "2 txtCommentBox:"+ txtCommentBox.getText().toString());
			
			mBtnSubmitComment.setText("Feed it");
    		mBtnSubmitComment.setEnabled(true);
    		mResetCommentTextBox = true;
    		mRefreshComments = false;
		}
	}
	
	private class AddCommentTask extends AsyncTask<Void, Integer, BasicStatus>{
		@Override
		protected BasicStatus doInBackground(Void... params) {
			String comment = txtCommentBox.getText().toString();
			BasicStatus status = CommentService.CreateComment(currentUser.getId(), mPostId, comment);
			//update post cache so that it reflects the comment update
			SimpleCache.remove("GetPostById_" + String.valueOf(mPostId));
			mRefreshComments = true;
			return status;
		}
		
		@Override
		protected void onPostExecute(BasicStatus status) {
			Log.d("AddCommentTask onPostExecute", "Finished adding comment, now in post");
			updateComment(status);
		}
	}
	
	private class LoadCommentsTask extends AsyncTask<Integer, Integer, Void>{
		@Override
		protected Void doInBackground(Integer... postIds) {
			Integer postId = postIds[0];
			mComments = CommentService.GetCommentsByPostId(postId);
			SimpleCache.put(mPostCommentsCacheKey, mComments);
			mRefreshComments = false;
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			loadComments();
		}
	}
	
	private void loadComments(){
		mCommentsAdapter = new CommentsAdapter(mContext);
		mLvComments.setAdapter(mCommentsAdapter);
	}
	
	static class CommentItemViewHolder{
		Comment comment;
	}
	
	public class CommentsAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private Context mContext;
		
		public CommentsAdapter(Context context){
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mComments.size();
		}

		@Override
		public Object getItem(int position) {
			if(position >= mComments.size())
				return null;
			return mComments.get(position);
		}

		@Override
		public long getItemId(int position) {
			if(position >= mComments.size())
				return -1;
			return mComments.get(position).getId();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommentItemViewHolder holder;
			Comment comment = (Comment) getItem(position);
			if(comment == null)
				return null;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.comment_row, parent, false);
				holder = new CommentItemViewHolder();
				holder.comment = comment;
				convertView.setTag(holder);
			}else{
				holder = (CommentItemViewHolder) convertView.getTag();
			}
			
			TextView txtName = (TextView) convertView.findViewById(R.id.txt_comment_row_name);
			TextView txtComment = (TextView) convertView.findViewById(R.id.txt_comment_row_content);
			TextView txtDate = (TextView) convertView.findViewById(R.id.txt_comment_row_time);
			
			txtName.setText(comment.getEmail());
			txtComment.setText(comment.getContent());
			txtDate.setText(comment.getCreateDate().toGMTString());
			
			return convertView;
		}
		
	}
}
