package com.snapperfiche.mobile;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class Profile extends Activity{
	private String lv_arr[] = { "Android", "iPhone", "BlackBerry",
								"AndroidPeople", "iPad", "Windows Mobile",
								"Sony", "HTC", "Motorola"};
	private String lv_arr2[] = {"Ken Wang", "feiyingx@gmail.com"};
	
	private static String[] data = new String[]{"0", "1", "2", "3", "4", "5", "6", "7",
		"8","9","10","11","12","13","14","15"};
	private ProfileRowAdapter adap;
	static GradientDrawable rowBg;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		WorkspaceView workspace = new WorkspaceView(this, null);
		workspace.setTouchSlop(32);
		
		//rowBg = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.BLACK, Color.DKGRAY});
		//rowBg.setDither(true);
		
		View postView = inflater.inflate(R.layout.profile_post, null, false);
		ListView postList = (ListView) postView.findViewById(R.id.profilePostList);
		adap = new ProfileRowAdapter(this);
		postList.setAdapter(adap);
		
		
		/*
		View v1 = inflater.inflate(R.layout.friend_tagger_vertical, null, false);
		ListView lv1 = (ListView)v1.findViewById(R.id.lv_friends);
		lv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr));
		*/
		
		View v2 = inflater.inflate(R.layout.friend_tagger_vertical, null, false);
		ListView lv2 = (ListView)v2.findViewById(R.id.lv_friends);
		lv2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr2));
		
		/*
		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0P, View arg1P, int arg2P, long arg3P){
				Toast toast = Toast.makeText(Profile.this, "click", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
		lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0P, View arg1P, int arg2P, long arg3P){
				Toast toast = Toast.makeText(Profile.this, "long click", Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});
		*/
		workspace.addView(postView);
		workspace.addView(v2);
		
		View v3 = inflater.inflate(R.layout.profile_friend_tab, null, false);
		TabHost tabs = (TabHost) v3.findViewById(R.id.tabhost);
		tabs.setup();
		
		TabHost.TabSpec spec = tabs.newTabSpec("tag 1");
		spec.setContent(R.id.profile_friend_tab);
		spec.setIndicator("Friends");
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("tag 2");
		spec.setContent(R.id.profile_follower_tab);
		spec.setIndicator("Followers");
		tabs.addTab(spec);
		
		GridView gv1 = (GridView)v3.findViewById(R.id.profile_follower_gridview);
		gv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr));
		
		workspace.addView(v3);
		workspace.setCurrentScreen(1);//set to the profile view
		setContentView(workspace);
	}
	
	public static class ProfileRowAdapter extends BaseAdapter implements Filterable{
		private LayoutInflater mInflater;
		private Bitmap mIcon1;
		private Context context;
		
		public ProfileRowAdapter(Context context){
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}
		
		static class ViewHolder{
			TextView txtCaption;
			TextView txtTime;
			TextView txtLocation;
			ImageView iconLine;
			Button favBtn;
			Button likeBtn;
			Button commentBtn;
		}
		
		//make a view to hold each row
		public View getView(final int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.profile_post_row, null);
				//convertView.setBackgroundDrawable(rowBg);
				
				holder = new ViewHolder();
				/*
				holder.txtCaption = (TextView) convertView.findViewById(R.id.profilePostRow_caption);
				holder.txtLocation = (TextView) convertView.findViewById(R.id.profilePostRow_location);
				holder.txtTime = (TextView) convertView.findViewById(R.id.profilePostRow_caption);
				holder.iconLine = (ImageView) convertView.findViewById(R.id.profilePostRow_img);
				holder.favBtn = (Button) convertView.findViewById(R.id.profilePostRow_favBtn);
				holder.likeBtn = (Button) convertView.findViewById(R.id.profilePostRow_likeBtn);
				holder.commentBtn = (Button) convertView.findViewById(R.id.profilePostRow_commentBtn);
				*/
				convertView.setOnClickListener(new OnClickListener(){
					private int pos = position;
					
					@Override
					public void onClick(View v){
						Toast.makeText(context, "click-" + v.toString(), Toast.LENGTH_SHORT).show();
					}
				});
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iconLine.setImageResource(R.drawable.wolfbitesmall);
			holder.txtCaption.setText("beautiful ficheDroid likes to peek out from the corner " + String.valueOf(position));
			holder.txtLocation.setText("@Cerritos, CA");
			return convertView;
		}
		
		@Override
		public Filter getFilter(){
			return null;
		}
		
		@Override
		public long getItemId(int position){
			return 0;
		}
		
		@Override
		public int getCount(){
			return data.length;
		}
		
		@Override
		public Object getItem(int position){
			return data[position];
		}
	}
}


