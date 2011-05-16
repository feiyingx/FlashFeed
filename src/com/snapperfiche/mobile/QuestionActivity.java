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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class QuestionActivity extends Activity {
	Context mContext = this;
	
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
        
        Button btnAskQuestion = (Button) findViewById(R.id.btn_ask_question);
        btnAskQuestion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext, CameraTest.class);
				startActivity(i);
			}
        });
        
        List<QuestionPost> questions = PostService.GetQuestionsByUser(AccountService.getUser().getId(), 20, 1);
        ListView lvMyQuestions = (ListView) findViewById(R.id.lv_questions_mine);
        lvMyQuestions.setAdapter(new MyQuestionAdapter(this, questions));
        
        ListView lvFriendsQuestions = (ListView) findViewById(R.id.lv_questions_friends);
        
        ListView lvGlobalQuestions = (ListView) findViewById(R.id.lv_questions_global);
	}
	
	static class QuestionRowViewHolder{
		QuestionPost question;
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
				holder = new QuestionRowViewHolder();
				holder.question = q;
				convertView.setTag(holder);
			}else{
				holder = (QuestionRowViewHolder) convertView.getTag();
			}
			
			holder.question = q;
			TextView tvQuestion = (TextView) convertView.findViewById(R.id.tv_my_question);
			TextView tvQuestionTime = (TextView) convertView.findViewById(R.id.tv_my_question_time);
			
			tvQuestion.setText(q.getCaption());
			tvQuestionTime.setText(String.valueOf(q.getDate().getHours()) + ":" + String.valueOf(q.getDate().getMinutes()));
			
			return convertView;
		}
    	
    }
}
