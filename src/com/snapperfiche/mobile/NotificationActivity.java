package com.snapperfiche.mobile;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.droidfu.widgets.WebImageView;
import com.snapperfiche.data.UserNotification;
import com.snapperfiche.mobile.custom.BaseActivity;
import com.snapperfiche.webservices.UserNotificationService;

public class NotificationActivity extends BaseActivity {
	ListView mLvNotifications;
	ViewSwitcher mVsLoading;
	List<UserNotification> mNotifications;
	Context mContext = this;
	GetNotificationsAsyncTask mTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications_layout);
		
		mLvNotifications = (ListView)findViewById(R.id.lv_notifications);
		mVsLoading = (ViewSwitcher)findViewById(R.id.vs_loading);
		
		/* load from dataholder if there is data */
		final NotificationActivityDataHolder dataHolder = (NotificationActivityDataHolder)getLastNonConfigurationInstance();
		if(dataHolder != null){
			if(dataHolder.mNotifications != null){
				mNotifications = dataHolder.mNotifications;
				loadNotifications();
			}
			
			if(dataHolder.mTask != null){
				dataHolder.mTask.attach(this);
				mTask = dataHolder.mTask;
			}
		}else{
			mTask = new GetNotificationsAsyncTask();
			mTask.attach(this);
			mTask.execute();
		}
	}
	
	//events
	@Override
	public Object onRetainNonConfigurationInstance(){
		NotificationActivityDataHolder holder = new NotificationActivityDataHolder();
		if(mTask != null) mTask.detach();
		holder.mTask = this.mTask;
		holder.mNotifications = this.mNotifications;
		return holder;		
	};
	
	OnItemClickListener onClick_notification = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			NotificationAdapterViewHolder holder = (NotificationAdapterViewHolder)view.getTag();
			UpdateNotificationAsyncTask task = new UpdateNotificationAsyncTask();
			int[] params = new int[1];
			params[0] = holder.notification.getId();
			task.execute(params);
			//reset GetReceivedNotifications cache
			UserNotificationService.CacheKeyReset_GetReceivedNotifications();
			//TODO: take user to the notified item
		}
	};
	
	//helpers
	private void loadNotifications(){
		if(mNotifications != null){
			mLvNotifications.setAdapter(new NotificationAdapter(mContext, mNotifications));
			//if we're currently on the loading view, then show the next view which is the listview with notifications
			if(mVsLoading.getDisplayedChild() == 0){
				mVsLoading.showNext();
			}
		}
	}
	
	//classes
	class NotificationActivityDataHolder{
		GetNotificationsAsyncTask mTask;
		List<UserNotification> mNotifications;
	}
	
	private class GetNotificationsAsyncTask extends AsyncTask<Void, Integer, List<UserNotification>>{
		NotificationActivity activity = null;
		
		@Override
		protected List<UserNotification> doInBackground(Void... params) {
			return UserNotificationService.GetReceivedNotifications(false);
		}
		
		@Override
		protected void onPostExecute(List<UserNotification> result){
			this.activity.mNotifications = result;
			this.activity.loadNotifications();
		}
		
		void attach(NotificationActivity activity){
			this.activity = activity;
		}
		
		void detach(){
			this.activity = null;
		}
	}
	
	private class UpdateNotificationAsyncTask extends AsyncTask<int[], Integer, Void>{
		@Override
		protected Void doInBackground(int[]... params){
			UserNotificationService.UpdateNotificationsViewed(params[0]);
			return null;
		}
	}
	
	class NotificationAdapterViewHolder{
		WebImageView imgProfile;
		TextView txtName, txtTime, txtNotification;
		UserNotification notification;
	}
	
	public class NotificationAdapter extends BaseAdapter{
		List<UserNotification> mNotifications;
		LayoutInflater mInflater;
		
		public NotificationAdapter(Context c, List<UserNotification> notifications){
			mNotifications = notifications;
			mInflater = LayoutInflater.from(c);
		}
		
		@Override
		public int getCount() {
			if(mNotifications != null) return mNotifications.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mNotifications != null) return mNotifications.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			if(mNotifications != null) return mNotifications.get(position).getId();
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UserNotification notification = (UserNotification)getItem(position);
			if(notification == null) return null;
			
			NotificationAdapterViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.notification_row, parent, false);
				WebImageView imgProfile = (WebImageView)convertView.findViewById(R.id.img_profile);
				TextView txtName = (TextView)convertView.findViewById(R.id.txt_name);
				TextView txtTime = (TextView)convertView.findViewById(R.id.txt_datetime);
				TextView txtNotification = (TextView)convertView.findViewById(R.id.txt_notification);
				holder = new NotificationAdapterViewHolder();
				holder.imgProfile = imgProfile;
				holder.txtName = txtName;
				holder.txtNotification = txtNotification;
				holder.txtTime = txtTime;				
			}else{
				holder = (NotificationAdapterViewHolder)convertView.getTag();
			}
			holder.notification = notification;
			//TODO:add profile img once there is one
			holder.txtName.setText(notification.getEmail());
			String notificationText = "";
			switch(notification.getNotificationType()){
				case NEW_ANSWER:
					notificationText = "Posted an answer";
					break;
				case NEW_POST:
					notificationText = "Made a post";
					break;
				case NEW_QUESTION:
					notificationText = "Asked a question";
					break;
			}
			holder.txtNotification.setText(notificationText);
			holder.txtTime.setText(notification.getDate().toString());
			convertView.setTag(holder);
			return convertView;
		}
		
	}
}