/*
public class Profile extends Activity {
	ViewFlipper flipper;
	private static final int SWIPE_MIN_DISTANCE = 60;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	TextView currentPanelText;
	
	private static String[] data = new String[]{"0", "1", "2", "3", "4", "5", "6", "7",
												"8","9","10","11","12","13","14","15"};
	private ProfileRowAdapter adap;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		currentPanelText = (TextView) findViewById(R.id.profileCurrentViewText);
		currentPanelText.setText("1");
		
		flipper = (ViewFlipper) findViewById(R.id.profileFlipper);
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		
		gestureDetector = new GestureDetector(new SwipeGestureDetector());
		gestureListener = new View.OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				if(gestureDetector.onTouchEvent(event)){
					return true;
				}
				return false;
			}
		};
		
		slideLeftOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				int index = flipper.getDisplayedChild();
				currentPanelText.setText(String.valueOf(index+1));
			}
		});
		
		slideRightOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				try{
					
				
				int index = flipper.getDisplayedChild();
				currentPanelText.setText(String.valueOf(index+1));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		ListView profilePosts = (ListView) findViewById(R.id.profilePostList);
		adap = new ProfileRowAdapter(this);
		profilePosts.setAdapter(adap);
		profilePosts.setOnTouchListener(gestureListener);
	}
	
	class SwipeGestureDetector extends SimpleOnGestureListener{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			try{
				if(Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				
				//right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
					flipper.setInAnimation(slideLeftIn);
					flipper.setOutAnimation(slideLeftOut);
					flipper.showNext();
				}else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
					flipper.setInAnimation(slideRightIn);
					flipper.setOutAnimation(slideRightOut);
					flipper.showPrevious();
				}
			}catch(Exception ex){
				
			}
			return false;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}
	
	public static class ProfileRowAdapter extends BaseAdapter implements Filterable{
		private LayoutInflater mInflater;
		private Bitmap mIcon1;
		private Context context;
		
		public ProfileRowAdapter(Context context){
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}
		
		static class ViewHolder{
			TextView textLine;
			ImageView iconLine;
			Button buttonLine;
		}
		
		//make a view to hold each row
		public View getView(final int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.profile_post_row, null);
				
				//create viewholder and store references to child views
				holder = new ViewHolder();
				holder.textLine = (TextView) convertView.findViewById(R.id.profilePostRow_caption);
				holder.iconLine = (ImageView) convertView.findViewById(R.id.profilePostRow_img);
				holder.buttonLine = (Button) convertView.findViewById(R.id.profilePostRow_btn);
				
				convertView.setOnClickListener(new OnClickListener(){
					private int pos = position;
					
					@Override
					public void onClick(View v){
						Toast.makeText(context, "click-" + v.toString(), Toast.LENGTH_SHORT).show();
					}
				});
				
				holder.buttonLine.setOnClickListener(new OnClickListener(){
					private int pos = position;
					
					@Override
					public void onClick(View v){
						Toast.makeText(context, "this is button click - " + v.toString(), Toast.LENGTH_SHORT);
					}
				});
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iconLine.setImageResource(R.drawable.icon);
			holder.textLine.setText("caption " + String.valueOf(position));
			
			return convertView;
		}
		
		@Override
		public Filter getFilter(){
			return null;
		}
		
		@Override
		public long getItemId(int position){
			return 0;
		}
		
		@Override
		public int getCount(){
			return data.length;
		}
		
		@Override
		public Object getItem(int position){
			return data[position];
		}
	}
}
*/
