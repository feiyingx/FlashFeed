package com.snapperfiche.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FriendTaggerActivity extends Activity{
	private static final String[] images = {"ProfileA", "ProfileB",
		"ProfileC", "ProfileD", "ProfileE", "ProfileF", "ProfileG",
		"ProfileH", "ProfileI", "ProfileJ", "K", "L", "M", "N","O"
	};
	private static final String[] names = {"Jae", "Jerm", "Calvin",
		"Wen", "Ken", "Shawn", "Wilson", "Pree", "And1", "Kevin", "Hong",
		"Mike", "Chris", "John", "Johnny"
	};
	private static List rowState = new ArrayList();
	
	static class ViewHolder {
        TextView text;
        TextView text2;
        int backgroundColor;
    }
	
	private ListView mlvSelectedFriends;
	
	private static class EfficientAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		
		
		
		public EfficientAdapter(Context context){
			mInflater = LayoutInflater.from(context);
			for(int i = 0; i < names.length; i++){
				rowState.add(false);
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.friend_tagger_row, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.friend_tagger_row_img_text);
				holder.text2 = (TextView) convertView.findViewById(R.id.friend_tagger_row_name_text);
				//holder.backgroundColor = Color.BLACK;
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text.setText(images[position]);
			holder.text2.setText(names[position]);
			if((Boolean) rowState.get(position) == false)
				convertView.setBackgroundColor(Color.BLACK);
			else
				convertView.setBackgroundColor(Color.MAGENTA);
			//convertView.setBackgroundColor(holder.backgroundColor);
			return convertView;
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_tagger_vertical);
		mlvSelectedFriends = (ListView) findViewById(R.id.lv_selectedNames);
		mlvSelectedFriends.setAdapter(new ArrayAdapter<String>(this, R.layout.friend_selected_row));
		mlvSelectedFriends.setDivider(null);
		mlvSelectedFriends.setDividerHeight(0);
		
		ListView lvFriends = (ListView) findViewById(R.id.lv_friends);
		lvFriends.setAdapter(new EfficientAdapter(this));
		
		lvFriends.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				ViewHolder holder = (ViewHolder) view.getTag();
				Boolean currentState = (Boolean) rowState.get(position);
				rowState.set(position, !currentState);
				if((Boolean)rowState.get(position) == false){
					view.setBackgroundColor(Color.BLACK);
					((ArrayAdapter) mlvSelectedFriends.getAdapter()).remove(holder.text2.getText());
				}
				else{
					view.setBackgroundColor(Color.MAGENTA);
					((ArrayAdapter) mlvSelectedFriends.getAdapter()).add(holder.text2.getText());
				}
			}});
	}
}
