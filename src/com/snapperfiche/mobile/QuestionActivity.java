package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import com.snapperfiche.data.QuestionPost;
import com.snapperfiche.data.User;
import com.snapperfiche.mobile.AddUserGroupActivity.FriendItemViewHolder;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.PostService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class QuestionActivity extends Activity {
	Context mContext = this;
	AutoCompleteTextView acTxtQuestion;
	static final String[] colPresetQuestions = {
		"What are you doing?", "Who are you with?", "Where are you?", "What are you wearing?",
		"What are you drinking?", "What are you eating?"
	};
	
	List<QuestionPost> mMyQuestions, mFriendQuestions, mGlobalQuestions;
	ListView mLvMyQuestions, mLvFriendsQuestions, mLvGlobalQuestions;
	ViewSwitcher mVsMyQuestions;
	GetQuestionsAsyncTask mMyQuestionsTask;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);
        
        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.questions_mine);
        spec.setIndicator("Mine");
        tabs.addTab(spec);
        
        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.questions_friends);
        spec.setIndicator("Friends");
        tabs.addTab(spec);
        
        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.questions_global);
        spec.setIndicator("Global");
        tabs.addTab(spec);
        
        //find controls
        acTxtQuestion = (AutoCompleteTextView) findViewById(R.id.question_actxt_ask_question);
        acTxtQuestion.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, colPresetQuestions));
        
        Button btnAskQuestion = (Button) findViewById(R.id.btn_ask_question);
        btnAskQuestion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext, UserTaggerActivity.class);
				startActivity(i);
			}
        });
        
        //List<QuestionPost> questions = PostService.GetQuestionsByUser(AccountService.getUser().getId(), 20, 1);
        mLvMyQuestions = (ListView) findViewById(R.id.lv_questions_mine);
        
        mLvFriendsQuestions = (ListView) findViewById(R.id.lv_questions_friends);
        
        mLvGlobalQuestions = (ListView) findViewById(R.id.lv_questions_global);
        
        mVsMyQuestions = (ViewSwitcher) findViewById(R.id.questions_layout_vs_my_questions);
        
        mMyQuestionsTask = new GetQuestionsAsyncTask(this, 0);
        GetQuestionsParam myQuestionParams = new GetQuestionsParam();
        myQuestionParams.numPerPage = 20;
        myQuestionParams.pageNum = 1;
        myQuestionParams.userId = AccountService.getUser().getId();
        mMyQuestionsTask.execute(myQuestionParams);
	}
	
	private void loadQuestions(int loadType){
		switch(loadType){
			case 0:
				mLvMyQuestions.setAdapter(new MyQuestionAdapter(mContext,mMyQuestions));
				mVsMyQuestions.showNext();
				break;
			case 1:
				break;
			case 2:
				break;
			default:
				break;
		}
	}
	
	static class QuestionRowViewHolder{
		QuestionPost question;
		TextView tvQuestion, tvQuestionTime;
	}
	
	static class GetQuestionsParam{
		int userId, numPerPage, pageNum;
	}
	
	public class GetQuestionsAsyncTask extends AsyncTask<GetQuestionsParam, Integer, Void>{
		QuestionActivity activity = null;
		int loadType;
		
		//loadType: 0 = my questions, 1 = friends, 2 = global
		public GetQuestionsAsyncTask(QuestionActivity activity, int loadType){
			this.activity = activity;
			this.loadType = loadType;
		}
		
		@Override
		protected Void doInBackground(GetQuestionsParam... params) {
			if(params[0] != null){
				switch(this.loadType){
					case 0:
						activity.mMyQuestions = PostService.GetQuestionsByUser(params[0].userId, params[0].numPerPage, params[0].pageNum);
						break;
					case 1:
						break;
					case 2:
						break;
					default:
						break;
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			activity.loadQuestions(loadType);
		}
		
		void attach(QuestionActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	public class MyQuestionAdapter extends BaseAdapter{
    	private LayoutInflater mInflater;
    	private List<QuestionPost> questions = new ArrayList<QuestionPost>();
    	public MyQuestionAdapter(Context c, List<QuestionPost> questions){
    		this.questions = questions;
    		mInflater = LayoutInflater.from(c);
    	}
    	
		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			if(position <= questions.size()){
				return questions.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(position <= questions.size()){
				return questions.get(position).getId();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			QuestionRowViewHolder holder;
			QuestionPost q = (QuestionPost) questions.get(position);
			if(q == null)
				return null;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.question_mine_row, null);
				TextView tvQuestion = (TextView) convertView.findViewById(R.id.tv_my_question);
				TextView tvQuestionTime = (TextView) convertView.findViewById(R.id.tv_my_question_time);
				holder = new QuestionRowViewHolder();
				holder.question = q;
				holder.tvQuestion = tvQuestion;
				holder.tvQuestionTime = tvQuestionTime;
				convertView.setTag(holder);
			}else{
				holder = (QuestionRowViewHolder) convertView.getTag();
			}
			
			holder.question = q;
			
			holder.tvQuestion.setText(q.getCaption());
			holder.tvQuestionTime.setText(String.valueOf(q.getDate().getHours()) + ":" + String.valueOf(q.getDate().getMinutes()));
			
			return convertView;
		}
    	
    }
}
