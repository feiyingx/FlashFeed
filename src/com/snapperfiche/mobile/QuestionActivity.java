package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.snapperfiche.mobile.custom.BaseActivity;
import com.snapperfiche.data.QuestionPost;
import com.snapperfiche.webservices.AccountService;
import com.snapperfiche.webservices.PostService;

public class QuestionActivity extends BaseActivity {
	Context mContext = this;
	AutoCompleteTextView acTxtQuestion;
	static final String[] colPresetQuestions = {
		"What are you doing?", "Who are you with?", "Where are you?", "What are you wearing?",
		"What are you drinking?", "What are you eating?"
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);
        
        /*TabHost tabs = (TabHost) findViewById(R.id.tabhost);
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
        tabs.addTab(spec);*/
        initTabs();
        
        //find controls
        acTxtQuestion = (AutoCompleteTextView) findViewById(R.id.question_actxt_ask_question);
        acTxtQuestion.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, colPresetQuestions));
        
        Button btnAskQuestion = (Button) findViewById(R.id.btn_ask_question);
        btnAskQuestion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext, CameraActivity.class);
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
	
	/**********************************************/
	/** Helpers **/
	/**********************************************/
	private void initTabs() {
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		
		addTab(tabHost, R.string.my, R.drawable.tab_info);
		addTab(tabHost, R.string.friends, R.drawable.tab_info);
		addTab(tabHost, R.string.global, R.drawable.tab_info);
	}
	
	private void addTab(TabHost tabHost, int labelId, int drawableId) {
		
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);
		
		switch (labelId) {
		case R.string.global:
			spec.setContent(R.id.questions_global);
			break;
		case R.string.friends:
			spec.setContent(R.id.questions_friends);
			break;
		case R.string.my:
		default:
			spec.setContent(R.id.questions_mine);
			break;
		}
		
		//Intent intent = new Intent(this, tagsfriends.class);
		//TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);		
		//TabWidget tabWidget = (TabWidget)findViewById(R.id.tab)
		
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
			
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
/*		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId); */
	
		spec.setIndicator(tabIndicator);
		//spec.setContent(intent);
		tabHost.addTab(spec);
		
		
	}
}
