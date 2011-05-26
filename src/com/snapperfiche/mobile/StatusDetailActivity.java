package com.snapperfiche.mobile;

import java.util.List;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class StatusDetailActivity extends Activity {
	ListView mLvComments;
	List<Comment> mComments;
	Context mContext;
	EditText txtCommentBox;
	int mPostId;
	User currentUser;
	CommentsAdapter mCommentsAdapter;
	Button mBtnSubmitComment;
	
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
		
		mLvComments = (ListView) findViewById(R.id.lv_post_comments);
		
		mPostId = bundle.getInt("post_id");
		
		final Object data = getLastNonConfigurationInstance();
        if(data != null){
        	//cast it into the data holder obj
        	StatusFeedDetailDataHolder dataHolder = (StatusFeedDetailDataHolder) data;
        	//set the existing data
        	mComments = dataHolder.comments;
        	loadComments();
        }else{
			LoadCommentsTask task = new LoadCommentsTask();
			task.execute(mPostId);
        }
        
        mBtnSubmitComment = (Button) findViewById(R.id.btnAddComment);
        mBtnSubmitComment.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v) {
        		mBtnSubmitComment.setText("Can't wait to see what you commented..");
        		mBtnSubmitComment.setEnabled(false);
        		AddCommentTask addCommentTask = new AddCommentTask();
        		addCommentTask.execute();
			}
        });
	}
	
	static class StatusFeedDetailDataHolder{
		List<Comment> comments;
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    final StatusFeedDetailDataHolder data = new StatusFeedDetailDataHolder();
	    data.comments = mComments;
	    
	    return data;
	}
	
	private void updateComment(BasicStatus status){
		if(status == Enumerations.BasicStatus.SUCCESS){
			mComments = CommentService.GetCommentsByPostId(mPostId);
			//update comments
			mCommentsAdapter.notifyDataSetChanged();
			txtCommentBox.setText("");
			
			mBtnSubmitComment.setText("Feed it");
    		mBtnSubmitComment.setEnabled(true);
		}
	}
	
	private class AddCommentTask extends AsyncTask<Void, Integer, BasicStatus>{
		@Override
		protected BasicStatus doInBackground(Void... params) {
			String comment = txtCommentBox.getText().toString();
			BasicStatus status = CommentService.CreateComment(currentUser.getId(), mPostId, comment);
			return status;
		}
		
		@Override
		protected void onPostExecute(BasicStatus status) {
			updateComment(status);
		}
	}
	
	private class LoadCommentsTask extends AsyncTask<Integer, Integer, Void>{
		@Override
		protected Void doInBackground(Integer... postIds) {
			Integer postId = postIds[0];
			mComments = CommentService.GetCommentsByPostId(postId);
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
